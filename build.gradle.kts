plugins {
    kotlin("jvm") version "1.9.24"
    id("io.ktor.plugin") version "2.3.11"
    kotlin("plugin.serialization") version "1.9.24"
}

group = "com.example"
version = "0.0.1"

application {
    mainClass.set("com.example.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:2.3.11")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.11")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.11")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.11")
    implementation("ch.qos.logback:logback-classic:1.5.6")

    implementation("io.ktor:ktor-server-openapi:2.3.11")
    implementation("io.ktor:ktor-server-swagger:2.3.11")
    implementation("io.ktor:ktor-server-status-pages-jvm:2.3.11")
    implementation("io.ktor:ktor-server-call-logging-jvm:2.3.11")

    testImplementation("io.ktor:ktor-server-test-host:2.3.11")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.24")
}