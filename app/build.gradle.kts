import java.io.FileInputStream
import java.util.*


plugins {
    id("com.android.application")
    id("kotlin-android")
    // https://github.com/google/ksp/releases
    id("com.google.devtools.ksp") version "1.6.10-1.0.2"
}

val composeVersion = "1.1.0-rc03"

android {
    compileSdk = 32
    buildToolsVersion = "32.0.0"

    defaultConfig {
        applicationId = "online.vapcom.swcomp"
        minSdk = 23
        targetSdk = 32
        versionCode = 34
        versionName = "1.0.0"

        setProperty("archivesBaseName", "skyword-compose-$versionName.$versionCode")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // конфиг подписи релизного APK-файла, параметры читаются из keystore.properties
    signingConfigs {
        create("release") {
            //NOTE: БОЛЬШИМИ БУКВАМИ, ДЛЯ ТЕХ, КТО В ТЕМЕ:
            // jks-файл с ключами подписи приложения, который лежит рядом, предназначен исключительно для подписи
            // отладочных APK-файлов. Для релизных сборок сделайте отдельный jks-файл и храните его отдельно от этих исходников.

            // keystorePropertiesFilename задан в gradle.properties или в командной строке, см. пример ниже
            val props = properties
            val keystoreFilename = props["keystorePropertiesFilename"] ?: throw InvalidUserDataException("Keystore properties filename not found")

            val keystorePropertiesFile = rootProject.file(keystoreFilename)
            val keystoreProperties = Properties()
            if (keystorePropertiesFile.exists()) {
                keystoreProperties.load(FileInputStream(keystorePropertiesFile))
            } else {
                println ("Error: Cannot find keystore properties file.\n" +
                        "A) Create this file, B) use a different file, or C) use assembleDebug.\n" +
                        "A) Create ${keystorePropertiesFile.absolutePath}\n" +
                        "B) ./gradlew -PkeystorePropertiesFilename=release-keystore.properties assembleRelease\n" +
                        "C) ./gradlew assembleDebug\n")
                throw InvalidUserDataException("Cannot find keystore properties file")
            }

            storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
            storePassword = keystoreProperties.getProperty("storePassword")
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
        }
    }

    buildTypes {
        getByName("release") {
            //NOTE: включить, когда будет делаться релиз и прочитайте NOTE выше про jks-файл
            //signingConfig = signingConfigs.getByName("release")

            isMinifyEnabled = true
            proguardFiles ("proguard-rules.pro", "moshi.pro", "moshi-kotlin.pro", getDefaultProguardFile("proguard-android.txt"))
            // versionNameSuffix = "-release"
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("release")
            buildConfigField("Boolean", "HTTP_LOGS_ON", "true")

            versionNameSuffix = "-debug"
        }
    }


    // конфигурации версий приложения
    flavorDimensions.add("versions")
    productFlavors {

        // боевые сервера
        create("prod") {
            versionNameSuffix = "-prod"
            // боевой  сервер словаря
            buildConfigField("String", "SERVER_DICT", "\"https://dictionary.skyeng.ru/api/public/v1\"")
        }

        // тестовые сервера
        create("dev") {
            versionNameSuffix = "-dev"
            applicationIdSuffix = ".dev"

            // тестовый сервер словаря
            //NOTE: в этом тестовом задании он такой же как и боевой
            buildConfigField("String", "SERVER_DICT", "\"https://dictionary.skyeng.ru/api/public/v1\"")
        }

        // для отладки на тестовом сервере tasty на локальной машине, приложение запускается в эмуляторе
        create("tasty") {
            versionNameSuffix = "-tasty"
            applicationIdSuffix = ".tasty"

            // для работы на голом HTTP в манифесте включить
            // android:usesCleartextTraffic="true"
            buildConfigField("String", "SERVER_DICT", "\"http://10.0.2.2:8080\"")
        }

        //TODO: tastyInside - тестовый сервер, запускаемый внутри приложения для ручной проверки без бэков
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xopt-in=kotlin.RequiresOptIn"     // некоторые композябли требуют @OptIn(ExperimentalComposeUiApi::class)
        )
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
    }

    packagingOptions {
        resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    }
}


dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
    // https://github.com/Kotlin/kotlinx.coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")
    implementation("androidx.media2:media2-player:1.2.0")

    // https://developer.android.com/jetpack/androidx/releases/compose-ui
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("androidx.navigation:navigation-compose:2.4.0")

    // https://github.com/google/accompanist/releases
    val accompanistVersion = "0.22.1-rc"
    implementation("com.google.accompanist:accompanist-flowlayout:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")

    implementation("com.google.android.material:material:1.5.0")

    // https://github.com/InsertKoinIO/koin/tags
    val koinVersion = "3.1.5"
    implementation("io.insert-koin:koin-android:$koinVersion")
    implementation("io.insert-koin:koin-androidx-compose:$koinVersion")

    // https://square.github.io/okhttp/changelog/
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // https://github.com/square/moshi/tags
    implementation("com.squareup.moshi:moshi:1.13.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.13.0")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.13.0")

    // загрузка картинок https://github.com/coil-kt/coil
    implementation("io.coil-kt:coil-compose:1.4.0")

    testImplementation("junit:junit:4.13.2")
    //androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
}