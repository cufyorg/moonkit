package org.cufy.mongodb.test

import kotlinx.coroutines.*
import org.cufy.mongodb.*
import org.cufy.mongodb.gridfs.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import java.io.InputStream
import java.util.*
import kotlin.io.path.createTempFile
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
object GridFSTest {
    private lateinit var client: MongoClient
    private lateinit var database: MongoDatabase
    private lateinit var bucket: MongoBucket

    @BeforeEach
    fun before() {
        val connectionString = "mongodb://localhost"
        val name = "mongodb-gridfs-test-${UUID.randomUUID()}"
        client = createMongoClient(connectionString)
        database = client[name]
        bucket = createMongoBucket(database)
    }

    @AfterEach
    fun after() {
        runBlocking {
            database.drop()
        }
    }

    @Test
    fun `upload simple files`() {
        runBlocking {
            val input = createTempFile().toFile()
            val output = createTempFile().toFile()

            input.fillWithGarbage()

            // UPLOADING
            val upload = bucket.asyncUpload("abc1def2")
            upload.writeFrom(input)
            val id = upload.closeAndAwait()

            // DOWNLOADING
            val download = bucket.asyncDownload(id)
            download.writeTo(output)
            val file = download.closeAndAwait()

            assertTrue(diff(input, output), "Input does not equal output")
        }
    }
}

fun File.fillWithGarbage() {
    bufferedWriter().use { writer ->
        // 8B * 125,000 = 1MB
        repeat(125_000) {
            writer.write("ABC1DEF2")
        }
    }
}

fun diff(a: File, b: File): Boolean {
    val aStream = a.inputStream()
    val bStream = b.inputStream()
    val aBuffer = ByteArray(1_000_000)
    val bBuffer = ByteArray(1_000_000)

    while (true) {
        val ar = aBuffer.fillFrom(aStream)
        val br = bBuffer.fillFrom(bStream)

        if (ar != br || !aBuffer.contentEquals(bBuffer))
            return false

        if (ar < 0)
            return true
    }
}

fun ByteArray.fillFrom(stream: InputStream): Int {
    var written = 0
    while (written < size) {
        val r = stream.read(this, written, size - written)
        if (r < 0) return if (written == 0) return -1 else written
        written += r
    }
    return written
}
