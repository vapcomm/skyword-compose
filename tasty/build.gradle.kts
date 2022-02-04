
// https://github.com/ktorio/ktor/releases
val ktorVersion = "1.6.7"
val kotlinVersion = "1.6.10"

plugins {
    application
    kotlin("jvm") version "1.6.10"
    // id("com.github.johnrengelman.shadow") version "6.1.0"   // Упаковка в JAR для tastyInside
}

group = "online.vapcom.swcomp.tasty"
version = "0.0.2"

application {
    mainClass.value("io.ktor.server.netty.EngineMain")

    // mainClassName требуется для shadowJar. Исправления плагина ожидаются:
    // https://github.com/johnrengelman/shadow/issues/609
    // https://github.com/johnrengelman/shadow/pull/612
    //mainClassName = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions:$ktorVersion")

    implementation("ch.qos.logback:logback-classic:1.2.3")

    // https://github.com/square/moshi/releases
    val moshiVersion = "1.13.0"
    implementation("com.squareup.moshi:moshi:$moshiVersion")
    implementation("com.squareup.moshi:moshi-kotlin:$moshiVersion")
    implementation("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")

    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")
