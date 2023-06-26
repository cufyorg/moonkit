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
import org.cufy.mongodb.gridfs.internal.MongoUploadOutputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer

/**
 * A GridFS upload instance.
 *
 * Can be used to asynchronously complete an upload
 * operation.
 *
 * This interface is a combination of a coroutine-based
 * [OutputStream] for writing the upload file chunks and
 * a [Deferred] instance for the upload result.
 *
 * The [write] functions fails when either the upload
 * operation has been closed, canceled or failed.
 *
 * This interface implements [kotlinx.coroutines.Job]
 * which represents the background upload job.
 *
 * @param T the result of the upload job.
 * @author LSafer
 * @since 2.0.0
 */
interface MongoUpload<T> : Deferred<T>, AutoCloseable {
    /**
     * True, if the upload is closed for writing.
     */
    val isClosedForWrite: Boolean

    /**
     * The preferred upload chunk size as set in the
     * [UploadOptions].
     */
    val chunkSizeBytes: Int

    /**
     * Close the upload channel.
     *
     * After calling this function, the upload
     * channel will be closed causing the upload
     * job to finish and the upload operation to
     * be completed and the upload result to be
     * accessible through [await].
     *
     * @since 2.0.0
     */
    override fun close()

    /**
     * Close the upload channel and **await** the results.
     *
     * If the upload has failed this function will
     * throw the failure cause.
     *
     * @return the upload results.
     * @since 2.0.0
     */
    suspend fun closeAndAwait(): T {
        close()
        return await()
    }

    /**
     * Write using the given [buffer] and **suspend**
     * until the buffer is consumed.
     *
     * @param buffer the buffer to consume.
     * @since 2.0.0
     */
    suspend fun write(buffer: ByteBuffer)

    /**
     * Write from the given [src] and **suspend**
     * until the data is written.
     *
     * @param src the source array to write from.
     * @since 2.0.0
     */
    suspend fun write(src: ByteArray) {
        write(src, 0, src.size)
    }

    /**
     * Write from the given [src] and **suspend**
     * until the data is written.
     *
     * @param src the source array to write from.
     * @param offset where to start reading at [src].
     * @param length how many bytes to write.
     * @throws IndexOutOfBoundsException if `offset + length > src.size`
     * @since 2.0.0
     */
    suspend fun write(src: ByteArray, offset: Int, length: Int)
}

/**
 * Return an output stream backed by this upload
 * instance.
 *
 * @since 2.0.0
 */
fun MongoUpload<*>.asOutputStream(): OutputStream {
    return MongoUploadOutputStream(this)
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
suspend fun MongoUpload<*>.writeFrom(flow: Flow<ByteBuffer>): Long {
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
suspend fun MongoUpload<*>.writeFrom(channel: ReceiveChannel<ByteBuffer>): Long {
    var transferred = 0L

    channel.consumeEach {
        transferred += it.remaining()
        write(it)
    }

    return transferred
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
suspend fun MongoUpload<*>.writeFrom(stream: InputStream): Long {
    var transferred = 0L
    val buffer = ByteArray(chunkSizeBytes)

    while (true) {
        // TODO OPTIMIZE should we use withContext(Dispatchers.IO) ?
        val length = stream.read(buffer)
        if (length < 0) break
        transferred += length
        write(buffer, 0, length)
    }

    return transferred
}

/**
 * Write the bytes in the given [file] and **suspend**
 * until the bytes are written.
 *
 * @param file the file to be written.
 * @return how many bytes have been written.
 * @since 2.0.0
 */
suspend fun MongoUpload<*>.writeFrom(file: File): Long {
    return file.inputStream().use { writeFrom(it) }
}
