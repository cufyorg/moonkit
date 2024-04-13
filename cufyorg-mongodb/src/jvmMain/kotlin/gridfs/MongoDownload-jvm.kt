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

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.cufy.mongodb.gridfs

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.getOrElse
import org.cufy.mongodb.ExperimentalMongodbApi
import org.cufy.mongodb.gridfs.internal.MongoDownloadInputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.min

/* ============= ------------------ ============= */

@ExperimentalMongodbApi
actual class MongoDownload internal constructor(
    actual val file: Deferred<MongoFile>,
    actual val bufferSizeBytes: Int,
    job: Deferred<Unit>,
    private val channel: ReceiveChannel<ByteBuffer>,
    private val onClose: () -> Unit,
) : Deferred<Unit> by job, AutoCloseable {
    actual override fun close() {
        leftover.set(null)
        onClose()
    }

    actual suspend fun closeAndAwait(): MongoFile {
        close()
        await()
        return file.await()
    }

    private val leftover = AtomicReference<ByteBuffer?>(null)

    actual suspend fun read(dest: ByteArray): Int {
        return read(dest, 0, dest.size)
    }

    actual suspend fun read(dest: ByteArray, offset: Int, limit: Int): Int {
        if (offset + limit > dest.size) {
            throw IndexOutOfBoundsException(
                "range: ${offset..offset + limit} while size: ${dest.size}"
            )
        }
        val buffer = read()
        buffer ?: return -1
        val length = min(limit, buffer.remaining())
        buffer.get(dest, offset, length)
        if (buffer.hasRemaining())
            leftover.set(buffer)
        return length
    }

    /**
     * Obtain the next buffer in the download.
     *
     * Once a buffer is returned from this function
     * it is considered read.
     *
     * @return the next buffer. Or `null` if no more data can be read.
     * @since 2.0.0
     */
    suspend fun read(): ByteBuffer? {
        var buffer = leftover.getAndSet(null)

        while (true) {
            if (buffer != null && buffer.hasRemaining())
                return buffer

            buffer = channel.receiveCatching()
                .getOrElse { return null }
        }
    }
}

/* ============= ------------------ ============= */

/**
 * Write all the remaining chunks to the given [upload]
 * instance and **suspend** until all the chunks has
 * been written.
 *
 * @param upload the instance to transfer the buffers to.
 * @return the number of bytes written.
 * @since 2.0.0
 */
@ExperimentalMongodbApi
actual suspend fun MongoDownload.writeTo(upload: MongoUpload): Long {
    var transferred = 0L
    while (true) {
        val buffer = read()
        buffer ?: return transferred
        transferred += buffer.remaining()
        upload.write(buffer)
    }
}

/**
 * Return an input stream backed by this download
 * instance.
 *
 * @since 2.0.0
 */
@ExperimentalMongodbApi
fun MongoDownload.asInputStream(): InputStream {
    return MongoDownloadInputStream(this)
}

/**
 * Write all the remaining chunks into the given
 * [file] and **suspend** until all the chunks has
 * been written.
 *
 * @param file the file to download to.
 * @return the number of bytes written.
 * @since 2.0.0
 */
@ExperimentalMongodbApi
suspend fun MongoDownload.writeTo(file: File): Long {
    return file.outputStream().use { writeTo(it) }
}

/**
 * Keep reading and writing chunks from this to
 * the given [stream] until reaching the end of
 * the download and **suspend** on each write until
 * it is completed.
 *
 * @param stream the stream to write to.
 * @return the number of bytes written.
 * @since 2.0.0
 */
@ExperimentalMongodbApi
suspend fun MongoDownload.writeTo(stream: OutputStream): Long {
    var transferred = 0L
    val buffer = ByteArray(bufferSizeBytes)

    while (true) {
        val length = read(buffer)
        if (length < 0) break
        transferred += length
        // TODO OPTIMIZE should we use withContext(Dispatchers.IO) ?
        @Suppress("BlockingMethodInNonBlockingContext")
        stream.write(buffer, 0, length)
    }

    return transferred
}

/**
 * Send all the remaining chunks through the given [channel]
 * and **suspend** until all the chunks has been sent.
 *
 * @param channel the channel to send through.
 * @return the number of bytes sent.
 * @since 2.0.0
 */
@ExperimentalMongodbApi
suspend fun MongoDownload.writeTo(channel: SendChannel<ByteBuffer>): Long {
    var transferred = 0L

    while (true) {
        val buffer = read()
        buffer ?: break
        transferred += buffer.remaining()
        channel.send(buffer)
    }

    return transferred
}

/* ============= ------------------ ============= */
