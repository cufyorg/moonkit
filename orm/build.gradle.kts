plugins {
    kotlin("jvm") version kotlin_version
    kotlin("plugin.serialization") version kotlin_version
    id("maven-publish")
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(project(":bson"))
    implementation(project(":coroutines"))

    implementation(kotlin("stdlib"))

    implementation(Dependencies.Kotlin.serialization)
    implementation(Dependencies.Kotlin.coroutines_core)
    implementation(Dependencies.Kotlin.coroutines_reactive)
    implementation(Dependencies.Kotlin.reflect)

    implementation(Dependencies.MongoDB.driver_sync)
    implementation(Dependencies.MongoDB.driver_reactivestreams)

    implementation(Dependencies.weakness)

    testImplementation(kotlin("test"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
