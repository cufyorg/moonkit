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

import com.mongodb.client.gridfs.model.GridFSDownloadOptions
import com.mongodb.reactivestreams.client.gridfs.GridFSDownloadPublisher
import com.mongodb.reactivestreams.client.gridfs.GridFSUploadPublisher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asPublisher
import org.bson.Document
import org.cufy.bson.BsonDocument
import org.cufy.bson.BsonElement
import org.cufy.bson.java
import org.cufy.mongodb.ClientSession
import org.cufy.mongodb.gridfs.*
import java.nio.ByteBuffer

internal fun JavaMongoBucket.uploadFromPublisher0(
    source: Flow<ByteBuffer>,
    filename: String,
    metadata: BsonDocument,
    options: UploadOptions,
    session: ClientSession?,
): GridFSUploadPublisher<org.bson.types.ObjectId> {
    val opts = options.java.metadata(Document(metadata.java))
    val publisher = when (session) {
        null -> uploadFromPublisher(filename, source.asPublisher(), opts)
        else -> uploadFromPublisher(session.java, filename, source.asPublisher(), opts)
    }
    return publisher
}

internal fun JavaMongoBucket.uploadFromPublisher0(
    source: Flow<ByteBuffer>,
    filename: String,
    id: BsonElement,
    metadata: BsonDocument,
    options: UploadOptions,
    session: ClientSession?,
): GridFSUploadPublisher<Void> {
    val opts = options.java.metadata(Document(metadata.java))
    val publisher = when (session) {
        null -> uploadFromPublisher(id.java, filename, source.asPublisher(), opts)
        else -> uploadFromPublisher(session.java, id.java, filename, source.asPublisher(), opts)
    }
    return publisher
}

internal fun JavaMongoBucket.downloadToPublisher0(
    id: BsonElement,
    options: DownloadOptions,
    session: ClientSession?,
): GridFSDownloadPublisher {
    val publisher = when (session) {
        null -> downloadToPublisher(id.java)
        else -> downloadToPublisher(session.java, id.java)
    }
    publisher.apply(options)
    return publisher
}

internal fun JavaMongoBucket.downloadToPublisher0(
    filename: String,
    options: DownloadOptions,
    revision: FileRevision,
    session: ClientSession?,
): GridFSDownloadPublisher {
    val opts = GridFSDownloadOptions().revision(revision.value)
    val publisher = when (session) {
        null -> downloadToPublisher(filename, opts)
        else -> downloadToPublisher(session.java, filename, opts)
    }
    publisher.apply(options)
    return publisher
}
