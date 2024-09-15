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
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.cufy.mongodb

import org.cufy.bson.BsonArray
import org.cufy.bson.BsonDocument
import org.cufy.bson.BsonDocumentBlock
import org.cufy.bson.BsonElement

/* ============= ------------------ ============= */

/**
 * A generic-free coroutine dependant wrapper for
 * a mongodb collection.
 *
 * @see com.mongodb.reactivestreams.client.MongoCollection
 * @author LSafer
 * @since 2.0.0
 */
expect class MongoCollection

/* ============= ------------------ ============= */

/**
 * Gets the namespace of this collection.
 *
 * @see com.mongodb.client.MongoCollection.getNamespace
 * @since 2.0.0
 */
expect val MongoCollection.namespace: MongoNamespace

/**
 * Get the read preference for the MongoCollection.
 *
 * @see com.mongodb.client.MongoCollection.getReadPreference
 * @since 2.0.0
 */
expect val MongoCollection.readPreference: ReadPreference

/**
 * Get the write concern for the MongoCollection.
 *
 * @see com.mongodb.client.MongoCollection.getWriteConcern
 * @since 2.0.0
 */
expect val MongoCollection.writeConcern: WriteConcern

/**
 * Get the read concern for the MongoCollection.
 *
 * @see com.mongodb.client.MongoCollection.getReadConcern
 * @since 2.0.0
 */
expect val MongoCollection.readConcern: ReadConcern

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
expect suspend fun MongoCollection.deleteOne(
    filter: BsonDocument,
    options: DeleteOptions = DeleteOptions(),
    session: ClientSession? = null,
): DeleteResult

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
    options: DeleteOptions.() -> Unit = {},
): DeleteResult {
    return deleteOne(
        filter = BsonDocument(filter),
        options = DeleteOptions(options),
        session = session
    )
}

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
expect suspend fun MongoCollection.deleteMany(
    filter: BsonDocument,
    options: DeleteOptions = DeleteOptions(),
    session: ClientSession? = null,
): DeleteResult

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
    options: DeleteOptions.() -> Unit = {},
): DeleteResult {
    return deleteMany(
        filter = BsonDocument(filter),
        options = DeleteOptions(options),
        session = session
    )
}

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
expect suspend fun MongoCollection.insertOne(
    document: BsonDocument,
    options: InsertOneOptions = InsertOneOptions(),
    session: ClientSession? = null,
): InsertOneResult

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
    options: InsertOneOptions.() -> Unit = {},
): InsertOneResult {
    return insertOne(
        document = BsonDocument(document),
        options = InsertOneOptions(options),
        session = session
    )
}

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
expect suspend fun MongoCollection.insertMany(
    documents: List<BsonDocument>,
    options: InsertManyOptions = InsertManyOptions(),
    session: ClientSession? = null,
): InsertManyResult

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
    options: InsertManyOptions.() -> Unit = {},
): InsertManyResult {
    return insertMany(
        documents = documents.map { BsonDocument(it) },
        options = InsertManyOptions(options),
        session = session
    )
}

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
expect suspend fun MongoCollection.updateOne(
    filter: BsonDocument,
    update: BsonDocument,
    options: UpdateOptions = UpdateOptions(),
    session: ClientSession? = null,
): UpdateResult

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
expect suspend fun MongoCollection.updateOne(
    filter: BsonDocument,
    update: List<BsonDocument>,
    options: UpdateOptions = UpdateOptions(),
    session: ClientSession? = null,
): UpdateResult

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
    options: UpdateOptions.() -> Unit = {},
): UpdateResult {
    return updateOne(
        filter = BsonDocument(filter),
        update = BsonDocument(update),
        options = UpdateOptions(options),
        session = session
    )
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
@Deprecated("Use updateOne with listOf()")
suspend fun MongoCollection.updateOne(
    filter: BsonDocumentBlock,
    vararg update: BsonDocumentBlock,
    session: ClientSession? = null,
    options: UpdateOptions.() -> Unit = {},
): UpdateResult {
    return updateOne(
        filter = BsonDocument(filter),
        update = update.map { BsonDocument(it) },
        options = UpdateOptions(options),
        session = session
    )
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
    update: List<BsonDocumentBlock>,
    session: ClientSession? = null,
    options: UpdateOptions.() -> Unit = {},
): UpdateResult {
    return updateOne(
        filter = BsonDocument(filter),
        update = update.map { BsonDocument(it) },
        options = UpdateOptions(options),
        session = session
    )
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
@Deprecated("Use updateOne with either explicit BsonDocument or explicit BsonArray")
suspend fun MongoCollection.updateOne(
    filter: BsonDocument,
    update: BsonElement,
    options: UpdateOptions = UpdateOptions(),
    session: ClientSession? = null,
): UpdateResult {
    return when (update) {
        is BsonDocument -> updateOne(
            filter = filter,
            update = update,
            options = options,
            session = session
        )

        is BsonArray -> updateOne(
            filter = filter,
            update = update.map { it as BsonDocument },
            options = options,
            session = session
        )

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
expect suspend fun MongoCollection.updateMany(
    filter: BsonDocument,
    update: BsonDocument,
    options: UpdateOptions = UpdateOptions(),
    session: ClientSession? = null,
): UpdateResult

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
expect suspend fun MongoCollection.updateMany(
    filter: BsonDocument,
    update: List<BsonDocument>,
    options: UpdateOptions = UpdateOptions(),
    session: ClientSession? = null,
): UpdateResult

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
    options: UpdateOptions.() -> Unit = {},
): UpdateResult {
    return updateMany(
        filter = BsonDocument(filter),
        update = BsonDocument(update),
        options = UpdateOptions(options),
        session = session
    )
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
@Deprecated("Use updateMany with listOf()")
suspend fun MongoCollection.updateMany(
    filter: BsonDocumentBlock,
    vararg update: BsonDocumentBlock,
    session: ClientSession? = null,
    options: UpdateOptions.() -> Unit = {},
): UpdateResult {
    return updateMany(
        filter = BsonDocument(filter),
        update = update.map { BsonDocument(it) },
        options = UpdateOptions(options),
        session = session
    )
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
    update: List<BsonDocumentBlock>,
    session: ClientSession? = null,
    options: UpdateOptions.() -> Unit = {},
): UpdateResult {
    return updateMany(
        filter = BsonDocument(filter),
        update = update.map { BsonDocument(it) },
        options = UpdateOptions(options),
        session = session
    )
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
@Deprecated("Use updateMany with either explicit BsonDocument or explicit BsonArray")
suspend fun MongoCollection.updateMany(
    filter: BsonDocument,
    update: BsonElement,
    options: UpdateOptions = UpdateOptions(),
    session: ClientSession? = null,
): UpdateResult {
    return when (update) {
        is BsonDocument -> updateMany(
            filter = filter,
            update = update,
            options = options,
            session = session
        )

        is BsonArray -> updateMany(
            filter = filter,
            update = update.map { it as BsonDocument },
            options = options,
            session = session
        )

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
expect suspend fun MongoCollection.replaceOne(
    filter: BsonDocument,
    replacement: BsonDocument,
    options: ReplaceOptions = ReplaceOptions(),
    session: ClientSession? = null,
): UpdateResult

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
    options: ReplaceOptions.() -> Unit = {},
): UpdateResult {
    return replaceOne(
        filter = BsonDocument(filter),
        replacement = BsonDocument(replacement),
        options = ReplaceOptions(options),
        session = session
    )
}

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
expect suspend fun MongoCollection.bulkWrite(
    requests: List<WriteModel>,
    options: BulkWriteOptions = BulkWriteOptions(),
    session: ClientSession? = null,
): BulkWriteResult

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
    options: BulkWriteOptions.() -> Unit = {},
): BulkWriteResult {
    return bulkWrite(
        requests = requests.asList(),
        options = BulkWriteOptions(options),
        session = session
    )
}

//

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
expect suspend fun MongoCollection.count(
    filter: BsonDocument = BsonDocument.Empty,
    options: CountOptions = CountOptions(),
    session: ClientSession? = null,
): Long

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
    options: CountOptions.() -> Unit = {},
): Long {
    return count(
        filter = BsonDocument(filter),
        options = CountOptions(options),
        session = session
    )
}

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
expect suspend fun MongoCollection.estimatedCount(
    options: EstimatedCountOptions = EstimatedCountOptions(),
): Long

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
    options: EstimatedCountOptions.() -> Unit,
): Long {
    return estimatedCount(
        options = EstimatedCountOptions(options)
    )
}

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
expect suspend fun MongoCollection.findOneAndDelete(
    filter: BsonDocument,
    options: FindOneAndDeleteOptions = FindOneAndDeleteOptions(),
    session: ClientSession? = null,
): BsonDocument?

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
    options: FindOneAndDeleteOptions.() -> Unit = {},
): BsonDocument? {
    return findOneAndDelete(
        filter = BsonDocument(filter),
        options = FindOneAndDeleteOptions(options),
        session = session
    )
}

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
expect suspend fun MongoCollection.findOneAndReplace(
    filter: BsonDocument,
    replacement: BsonDocument,
    options: FindOneAndReplaceOptions = FindOneAndReplaceOptions(),
    session: ClientSession? = null,
): BsonDocument?

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
    options: FindOneAndReplaceOptions.() -> Unit = {},
): BsonDocument? {
    return findOneAndReplace(
        filter = BsonDocument(filter),
        replacement = BsonDocument(replacement),
        options = FindOneAndReplaceOptions(options),
        session = session
    )
}

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
expect suspend fun MongoCollection.findOneAndUpdate(
    filter: BsonDocument,
    update: BsonDocument,
    options: FindOneAndUpdateOptions = FindOneAndUpdateOptions(),
    session: ClientSession? = null,
): BsonDocument?

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
expect suspend fun MongoCollection.findOneAndUpdate(
    filter: BsonDocument,
    update: List<BsonDocument>,
    options: FindOneAndUpdateOptions = FindOneAndUpdateOptions(),
    session: ClientSession? = null,
): BsonDocument?

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
    options: FindOneAndUpdateOptions.() -> Unit = {},
): BsonDocument? {
    return findOneAndUpdate(
        filter = BsonDocument(filter),
        update = BsonDocument(update),
        options = FindOneAndUpdateOptions(options),
        session = session
    )
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
@Deprecated("Use findOneAndUpdate with listOf()")
suspend fun MongoCollection.findOneAndUpdate(
    filter: BsonDocumentBlock,
    vararg update: BsonDocumentBlock,
    session: ClientSession? = null,
    options: FindOneAndUpdateOptions.() -> Unit = {},
): BsonDocument? {
    return findOneAndUpdate(
        filter = BsonDocument(filter),
        update = update.map { BsonDocument(it) },
        options = FindOneAndUpdateOptions(options),
        session = session
    )
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
    update: List<BsonDocumentBlock>,
    session: ClientSession? = null,
    options: FindOneAndUpdateOptions.() -> Unit = {},
): BsonDocument? {
    return findOneAndUpdate(
        filter = BsonDocument(filter),
        update = update.map { BsonDocument(it) },
        options = FindOneAndUpdateOptions(options),
        session = session
    )
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
@Deprecated("Use findOneAndUpdate with either explicit BsonDocument or explicit BsonArray")
suspend fun MongoCollection.findOneAndUpdate(
    filter: BsonDocument,
    update: BsonElement,
    options: FindOneAndUpdateOptions = FindOneAndUpdateOptions(),
    session: ClientSession? = null,
): BsonDocument? {
    return when (update) {
        is BsonDocument -> findOneAndUpdate(
            filter = filter,
            update = update,
            options = options,
            session = session
        )

        is BsonArray -> findOneAndUpdate(
            filter = filter,
            update = update.map { it as BsonDocument },
            options = options,
            session = session
        )

        else ->
            error("update is expected to be either a document or an array of documents.")
    }
}

//

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
expect suspend fun MongoCollection.find(
    filter: BsonDocument = BsonDocument.Empty,
    options: FindOptions = FindOptions(),
    session: ClientSession? = null,
): List<BsonDocument>

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
    options: FindOptions.() -> Unit = {},
): List<BsonDocument> {
    return find(
        filter = BsonDocument(filter),
        options = FindOptions(options),
        session = session
    )
}

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
expect suspend fun MongoCollection.aggregate(
    pipeline: List<BsonDocument>,
    options: AggregateOptions = AggregateOptions(),
    session: ClientSession? = null,
): List<BsonDocument>

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
    options: AggregateOptions.() -> Unit = {},
): List<BsonDocument> {
    return aggregate(
        pipeline = pipeline.map { BsonDocument(it) },
        options = AggregateOptions(options),
        session = session
    )
}

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
expect suspend fun MongoCollection.distinct(
    field: String,
    filter: BsonDocument = BsonDocument.Empty,
    options: DistinctOptions = DistinctOptions(),
    session: ClientSession? = null,
): List<BsonDocument>

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
    options: DistinctOptions.() -> Unit = {},
): List<BsonDocument> {
    return distinct(
        field = field,
        filter = BsonDocument(filter),
        options = DistinctOptions(options),
        session = session
    )
}

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
expect suspend fun MongoCollection.listIndexes(
    options: ListIndexesOptions = ListIndexesOptions(),
    session: ClientSession? = null,
): List<BsonDocument>

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
): List<BsonDocument> {
    return listIndexes(
        options = ListIndexesOptions(options),
        session = session
    )
}

//

/**
 * Creates an index.
 *
 * @param session the client session with which to associate this operation.
 * @param keys an object describing the index key(s), which may not be null.
 * @param options the options for the index.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.createIndex
 */
expect suspend fun MongoCollection.createIndex(
    keys: BsonDocument,
    options: CreateIndexOptions = CreateIndexOptions(),
    session: ClientSession? = null,
): String

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
    options: CreateIndexOptions.() -> Unit = {},
): String {
    return createIndex(
        keys = BsonDocument(keys),
        options = CreateIndexOptions(options),
        session = session
    )
}

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
expect suspend fun MongoCollection.createIndexes(
    indexes: List<CreateIndexModel>,
    options: CreateIndexesOptions = CreateIndexesOptions(),
    session: ClientSession? = null,
): List<String>

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
    options: CreateIndexesOptions.() -> Unit = {},
): List<String> {
    return createIndexes(
        indexes = indexes.asList(),
        options = CreateIndexesOptions(options),
        session = session
    )
}

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
expect suspend fun MongoCollection.dropIndex(
    name: String,
    options: DropIndexOptions = DropIndexOptions(),
    session: ClientSession? = null,
)

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
    options: DropIndexOptions.() -> Unit,
) {
    dropIndex(
        name = name,
        options = DropIndexOptions(options),
        session = session
    )
}

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
expect suspend fun MongoCollection.dropIndex(
    keys: BsonDocument,
    options: DropIndexOptions = DropIndexOptions(),
    session: ClientSession? = null,
)

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
    options: DropIndexOptions.() -> Unit = {},
) {
    dropIndex(
        keys = BsonDocument(keys),
        options = DropIndexOptions(options),
        session = session
    )
}

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
expect suspend fun MongoCollection.dropIndexes(
    options: DropIndexOptions = DropIndexOptions(),
    session: ClientSession? = null,
)

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
    options: DropIndexOptions.() -> Unit,
) {
    dropIndexes(
        options = DropIndexOptions(options),
        session = session
    )
}

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
expect suspend fun MongoCollection.renameCollection(
    namespace: MongoNamespace,
    options: RenameCollectionOptions = RenameCollectionOptions(),
    session: ClientSession? = null,
)

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
    options: RenameCollectionOptions.() -> Unit,
) {
    renameCollection(
        namespace = namespace,
        options = RenameCollectionOptions(options),
        session = session
    )
}

//

/**
 * Drops this collection from the Database.
 *
 * @param session the client session with which to associate this operation.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.drop
 */
expect suspend fun MongoCollection.drop(
    session: ClientSession? = null,
)

/* ============= ------------------ ============= */
