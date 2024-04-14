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

import com.mongodb.reactivestreams.client.gridfs.GridFSBuckets
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactive.collect
import org.cufy.bson.*
import org.cufy.mongodb.*
import org.cufy.mongodb.gridfs.internal.downloadToPublisher0
import org.cufy.mongodb.gridfs.internal.uploadFromPublisher0
import java.nio.ByteBuffer

/* ============= ------------------ ============= */

actual data class MongoBucket(val java: JavaMongoBucket) {
    override fun toString() = "MongoBucket(${java.bucketName})"
}

/* ============= ------------------ ============= */

/**
 * Create a new [MongoBucket] instance wrapping
 * this bucket instance.
 *
 * @since 2.0.0
 */
val JavaMongoBucket.kt: MongoBucket
    get() = MongoBucket(this)

/* ============= ------------------ ============= */

actual fun createMongoBucket(database: MongoDatabase): MongoBucket {
    return GridFSBuckets.create(database.java).kt
}

actual fun createMongoBucket(database: MongoDatabase, name: String): MongoBucket {
    return GridFSBuckets.create(database.java, name).kt
}

/* ============= ------------------ ============= */

actual val MongoBucket.bucketName: String
    get() = java.bucketName

actual val MongoBucket.chunkSizeBytes: Int
    get() = java.chunkSizeBytes

actual val MongoBucket.writeConcern: WriteConcern
    get() = java.writeConcern.kt

actual val MongoBucket.readPreference: ReadPreference
    get() = java.readPreference

actual val MongoBucket.readConcern: ReadConcern
    get() = java.readConcern.kt

/* ============= ------------------ ============= */

@ExperimentalMongodbApi
actual suspend fun MongoBucket.upload(
    channel: ReceiveChannel<ByteBuffer>,
    filename: String,
    metadata: BsonDocument,
    options: UploadOptions,
    session: ClientSession?,
): BsonObjectId {
    val publisher = java.uploadFromPublisher0(channel.receiveAsFlow(), filename, metadata, options, session)
    return publisher.awaitSingle().kt.bson
}

@ExperimentalMongodbApi
actual suspend fun MongoBucket.upload(
    channel: ReceiveChannel<ByteBuffer>,
    filename: String,
    id: BsonElement,
    metadata: BsonDocument,
    options: UploadOptions,
    session: ClientSession?,
) {
    val publisher = java.uploadFromPublisher0(channel.receiveAsFlow(), filename, id, metadata, options, session)

    publisher.awaitFirstOrNull()
}

@ExperimentalMongodbApi
actual suspend fun MongoBucket.download(
    channel: SendChannel<ByteBuffer>,
    id: BsonElement,
    options: DownloadOptions,
    session: ClientSession?,
): MongoFile {
    val publisher = java.downloadToPublisher0(id, options, session)
    try {
        publisher.collect { channel.send(it) }
    } catch (_: ClosedSendChannelException) {
        // it is completely ok to close mid-download
    }
    return publisher.gridFSFile.awaitSingle().kt
}

@ExperimentalMongodbApi
actual suspend fun MongoBucket.download(
    channel: SendChannel<ByteBuffer>,
    filename: String,
    options: DownloadOptions,
    revision: FileRevision,
    session: ClientSession?,
): MongoFile {
    val publisher = java.downloadToPublisher0(filename, options, revision, session)
    try {
        publisher.collect { channel.send(it) }
    } catch (_: ClosedSendChannelException) {
        // it is completely ok to close mid-download
    }
    return publisher.gridFSFile.awaitSingle().kt
}

/**
 * Finds all documents in the collection that match the filter.
 *
 * Below is an example of filtering against the filename and some nested metadata that can also be stored along with the file data:
 *
 * ```kotlin
 * find({
 *   "filename" by "mongodb.png"
 *   "metadata.contentType" by "image/png"
 * })
 * ```
 *
 * @param session the client session with which to associate this operation.
 * @param filter the query filter.
 * @return a list containing the found files.
 * @since 2.0.0
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.find
 */
actual suspend fun MongoBucket.find(
    filter: BsonDocument,
    options: BucketFindOptions,
    session: ClientSession?,
): List<MongoFile> {
    val publisher = when (session) {
        null -> java.find(filter.java)
        else -> java.find(session.java, filter.java)
    }
    return publisher
        .apply(options)
        .asFlow()
        .toList()
        .map { it.kt }
}

/**
 * Given a [id], delete this stored file's `files`
 * collection document and associated `chunks` from
 * a GridFS bucket.
 *
 * @param session the client session with which to associate this operation.
 * @param id the id of the file to be deleted
 * @since 2.0.0
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.delete
 */
actual suspend fun MongoBucket.delete(
    id: BsonElement,
    session: ClientSession?,
) {
    val publisher = when (session) {
        null -> java.delete(id.java)
        else -> java.delete(session.java, id.java)
    }
    publisher.awaitFirstOrNull()
}

/**
 * Renames the stored file with the specified [id].
 *
 * @param session the client session with which to associate this operation.
 * @param id the id of the file in the files collection to rename
 * @param filename the new filename for the file
 * @since 2.0.0
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.rename
 */
actual suspend fun MongoBucket.rename(
    id: BsonElement,
    filename: String,
    session: ClientSession?,
) {
    val publisher = when (session) {
        null -> java.rename(id.java, filename)
        else -> java.rename(session.java, id.java, filename)
    }
    publisher.awaitFirstOrNull()
}

/**
 * Drops the data associated with this bucket from the database.
 *
 * @param session the client session with which to associate this operation.
 * @since 2.0.0
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.drop
 */
actual suspend fun MongoBucket.drop(
    session: ClientSession?,
) {
    val publisher = when (session) {
        null -> java.drop()
        else -> java.drop(session.java)
    }
    publisher.awaitFirstOrNull()
}

/* ============= ------------------ ============= */
