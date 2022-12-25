const val kotlin_version = "1.7.22"

object Dependencies {
    object Kotlin {
        const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1"
        const val coroutines_core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4"
    }

    object MongoDB {
        const val driver_sync = "org.mongodb:mongodb-driver-sync:4.8.0"
        const val driver_reactivestreams = "org.mongodb:mongodb-driver-reactivestreams:4.8.0"
    }

    // TODO kmongo usage
    const val kmongo = "org.litote.kmongo:kmongo:4.6.0"
    const val kmongo_coroutine = "org.litote.kmongo:kmongo-coroutine:4.6.0"

    const val weakness = "org.cufy:weakness:1.0.0"
}
