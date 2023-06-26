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

import org.cufy.bson.BsonDocument

/* ============= ------------------ ============= */

/**
 * Create a new options instance from the given [block].
 */
fun UploadOptions(
    block: UploadOptions.() -> Unit
) = UploadOptions().apply(block)

/**
 * The options that can be applied to upload operations.
 *
 * @see com.mongodb.client.gridfs.model.GridFSUploadOptions
 * @author LSafer
 * @since 2.0.0
 */
data class UploadOptions(
    /**
     * The number of bytes per chunk of this file.
     *
     * Note: If no value has been set then, the [MongoBucket.chunkSizeBytes] will be used.
     *
     * @see com.mongodb.client.gridfs.model.GridFSUploadOptions.chunkSizeBytes
     * @since 2.0.0
     */
    var chunkSizeBytes: Int? = null,
    /**
     * Returns any user provided data for the `metadata` field of the files collection document.
     *
     * @see com.mongodb.client.gridfs.model.GridFSUploadOptions.metadata
     * @since 2.0.0
     */
    var metadata: BsonDocument? = null
)

/* ============= ------------------ ============= */

/**
 * Create a new options instance from the given [block].
 */
fun DownloadOptions(
    block: DownloadOptions.() -> Unit
) = DownloadOptions().apply(block)

/**
 * The options that can be applied to download operations.
 *
 * @see com.mongodb.client.gridfs.model.GridFSUploadOptions
 * @author LSafer
 * @since 2.0.0
 */
data class DownloadOptions(
    /**
     * The preferred number of bytes per [java.nio.ByteBuffer].
     *
     * Allows for larger than chunk sizes.
     * The actual chunk size of the data stored in MongoDB is the smallest allowable
     * [java.nio.ByteBuffer] size.
     *
     * Can be used to control the memory consumption.
     * The smaller the [bufferSizeBytes] the lower the memory
     * consumption and higher latency.
     *
     * @see com.mongodb.reactivestreams.client.gridfs.GridFSDownloadPublisher.bufferSizeBytes
     * @return this
     */
    var bufferSizeBytes: Int? = null,
)

/* ============= ------------------ ============= */
