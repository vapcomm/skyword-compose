/*
 * (c) VAP Communications Group, 2020
 */

package online.vapcom.swcomp.network

import android.util.Base64
import android.util.Log
import online.vapcom.swcomp.BuildConfig
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import online.vapcom.swcomp.data.ErrorDescription
import online.vapcom.swcomp.data.UIErrno
import online.vapcom.swcomp.data.errCode
import online.vapcom.swcomp.data.stackTraceToString
import java.io.IOException
import java.net.ConnectException
import java.net.ProtocolException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

private const val TAG = "BaseSrCall"

private const val ERROR_MODULE_NUM = 50     // номер модуля в кодах ошибок, см. ErrorDescription


/**
 * Базовый класс запросов к серверу
 */
abstract class BaseServerCall {

    companion object {

        //------- адреса сервиса словаря -------
        private const val BASE_DICT_URL: String = BuildConfig.SERVER_DICT

        const val SEARCH_WORD_URL = "$BASE_DICT_URL/words/search"    // запрос поиска слова в словаре
        const val MEANING_DETAILS_URL = "$BASE_DICT_URL/meanings"    // запрос подробных данных значения слова

        val MEDIA_JSON = "application/json; charset=utf-8".toMediaType()

        private const val WRITE_TIMEOUT_MS = 5000L     // таймаут TCP-сокета на установление соединения и запись данных
        private const val READ_TIMEOUT_MS = 5000L      // таймаут TCP-сокета на чтение данных

        // один OkHttpClient на все запросы
        @Volatile private var httpClient: OkHttpClient? = null

        fun getHttpClient() =
            httpClient ?: synchronized(this) {
                httpClient ?:
                    (if(BuildConfig.HTTP_LOGS_ON) { // логи HTTP включаются только в debug сборке
                        val logging = HttpLoggingInterceptor()
                        logging.level = (HttpLoggingInterceptor.Level.BODY)
                        OkHttpClient.Builder()
                            .addInterceptor(logging)
                            .connectTimeout(WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                            .readTimeout(READ_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                            .writeTimeout(WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                            .build()
                    } else {
                        OkHttpClient.Builder()
                            .connectTimeout(WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                            .readTimeout(READ_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                            .writeTimeout(WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                            .build()
                    }
                    ).also { httpClient = it}
            }

        /**
         * Добавляет схему к URL, который приходит с сервера, если serverUrl без схемы
         */
        fun addSchemeToURL(serverUrl: String): String {
            return if(serverUrl.startsWith("//")) BASE_DICT_URL.substringBefore("//") + serverUrl
                   else serverUrl
        }

        // один Moshi на всех, он нитебезопасный и в нём хранится кэш адаптеров
        val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }   // companion

    var success: Boolean = false    // флаг успешности выполнения запроса
    var errorDescription: ErrorDescription = ErrorDescription.EMPTY  // сюда обработчики ответа помещают описание ошибки если success == false
    /**
     * Выполняет POST - запрос на сервер с JSON, должна запускаться в IO-нити
     *
     */
    protected fun executePostRequest(url: String, json: String, authToken: String = "") {
        val builder = Request.Builder().post(json.toRequestBody(MEDIA_JSON))

        executeHTTPRequest(builder, url, authToken) { rsp ->
            parseJsonResponse(rsp.body?.string() ?: "")
        }
    }

    /**
     * Выполняет POST - запрос на сервер с данными формы. application/x-www-form-urlencoded
     * Должна запускаться в IO-нити
     *
     */
    fun executePostRequest(url: String, body: FormBody, authToken: String = "") {
        val builder = Request.Builder().post(body)

        executeHTTPRequest(builder, url, authToken) { rsp ->
            parseJsonResponse(rsp.body?.string() ?: "")
        }
    }

    /**
     * Выполняет GET - запрос на сервер, в ответ ожидает JSON
     * должна запускаться в IO-нити
     *
     */
    protected fun executeGetRequest(url: String, authToken: String = "") {
        val builder = Request.Builder().get()

        executeHTTPRequest(builder, url, authToken) { rsp ->
            parseJsonResponse(rsp.body?.string() ?: "")
        }
    }

    /**
     * Делает GET-запрос, обработчик успешного ответа задаётся в responseProc
     */
    protected fun executeRawGetRequest(url: String, authToken: String = "", responseProc: (rsp: Response) -> Unit ) {
        val builder = Request.Builder().get()

        executeHTTPRequest(builder, url, authToken, responseProc)
    }


    /**
     * Выполняет запрос на сервер, в builder уже должны быть добавлены .post/.get
     *
     * @param authToken токен, полученный после успешной аутиентификации, строка вида "<тип> <значение>",
     *                  для неаутентифицированных запросов может отсутствовать
     * @param responseProc функция по обработке HTTP-ответа
     */
    private fun executeHTTPRequest(builder: Request.Builder, url: String, authToken: String = "",
                                   responseProc: (rsp: Response) -> Unit) {
        success = false

        var method = "UNKNOWN"

        try {
            builder.url(url)

            if(authToken.isNotEmpty())
                builder.header("Authorization", authToken)

            val request = builder.build()
            method = request.method

            getHttpClient().newCall(request).execute().use { rsp ->
                if(rsp.isSuccessful) {
                    responseProc(rsp)
                }
                else {
                    success = false
                    errorDescription = when(rsp.code) {
                        401 -> ErrorDescription(false, UIErrno.SESSION_CLOSED.errno, "${rsp.code} ${rsp.message}")
                        in 400..499 -> ErrorDescription(false, UIErrno.CLIENT_ERROR.errno, "${rsp.code} ${rsp.message}")
                        in 500..599 -> ErrorDescription(false, UIErrno.SERVER_ERROR.errno, "${rsp.code} ${rsp.message}")
                        else -> ErrorDescription(false, UIErrno.SERVER_ERROR.errno, "${rsp.code} ${rsp.message}")
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, "$method Request to '$url' error: $ex")

            errorDescription = when(ex) {
                is SocketTimeoutException -> {
                    ErrorDescription(false, UIErrno.TIMEOUT.errno, ex.localizedMessage ?: "")
                }
                is UnknownHostException,    // хост не найден в DNS
                is ConnectException -> {    // сеть не доступна, здесь же Connection Refused
                    ErrorDescription(false, UIErrno.CONNECTION_ERROR.errno, ex.localizedMessage ?: "")
                }
                is ProtocolException -> {   // ловил unexpected end of stream
                    ErrorDescription(false, UIErrno.SERVER_ERROR.errno, ex.localizedMessage ?: "")
                }
                else -> ErrorDescription(false, errCode(ERROR_MODULE_NUM, 10),
                    "ServerCall fatal error", ex.localizedMessage ?: "", stackTraceToString(ex)
                )
            }
        }

    }

    /**
     * Разбор JSON-ответа от сервера
     */
    private fun parseJsonResponse(json: String) {
        success = false

        Log.i(TAG, ">>>>> parseResponse: json: '$json'")

        if (json.isBlank()) {
            Log.e(TAG, "Error: Empty server response: '$json'")
            errorDescription = ErrorDescription(false, errCode(ERROR_MODULE_NUM, 20),
                "Empty server response: '$json'")
            return
        }

        try {
            parseJson(json)              // потомки здесь разбирают конкретный JSON
        } catch (ex : JsonDataException) {
            Log.e(TAG, "Error: Bad format of server response: '$json', ex: ${ex.message}")
            errorDescription = ErrorDescription(false, errCode(ERROR_MODULE_NUM, 21),
                "Bad format of server response", ex.message ?: "")
        } catch (ex : IOException) {
            Log.e(TAG, "Error: Malformed server response: '$json', ex: ${ex.message}")
            errorDescription = ErrorDescription(false, errCode(ERROR_MODULE_NUM, 22),
                "Malformed server response: ${ex.localizedMessage}", ex.message ?: "")
        } catch (ex : Exception) {
            Log.e(TAG, "Error: Unable to parse server response: '$json', ex: ${ex.message}")
            errorDescription = ErrorDescription(false, errCode(ERROR_MODULE_NUM, 23),
                "Unable to parse server response: ${ex.localizedMessage}", ex.message ?: "")
        }

    }

    /**
     * Производит окончательную разборку пришедшего JSON-ответа, исключения отлавливает вызывающая функция
     */
    open fun parseJson(json: String) {
        Log.e(TAG, "Error: NOT OVERRIDDEN parseJson()")
    }

    /**
     * Создаёт токен базовой аутентификации, см. https://en.wikipedia.org/wiki/Basic_access_authentication
     */
    protected fun basicAuthToken(login: String, password: String): String {
        return "Basic " + Base64.encodeToString("$login:$password".toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    }

}
