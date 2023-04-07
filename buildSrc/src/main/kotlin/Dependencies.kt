const val kotlin_version = "1.8.10"

object Dependencies {
    object Kotlin {
        const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1"
        const val coroutines_core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4"
        const val coroutines_reactive = "org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.6.4"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:1.7.22"
    }

    object MongoDB {
        const val driver_sync = "org.mongodb:mongodb-driver-sync:4.8.0"
        const val driver_reactivestreams = "org.mongodb:mongodb-driver-reactivestreams:4.8.0"
    }

    const val weakness = "org.cufy:weakness:1.0.0"
}
