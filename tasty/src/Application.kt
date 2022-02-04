/*
 * (c) VAP Communications Group, 2022
 */

package online.vapcom.swcomp.tasty

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.event.Level

//NOTE: этоm вид main() используется для запуска с application.conf (см. https://ktor.io/docs/gradle.html#create-engine-main),
// плюс ktor-модуль оформляется как Application.module (https://ktor.io/docs/modules.html)
fun main(args: Array<String>): Unit = EngineMain.main(args)

/**
 * Запуск сервера через embeddedServer, чтобы то же самое можно было бы делать в Android-приложении
 */
fun main() {
    getTastyEmbeddedServer(8080).start(wait = true)
}

/**
 * Возвращает встраиваемый сервер для запуска на заданном TCP-порту
 */
fun getTastyEmbeddedServer(port: Int): NettyApplicationEngine {
    return embeddedServer(Netty, port = port) {
        module()
    }
}

/**
 * Модуль ktor-приложения, см. https://ktor.io/docs/modules.html
 */
fun Application.module() {

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }

    install(PartialContent) {
        // Maximum number of ranges that will be accepted from a HTTP request.
        // If the HTTP request specifies more ranges, they will all be merged into a single range.
        // maxRangeCount = 10
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        // Static feature. Try to access `/static/ktor_logo.svg`
        static("/static") {
            resources("static")
        }
        static("/cdn") {
            resources("cdn")
        }


        // запрос токенов авторизации в конце процедуры аутентификации
//        get("/mobile/auth/callback") {
//            call.respondText(authCallbackResult(), ContentType.Application.Json)
//        }

        // поиск слова
        get("/words/search") {
            val word = call.parameters["search"]
            if (word == null) {
                call.respond(HttpStatusCode.BadRequest, "no word for search in request")
                return@get
            }

            when (word) {
                "sky" -> { // известное слово
                    call.respondText(getSkyResponse(call.request.local), ContentType.Application.Json)
                }
                "error" -> { // эмуляция ошибки сервера
                    call.respond(HttpStatusCode.InternalServerError, "Internal Server Error")
                }

                // неизвестное слово, пустой массив
                else -> call.respondText("[]", ContentType.Application.Json)
            }
        }

        get("/meanings") {
            val id = call.parameters["ids"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "meaning id is missed in request")
                return@get
            }

            call.respondText(getMeaningResponse(id, call.request.local), ContentType.Application.Json)
        }


    } // routing
} // embeddedServer module

/**
 * Возвращает JSON-ответ на запрос поиска слова sky, по сравнению с оригинальным сильно урезан.
 * NOTE: URL cdn-файлов заменяются на адрес tasty
 */
fun getSkyResponse(cp: RequestConnectionPoint): String {
    return """
[
  {"id":1017,"text":"sky", "meanings":[
    {"id":225302,"partOfSpeechCode":"n",
      "translation":{"text":"небо","note":null},
      "previewUrl":"//${cp.host}:${cp.port}/cdn/resized-images/96x72/52c194b8ee9abe90b6d03572afddf0d5.jpeg",
      "imageUrl":"//${cp.host}:${cp.port}/cdn/resized-images/640x480/52c194b8ee9abe90b6d03572afddf0d5.jpeg",
      "transcription":"ska\u026a",
      "soundUrl":"//${cp.host}:${cp.port}/cdn/sound/sky.mp3"},
    {"id":225303,"partOfSpeechCode":"v",
      "translation":{"text":"подбрасывать","note":null},
      "previewUrl":"//${cp.host}:${cp.port}/cdn/resized-images/96x72/1f36a139ec8ee51fa34355f817e0a177.jpeg",
      "imageUrl":"//${cp.host}:${cp.port}/cdn/resized-images/640x480/1f36a139ec8ee51fa34355f817e0a177.jpeg",
      "transcription":"ska\u026a",
      "soundUrl":"//${cp.host}:${cp.port}/cdn/sound/sky.mp3"}]},
  {"id":8210,"text":"skyline","meanings":[
    {"id":73256,"partOfSpeechCode":"n","translation":{"text":"горизонт","note":""},
      "previewUrl":"//${cp.host}:${cp.port}/cdn/resized-images/96x72/840686e807083ed569b717cc82dc17b2.jpeg",
      "imageUrl":"//${cp.host}:${cp.port}/cdn/resized-images/640x480/840686e807083ed569b717cc82dc17b2.jpeg",
      "transcription":"\u02c8ska\u026ala\u026an",
      "soundUrl":"//${cp.host}:${cp.port}/cdn/sound/skyline.mp3"}
    ]}
]        
    """.trimIndent()
}

/**
 * Возвращает JSON с ответом на запрос значения слова.
 * NOTE: Реальных полей намного больше
 */
fun getMeaningResponse(meaningID: String, cp: RequestConnectionPoint): String {
    return when(meaningID) {
        "225302" ->     // небо
"""
[{
    "id":"225302","wordId":1017,"difficultyLevel":2,
    "partOfSpeechCode":"n","prefix":null,
    "text":"sky",
    "soundUrl":"//${cp.host}:${cp.port}/cdn/sound/sky.mp3",
    "transcription":"ska\u026a",
    "updatedAt":"2021-11-23 21:38:46",
    "mnemonics":null,
    "translation":{"text":"\u043d\u0435\u0431\u043e","note":null},
    "images":[{"url":"//${cp.host}:${cp.port}/cdn/resized-images/200x150/52c194b8ee9abe90b6d03572afddf0d5.jpeg"}]
}]
""".trimIndent()

        "225303" ->     // подбрасывать
"""
[{
    "id":"225303","wordId":1017,"difficultyLevel":2,
    "partOfSpeechCode":"v","prefix":null,
    "text":"sky",
    "soundUrl":"//${cp.host}:${cp.port}/cdn/sound/sky.mp3",
    "transcription":"ska\u026a",
    "updatedAt":"2021-11-23 21:38:46",
    "mnemonics":null,
    "translation":{"text":"\u043d\u0435\u0431\u043e","note":null},
    "images":[{"url":"//${cp.host}:${cp.port}/cdn/resized-images/200x150/1f36a139ec8ee51fa34355f817e0a177.jpeg"}]
}]
""".trimIndent()

        "73256" ->     // горизонт
"""
[{
    "id":"73256","wordId":8210,"difficultyLevel":null,
    "partOfSpeechCode":"n","prefix":null,
    "text":"skyline",
    "soundUrl":"//${cp.host}:${cp.port}/cdn/sound/skyline.mp3",
    "transcription":"ska\u026a",
    "updatedAt":"2021-11-23 16:20:33",
    "mnemonics":null,
    "translation":{"text":"горизонт","note":null},
    "images":[{"url":"//${cp.host}:${cp.port}/cdn/resized-images/200x150/840686e807083ed569b717cc82dc17b2.jpeg"}]
}]
""".trimIndent()

        else -> "[]"    // значение не найдено
    }
}


/**
 * Читает файл из ресурсов в байтовый массив
 */
suspend fun readResourceContent(call: ApplicationCall, path: String): ByteArray {
    val content = call.resolveResource(path) as OutgoingContent.ReadChannelContent
    val buff = ByteArray(content.contentLength?.toInt() ?: 0)
    if (buff.isEmpty()) {
        return buff
    } else {
        content.readFrom().readFully(buff, 0, buff.size)
        return buff
    }
}

