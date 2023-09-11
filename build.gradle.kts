import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version libs.versions.kotlin
    kotlin("plugin.serialization") version libs.versions.kotlin
    id("maven-publish")
}

group = "org.cufy"
version = "2.0.0"

tasks.wrapper {
    gradleVersion = "8.2.1"
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
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
                artifactId = "monkt"
            }
        }
    }
}

subprojects {
    group = "org.cufy.monkt"

    afterEvaluate {
        publishing {
            publications {
                create<MavenPublication>("maven") {
                    from(components["java"])
                    artifactId = project.name
                }
            }
        }
    }
}
