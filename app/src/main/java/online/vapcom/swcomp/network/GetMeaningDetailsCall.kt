/*
 * (c) VAP Communications Group, 2020
 */

package online.vapcom.swcomp.network

import android.util.Log
import com.squareup.moshi.*
import online.vapcom.swcomp.data.*

private const val TAG = "GetWordDCl"
private const val ERROR_MODULE_NUM = 52     // номер модуля в кодах ошибок, см. ErrorDescription

/**
 * Запрос данных значения слова
 */
class GetMeaningDetailsCall(private val meaningID: String): BaseServerCall() {

    // ответ на верхний уровень
    var hasDetails: Boolean = false
    lateinit var meaningDetails: MeaningDetails

    /**
     * Выполняет запрос на сервер, должна запускаться в IO-нити
     */
    fun execute() {
        if(meaningID.isBlank()) {
            success = false
            errorDescription = ErrorDescription(false, errCode(ERROR_MODULE_NUM, 12),"Empty word ID")
            return
        }
        executeGetRequest("$MEANING_DETAILS_URL?ids=$meaningID")
    }

    /**
     * Ответ, приходящий от сервера.
     * Пример JSON см. в docs/api_readme.txt
     * NOTE: в images приходит картинка 200x150, хотя на CDN есть большие 640x480, которые возвращаются
     * в ответе на запрос поиска слова.
     */
    @JsonClass(generateAdapter = true)
    data class ServerMeaning(val id: String = "",                               // ID значения
                             val partOfSpeechCode: String = "",                 // код части речи
                             val text: String = "",                             // слово, к которому относится запрашиваемое значение
                             val soundUrl: String = "",                         // URL аудиофайла с произношением слова
                             val transcription: String = "",                    // транскрипция слова
                             val translation: ServerTranslation = ServerTranslation(),
                             @Json(name = "mnemonics")
                             val _mnemonics: String? = "",                      // пример фразы для запоминания слова, может быть null
                             val images: List<ServerImageUrl> = emptyList()     // массив URL картинок, берём только первую
    ) {
        val mnemonics: String = _mnemonics ?: ""
    }

    @JsonClass(generateAdapter = true)
    data class ServerTranslation(val text: String = "") // note игнорируем

    @JsonClass(generateAdapter = true)
    data class ServerImageUrl(val url: String = "")

    override fun parseJson(json: String) {
        // сервер присылает список значений слов на верхнем уровне
        val listType = Types.newParameterizedType(List::class.java, ServerMeaning::class.java)
        val jsonAdapter: JsonAdapter<List<ServerMeaning>> = moshi.adapter(listType)

        val r = jsonAdapter.fromJson(json)
        if (r == null) {
            Log.e(TAG, "Error: unable to parse server response: '$json'")
            errorDescription = ErrorDescription(false, errCode(ERROR_MODULE_NUM, 10),"Unable to parse server response")
        } else {
            if(r.isNotEmpty()) {
                // берём только первое значение из списка
                val sm = r[0]
                if(sm.id != meaningID) {    // ответ не на то, что запрашивали
                    Log.e(TAG, "Error: wrong meaning ID in response: ${sm.id}, requested: $meaningID")
                    errorDescription = ErrorDescription(false, errCode(ERROR_MODULE_NUM, 11),
                        "Wrong meaning ID in response: ${sm.id}, requested: $meaningID")
                    success = false
                    return
                }

                meaningDetails = MeaningDetails(
                    id = sm.id,
                    pos = PartOfSpeech.fromServerPOSCode(sm.partOfSpeechCode),
                    text = sm.text,
                    transcription = sm.transcription,
                    pronunciationURL = addSchemeToURL(sm.soundUrl),
                    translation = sm.translation.text,
                    mnemonics = sm.mnemonics,
                    //NOTE: подменяем разрешение картинки с 200x150 на 640x480, чтобы их можно было показывать на всю ширину экрана
                    imageURL = addSchemeToURL(if(sm.images.isEmpty()) "" else sm.images[0].url).replace("200x150","640x480")
                )
                hasDetails = true
            } else {
                hasDetails = false
            }

            success = true
        }
    }
}