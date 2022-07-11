import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.0"
    kotlin("plugin.serialization") version "1.7.0"
    id("maven-publish")
}

group = "org.cufy.mangaka"
version = "1.0.0"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")

    implementation("org.cufy:weakness:1.0.0")

    implementation("org.litote.kmongo:kmongo:4.6.0")
    implementation("org.litote.kmongo:kmongo-coroutine:4.6.0")
    implementation("org.mongodb:mongodb-driver-sync:4.6.0")
    implementation("org.mongodb:mongodb-driver-reactivestreams:4.6.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                artifactId = "mangaka"
            }
        }
    }
}
