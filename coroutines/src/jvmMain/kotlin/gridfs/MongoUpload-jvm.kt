/*
 *	Copyright 2022-2023 cufy.org and meemer.com
 *
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *	    http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 */
package org.cufy.mongodb.gridfs

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import org.cufy.bson.BsonElement
import org.cufy.mongodb.gridfs.internal.MongoUploadOutputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer

/* ============= ------------------ ============= */

actual interface MongoUpload : Deferred<Unit>, AutoCloseable {
    actual val isClosedForWrite: Boolean
    actual val chunkSizeBytes: Int
    actual val id: Deferred<BsonElement>

    actual suspend fun closeAndAwait(): BsonElement
    actual suspend fun write(src: ByteArray)
    actual suspend fun write(src: ByteArray, offset: Int, length: Int)

    /**
     * Write using the given [buffer] and **suspend**
     * until the buffer is consumed.
     *
     * @param buffer the buffer to consume.
     * @since 2.0.0
     */
    suspend fun write(buffer: ByteBuffer)
}

/* ============= ------------------ ============= */

/**
 * Return an output stream backed by this upload
 * instance.
 *
 * @since 2.0.0
 */
fun MongoUpload.asOutputStream(): OutputStream {
    return MongoUploadOutputStream(this)
}

/**
 * Write the bytes in the given [file] and **suspend**
 * until the bytes are written.
 *
 * @param file the file to be written.
 * @return how many bytes have been written.
 * @since 2.0.0
 */
suspend fun MongoUpload.writeFrom(file: File): Long {
    return file.inputStream().use { writeFrom(it) }
}

/**
 * Keep reading and writing chunks from the given
 * [stream] to this until the given [stream]
 * reaches its end and **suspend** on each write
 * until it is completed.
 *
 * @param stream the stream to read from.
 * @return how many bytes have been written.
 * @since 2.0.0
 */
suspend fun MongoUpload.writeFrom(stream: InputStream): Long {
    var transferred = 0L
    val buffer = ByteArray(chunkSizeBytes)

    while (true) {
        // TODO OPTIMIZE should we use withContext(Dispatchers.IO) ?
        @Suppress("BlockingMethodInNonBlockingContext")
        val length = stream.read(buffer)
        if (length < 0) break
        transferred += length
        write(buffer, 0, length)
    }

    return transferred
}

/**
 * Collect the buffers in the given [flow] and write
 * to this and **suspend** until all the buffers has
 * been written.
 *
 * @param flow the flow to collect.
 * @return how many bytes have been written.
 * @since 2.0.0
 */
suspend fun MongoUpload.writeFrom(flow: Flow<ByteBuffer>): Long {
    var transferred = 0L

    flow.collect {
        transferred += it.remaining()
        write(it)
    }

    return transferred
}

/**
 * Receive the buffers sent to the given [channel]
 * and write to this and **suspend** until all the
 * buffers has been written.
 *
 * **Note: this function will remain suspended
 * until the given [channel] is closed**
 *
 * @param channel the channel to receive from.
 * @return how many bytes have been written.
 * @since 2.0.0
 */
suspend fun MongoUpload.writeFrom(channel: ReceiveChannel<ByteBuffer>): Long {
    var transferred = 0L

    channel.consumeEach {
        transferred += it.remaining()
        write(it)
    }

    return transferred
}

/* ============= ------------------ ============= */
