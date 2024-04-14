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

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import org.cufy.bson.BsonDocument
import org.cufy.bson.BsonDocumentBlock
import org.cufy.bson.BsonElement
import org.cufy.bson.BsonObjectId
import org.cufy.mongodb.*
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
expect class MongoBucket

/* ============= ------------------ ============= */

/**
 * Create a new GridFS bucket with the default `fs` bucket name.
 *
 * @param database the database instance to use with GridFS.
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBuckets.create
 * @since 2.0.0
 */
expect fun createMongoBucket(database: MongoDatabase): MongoBucket

/**
 * Create a new GridFS bucket with a custom bucket name.
 *
 * @param database the database instance to use with GridFS.
 * @param name the custom bucket name to use.
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBuckets.create
 * @since 2.0.0
 */
expect fun createMongoBucket(database: MongoDatabase, name: String): MongoBucket

/* ============= ------------------ ============= */

/**
 * The bucket name.
 *
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.getBucketName
 * @since 2.0.0
 */
expect val MongoBucket.bucketName: String

/**
 * Sets the chunk size in bytes. Defaults to 255.
 *
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.getChunkSizeBytes
 * @since 2.0.0
 */
expect val MongoBucket.chunkSizeBytes: Int

/**
 * Get the write concern for the GridFSBucket.
 *
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.getWriteConcern
 * @since 2.0.0
 */
expect val MongoBucket.writeConcern: WriteConcern

/**
 * Get the read preference for the GridFSBucket.
 *
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.getReadPreference
 * @since 2.0.0
 */
expect val MongoBucket.readPreference: ReadPreference

/**
 * Get the read concern for the GridFSBucket.
 *
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.getReadConcern
 * @since 2.0.0
 */
expect val MongoBucket.readConcern: ReadConcern

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
 * @param metadata user provided data for the `metadata` field of the files collection document.
 * @param channel the channel providing the file data.
 * @param options the upload options.
 * @return the id of the uploaded file.
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.uploadFromPublisher
 * @since 2.0.0
 */
@ExperimentalMongodbApi
expect suspend fun MongoBucket.upload(
    channel: ReceiveChannel<ByteBuffer>,
    filename: String,
    metadata: BsonDocument = BsonDocument.Empty,
    options: UploadOptions = UploadOptions(),
    session: ClientSession? = null,
): BsonObjectId

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
 * @param metadata user provided data for the `metadata` field of the files collection document.
 * @param channel the channel providing the file data.
 * @param options  the upload options.
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.uploadFromPublisher
 * @since 2.0.0
 */
@ExperimentalMongodbApi
expect suspend fun MongoBucket.upload(
    channel: ReceiveChannel<ByteBuffer>,
    filename: String,
    id: BsonElement,
    metadata: BsonDocument = BsonDocument.Empty,
    options: UploadOptions = UploadOptions(),
    session: ClientSession? = null,
)

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
 * @param metadata user provided data for the `metadata` field of the files collection document.
 * @param channel the channel providing the file data.
 * @param options  the upload options.
 * @return the id of the uploaded file.
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.uploadFromPublisher
 * @since 2.0.0
 */
@ExperimentalMongodbApi
suspend fun MongoBucket.upload(
    channel: ReceiveChannel<ByteBuffer>,
    filename: String,
    metadata: BsonDocumentBlock,
    session: ClientSession? = null,
    options: UploadOptions.() -> Unit = {},
): BsonObjectId {
    return upload(
        channel = channel,
        filename = filename,
        metadata = BsonDocument(metadata),
        options = UploadOptions(options),
        session = session
    )
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
 * @param metadata user provided data for the `metadata` field of the files collection document.
 * @param channel the channel providing the file data.
 * @param options  the upload options.
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.uploadFromPublisher
 * @since 2.0.0
 */
@ExperimentalMongodbApi
suspend fun MongoBucket.upload(
    channel: ReceiveChannel<ByteBuffer>,
    filename: String,
    id: BsonElement,
    metadata: BsonDocumentBlock,
    session: ClientSession? = null,
    options: UploadOptions.() -> Unit = {},
) {
    upload(
        channel = channel,
        filename = filename,
        id = id,
        metadata = BsonDocument(metadata),
        options = UploadOptions(options),
        session = session
    )
}

//

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
@ExperimentalMongodbApi
expect suspend fun MongoBucket.download(
    channel: SendChannel<ByteBuffer>,
    id: BsonElement,
    options: DownloadOptions = DownloadOptions(),
    session: ClientSession? = null,
): MongoFile

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
@ExperimentalMongodbApi
expect suspend fun MongoBucket.download(
    channel: SendChannel<ByteBuffer>,
    filename: String,
    options: DownloadOptions = DownloadOptions(),
    revision: FileRevision = FileRevision.Latest,
    session: ClientSession? = null,
): MongoFile

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
@ExperimentalMongodbApi
suspend fun MongoBucket.download(
    channel: SendChannel<ByteBuffer>,
    id: BsonElement,
    session: ClientSession? = null,
    options: DownloadOptions.() -> Unit,
): MongoFile {
    return download(
        channel = channel,
        id = id,
        options = DownloadOptions(options),
        session = session
    )
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
@ExperimentalMongodbApi
suspend fun MongoBucket.download(
    channel: SendChannel<ByteBuffer>,
    filename: String,
    revision: FileRevision = FileRevision.Latest,
    session: ClientSession? = null,
    options: DownloadOptions.() -> Unit,
): MongoFile {
    return download(
        channel = channel,
        filename = filename,
        options = DownloadOptions(options),
        revision = revision,
        session = session
    )
}

//

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
expect suspend fun MongoBucket.find(
    filter: BsonDocument = BsonDocument.Empty,
    options: BucketFindOptions = BucketFindOptions(),
    session: ClientSession? = null,
): List<MongoFile>

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
    options: BucketFindOptions.() -> Unit = {},
): List<MongoFile> {
    return find(
        filter = BsonDocument(filter),
        options = BucketFindOptions(options),
        session = session
    )
}

//

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
expect suspend fun MongoBucket.delete(
    id: BsonElement,
    session: ClientSession? = null,
)

//

/**
 * Renames the stored file with the specified [id].
 *
 * @param session the client session with which to associate this operation.
 * @param id the id of the file in the files collection to rename
 * @param filename the new filename for the file
 * @since 2.0.0
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.rename
 */
expect suspend fun MongoBucket.rename(
    id: BsonElement,
    filename: String,
    session: ClientSession? = null,
)

//

/**
 * Drops the data associated with this bucket from the database.
 *
 * @param session the client session with which to associate this operation.
 * @since 2.0.0
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.drop
 */
expect suspend fun MongoBucket.drop(
    session: ClientSession? = null,
)

/* ============= ------------------ ============= */
