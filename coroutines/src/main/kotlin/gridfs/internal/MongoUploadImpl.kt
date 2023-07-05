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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.runBlocking
import org.cufy.bson.BsonElement
import org.cufy.mongodb.gridfs.MongoUpload
import java.io.OutputStream
import java.nio.ByteBuffer

internal class MongoUploadImpl(
    job: Deferred<Unit>,
    override val id: Deferred<BsonElement>,
    private val channel: SendChannel<ByteBuffer>,
    override val chunkSizeBytes: Int,
    private val onClose: () -> Unit
) : MongoUpload, Deferred<Unit> by job {
    @OptIn(ExperimentalCoroutinesApi::class)
    override val isClosedForWrite get() = channel.isClosedForSend
    override fun close() = onClose()

    override suspend fun write(buffer: ByteBuffer) {
        channel.send(buffer)
    }

    override suspend fun write(src: ByteArray, offset: Int, length: Int) {
        if (offset + length > src.size) {
            throw IndexOutOfBoundsException("range: ${offset..offset + length} while size: ${src.size}")
        }
        // ignore this comment; only uncomment if encountered a bug
        //        // we can't just wrap the array, the receiver might receive
        //        // the buffer after the return of this function.
        //        val buffer = allocate(limit)
        //        buffer.put(src, offset, limit)
        //        buffer.flip()

        val buffer = ByteBuffer.wrap(src, offset, length)
        write(buffer)
    }
}

internal class MongoUploadOutputStream(private val descriptor: MongoUpload) : OutputStream() {
    override fun write(b: Int) {
        val buffer = ByteArray(1)
        buffer[0] = b.toByte()
        runBlocking { descriptor.write(buffer) }
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        runBlocking { descriptor.write(b, off, len) }
    }

    override fun close() {
        descriptor.close()
    }
}
