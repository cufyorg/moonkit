plugins {
    kotlin("jvm") version libs.versions.kotlin
    kotlin("plugin.serialization") version libs.versions.kotlin
    id("maven-publish")
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(project(":bson"))

    implementation(kotlin("stdlib"))

    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.coroutines.reactive)

    implementation(libs.mongodb.sync)
    implementation(libs.mongodb.reactivestreams)

    testImplementation(kotlin("test"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
