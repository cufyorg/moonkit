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

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactive.collect
import org.cufy.bson.BsonDocument
import org.cufy.bson.BsonDocumentBlock
import org.cufy.bson.BsonElement
import org.cufy.bson.java.kt
import org.cufy.mongodb.ClientSession
import org.cufy.mongodb.gridfs.internal.MongoDownloadImpl
import org.cufy.mongodb.gridfs.internal.MongoUploadImpl
import org.cufy.mongodb.gridfs.internal.downloadToPublisher0
import org.cufy.mongodb.gridfs.internal.uploadFromPublisher0
import org.cufy.mongodb.gridfs.java.kt
import java.nio.ByteBuffer

/*
These implementations are back-pressure wrappers
over the same functions in `MongoBucket.kt`
*/

/* ============= ------------------ ============= */

/**
 * Initiates a file upload process to a GridFS bucket and
 * returns a [MongoUpload] instance to complete the upload
 * process.
 *
 * **Note: don't forget to [close][MongoUpload.close] the
 * returned instance to complete the upload and close the
 * resources**
 *
 * @param session the client session with which to associate this operation.
 * @param filename the filename.
 * @param metadata user provided data for the `metadata` field of the files collection document.
 * @param options  the upload options.
 * @return an upload instance to complete the upload process.
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.uploadFromPublisher
 * @since 2.0.0
 */
suspend fun MongoBucket.asyncUpload(
    filename: String,
    metadata: BsonDocument = BsonDocument.Empty,
    options: UploadOptions = UploadOptions(),
    session: ClientSession? = null
): MongoUpload {
    val chunkSize = options.chunkSizeBytes ?: chunkSizeBytes
    val channel = Channel<ByteBuffer>()
    val idJob = CompletableDeferred<BsonElement>()
    val job = CoroutineScope(Dispatchers.IO).async<Unit> {
        val publisher = java.uploadFromPublisher0(channel.receiveAsFlow(), filename, metadata, options, session)
        idJob.complete(publisher.id.kt)
        publisher.awaitFirstOrNull()
    }

    job.invokeOnCompletion { error ->
        channel.close()
        error?.let {
            idJob.completeExceptionally(it)
        }
    }

    return MongoUploadImpl(job, idJob, channel, chunkSize) {
        // no need to close the job.
        // the job already depends on the channel
        channel.close()
    }
}

/**
 * Initiates a file upload process to a GridFS bucket and
 * returns a [MongoUpload] instance to complete the upload
 * process.
 *
 * **Note: don't forget to [close][MongoUpload.close] the
 * returned instance to complete the upload and close the
 * resources**
 *
 * @param session the client session with which to associate this operation.
 * @param filename the filename.
 * @param metadata user provided data for the `metadata` field of the files collection document.
 * @param options  the upload options.
 * @return an upload instance to complete the upload process.
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.uploadFromPublisher
 * @since 2.0.0
 */
suspend fun MongoBucket.asyncUpload(
    filename: String,
    metadata: BsonDocumentBlock,
    session: ClientSession? = null,
    options: UploadOptions.() -> Unit = {}
) = asyncUpload(filename, BsonDocument(metadata), UploadOptions(options), session)

//

/**
 * Initiates a file upload process to a GridFS bucket and
 * returns a [MongoUpload] instance to complete the upload
 * process.
 *
 * **Note: don't forget to [close][MongoUpload.close] the
 * returned instance to complete the upload and close the
 * resources**
 *
 * @param session the client session with which to associate this operation.
 * @param id       the custom id value of the file.
 * @param filename the filename.
 * @param metadata user provided data for the `metadata` field of the files collection document.
 * @param options  the upload options.
 * @return an upload instance to complete the upload process.
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.uploadFromPublisher
 * @since 2.0.0
 */
suspend fun MongoBucket.asyncUpload(
    filename: String,
    id: BsonElement,
    metadata: BsonDocument = BsonDocument.Empty,
    options: UploadOptions = UploadOptions(),
    session: ClientSession? = null
): MongoUpload {
    val chunkSize = options.chunkSizeBytes ?: chunkSizeBytes
    val channel = Channel<ByteBuffer>()
    val idJob = CompletableDeferred(id)
    val job = CoroutineScope(Dispatchers.IO).async<Unit> {
        val publisher = java.uploadFromPublisher0(channel.receiveAsFlow(), filename, id, metadata, options, session)
        publisher.awaitFirstOrNull()
    }

    job.invokeOnCompletion {
        channel.close()
        // no need to notify idJob
    }

    return MongoUploadImpl(job, idJob, channel, chunkSize) {
        // no need to close the job.
        // the job already depends on the channel
        channel.close()
    }
}

/**
 * Initiates a file upload process to a GridFS bucket and
 * returns a [MongoUpload] instance to complete the upload
 * process.
 *
 * **Note: don't forget to [close][MongoUpload.close] the
 * returned instance to complete the upload and close the
 * resources**
 *
 * @param session the client session with which to associate this operation.
 * @param id       the custom id value of the file.
 * @param filename the filename.
 * @param metadata user provided data for the `metadata` field of the files collection document.
 * @param options  the upload options.
 * @return an upload instance to complete the upload process.
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.uploadFromPublisher
 * @since 2.0.0
 */
suspend fun MongoBucket.asyncUpload(
    filename: String,
    id: BsonElement,
    metadata: BsonDocumentBlock,
    session: ClientSession? = null,
    options: UploadOptions.() -> Unit = {}
) = asyncUpload(filename, id, BsonDocument(metadata), UploadOptions(options), session)

/* ============= ------------------ ============= */

/**
 * Downloads the contents of the stored file specified by [id]
 * and return an instance of [MongoDownload] to complete the
 * download process.
 *
 * **Note: don't forget to [close][MongoDownload.close] the
 * returned instance to complete the download and close the
 * resources**
 *
 * @param session the client session with which to associate this operation.
 * @param id the id of the file to be downloaded.
 * @param options the download options.
 * @return a download instance to complete the download process.
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.downloadToPublisher
 * @since 2.0.0
 */
suspend fun MongoBucket.asyncDownload(
    id: BsonElement,
    options: DownloadOptions = DownloadOptions(),
    session: ClientSession? = null
): MongoDownload {
    val chunkSize = options.bufferSizeBytes ?: chunkSizeBytes
    val channel = Channel<ByteBuffer>()
    val fileJob = CompletableDeferred<MongoFile>()
    val job = CoroutineScope(Dispatchers.IO).async {
        val publisher = java.downloadToPublisher0(id, options, session)
        fileJob.complete(publisher.gridFSFile.awaitSingle().kt)
        try {
            publisher.collect { channel.send(it) }
        } catch (_: ClosedSendChannelException) {
            // it is completely ok to close mid-download
        }
    }

    job.invokeOnCompletion { error ->
        channel.close()
        error?.let {
            fileJob.completeExceptionally(it)
        }
    }

    return MongoDownloadImpl(job, fileJob, channel, chunkSize) {
        // no need to close the job.
        // the job already depends on the channel
        channel.cancel()
    }
}

/**
 * Downloads the contents of the stored file specified by [id]
 * and return an instance of [MongoDownload] to complete the
 * download process.
 *
 * **Note: don't forget to [close][MongoDownload.close] the
 * returned instance to complete the download and close the
 * resources**
 *
 * @param session the client session with which to associate this operation.
 * @param id the id of the file to be downloaded.
 * @param options the download options.
 * @return a download instance to complete the download process.
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.downloadToPublisher
 * @since 2.0.0
 */
suspend fun MongoBucket.asyncDownload(
    id: BsonElement,
    session: ClientSession? = null,
    options: DownloadOptions.() -> Unit
) = asyncDownload(id, DownloadOptions(options), session)

//

/**
 * Downloads the contents of the stored file specified by [filename] and [revision]
 * and return an instance of [MongoDownload] to complete the
 * download process.
 *
 * **Note: don't forget to [close][MongoDownload.close] the
 * returned instance to complete the download and close the
 * resources**
 *
 * @param session the client session with which to associate this operation.
 * @param filename the name of the file to be downloaded.
 * @param revision the revision of the file to be downloaded.
 * @param options the download options.
 * @return a download instance to complete the download process.
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.downloadToPublisher
 * @since 2.0.0
 */
suspend fun MongoBucket.asyncDownload(
    filename: String,
    options: DownloadOptions = DownloadOptions(),
    revision: FileRevision = FileRevision.Latest,
    session: ClientSession? = null
): MongoDownload {
    val chunkSize = options.bufferSizeBytes ?: chunkSizeBytes
    val channel = Channel<ByteBuffer>()
    val fileJob = CompletableDeferred<MongoFile>()
    val job = CoroutineScope(Dispatchers.IO).async {
        val publisher = java.downloadToPublisher0(filename, options, revision, session)
        fileJob.complete(publisher.gridFSFile.awaitSingle().kt)
        try {
            publisher.collect { channel.send(it) }
        } catch (_: ClosedSendChannelException) {
            // it is completely ok to close mid-download
        }
    }

    job.invokeOnCompletion { error ->
        channel.close()
        error?.let {
            fileJob.completeExceptionally(error)
        }
    }

    return MongoDownloadImpl(job, fileJob, channel, chunkSize) {
        // no need to close the job.
        // the job already depends on the channel
        channel.cancel()
    }
}

/**
 * Downloads the contents of the stored file specified by [filename] and [revision]
 * and return an instance of [MongoDownload] to complete the
 * download process.
 *
 * **Note: don't forget to [close][MongoDownload.close] the
 * returned instance to complete the download and close the
 * resources**
 *
 * @param session the client session with which to associate this operation.
 * @param filename the name of the file to be downloaded.
 * @param revision the revision of the file to be downloaded.
 * @param options the download options.
 * @return a download instance to complete the download process.
 * @see com.mongodb.reactivestreams.client.gridfs.GridFSBucket.downloadToPublisher
 * @since 2.0.0
 */
suspend fun MongoBucket.asyncDownload(
    filename: String,
    revision: FileRevision = FileRevision.Latest,
    session: ClientSession? = null,
    options: DownloadOptions.() -> Unit
) = asyncDownload(filename, DownloadOptions(options), revision, session)

/* ============= ------------------ ============= */
