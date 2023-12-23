plugins {
    `maven-publish`

    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    jvm {
        withJava()
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":coroutines"))

                implementation(kotlin("stdlib"))
                implementation(kotlin("reflect"))

                implementation(libs.cufyorg.bson)

                implementation(libs.kotlin.serialization.json)
                implementation(libs.kotlin.coroutines.core)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.kotlin.coroutines.reactive)

                implementation(libs.mongodb.sync)
                implementation(libs.mongodb.reactivestreams)
            }
        }
        jvmTest {
            dependencies {
                implementation(libs.cufyorg.ped.core)
                implementation(libs.cufyorg.ped.bson)
            }
        }
    }
}
