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
import kotlinx.coroutines.channels.SendChannel
import org.cufy.mongodb.gridfs.internal.MongoDownloadInputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer

/**
 * A GridFS download instance.
 *
 * Can be used to asynchronously complete a download
 * operation.
 *
 * This interface is a combination of a coroutine-based
 * [InputStream] for reading the downloaded file chunks
 * and [Deferred] instance for the download result.
 *
 * The [read] functions does not fail. Instead, it
 * returns special values representing that no more
 * data can be obtained.
 *
 * This interface implements [kotlinx.coroutines.Job]
 * which represents the background download job.
 *
 * @param T the result of the download job
 * @author LSafer
 * @since 2.0.0
 */
interface MongoDownload<T> : Deferred<T>, AutoCloseable {
    /**
     * The preferred download buffer size as set in the
     * [DownloadOptions].
     */
    val bufferSizeBytes: Int

    /**
     * Close the download channel.
     *
     * After calling this function, the download
     * channel will be closed causing the download
     * job to finish and the download operation to
     * be completed and the download result to be
     * accessible through [await].
     *
     * @since 2.0.0
     */
    override fun close()

    /**
     * Close the download channel and **await** the results.
     *
     * If the download has failed this function will
     * throw the failure cause.
     *
     * @return the download results.
     * @since 2.0.0
     */
    suspend fun closeAndAwait(): T {
        close()
        return await()
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
    suspend fun read(): ByteBuffer?

    /**
     * Read the next bytes into the given [dest] array.
     *
     * @return how many bytes have been read. Or `-1` if no more data can be read.
     * @since 2.0.0
     */
    suspend fun read(dest: ByteArray): Int {
        return read(dest, 0, dest.size)
    }

    /**
     * Read the next bytes into the given [dest] array.
     *
     * @param offset where to start writing at [dest].
     * @param limit the limit to how many bytes to be written.
     * @return how many bytes have been read. Or `-1` if no more data can be read.
     * @throws IndexOutOfBoundsException if `offset + limit > dest.size`
     * @since 2.0.0
     */
    suspend fun read(dest: ByteArray, offset: Int, limit: Int): Int
}

/**
 * Return an input stream backed by this download
 * instance.
 *
 * @since 2.0.0
 */
fun MongoDownload<*>.asInputStream(): InputStream {
    return MongoDownloadInputStream(this)
}

/**
 * Send all the remaining chunks through the given [channel]
 * and **suspend** until all the chunks has been sent.
 *
 * @param channel the channel to send through.
 * @return the number of bytes sent.
 * @since 2.0.0
 */
suspend fun MongoDownload<*>.writeTo(channel: SendChannel<ByteBuffer>): Long {
    var transferred = 0L

    while (true) {
        val buffer = read()
        buffer ?: break
        transferred += buffer.remaining()
        channel.send(buffer)
    }

    return transferred
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
suspend fun MongoDownload<*>.writeTo(stream: OutputStream): Long {
    var transferred = 0L
    val buffer = ByteArray(bufferSizeBytes)

    while (true) {
        val length = read(buffer)
        if (length < 0) break
        transferred += length
        // TODO OPTIMIZE should we use withContext(Dispatchers.IO) ?
        stream.write(buffer, 0, length)
    }

    return transferred
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
suspend fun MongoDownload<*>.writeTo(file: File): Long {
    return file.outputStream().use { writeTo(it) }
}

/**
 * Write all the remaining chunks to the given [upload]
 * instance and **suspend** until all the chunks has
 * been written.
 *
 * @param upload the instance to transfer the buffers to.
 * @return the number of bytes written.
 * @since 2.0.0
 */
suspend fun MongoDownload<*>.writeTo(upload: MongoUpload<*>): Long {
    var transferred = 0L
    while (true) {
        val buffer = read()
        buffer ?: return transferred
        transferred += buffer.remaining()
        upload.write(buffer)
    }
}
