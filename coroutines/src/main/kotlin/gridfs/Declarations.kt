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
import org.cufy.bson.BsonElement
import java.util.*

/* ============= ------------------ ============= */

/**
 * An object holding basic data about a file stored
 * in GridFS.
 *
 * @author LSafer
 * @since 2.0.0
 * @see com.mongodb.client.gridfs.model.GridFSFile
 */
data class MongoFile(
    /**
     * The id of this file.
     *
     * @see com.mongodb.client.gridfs.model.GridFSFile.id
     * @since 2.0.0
     */
    val id: BsonElement,
    /**
     * The filename.
     *
     * @see com.mongodb.client.gridfs.model.GridFSFile.filename
     * @since 2.0.0
     */
    val filename: String,
    /**
     * The length, in bytes of this file
     *
     * @see com.mongodb.client.gridfs.model.GridFSFile.length
     * @since 2.0.0
     */
    val length: Long,
    /**
     * The size, in bytes, of each data chunk of this file
     *
     * @see com.mongodb.client.gridfs.model.GridFSFile.chunkSize
     * @since 2.0.0
     */
    val chunkSize: Int,
    /**
     * The date and time this file was added to GridFS
     *
     * @see com.mongodb.client.gridfs.model.GridFSFile.uploadDate
     * @since 2.0.0
     */
    val uploadDate: Date,
    /**
     * Any additional metadata stored along with the file
     *
     * @see com.mongodb.client.gridfs.model.GridFSFile.metadata
     * @since 2.0.0
     */
    val metadata: BsonDocument?,
)

/* ============= ------------------ ============= */

/**
 *  The revision of a file to retrieve.
 *
 * - **0** = the original stored file
 * - **N** = the n-th revision
 * - **-N** = the most n-th recent revision
 *
 * @author LSafer
 * @since 2.0.0
 * @see com.mongodb.client.gridfs.model.GridFSDownloadOptions.revision
 */
@JvmInline
value class FileRevision(val value: Int) {
    companion object {
        /**
         * The original stored file.
         */
        val Original = FileRevision(0)

        /**
         * The most recent revision.
         */
        val Latest = FileRevision(-1)
    }
}

/* ============= ------------------ ============= */
