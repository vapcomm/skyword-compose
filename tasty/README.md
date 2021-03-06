# Tasty test server

Сервер на ktor для локального тестирования и разработки приложений без бэков.
На нём можно сэмулировать работу любого HTTP-сервера, используя тестовые данные, специально подобранные
для разработки мобильных приложений. Можно сгенерировать любые HTTP-ошибки или ошибки протокола,
используемого бэкенд-серверами.

При желании tasty-сервер можно поместить в приложение в виде JAR-файла, и запускать при старте приложения (работает с Android 7+), 
чтобы тестировщики могли проверить новую функциональность на своих устройствах без готовых бэков.
При этом код приложения, все репозитории, модели данных, сетевые запросы останутся нетронутыми,
не надо будет делать подмену модулей приложения моками и выдумывать новые данные, всё пишется один раз здесь.

Сервер запускается на машине разработчика в консоли
    ./gradlew run
либо для запуска настраивается конфигурация как Gradle-задача в Android Studio.

Приложение в варианте tastyDebug/Release, запущенное в эмуляторе делает HTTP-запросы на адрес <http://10.0.2.2:8080>.
Приложения на устройствах могут подключатся к серверу через локальную сеть. Для этого можно создать ещё один productFlavor,
задав в переменной SERVER_DICT адрес машины разработчика/сервера в локальной сети.

Известные слова:
    sky - возвращает правильный JSON
    error - возвращает 500 ошибку сервера
На остальные слова возвращает "слово не найдено".

Значение слова: "небо", остальные не реализованы, приложение грамотно отобразит ошибку.
