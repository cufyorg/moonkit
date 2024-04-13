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
import org.cufy.bson.BsonElement

/* ============= ------------------ ============= */

/**
 * A GridFS upload instance.
 *
 * Can be used to asynchronously complete an upload
 * operation.
 *
 * This interface is a combination of a coroutine-based
 * [java.io.OutputStream] for writing the upload file chunks and
 * a [Deferred] instance for the upload result.
 *
 * The [write] functions fails when either the upload
 * operation has been closed, canceled or failed.
 *
 * This interface implements [kotlinx.coroutines.Job]
 * which represents the background upload job.
 *
 * @author LSafer
 * @since 2.0.0
 */
expect interface MongoUpload : Deferred<Unit>, AutoCloseable {
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
     * The id of the uploaded file.
     *
     * @since 2.0.0
     */
    val id: Deferred<BsonElement>

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
    suspend fun closeAndAwait(): BsonElement

    /**
     * Write from the given [src] and **suspend**
     * until the data is written.
     *
     * @param src the source array to write from.
     * @since 2.0.0
     */
    suspend fun write(src: ByteArray)

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

/* ============= ------------------ ============= */
