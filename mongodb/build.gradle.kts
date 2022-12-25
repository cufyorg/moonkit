plugins {
    kotlin("jvm") version kotlin_version
    kotlin("plugin.serialization") version kotlin_version
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(project(":bson"))

    implementation(kotlin("stdlib"))

    implementation(Dependencies.Kotlin.serialization)
    implementation(Dependencies.Kotlin.coroutines_core)

    implementation(Dependencies.MongoDB.driver_sync)
    implementation(Dependencies.MongoDB.driver_reactivestreams)

    implementation(Dependencies.kmongo)
    implementation(Dependencies.kmongo_coroutine)

    testImplementation(kotlin("test"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
