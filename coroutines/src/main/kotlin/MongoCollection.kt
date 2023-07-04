/*
 *	Copyright 2022-2023 cufy.org
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
package org.cufy.mongodb

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.cufy.bson.BsonArray
import org.cufy.bson.BsonDocument
import org.cufy.bson.BsonDocumentBlock
import org.cufy.bson.BsonElement
import org.cufy.bson.java.java
import org.cufy.bson.java.kt
import org.cufy.mongodb.java.JavaMongoCollection
import org.cufy.mongodb.java.apply
import org.cufy.mongodb.java.java
import org.cufy.mongodb.java.kt

/* ============= ------------------ ============= */

/**
 * A generic-free coroutine dependant wrapper for
 * a mongodb collection.
 *
 * @see com.mongodb.reactivestreams.client.MongoCollection
 * @author LSafer
 * @since 2.0.0
 */
interface MongoCollection {
    /**
     * The wrapped collection.
     */
    val java: JavaMongoCollection
}

/* ============= ------------------ ============= */

/**
 * Gets the namespace of this collection.
 *
 * @see com.mongodb.client.MongoCollection.getNamespace
 * @since 2.0.0
 */
val MongoCollection.namespace: MongoNamespace
    get() = java.namespace.kt

/**
 * Get the read preference for the MongoCollection.
 *
 * @see com.mongodb.client.MongoCollection.getReadPreference
 * @since 2.0.0
 */
val MongoCollection.readPreference: ReadPreference
    get() = java.readPreference

/**
 * Get the write concern for the MongoCollection.
 *
 * @see com.mongodb.client.MongoCollection.getWriteConcern
 * @since 2.0.0
 */
val MongoCollection.writeConcern: WriteConcern
    get() = java.writeConcern.kt

/**
 * Get the read concern for the MongoCollection.
 *
 * @see com.mongodb.client.MongoCollection.getReadConcern
 * @since 2.0.0
 */
val MongoCollection.readConcern: ReadConcern
    get() = java.readConcern.kt

/* ============= ------------------ ============= */

/**
 * Removes at most one document from the
 * collection that matches the given filter.
 *
 * If no documents match, the collection is not
 * modified.
 *
 * @param session the client session with which to associate this operation.
 * @param filter the query filter to apply the delete operation.
 * @param options the options to apply to the delete operation.
 * @return the DeleteResult.
 * @throws com.mongodb.MongoException
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.deleteOne
 */
suspend fun MongoCollection.deleteOne(
    filter: BsonDocument,
    options: DeleteOptions = DeleteOptions(),
    session: ClientSession? = null
): DeleteResult {
    val publisher = when (session) {
        null -> java.deleteOne(filter.java, options.java)
        else -> java.deleteOne(session.java, filter.java, options.java)
    }
    return publisher.awaitSingle().kt
}

/**
 * Removes at most one document from the
 * collection that matches the given filter.
 *
 * If no documents match, the collection is not
 * modified.
 *
 * @param session the client session with which to associate this operation.
 * @param filter the query filter to apply the delete operation.
 * @param options the options to apply to the delete operation.
 * @return the DeleteResult.
 * @throws com.mongodb.MongoException
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.deleteOne
 */
suspend fun MongoCollection.deleteOne(
    filter: BsonDocumentBlock,
    session: ClientSession? = null,
    options: DeleteOptions.() -> Unit = {}
) = deleteOne(BsonDocument(filter), DeleteOptions(options), session)

//

/**
 * Removes all documents from the collection that
 * match the given query filter.
 *
 * If no documents match, the collection is not
 * modified.
 *
 * @param session the client session with which to associate this operation.
 * @param filter the query filter to apply the delete operation.
 * @param options the options to apply to the delete operation.
 * @return the DeleteResult.
 * @throws com.mongodb.MongoException
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.deleteMany
 */
suspend fun MongoCollection.deleteMany(
    filter: BsonDocument,
    options: DeleteOptions = DeleteOptions(),
    session: ClientSession? = null
): DeleteResult {
    val publisher = when (session) {
        null -> java.deleteMany(filter.java, options.java)
        else -> java.deleteMany(session.java, filter.java, options.java)
    }
    return publisher.awaitSingle().kt
}

/**
 * Removes all documents from the collection that
 * match the given query filter.
 *
 * If no documents match, the collection is not
 * modified.
 *
 * @param session the client session with which to associate this operation.
 * @param filter the query filter to apply the delete operation.
 * @param options the options to apply to the delete operation.
 * @return the DeleteResult.
 * @throws com.mongodb.MongoException
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.deleteMany
 */
suspend fun MongoCollection.deleteMany(
    filter: BsonDocumentBlock,
    session: ClientSession? = null,
    options: DeleteOptions.() -> Unit = {}
) = deleteMany(BsonDocument(filter), DeleteOptions(options), session)

//

/**
 * Inserts the provided document. If the document
 * is missing an identifier, the driver should
 * generate one.
 *
 * @param session the client session with which to associate this operation.
 * @param document the document to insert.
 * @param options the options to apply to the operation.
 * @return the InsertOneResult
 * @throws com.mongodb.DuplicateKeyException
 * @throws com.mongodb.MongoException
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.insertOne
 */
suspend fun MongoCollection.insertOne(
    document: BsonDocument,
    options: InsertOneOptions = InsertOneOptions(),
    session: ClientSession? = null
): InsertOneResult {
    val publisher = when (session) {
        null -> java.insertOne(document.java, options.java)
        else -> java.insertOne(session.java, document.java, options.java)
    }
    return publisher.awaitSingle().kt
}

/**
 * Inserts the provided document. If the document
 * is missing an identifier, the driver should
 * generate one.
 *
 * @param session the client session with which to associate this operation.
 * @param document the document to insert.
 * @param options the options to apply to the operation.
 * @return the InsertOneResult
 * @throws com.mongodb.DuplicateKeyException
 * @throws com.mongodb.MongoException
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.insertOne
 */
suspend fun MongoCollection.insertOne(
    document: BsonDocumentBlock,
    session: ClientSession? = null,
    options: InsertOneOptions.() -> Unit = {}
) = insertOne(BsonDocument(document), InsertOneOptions(options), session)

//

/**
 * Inserts a batch of documents.
 *
 * @param session the client session with which to associate this operation.
 * @param documents the documents to insert.
 * @param options the options to apply to the operation.
 * @return the InsertManyResult
 * @throws com.mongodb.DuplicateKeyException
 * @throws com.mongodb.MongoException
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.insertMany
 */
suspend fun MongoCollection.insertMany(
    documents: List<BsonDocument>,
    options: InsertManyOptions = InsertManyOptions(),
    session: ClientSession? = null
): InsertManyResult {
    val publisher = when (session) {
        null -> java.insertMany(documents.map { it.java }, options.java)
        else -> java.insertMany(session.java, documents.map { it.java }, options.java)
    }
    return publisher.awaitSingle().kt
}

/**
 * Inserts a batch of documents.
 *
 * @param session the client session with which to associate this operation.
 * @param documents the documents to insert.
 * @param options the options to apply to the operation.
 * @return the InsertManyResult
 * @throws com.mongodb.DuplicateKeyException
 * @throws com.mongodb.MongoException
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.insertMany
 */
suspend fun MongoCollection.insertMany(
    vararg documents: BsonDocumentBlock,
    session: ClientSession? = null,
    options: InsertManyOptions.() -> Unit = {}
) = insertMany(documents.map { BsonDocument(it) }, InsertManyOptions(options), session)

//

/**
 * Update a single document in the collection
 * according to the specified arguments.
 *
 * @param session the client session with which to associate this operation.
 * @param filter a document describing the query filter, which may not be null.
 * @param update a document describing the update, which may not be null.
 *               The update to apply must include only update operators.
 * @param options the options to apply to the update operation.
 * @return the UpdateResult
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.updateOne
 */
suspend fun MongoCollection.updateOne(
    filter: BsonDocument,
    update: BsonDocument,
    options: UpdateOptions = UpdateOptions(),
    session: ClientSession? = null
): UpdateResult {
    val publisher = when (session) {
        null -> java.updateOne(filter.java, update.java, options.java)
        else -> java.updateOne(session.java, filter.java, update.java, options.java)
    }
    return publisher.awaitSingle().kt
}

/**
 * Update a single document in the collection
 * according to the specified arguments.
 *
 * @param session the client session with which to associate this operation.
 * @param filter a document describing the query filter, which may not be null.
 * @param update a document describing the update, which may not be null.
 *               The update to apply must include only update operators.
 * @param options the options to apply to the update operation.
 * @return the UpdateResult
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.updateOne
 */
suspend fun MongoCollection.updateOne(
    filter: BsonDocumentBlock,
    update: BsonDocumentBlock,
    session: ClientSession? = null,
    options: UpdateOptions.() -> Unit = {}
) = updateOne(BsonDocument(filter), BsonDocument(update), UpdateOptions(options), session)

//

/**
 * Update a single document in the collection
 * according to the specified arguments.
 *
 * Note: Supports retryable writes on MongoDB
 * server versions 3.6 or higher when the
 * retryWrites setting is enabled.
 *
 * @param session the client session with which to associate this operation.
 * @param filter a document describing the query filter, which may not be null.
 * @param update a pipeline describing the update, which may not be null.
 * @param options the options to apply to the update operation
 * @return the UpdateResult
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.updateOne
 */
suspend fun MongoCollection.updateOne(
    filter: BsonDocument,
    update: List<BsonDocument>,
    options: UpdateOptions = UpdateOptions(),
    session: ClientSession? = null
): UpdateResult {
    val publisher = when (session) {
        null -> java.updateOne(filter.java, update.map { it.java }, options.java)
        else -> java.updateOne(session.java, filter.java, update.map { it.java }, options.java)
    }
    return publisher.awaitSingle().kt
}

/**
 * Update a single document in the collection
 * according to the specified arguments.
 *
 * Note: Supports retryable writes on MongoDB
 * server versions 3.6 or higher when the
 * retryWrites setting is enabled.
 *
 * @param session the client session with which to associate this operation.
 * @param filter a document describing the query filter, which may not be null.
 * @param update a pipeline describing the update, which may not be null.
 * @param options the options to apply to the update operation
 * @return the UpdateResult
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.updateOne
 */
suspend fun MongoCollection.updateOne(
    filter: BsonDocumentBlock,
    vararg update: BsonDocumentBlock,
    session: ClientSession? = null,
    options: UpdateOptions.() -> Unit = {}
) = updateOne(BsonDocument(filter), update.map { BsonDocument(it) }, UpdateOptions(options), session)

//

/**
 * Update a single document in the collection
 * according to the specified arguments.
 *
 * @param session the client session with which to associate this operation.
 * @param filter a document describing the query filter, which may not be null.
 * @param update a document describing the update, which may not be null.
 *               The update to apply must include only update operators.
 * @param options the options to apply to the update operation.
 * @return the UpdateResult
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.updateOne
 */
suspend fun MongoCollection.updateOne(
    filter: BsonDocument,
    update: BsonElement,
    options: UpdateOptions = UpdateOptions(),
    session: ClientSession? = null
): UpdateResult {
    return when (update) {
        is BsonDocument ->
            updateOne(filter, update, options, session)
        is BsonArray ->
            updateOne(filter, update.map { it as BsonDocument }, options, session)
        else ->
            error("update is expected to be either a document or an array of documents.")
    }
}

//

/**
 * Update all documents in the collection
 * according to the specified arguments.
 *
 * @param session the client session with which to associate this operation.
 * @param filter a document describing the query filter, which may not be null.
 * @param update a document describing the update, which may not be null.
 *               The update to apply must include only update operators.
 * @param options the options to apply to the update operation
 * @return the UpdateResult
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.updateMany
 */
suspend fun MongoCollection.updateMany(
    filter: BsonDocument,
    update: BsonDocument,
    options: UpdateOptions = UpdateOptions(),
    session: ClientSession? = null
): UpdateResult {
    val publisher = when (session) {
        null -> java.updateMany(filter.java, update.java, options.java)
        else -> java.updateMany(session.java, filter.java, update.java, options.java)
    }
    return publisher.awaitSingle().kt
}

/**
 * Update all documents in the collection
 * according to the specified arguments.
 *
 * @param session the client session with which to associate this operation.
 * @param filter a document describing the query filter, which may not be null.
 * @param update a document describing the update, which may not be null.
 *               The update to apply must include only update operators.
 * @param options the options to apply to the update operation
 * @return the UpdateResult
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.updateMany
 */
suspend fun MongoCollection.updateMany(
    filter: BsonDocumentBlock,
    update: BsonDocumentBlock,
    session: ClientSession? = null,
    options: UpdateOptions.() -> Unit = {}
) = updateMany(BsonDocument(filter), BsonDocument(update), UpdateOptions(options), session)

//

/**
 * Update all documents in the collection
 * according to the specified arguments.
 *
 * @param session the client session with which to associate this operation.
 * @param filter a document describing the query filter, which may not be null.
 * @param update a pipeline describing the update, which may not be null.
 * @param options the options to apply to the update operation
 * @return the UpdateResult
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.updateMany
 */
suspend fun MongoCollection.updateMany(
    filter: BsonDocument,
    update: List<BsonDocument>,
    options: UpdateOptions = UpdateOptions(),
    session: ClientSession? = null
): UpdateResult {
    val publisher = when (session) {
        null -> java.updateMany(filter.java, update.map { it.java }, options.java)
        else -> java.updateMany(session.java, filter.java, update.map { it.java }, options.java)
    }
    return publisher.awaitSingle().kt
}

/**
 * Update all documents in the collection
 * according to the specified arguments.
 *
 * @param session the client session with which to associate this operation.
 * @param filter a document describing the query filter, which may not be null.
 * @param update a pipeline describing the update, which may not be null.
 * @param options the options to apply to the update operation
 * @return the UpdateResult
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.updateMany
 */
suspend fun MongoCollection.updateMany(
    filter: BsonDocumentBlock,
    vararg update: BsonDocumentBlock,
    session: ClientSession? = null,
    options: UpdateOptions.() -> Unit = {}
) = updateMany(BsonDocument(filter), update.map { BsonDocument(it) }, UpdateOptions(options), session)

//

/**
 * Update all documents in the collection
 * according to the specified arguments.
 *
 * @param session the client session with which to associate this operation.
 * @param filter a document describing the query filter, which may not be null.
 * @param update a document describing the update, which may not be null.
 *               The update to apply must include only update operators.
 * @param options the options to apply to the update operation
 * @return the UpdateResult
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.updateMany
 */
suspend fun MongoCollection.updateMany(
    filter: BsonDocument,
    update: BsonElement,
    options: UpdateOptions = UpdateOptions(),
    session: ClientSession? = null
): UpdateResult {
    return when (update) {
        is BsonDocument ->
            updateMany(filter, update, options, session)
        is BsonArray ->
            updateMany(filter, update.map { it as BsonDocument }, options, session)
        else ->
            error("update is expected to be either a document or an array of documents.")
    }
}

//

/**
 * Replace a document in the collection according
 * to the specified arguments.
 *
 * @param session the client session with which to associate this operation
 * @param filter the query filter to apply the replace operation
 * @param replacement the replacement document
 * @param options the options to apply to the replace operation
 * @return the UpdateResult
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.replaceOne
 */
suspend fun MongoCollection.replaceOne(
    filter: BsonDocument,
    replacement: BsonDocument,
    options: ReplaceOptions = ReplaceOptions(),
    session: ClientSession? = null
): UpdateResult {
    val publisher = when (session) {
        null -> java.replaceOne(filter.java, replacement.java, options.java)
        else -> java.replaceOne(session.java, filter.java, replacement.java, options.java)
    }
    return publisher.awaitSingle().kt
}

/**
 * Replace a document in the collection according
 * to the specified arguments.
 *
 * @param session the client session with which to associate this operation
 * @param filter the query filter to apply the replace operation
 * @param replacement the replacement document
 * @param options the options to apply to the replace operation
 * @return the UpdateResult
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.replaceOne
 */
suspend fun MongoCollection.replaceOne(
    filter: BsonDocumentBlock,
    replacement: BsonDocumentBlock,
    session: ClientSession? = null,
    options: ReplaceOptions.() -> Unit = {}
) = replaceOne(BsonDocument(filter), BsonDocument(replacement), ReplaceOptions(options), session)

//

/**
 * Executes a mix of inserts, updates, replaces,
 * and deletes.
 *
 * @param session the client session with which to associate this operation.
 * @param requests the writes to execute.
 * @param options the options to apply to the bulk write operation.
 * @return the BulkWriteResult.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.bulkWrite
 */
suspend fun MongoCollection.bulkWrite(
    requests: List<WriteModel>,
    options: BulkWriteOptions = BulkWriteOptions(),
    session: ClientSession? = null
): BulkWriteResult {
    val publisher = when (session) {
        null -> java.bulkWrite(requests.map { it.java }, options.java)
        else -> java.bulkWrite(session.java, requests.map { it.java }, options.java)
    }
    return publisher.awaitSingle().kt
}

/**
 * Executes a mix of inserts, updates, replaces,
 * and deletes.
 *
 * @param session the client session with which to associate this operation.
 * @param requests the writes to execute.
 * @param options the options to apply to the bulk write operation.
 * @return the BulkWriteResult.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.bulkWrite
 */
suspend fun MongoCollection.bulkWrite(
    vararg requests: WriteModel,
    session: ClientSession? = null,
    options: BulkWriteOptions.() -> Unit = {}
) = bulkWrite(requests.asList(), BulkWriteOptions(options), session)

/* ============= ------------------ ============= */

/**
 * Counts the number of documents in the collection
 * according to the given options.
 *
 * @param session the client session with which to associate this operation.
 * @param filter  the query filter.
 * @param options the options describing the count.
 * @return the number of documents.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.countDocuments
 */
suspend fun MongoCollection.count(
    filter: BsonDocument = BsonDocument.Empty,
    options: CountOptions = CountOptions(),
    session: ClientSession? = null
): Long {
    val publisher = when (session) {
        null -> java.countDocuments(filter.java, options.java)
        else -> java.countDocuments(session.java, filter.java, options.java)
    }
    return publisher.awaitSingle()
}

/**
 * Counts the number of documents in the collection
 * according to the given options.
 *
 * @param session the client session with which to associate this operation.
 * @param filter  the query filter.
 * @param options the options describing the count.
 * @return the number of documents.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.countDocuments
 */
suspend fun MongoCollection.count(
    filter: BsonDocumentBlock,
    session: ClientSession? = null,
    options: CountOptions.() -> Unit = {}
) = count(BsonDocument(filter), CountOptions(options), session)

//

/**
 * Gets an estimate of the count of documents in a
 * collection using collection metadata.
 *
 * @param options the options describing the count
 * @return the estimated number of documents
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.estimatedDocumentCount
 */
suspend fun MongoCollection.estimatedCount(
    options: EstimatedCountOptions = EstimatedCountOptions()
): Long {
    val publisher = java.estimatedDocumentCount(options.java)
    return publisher.awaitSingle()
}

/**
 * Gets an estimate of the count of documents in a
 * collection using collection metadata.
 *
 * @param options the options describing the count
 * @return the estimated number of documents
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.estimatedDocumentCount
 */
suspend fun MongoCollection.estimatedCount(
    options: EstimatedCountOptions.() -> Unit
) = estimatedCount(EstimatedCountOptions(options))

//

/**
 * Atomically find a document and remove it.
 *
 * @param session the client session with which to associate this operation.
 * @param filter the query filter to find the document with.
 * @param options the options to apply to the operation.
 * @return the document that was removed.
 *         If no documents matched the query filter, then null will be returned.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.findOneAndDelete
 */
suspend fun MongoCollection.findOneAndDelete(
    filter: BsonDocument,
    options: FindOneAndDeleteOptions = FindOneAndDeleteOptions(),
    session: ClientSession? = null
): BsonDocument? {
    val publisher = when (session) {
        null -> java.findOneAndDelete(filter.java, options.java)
        else -> java.findOneAndDelete(session.java, filter.java, options.java)
    }
    return publisher.awaitFirstOrNull()?.kt
}

/**
 * Atomically find a document and remove it.
 *
 * @param session the client session with which to associate this operation.
 * @param filter the query filter to find the document with.
 * @param options the options to apply to the operation.
 * @return the document that was removed.
 *         If no documents matched the query filter, then null will be returned.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.findOneAndDelete
 */
suspend fun MongoCollection.findOneAndDelete(
    filter: BsonDocumentBlock,
    session: ClientSession? = null,
    options: FindOneAndDeleteOptions.() -> Unit = {}
) = findOneAndDelete(BsonDocument(filter), FindOneAndDeleteOptions(options), session)

//

/**
 * Atomically find a document and replace it.
 *
 * @param session the client session with which to associate this operation.
 * @param filter the query filter to apply the replace operation.
 * @param replacement the replacement document.
 * @param options the options to apply to the operation.
 * @return the document that was replaced.
 *         Depending on the value of the `returnOriginal` property, this will either
 *         be the document as it was before the update or as it is after the update.
 *         If no documents matched the query filter, then null will be returned
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.findOneAndReplace
 */
suspend fun MongoCollection.findOneAndReplace(
    filter: BsonDocument,
    replacement: BsonDocument,
    options: FindOneAndReplaceOptions = FindOneAndReplaceOptions(),
    session: ClientSession? = null
): BsonDocument? {
    val publisher = when (session) {
        null -> java.findOneAndReplace(filter.java, replacement.java, options.java)
        else -> java.findOneAndReplace(session.java, filter.java, replacement.java, options.java)
    }
    return publisher.awaitFirstOrNull()?.kt
}

/**
 * Atomically find a document and replace it.
 *
 * @param session the client session with which to associate this operation.
 * @param filter the query filter to apply the replace operation.
 * @param replacement the replacement document.
 * @param options the options to apply to the operation.
 * @return the document that was replaced.
 *         Depending on the value of the `returnOriginal` property, this will either
 *         be the document as it was before the update or as it is after the update.
 *         If no documents matched the query filter, then null will be returned
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.findOneAndReplace
 */
suspend fun MongoCollection.findOneAndReplace(
    filter: BsonDocumentBlock,
    replacement: BsonDocumentBlock,
    session: ClientSession? = null,
    options: FindOneAndReplaceOptions.() -> Unit = {}
) = findOneAndReplace(BsonDocument(filter), BsonDocument(replacement), FindOneAndReplaceOptions(options), session)

//

/**
 * Atomically find a document and update it.
 *
 * @param session the client session with which to associate this operation.
 * @param filter  a document describing the query filter, which may not be null.
 * @param update  a document describing the update, which may not be null.
 *                The update to apply must include only update operators.
 * @param options the options to apply to the operation.
 * @return the document that was updated.
 *         Depending on the value of the `returnOriginal` property, this will either be
 *         the document as it was before the update or as it is after the update.
 *         If no documents matched the query filter, then null will be returned.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.findOneAndUpdate
 */
suspend fun MongoCollection.findOneAndUpdate(
    filter: BsonDocument,
    update: BsonDocument,
    options: FindOneAndUpdateOptions = FindOneAndUpdateOptions(),
    session: ClientSession? = null
): BsonDocument? {
    val publisher = when (session) {
        null -> java.findOneAndUpdate(filter.java, update.java, options.java)
        else -> java.findOneAndUpdate(session.java, filter.java, update.java, options.java)
    }
    return publisher.awaitFirstOrNull()?.kt
}

/**
 * Atomically find a document and update it.
 *
 * @param session the client session with which to associate this operation.
 * @param filter  a document describing the query filter, which may not be null.
 * @param update  a document describing the update, which may not be null.
 *                The update to apply must include only update operators.
 * @param options the options to apply to the operation.
 * @return the document that was updated.
 *         Depending on the value of the `returnOriginal` property, this will either be
 *         the document as it was before the update or as it is after the update.
 *         If no documents matched the query filter, then null will be returned.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.findOneAndUpdate
 */
suspend fun MongoCollection.findOneAndUpdate(
    filter: BsonDocumentBlock,
    update: BsonDocumentBlock,
    session: ClientSession? = null,
    options: FindOneAndUpdateOptions.() -> Unit = {}
) = findOneAndUpdate(BsonDocument(filter), BsonDocument(update), FindOneAndUpdateOptions(options), session)

//

/**
 * Atomically find a document and update it.
 *
 * Note: Supports retryable writes on MongoDB
 * server versions 3.6 or higher when the
 * retryWrites setting is enabled.
 *
 * @param session the client session with which to associate this operation.
 * @param filter a document describing the query filter, which may not be null.
 * @param update a pipeline describing the update, which may not be null.
 * @param options the options to apply to the operation.
 * @return the document that was updated.
 *         Depending on the value of the `returnOriginal` property, this will either be
 *         the document as it was before the update or as it is after the update.
 *         If no documents matched the query filter, then null will be returned.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.findOneAndUpdate
 */
suspend fun MongoCollection.findOneAndUpdate(
    filter: BsonDocument,
    update: List<BsonDocument>,
    options: FindOneAndUpdateOptions = FindOneAndUpdateOptions(),
    session: ClientSession? = null
): BsonDocument? {
    val publisher = when (session) {
        null -> java.findOneAndUpdate(filter.java, update.map { it.java }, options.java)
        else -> java.findOneAndUpdate(session.java, filter.java, update.map { it.java }, options.java)
    }
    return publisher.awaitFirstOrNull()?.kt
}

/**
 * Atomically find a document and update it.
 *
 * Note: Supports retryable writes on MongoDB
 * server versions 3.6 or higher when the
 * retryWrites setting is enabled.
 *
 * @param session the client session with which to associate this operation.
 * @param filter a document describing the query filter, which may not be null.
 * @param update a pipeline describing the update, which may not be null.
 * @param options the options to apply to the operation.
 * @return the document that was updated.
 *         Depending on the value of the `returnOriginal` property, this will either be
 *         the document as it was before the update or as it is after the update.
 *         If no documents matched the query filter, then null will be returned.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.findOneAndUpdate
 */
suspend fun MongoCollection.findOneAndUpdate(
    filter: BsonDocumentBlock,
    vararg update: BsonDocumentBlock,
    session: ClientSession? = null,
    options: FindOneAndUpdateOptions.() -> Unit = {}
) = findOneAndUpdate(BsonDocument(filter), update.map { BsonDocument(it) }, FindOneAndUpdateOptions(options), session)

//

/**
 * Atomically find a document and update it.
 *
 * @param session the client session with which to associate this operation.
 * @param filter  a document describing the query filter, which may not be null.
 * @param update  a document describing the update, which may not be null.
 *                The update to apply must include only update operators.
 * @param options the options to apply to the operation.
 * @return the document that was updated.
 *         Depending on the value of the `returnOriginal` property, this will either be
 *         the document as it was before the update or as it is after the update.
 *         If no documents matched the query filter, then null will be returned.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.findOneAndUpdate
 */
suspend fun MongoCollection.findOneAndUpdate(
    filter: BsonDocument,
    update: BsonElement,
    options: FindOneAndUpdateOptions = FindOneAndUpdateOptions(),
    session: ClientSession? = null
): BsonDocument? {
    return when (update) {
        is BsonDocument ->
            findOneAndUpdate(filter, update, options, session)
        is BsonArray ->
            findOneAndUpdate(filter, update.map { it as BsonDocument }, options, session)
        else ->
            error("update is expected to be either a document or an array of documents.")
    }
}

/* ============= ------------------ ============= */

/**
 * Finds all documents in the collection.
 *
 * @param session the client session with which to associate this operation.
 * @param filter the query filter.
 * @param options the operation options.
 * @return the found documents.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.find
 */
suspend fun MongoCollection.find(
    filter: BsonDocument = BsonDocument.Empty,
    options: FindOptions = FindOptions(),
    session: ClientSession? = null
): List<BsonDocument> {
    val publisher = when (session) {
        null -> java.find(filter.java, org.bson.BsonDocument::class.java)
        else -> java.find(session.java, filter.java, org.bson.BsonDocument::class.java)
    }
    return publisher
        .apply(options)
        .asFlow()
        .toList()
        .map { it.kt }
}

/**
 * Finds all documents in the collection.
 *
 * @param session the client session with which to associate this operation.
 * @param filter the query filter.
 * @param options the operation options.
 * @return the found documents.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.find
 */
suspend fun MongoCollection.find(
    filter: BsonDocumentBlock,
    session: ClientSession? = null,
    options: FindOptions.() -> Unit = {}
) = find(BsonDocument(filter), FindOptions(options), session)

//

/**
 * Aggregates documents according to the specified
 * aggregation pipeline.
 *
 * @param session the client session with which to associate this operation.
 * @param pipeline  the aggregate pipeline.
 * @param options the operation options.
 * @return the result of the aggregation operation.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.aggregate
 */
suspend fun MongoCollection.aggregate(
    pipeline: List<BsonDocument>,
    options: AggregateOptions = AggregateOptions(),
    session: ClientSession? = null
): List<BsonDocument> {
    val publisher = when (session) {
        null -> java.aggregate(pipeline.map { it.java }, org.bson.BsonDocument::class.java)
        else -> java.aggregate(session.java, pipeline.map { it.java }, org.bson.BsonDocument::class.java)
    }
    return publisher
        .apply(options)
        .asFlow()
        .toList()
        .map { it.kt }
}

/**
 * Aggregates documents according to the specified
 * aggregation pipeline.
 *
 * @param session the client session with which to associate this operation.
 * @param pipeline  the aggregate pipeline.
 * @param options the operation options.
 * @return the result of the aggregation operation.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.aggregate
 */
suspend fun MongoCollection.aggregate(
    vararg pipeline: BsonDocumentBlock,
    session: ClientSession? = null,
    options: AggregateOptions.() -> Unit = {}
) = aggregate(pipeline.map { BsonDocument(it) }, AggregateOptions(options), session)

//

/**
 * Gets the distinct values of the specified
 * field name.
 *
 * @param session the client session with which to associate this operation.
 * @param field the field name.
 * @param filter the query filter.
 * @param options the operation options.
 * @return an iterable of distinct values.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.distinct
 */
suspend fun MongoCollection.distinct(
    field: String,
    filter: BsonDocument = BsonDocument.Empty,
    options: DistinctOptions = DistinctOptions(),
    session: ClientSession? = null
): List<BsonDocument> {
    val publisher = when (session) {
        null -> java.distinct(field, filter.java, org.bson.BsonDocument::class.java)
        else -> java.distinct(session.java, field, filter.java, org.bson.BsonDocument::class.java)
    }
    return publisher
        .apply(options)
        .asFlow()
        .toList()
        .map { it.kt }
}

/**
 * Gets the distinct values of the specified
 * field name.
 *
 * @param session the client session with which to associate this operation.
 * @param field the field name.
 * @param filter the query filter.
 * @param options the operation options.
 * @return an iterable of distinct values.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.distinct
 */
suspend fun MongoCollection.distinct(
    field: String,
    filter: BsonDocumentBlock,
    session: ClientSession? = null,
    options: DistinctOptions.() -> Unit = {}
) = distinct(field, BsonDocument(filter), DistinctOptions(options), session)

//

/* TODO MongoCollection.watch(pipeline, options, session) */

//

/**
 * Get all the indexes in this collection.
 *
 * @param session the client session with which to associate this operation.
 * @param options the operation options.
 * @return the indexes.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.listIndexes
 */
suspend fun MongoCollection.listIndexes(
    options: ListIndexesOptions = ListIndexesOptions(),
    session: ClientSession? = null,
): List<BsonDocument> {
    val publisher = when (session) {
        null -> java.listIndexes(org.bson.BsonDocument::class.java)
        else -> java.listIndexes(session.java, org.bson.BsonDocument::class.java)
    }
    return publisher
        .apply(options)
        .asFlow()
        .toList()
        .map { it.kt }
}

/**
 * Get all the indexes in this collection.
 *
 * @param session the client session with which to associate this operation.
 * @param options the operation options.
 * @return the indexes.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.listIndexes
 */
suspend fun MongoCollection.listIndexes(
    session: ClientSession? = null,
    options: ListIndexesOptions.() -> Unit,
) = listIndexes(ListIndexesOptions(options), session)

/* ============= ------------------ ============= */

/**
 * Creates an index.
 *
 * @param session the client session with which to associate this operation.
 * @param keys an object describing the index key(s), which may not be null.
 * @param options the options for the index.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.createIndex
 */
suspend fun MongoCollection.createIndex(
    keys: BsonDocument,
    options: CreateIndexOptions = CreateIndexOptions(),
    session: ClientSession? = null
): String {
    val publisher = when (session) {
        null -> java.createIndex(keys.java, options.java)
        else -> java.createIndex(session.java, keys.java, options.java)
    }
    return publisher.awaitSingle()
}

/**
 * Creates an index.
 *
 * @param session the client session with which to associate this operation.
 * @param keys an object describing the index key(s), which may not be null.
 * @param options the options for the index.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.createIndex
 */
suspend fun MongoCollection.createIndex(
    keys: BsonDocumentBlock,
    session: ClientSession? = null,
    options: CreateIndexOptions.() -> Unit = {}
) = createIndex(BsonDocument(keys), CreateIndexOptions(options), session)

//

/**
 * Create multiple indexes.
 *
 * @param session the client session with which to associate this operation.
 * @param indexes the list of indexes.
 * @param options options to use when creating indexes.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.createIndexes
 */
suspend fun MongoCollection.createIndexes(
    indexes: List<CreateIndexModel>,
    options: CreateIndexesOptions = CreateIndexesOptions(),
    session: ClientSession? = null
): List<String> {
    val publisher = when (session) {
        null -> java.createIndexes(indexes.map { it.java }, options.java)
        else -> java.createIndexes(session.java, indexes.map { it.java }, options.java)
    }
    return publisher.asFlow().toList()
}

/**
 * Create multiple indexes.
 *
 * @param session the client session with which to associate this operation.
 * @param indexes the list of indexes.
 * @param options options to use when creating indexes.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.createIndexes
 */
suspend fun MongoCollection.createIndexes(
    vararg indexes: CreateIndexModel,
    session: ClientSession? = null,
    options: CreateIndexesOptions.() -> Unit = {}
) = createIndexes(indexes.asList(), CreateIndexesOptions(options), session)

//

/**
 * Drops the given index.
 *
 * @param session the client session with which to associate this operation.
 * @param name the name of the index to remove.
 * @param options options to use when dropping indexes.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.dropIndex
 */
suspend fun MongoCollection.dropIndex(
    name: String,
    options: DropIndexOptions = DropIndexOptions(),
    session: ClientSession? = null
) {
    val publisher = when (session) {
        null -> java.dropIndex(name, options.java)
        else -> java.dropIndex(session.java, name, options.java)
    }
    publisher.awaitFirstOrNull()
}

/**
 * Drops the given index.
 *
 * @param session the client session with which to associate this operation.
 * @param name the name of the index to remove.
 * @param options options to use when dropping indexes.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.dropIndex
 */
suspend fun MongoCollection.dropIndex(
    name: String,
    session: ClientSession? = null,
    options: DropIndexOptions.() -> Unit
) = dropIndex(name, DropIndexOptions(options), session)

//

/**
 * Drops the index given the keys used to create it.
 *
 * @param session the client session with which to associate this operation.
 * @param keys the keys of the index to remove.
 * @param options options to use when dropping indexes.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.dropIndex
 */
suspend fun MongoCollection.dropIndex(
    keys: BsonDocument,
    options: DropIndexOptions = DropIndexOptions(),
    session: ClientSession? = null
) {
    val publisher = when (session) {
        null -> java.dropIndex(keys.java, options.java)
        else -> java.dropIndex(session.java, keys.java, options.java)
    }
    publisher.awaitFirstOrNull()
}

/**
 * Drops the index given the keys used to create it.
 *
 * @param session the client session with which to associate this operation.
 * @param keys the keys of the index to remove.
 * @param options options to use when dropping indexes.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.dropIndex
 */
suspend fun MongoCollection.dropIndex(
    keys: BsonDocumentBlock,
    session: ClientSession? = null,
    options: DropIndexOptions.() -> Unit = {}
) = dropIndex(BsonDocument(keys), DropIndexOptions(options), session)

//

/**
 * Drop all the indexes on this collection, except
 * for the default on _id.
 *
 * @param session the client session with which to associate this operation.
 * @param options options to use when dropping indexes.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.dropIndexes
 */
suspend fun MongoCollection.dropIndexes(
    options: DropIndexOptions = DropIndexOptions(),
    session: ClientSession? = null
) {
    val publisher = when (session) {
        null -> java.dropIndexes(options.java)
        else -> java.dropIndexes(session.java, options.java)
    }
    publisher.awaitFirstOrNull()
}

/**
 * Drop all the indexes on this collection, except
 * for the default on _id.
 *
 * @param session the client session with which to associate this operation.
 * @param options options to use when dropping indexes.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.dropIndexes
 */
suspend fun MongoCollection.dropIndexes(
    session: ClientSession? = null,
    options: DropIndexOptions.() -> Unit
) = dropIndexes(DropIndexOptions(options), session)

//

/**
 * Rename the collection with oldCollectionName to
 * the newCollectionName.
 *
 * @param session the client session with which to associate this operation.
 * @param namespace the name the collection will be renamed to.
 * @param options the options for renaming a collection.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.renameCollection
 */
suspend fun MongoCollection.renameCollection(
    namespace: MongoNamespace,
    options: RenameCollectionOptions = RenameCollectionOptions(),
    session: ClientSession? = null
) {
    val publisher = when (session) {
        null -> java.renameCollection(namespace.java, options.java)
        else -> java.renameCollection(session.java, namespace.java, options.java)
    }
    publisher.awaitFirstOrNull()
}

/**
 * Rename the collection with oldCollectionName to
 * the newCollectionName.
 *
 * @param session the client session with which to associate this operation.
 * @param namespace the name the collection will be renamed to.
 * @param options the options for renaming a collection.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.renameCollection
 */
suspend fun MongoCollection.renameCollection(
    namespace: MongoNamespace,
    session: ClientSession? = null,
    options: RenameCollectionOptions.() -> Unit
) = renameCollection(namespace, RenameCollectionOptions(options), session)

//

/**
 * Drops this collection from the Database.
 *
 * @param session the client session with which to associate this operation.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.drop
 */
suspend fun MongoCollection.drop(
    session: ClientSession? = null
) {
    val publisher = when (session) {
        null -> java.drop()
        else -> java.drop(session.java)
    }
    publisher.awaitFirstOrNull()
}

/* ============= ------------------ ============= */
