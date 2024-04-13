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
package org.cufy.mongodb.gridfs.internal

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.getOrElse
import kotlinx.coroutines.runBlocking
import org.cufy.mongodb.gridfs.MongoDownload
import org.cufy.mongodb.gridfs.MongoFile
import java.io.InputStream
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.min

internal class MongoDownloadImpl(
    job: Deferred<Unit>,
    override val file: Deferred<MongoFile>,
    private val channel: ReceiveChannel<ByteBuffer>,
    override val bufferSizeBytes: Int,
    val onClose: () -> Unit,
) : MongoDownload, Deferred<Unit> by job {
    override fun close() {
        leftover.set(null)
        onClose()
    }

    override suspend fun closeAndAwait(): MongoFile {
        close()
        await()
        return file.await()
    }

    override suspend fun read(dest: ByteArray): Int {
        return read(dest, 0, dest.size)
    }

    private val leftover = AtomicReference<ByteBuffer?>(null)

    override suspend fun read(): ByteBuffer? {
        var buffer = leftover.getAndSet(null)

        while (true) {
            if (buffer != null && buffer.hasRemaining())
                return buffer

            buffer = channel.receiveCatching()
                .getOrElse { return null }
        }
    }

    override suspend fun read(dest: ByteArray, offset: Int, limit: Int): Int {
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
}

internal class MongoDownloadInputStream(private val descriptor: MongoDownload) : InputStream() {
    override fun read(): Int {
        val bytes = ByteArray(1)
        runBlocking { descriptor.read(bytes) }
        return bytes[0].toInt()
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return runBlocking { descriptor.read(b, off, len) }
    }

    override fun close() {
        descriptor.close()
    }
}
