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

import com.mongodb.client.gridfs.model.GridFSDownloadOptions
import com.mongodb.reactivestreams.client.gridfs.GridFSBuckets
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.*
import org.cufy.bson.*
import org.cufy.bson.java.java
import org.cufy.mongodb.*
import org.cufy.mongodb.gridfs.java.JavaMongoBucket
import org.cufy.mongodb.gridfs.java.apply
import org.cufy.mongodb.gridfs.java.java
import org.cufy.mongodb.gridfs.java.kt
import org.cufy.mongodb.java.kt
import java.nio.ByteBuffer

/* ============= ------------------ ============= */

/**
 * A generic-free coroutine dependant wrapper for
 * a mongodb bucket.
 *
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket
 * @author LSafer
 * @since 2.0.0
 */
interface MongoBucket {
    /**
     * The wrapped bucket.
     */
    val java: JavaMongoBucket
}

/**
 * Create a new GridFS bucket with the default `fs` bucket name.
 *
 * @param database the database instance to use with GridFS.
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBuckets.create
 * @since 2.0.0
 */
fun createMongoBucket(database: MongoDatabase): MongoBucket {
    return GridFSBuckets.create(database.java).kt
}

/**
 * Create a new GridFS bucket with a custom bucket name.
 *
 * @param database the database instance to use with GridFS.
 * @param name the custom bucket name to use.
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBuckets.create
 * @since 2.0.0
 */
fun createMongoBucket(database: MongoDatabase, name: String): MongoBucket {
    return GridFSBuckets.create(database.java, name).kt
}

/* ============= ------------------ ============= */

/**
 * The bucket name.
 *
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.getBucketName
 * @since 2.0.0
 */
val MongoBucket.bucketName: String
    get() = java.bucketName

/**
 * Sets the chunk size in bytes. Defaults to 255.
 *
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.getChunkSizeBytes
 * @since 2.0.0
 */
val MongoBucket.chunkSizeBytes: Int
    get() = java.chunkSizeBytes

/**
 * Get the write concern for the GridFSBucket.
 *
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.getWriteConcern
 * @since 2.0.0
 */
val MongoBucket.writeConcern: WriteConcern
    get() = java.writeConcern.kt

/**
 * Get the read preference for the GridFSBucket.
 *
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.getReadPreference
 * @since 2.0.0
 */
val MongoBucket.readPreference: ReadPreference
    get() = java.readPreference

/**
 * Get the read concern for the GridFSBucket.
 *
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.getReadConcern
 * @since 2.0.0
 */
val MongoBucket.readConcern: ReadConcern
    get() = java.readConcern.kt

/* ============= ------------------ ============= */

/**
 * Uploads the chunks sent to the given [channel] to a GridFS bucket.
 *
 * Receives the chunks from [channel] and uploads it
 * as chunks in the `chunks` collection.
 * After all the chunks have been uploaded, it creates a files
 * collection document for [filename] in the `files` collection.
 *
 * **Note: This function will suspend until the file is done uploading.**
 *
 * Closing [channel] will only stop the receiving the chunks from it
 * and won't cause the invocation of this function to fail.
 *
 * @param session the client session with which to associate this operation.
 * @param filename the filename.
 * @param channel the channel providing the file data.
 * @param options the upload options.
 * @return the id of the uploaded file.
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.uploadFromPublisher
 * @since 2.0.0
 */
suspend fun MongoBucket.upload(
    channel: ReceiveChannel<ByteBuffer>,
    filename: String,
    options: UploadOptions = UploadOptions(),
    session: ClientSession? = null
): BsonObjectId {
    val outPublisher = channel.receiveAsFlow()
    val publisher = when (session) {
        null -> java.uploadFromPublisher(filename, outPublisher.asPublisher(), options.java)
        else -> java.uploadFromPublisher(session.java, filename, outPublisher.asPublisher(), options.java)
    }

    return publisher.awaitSingle().bson
}

/**
 * Uploads the chunks sent to the given [channel] to a GridFS bucket.
 *
 * Receives the chunks from [channel] and uploads it
 * as chunks in the `chunks` collection.
 * After all the chunks have been uploaded, it creates a files
 * collection document for [filename] in the `files` collection.
 *
 * **Note: This function will suspend until the file is done uploading.**
 *
 * Closing [channel] will only stop the receiving the chunks from it
 * and won't cause the invocation of this function to fail.
 *
 * @param session the client session with which to associate this operation.
 * @param filename the filename.
 * @param channel the channel providing the file data.
 * @param options  the upload options.
 * @return the id of the uploaded file.
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.uploadFromPublisher
 * @since 2.0.0
 */
suspend fun MongoBucket.upload(
    channel: ReceiveChannel<ByteBuffer>,
    filename: String,
    session: ClientSession? = null,
    options: UploadOptions.() -> Unit
) = upload(channel, filename, UploadOptions(options), session)

//

/**
 * Uploads the chunks sent to the given [channel] to a GridFS bucket.
 *
 * Receives the chunks from [channel] and uploads it
 * as chunks in the `chunks` collection.
 * After all the chunks have been uploaded, it creates a files
 * collection document for [filename] in the `files` collection.
 *
 * **Note: This function will suspend until the file is done uploading.**
 *
 * Closing [channel] will only stop the receiving the chunks from it
 * and won't cause the invocation of this function to fail.
 *
 * @param session the client session with which to associate this operation.
 * @param id       the custom id value of the file.
 * @param filename the filename.
 * @param channel the channel providing the file data.
 * @param options  the upload options.
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.uploadFromPublisher
 * @since 2.0.0
 */
suspend fun MongoBucket.upload(
    channel: ReceiveChannel<ByteBuffer>,
    filename: String,
    id: BsonElement,
    options: UploadOptions = UploadOptions(),
    session: ClientSession? = null
) {
    val flow = channel.receiveAsFlow()

    val publisher = when (session) {
        null -> java.uploadFromPublisher(id.java, filename, flow.asPublisher(), options.java)
        else -> java.uploadFromPublisher(session.java, id.java, filename, flow.asPublisher(), options.java)
    }

    publisher.awaitFirstOrNull()
}

/**
 * Uploads the chunks sent to the given [channel] to a GridFS bucket.
 *
 * Receives the chunks from [channel] and uploads it
 * as chunks in the `chunks` collection.
 * After all the chunks have been uploaded, it creates a files
 * collection document for [filename] in the `files` collection.
 *
 * **Note: This function will suspend until the file is done uploading.**
 *
 * Closing [channel] will only stop the receiving the chunks from it
 * and won't cause the invocation of this function to fail.
 *
 * @param session the client session with which to associate this operation.
 * @param id       the custom id value of the file.
 * @param filename the filename.
 * @param channel the channel providing the file data.
 * @param options  the upload options.
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.uploadFromPublisher
 * @since 2.0.0
 */
suspend fun MongoBucket.upload(
    channel: ReceiveChannel<ByteBuffer>,
    filename: String,
    id: BsonElement,
    session: ClientSession? = null,
    options: UploadOptions.() -> Unit
) = upload(channel, filename, id, UploadOptions(options), session)

/* ============= ------------------ ============= */

/**
 * Downloads the contents of the stored file specified by [id]
 * to the given [channel].
 *
 * **Note: this function will suspend until the file is done downloading.**
 *
 * Closing [channel] will only stop the sending of the chunks through it
 * and won't cause the invocation of this function to fail.
 *
 * @param session the client session with which to associate this operation.
 * @param id the id of the file to be downloaded.
 * @param channel the channel receiving the file data.
 * @param options the download options.
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.downloadToPublisher
 * @since 2.0.0
 */
suspend fun MongoBucket.download(
    channel: SendChannel<ByteBuffer>,
    id: BsonElement,
    options: DownloadOptions = DownloadOptions(),
    session: ClientSession? = null
): MongoFile {
    val publisher = when (session) {
        null -> java.downloadToPublisher(id.java)
        else -> java.downloadToPublisher(session.java, id.java)
    }
    publisher.apply(options)
    try {
        publisher.collect { channel.send(it) }
    } catch (_: ClosedSendChannelException) {
        // it is completely ok to close mid-download
    }
    return publisher.gridFSFile.awaitSingle().kt
}

/**
 * Downloads the contents of the stored file specified by [id]
 * to the given [channel].
 *
 * **Note: this function will suspend until the file is done downloading.**
 *
 * Closing [channel] will only stop the sending of the chunks through it
 * and won't cause the invocation of this function to fail.
 *
 * @param session the client session with which to associate this operation.
 * @param id the id of the file to be downloaded.
 * @param channel the channel receiving the file data.
 * @param options the download options.
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.downloadToPublisher
 * @since 2.0.0
 */
suspend fun MongoBucket.download(
    channel: SendChannel<ByteBuffer>,
    id: BsonElement,
    session: ClientSession? = null,
    options: DownloadOptions.() -> Unit
) = download(channel, id, DownloadOptions(options), session)

//

/**
 * Downloads the contents of the stored file specified by [filename] and [revision]
 * to the given [channel].
 *
 * **Note: this function will suspend until the file is done downloading.**
 *
 * Closing [channel] will only stop the sending of the chunks through it
 * and won't cause the invocation of this function to fail.
 *
 * @param session the client session with which to associate this operation.
 * @param filename the name of the file to be downloaded.
 * @param revision the revision of the file to be downloaded.
 * @param channel the channel receiving the file data.
 * @param options the download options.
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.downloadToPublisher
 * @since 2.0.0
 */
suspend fun MongoBucket.download(
    channel: SendChannel<ByteBuffer>,
    filename: String,
    options: DownloadOptions = DownloadOptions(),
    revision: FileRevision = FileRevision.Latest,
    session: ClientSession? = null
): MongoFile {
    val opts = GridFSDownloadOptions().revision(revision.value)
    val publisher = when (session) {
        null -> java.downloadToPublisher(filename, opts)
        else -> java.downloadToPublisher(session.java, filename, opts)
    }
    publisher.apply(options)
    try {
        publisher.collect { channel.send(it) }
    } catch (_: ClosedSendChannelException) {
        // it is completely ok to close mid-download
    }
    return publisher.gridFSFile.awaitSingle().kt
}

/**
 * Downloads the contents of the stored file specified by [filename] and [revision]
 * to the given [channel].
 *
 * **Note: this function will suspend until the file is done downloading.**
 *
 * Closing [channel] will only stop the sending of the chunks through it
 * and won't cause the invocation of this function to fail.
 *
 * @param session the client session with which to associate this operation.
 * @param filename the name of the file to be downloaded.
 * @param revision the revision of the file to be downloaded.
 * @param channel the channel receiving the file data.
 * @param options the download options.
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.downloadToPublisher
 * @since 2.0.0
 */
suspend fun MongoBucket.download(
    channel: SendChannel<ByteBuffer>,
    filename: String,
    revision: FileRevision = FileRevision.Latest,
    session: ClientSession? = null,
    options: DownloadOptions.() -> Unit
) = download(channel, filename, DownloadOptions(options), revision, session)

/* ============= ------------------ ============= */

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
suspend fun MongoBucket.find(
    filter: BsonDocument = BsonDocument.Empty,
    options: BucketFindOptions = BucketFindOptions(),
    session: ClientSession? = null
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
suspend fun MongoBucket.find(
    filter: BsonDocumentBlock,
    session: ClientSession? = null,
    options: BucketFindOptions.() -> Unit = {}
) = find(BsonDocument(filter), BucketFindOptions(options), session)

/* ============= ------------------ ============= */

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
suspend fun MongoBucket.delete(
    id: BsonElement,
    session: ClientSession? = null
) {
    val publisher = when (session) {
        null -> java.delete(id.java)
        else -> java.delete(session.java, id.java)
    }
    publisher.awaitFirstOrNull()
}

/* ============= ------------------ ============= */

/**
 * Renames the stored file with the specified [id].
 *
 * @param session the client session with which to associate this operation.
 * @param id the id of the file in the files collection to rename
 * @param filename the new filename for the file
 * @since 2.0.0
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.rename
 */
suspend fun MongoBucket.rename(
    id: BsonElement,
    filename: String,
    session: ClientSession? = null
) {
    val publisher = when (session) {
        null -> java.rename(id.java, filename)
        else -> java.rename(session.java, id.java, filename)
    }
    publisher.awaitFirstOrNull()
}

/* ============= ------------------ ============= */

/**
 * Drops the data associated with this bucket from the database.
 *
 * @param session the client session with which to associate this operation.
 * @since 2.0.0
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.drop
 */
suspend fun MongoBucket.drop(
    session: ClientSession? = null
) {
    val publisher = when (session) {
        null -> java.drop()
        else -> java.drop(session.java)
    }
    publisher.awaitFirstOrNull()
}

/* ============= ------------------ ============= */
