/*
 *	Copyright 2022 cufy.org
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
package org.cufy.monkt

import com.mongodb.DuplicateKeyException
import com.mongodb.MongoCommandException
import com.mongodb.MongoException
import com.mongodb.MongoNamespace
import com.mongodb.bulk.BulkWriteResult
import com.mongodb.client.model.*
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertManyResult
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult
import com.mongodb.reactivestreams.client.ClientSession
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.cufy.bson.*

/* ========= - estimatedDocumentCount - ========= */

/**
 * Gets an estimate of the count of documents in a
 * collection using collection metadata.
 *
 * @param options the options describing the count
 * @return the estimated number of documents
 * @since 2.0.0
 * @see MonktCollection.estimatedDocumentCount
 */
suspend fun MonktCollection.estimatedDocumentCountSuspend(
    options: EstimatedDocumentCountOptions
): Long {
    val publisher = estimatedDocumentCount(options)
    return publisher.awaitSingle()
}

/**
 * Gets an estimate of the count of documents in a
 * collection using collection metadata.
 *
 * @param block the options block describing the count
 * @return the estimated number of documents
 * @since 2.0.0
 * @see MonktCollection.estimatedDocumentCount
 */
suspend fun MonktCollection.estimatedDocumentCountSuspend(
    block: EstimatedDocumentCountOptionsScope.() -> Unit = {}
): Long {
    var options = EstimatedDocumentCountOptions()
    options = options.configure(block)
    return estimatedDocumentCountSuspend(options)
}

/* ============= - countDocuments - ============= */

/**
 * Counts the number of documents in the collection
 * according to the given options.
 *
 * @param session the client session with which to associate this operation.
 * @param filter  the query filter.
 * @param options the options describing the count.
 * @return the number of documents.
 * @since 2.0.0
 * @see MonktCollection.countDocuments
 */
suspend fun MonktCollection.countDocumentsSuspend(
    filter: Bson = bdocument,
    session: ClientSession? = null,
    options: CountOptions
): Long {
    val publisher = countDocuments(filter, options, session)
    return publisher.awaitSingle()
}

/**
 * Counts the number of documents in the collection
 * according to the given options.
 *
 * @param session the client session with which to associate this operation.
 * @param filter  the query filter.
 * @param block the options block describing the count.
 * @return the number of documents.
 * @since 2.0.0
 * @see MonktCollection.countDocuments
 */
suspend fun MonktCollection.countDocumentsSuspend(
    filter: Bson = bdocument,
    session: ClientSession? = null,
    block: CountOptionsScope.() -> Unit = {}
): Long {
    var options = CountOptions()
    options = options.configure(block)
    return countDocumentsSuspend(filter, session, options)
}

/**
 * Counts the number of documents in the collection
 * according to the given options.
 *
 * @param session the client session with which to associate this operation.
 * @param filter  the query filter.
 * @param block the options block describing the count.
 * @return the number of documents.
 * @since 2.0.0
 * @see MonktCollection.countDocuments
 */
suspend fun MonktCollection.countDocumentsSuspend(
    filter: BsonDocumentBlock,
    session: ClientSession? = null,
    block: CountOptionsScope.() -> Unit = {}
): Long {
    return countDocumentsSuspend(document(filter), session, block)
}

/* ============= ---- distinct ---- ============= */

/**
 * Gets the distinct values of the specified
 * field name.
 *
 * @param session the client session with which to associate this operation.
 * @param field the field name.
 * @param filter the query filter.
 * @param block the publisher configuration block.
 * @return an iterable of distinct values.
 * @since 2.0.0
 * @see MonktCollection.distinct
 */
suspend fun MonktCollection.distinctSuspend(
    field: String,
    filter: Bson = bdocument,
    session: ClientSession? = null,
    block: DistinctPublisherScope.() -> Unit = {}
): List<BsonDocument> {
    var publisher = distinct(field, filter, session)
    publisher = publisher.configure(block)
    return publisher.asFlow().toList()
}

/**
 * Gets the distinct values of the specified
 * field name.
 *
 * @param session the client session with which to associate this operation.
 * @param field   the field name.
 * @param filter  the query filter.
 * @param block the publisher configuration block.
 * @return an iterable of distinct values.
 * @since 2.0.0
 * @see MonktCollection.distinct
 */
suspend fun MonktCollection.distinctSuspend(
    field: String,
    filter: BsonDocumentBlock,
    session: ClientSession? = null,
    block: DistinctPublisherScope.() -> Unit = {}
): List<BsonDocument> {
    return distinctSuspend(field, document(filter), session, block)
}

/* ============= ------ find ------ ============= */

/**
 * Finds all documents in the collection.
 *
 * @param session the client session with which to associate this operation.
 * @param filter the query filter.
 * @param block the publisher configuration block.
 * @return the found documents.
 * @since 2.0.0
 * @see MonktCollection.find
 */
suspend fun MonktCollection.findSuspend(
    filter: Bson = bdocument,
    session: ClientSession? = null,
    block: FindPublisherScope.() -> Unit = {}
): List<BsonDocument> {
    var publisher = find(filter, session)
    publisher = publisher.configure(block)
    return publisher.asFlow().toList()
}

/**
 * Finds all documents in the collection.
 *
 * @param session the client session with which to associate this operation.
 * @param filter the query filter.
 * @param block the publisher configuration block.
 * @return the found documents.
 * @since 2.0.0
 * @see MonktCollection.find
 */
suspend fun MonktCollection.findSuspend(
    filter: BsonDocumentBlock,
    session: ClientSession? = null,
    block: FindPublisherScope.() -> Unit = {}
): List<BsonDocument> {
    return findSuspend(document(filter), session, block)
}

/* ============= --- aggregate  --- ============= */

/**
 * Aggregates documents according to the specified
 * aggregation pipeline.
 *
 * @param session the client session with which to associate this operation.
 * @param pipeline  the aggregate pipeline.
 * @param block the publisher configuration block.
 * @return the result of the aggregation operation.
 * @since 2.0.0
 * @see MonktCollection.aggregate
 */
suspend fun MonktCollection.aggregateSuspend(
    pipeline: List<Bson>,
    session: ClientSession? = null,
    block: AggregatePublisherScope.() -> Unit = {}
): List<BsonDocument> {
    var publisher = aggregate(pipeline, session)
    publisher = publisher.configure(block)
    return publisher.asFlow().toList()
}

/**
 * Aggregates documents according to the specified
 * aggregation pipeline.
 *
 * @param session the client session with which to associate this operation.
 * @param pipeline the aggregate pipeline.
 * @param block the publisher configuration block.
 * @return the result of the aggregation operation.
 * @since 2.0.0
 * @see MonktCollection.aggregate
 */
suspend fun MonktCollection.aggregateSuspend(
    vararg pipeline: BsonDocumentBlock,
    session: ClientSession? = null,
    block: AggregatePublisherScope.() -> Unit = {}
): List<BsonDocument> {
    return aggregateSuspend(pipeline.map { document(it) }, session, block)
}

/* ============= ----- watch  ----- ============= */

// TODO watch

/* ============= --- bulkWrite  --- ============= */

/**
 * Executes a mix of inserts, updates, replaces,
 * and deletes.
 *
 * @param session the client session with which to associate this operation.
 * @param requests the writes to execute.
 * @param options the options to apply to the bulk write operation.
 * @return the BulkWriteResult.
 * @since 2.0.0
 * @see MonktCollection.bulkWrite
 */
suspend fun MonktCollection.bulkWriteSuspend(
    requests: List<WriteModel<BsonDocument>>,
    session: ClientSession? = null,
    options: BulkWriteOptions
): BulkWriteResult {
    val publisher = bulkWrite(requests, options, session)
    return publisher.awaitSingle()
}

/**
 * Executes a mix of inserts, updates, replaces,
 * and deletes.
 *
 * @param session the client session with which to associate this operation.
 * @param requests the writes to execute.
 * @param block the options to apply to the bulk write operation.
 * @return the BulkWriteResult.
 * @since 2.0.0
 * @see MonktCollection.bulkWrite
 */
suspend fun MonktCollection.bulkWriteSuspend(
    requests: List<WriteModel<BsonDocument>>,
    session: ClientSession? = null,
    block: BulkWriteOptionsScope.() -> Unit = {}
): BulkWriteResult {
    var options = BulkWriteOptions()
    options = options.configure(block)
    return bulkWriteSuspend(requests, session, options)
}

/**
 * Executes a mix of inserts, updates, replaces,
 * and deletes.
 *
 * @param session the client session with which to associate this operation.
 * @param requests the writes to execute.
 * @param block  the options to apply to the bulk write operation.
 * @return the BulkWriteResult.
 * @since 2.0.0
 * @see MonktCollection.bulkWrite
 */
suspend fun MonktCollection.bulkWriteSuspend(
    vararg requests: WriteModel<BsonDocument>,
    session: ClientSession? = null,
    block: BulkWriteOptionsScope.() -> Unit = {}
): BulkWriteResult {
    return bulkWriteSuspend(requests.asList(), session, block)
}

/* ============= --- insertOne  --- ============= */

/**
 * Inserts the provided document. If the document
 * is missing an identifier, the driver should
 * generate one.
 *
 * @param session the client session with which to associate this operation.
 * @param document the document to insert.
 * @param options the options to apply to the operation.
 * @return the InsertOneResult
 * @throws DuplicateKeyException
 * @throws MongoException
 * @since 2.0.0
 * @see MonktCollection.insertOne
 */
suspend fun MonktCollection.insertOneSuspend(
    document: BsonDocument,
    session: ClientSession? = null,
    options: InsertOneOptions
): InsertOneResult {
    val publisher = insertOne(document, options, session)
    return publisher.awaitSingle()
}

/**
 * Inserts the provided document. If the document
 * is missing an identifier, the driver should
 * generate one.
 *
 * @param session the client session with which to associate this operation.
 * @param document the document to insert.
 * @param block the options block to apply to the operation.
 * @return the InsertOneResult
 * @throws DuplicateKeyException
 * @throws MongoException
 * @since 2.0.0
 * @see MonktCollection.insertOne
 */
suspend fun MonktCollection.insertOneSuspend(
    document: BsonDocument,
    session: ClientSession? = null,
    block: InsertOneOptionsScope.() -> Unit = {}
): InsertOneResult {
    var options = InsertOneOptions()
    options = options.configure(block)
    return insertOneSuspend(document, session, options)
}

/**
 * Inserts the provided document. If the document
 * is missing an identifier, the driver should
 * generate one.
 *
 * @param session the client session with which to associate this operation.
 * @param document the document to insert.
 * @param block the options block to apply to the operation.
 * @return the InsertOneResult
 * @throws DuplicateKeyException
 * @throws MongoException
 * @since 2.0.0
 * @see MonktCollection.insertOne
 */
suspend fun MonktCollection.insertOneSuspend(
    document: BsonDocumentBlock,
    session: ClientSession? = null,
    block: InsertOneOptionsScope.() -> Unit = {}
): InsertOneResult {
    return insertOneSuspend(document(document), session, block)
}

/* ============= --- insertMany --- ============= */

/**
 * Inserts a batch of documents.
 *
 * @param session the client session with which to associate this operation.
 * @param documents the documents to insert.
 * @param options the options to apply to the operation.
 * @return the InsertManyResult
 * @throws DuplicateKeyException
 * @throws MongoException
 * @since 2.0.0
 * @see MonktCollection.insertMany
 */
suspend fun MonktCollection.insertManySuspend(
    documents: List<BsonDocument>,
    session: ClientSession? = null,
    options: InsertManyOptions
): InsertManyResult {
    val publisher = insertMany(documents, options, session)
    return publisher.awaitSingle()
}

/**
 * Inserts a batch of documents.
 *
 * @param session the client session with which to associate this operation.
 * @param documents the documents to insert.
 * @param block the options block to apply to the operation.
 * @return the InsertManyResult
 * @throws DuplicateKeyException
 * @throws MongoException
 * @since 2.0.0
 * @see MonktCollection.insertMany
 */
suspend fun MonktCollection.insertManySuspend(
    documents: List<BsonDocument>,
    session: ClientSession? = null,
    block: InsertManyOptionsScope.() -> Unit = {}
): InsertManyResult {
    var options = InsertManyOptions()
    options = options.configure(block)
    return insertManySuspend(documents, session, options)
}

/**
 * Inserts a batch of documents.
 *
 * @param session the client session with which to associate this operation.
 * @param documents the documents to insert.
 * @param block the options block to apply to the operation.
 * @return the InsertManyResult
 * @throws DuplicateKeyException
 * @throws MongoException
 * @since 2.0.0
 * @see MonktCollection.insertMany
 */
suspend fun MonktCollection.insertManySuspend(
    vararg documents: BsonDocumentBlock,
    session: ClientSession? = null,
    block: InsertManyOptionsScope.() -> Unit = {}
): InsertManyResult {
    return insertManySuspend(documents.map { document(it) }, session, block)
}

/* ============= --- deleteOne  --- ============= */

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
 * @throws MongoException
 * @since 2.0.0
 * @see MonktCollection.deleteOne
 */
suspend fun MonktCollection.deleteOneSuspend(
    filter: Bson,
    session: ClientSession? = null,
    options: DeleteOptions
): DeleteResult {
    val publisher = deleteOne(filter, options, session)
    return publisher.awaitSingle()
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
 * @param block the options bloc to apply to the delete operation.
 * @return the DeleteResult.
 * @throws MongoException
 * @since 2.0.0
 * @see MonktCollection.deleteOne
 */
suspend fun MonktCollection.deleteOneSuspend(
    filter: Bson,
    session: ClientSession? = null,
    block: DeleteOptionsScope.() -> Unit = {}
): DeleteResult {
    var options = DeleteOptions()
    options = options.configure(block)
    return deleteOneSuspend(filter, session, options)
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
 * @param block the options bloc to apply to the delete operation.
 * @return the DeleteResult.
 * @throws MongoException
 * @since 2.0.0
 * @see MonktCollection.deleteOne
 */
suspend fun MonktCollection.deleteOneSuspend(
    filter: BsonDocumentBlock,
    session: ClientSession? = null,
    block: DeleteOptionsScope.() -> Unit = {}
): DeleteResult {
    return deleteOneSuspend(document(filter), session, block)
}

/* ============= --- deleteMany --- ============= */

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
 * @throws MongoException
 * @since 2.0.0
 * @see MonktCollection.deleteMany
 */
suspend fun MonktCollection.deleteManySuspend(
    filter: Bson,
    session: ClientSession? = null,
    options: DeleteOptions
): DeleteResult {
    val publisher = deleteMany(filter, options, session)
    return publisher.awaitSingle()
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
 * @param block the options block to apply to the delete operation.
 * @return the DeleteResult.
 * @throws MongoException
 * @since 2.0.0
 * @see MonktCollection.deleteMany
 */
suspend fun MonktCollection.deleteManySuspend(
    filter: Bson,
    session: ClientSession? = null,
    block: DeleteOptionsScope.() -> Unit = {}
): DeleteResult {
    var options = DeleteOptions()
    options = options.configure(block)
    return deleteManySuspend(filter, session, options)
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
 * @param block the options block to apply to the delete operation.
 * @return the DeleteResult.
 * @throws MongoException
 * @since 2.0.0
 * @see MonktCollection.deleteMany
 */
suspend fun MonktCollection.deleteManySuspend(
    filter: BsonDocumentBlock,
    session: ClientSession? = null,
    block: DeleteOptionsScope.() -> Unit = {}
): DeleteResult {
    return deleteManySuspend(document(filter), session, block)
}

/* ============= --- replaceOne --- ============= */

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
 * @see MonktCollection.replaceOne
 */
suspend fun MonktCollection.replaceOneSuspend(
    filter: Bson,
    replacement: BsonDocument,
    session: ClientSession? = null,
    options: ReplaceOptions
): UpdateResult {
    val publisher = replaceOne(filter, replacement, options, session)
    return publisher.awaitSingle()
}

/**
 * Replace a document in the collection according
 * to the specified arguments.
 *
 * @param session the client session with which to associate this operation
 * @param filter the query filter to apply the replace operation
 * @param replacement the replacement document
 * @param block the options block to apply to the replace operation
 * @return the UpdateResult
 * @since 2.0.0
 * @see MonktCollection.replaceOne
 */
suspend fun MonktCollection.replaceOneSuspend(
    filter: Bson,
    replacement: BsonDocument,
    session: ClientSession? = null,
    block: ReplaceOptionsScope.() -> Unit = {}
): UpdateResult {
    var options = ReplaceOptions()
    options = options.configure(block)
    return replaceOneSuspend(filter, replacement, session, options)
}

/**
 * Replace a document in the collection according
 * to the specified arguments.
 *
 * @param session the client session with which to associate this operation
 * @param filter the query filter to apply the replace operation
 * @param replacement the replacement document
 * @param block the options block to apply to the replace operation
 * @return the UpdateResult
 * @since 2.0.0
 * @see MonktCollection.replaceOne
 */
suspend fun MonktCollection.replaceOneSuspend(
    filter: BsonDocumentBlock,
    replacement: BsonDocumentBlock,
    session: ClientSession? = null,
    block: ReplaceOptionsScope.() -> Unit = {}
): UpdateResult {
    return replaceOneSuspend(document(filter), document(replacement), session, block)
}

/* ============= --- updateOne  --- ============= */

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
 * @see MonktCollection.updateOne
 */
suspend fun MonktCollection.updateOneSuspend(
    filter: Bson,
    update: Bson,
    session: ClientSession? = null,
    options: UpdateOptions
): UpdateResult {
    val publisher = updateOne(filter, update, options, session)
    return publisher.awaitSingle()
}

/**
 * Update a single document in the collection
 * according to the specified arguments.
 *
 * @param session the client session with which to associate this operation.
 * @param filter a document describing the query filter, which may not be null.
 * @param update a document describing the update, which may not be null.
 *               The update to apply must include only update operators.
 * @param block the options block to apply to the update operation.
 * @return the UpdateResult
 * @since 2.0.0
 * @see MonktCollection.updateOne
 */
suspend fun MonktCollection.updateOneSuspend(
    filter: Bson,
    update: Bson,
    session: ClientSession? = null,
    block: UpdateOptionsScope.() -> Unit = {}
): UpdateResult {
    var options = UpdateOptions()
    options = options.configure(block)
    return updateOneSuspend(filter, update, session, options)
}

/**
 * Update a single document in the collection
 * according to the specified arguments.
 *
 * @param session the client session with which to associate this operation.
 * @param filter a document describing the query filter, which may not be null.
 * @param update a document describing the update, which may not be null.
 *               The update to apply must include only update operators.
 * @param block the options block to apply to the update operation.
 * @return the UpdateResult
 * @since 2.0.0
 * @see MonktCollection.updateOne
 */
suspend fun MonktCollection.updateOneSuspend(
    filter: BsonDocumentBlock,
    update: BsonDocumentBlock,
    session: ClientSession? = null,
    block: UpdateOptionsScope.() -> Unit = {}
): UpdateResult {
    return updateOneSuspend(document(filter), document(update), session, block)
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
 * @see MonktCollection.updateOne
 */
suspend fun MonktCollection.updateOneSuspend(
    filter: Bson,
    update: List<Bson>,
    session: ClientSession? = null,
    options: UpdateOptions
): UpdateResult {
    val publisher = updateOne(filter, update, options, session)
    return publisher.awaitSingle()
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
 * @param block the options block to apply to the update operation
 * @return the UpdateResult
 * @since 2.0.0
 * @see MonktCollection.updateOne
 */
suspend fun MonktCollection.updateOneSuspend(
    filter: Bson,
    update: List<Bson>,
    session: ClientSession? = null,
    block: UpdateOptionsScope.() -> Unit = {}
): UpdateResult {
    var options = UpdateOptions()
    options = options.configure(block)
    return updateOneSuspend(filter, update, session, options)
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
 * @param block the options block to apply to the update operation
 * @return the UpdateResult
 * @since 2.0.0
 * @see MonktCollection.updateOne
 */
suspend fun MonktCollection.updateOneSuspend(
    filter: BsonDocumentBlock,
    vararg update: BsonDocumentBlock,
    session: ClientSession? = null,
    block: UpdateOptionsScope.() -> Unit = {}
): UpdateResult {
    return updateOneSuspend(document(filter), update.map { document(it) }, session, block)
}

/* ============= --- updateMany --- ============= */

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
 * @see MonktCollection.updateMany
 */
suspend fun MonktCollection.updateManySuspend(
    filter: Bson,
    update: Bson,
    session: ClientSession? = null,
    options: UpdateOptions
): UpdateResult {
    val publisher = updateMany(filter, update, options, session)
    return publisher.awaitSingle()
}

/**
 * Update all documents in the collection
 * according to the specified arguments.
 *
 * @param session the client session with which to associate this operation.
 * @param filter a document describing the query filter, which may not be null.
 * @param update a document describing the update, which may not be null.
 *               The update to apply must include only update operators.
 * @param block the options block to apply to the update operation
 * @return the UpdateResult
 * @since 2.0.0
 * @see MonktCollection.updateMany
 */
suspend fun MonktCollection.updateManySuspend(
    filter: Bson,
    update: Bson,
    session: ClientSession? = null,
    block: UpdateOptionsScope.() -> Unit = {}
): UpdateResult {
    var options = UpdateOptions()
    options = options.configure(block)
    return updateManySuspend(filter, update, session, options)
}

/**
 * Update all documents in the collection
 * according to the specified arguments.
 *
 * @param session the client session with which to associate this operation.
 * @param filter a document describing the query filter, which may not be null.
 * @param update a document describing the update, which may not be null.
 *               The update to apply must include only update operators.
 * @param block the options block to apply to the update operation
 * @return the UpdateResult
 * @since 2.0.0
 * @see MonktCollection.updateMany
 */
suspend fun MonktCollection.updateManySuspend(
    filter: BsonDocumentBlock,
    update: BsonDocumentBlock,
    session: ClientSession? = null,
    block: UpdateOptionsScope.() -> Unit = {}
): UpdateResult {
    return updateManySuspend(document(filter), document(update), session, block)
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
 * @see MonktCollection.updateMany
 */
suspend fun MonktCollection.updateManySuspend(
    filter: Bson,
    update: List<Bson>,
    session: ClientSession? = null,
    options: UpdateOptions
): UpdateResult {
    val publisher = updateMany(filter, update, options, session)
    return publisher.awaitSingle()
}

/**
 * Update all documents in the collection
 * according to the specified arguments.
 *
 * @param session the client session with which to associate this operation.
 * @param filter a document describing the query filter, which may not be null.
 * @param update a pipeline describing the update, which may not be null.
 * @param block the options block to apply to the update operation
 * @return the UpdateResult
 * @since 2.0.0
 * @see MonktCollection.updateMany
 */
suspend fun MonktCollection.updateManySuspend(
    filter: Bson,
    update: List<Bson>,
    session: ClientSession? = null,
    block: UpdateOptionsScope.() -> Unit = {}
): UpdateResult {
    var options = UpdateOptions()
    options = options.configure(block)
    return updateManySuspend(filter, update, session, options)
}

/**
 * Update all documents in the collection
 * according to the specified arguments.
 *
 * @param session the client session with which to associate this operation.
 * @param filter a document describing the query filter, which may not be null.
 * @param update a pipeline describing the update, which may not be null.
 * @param block the options block to apply to the update operation
 * @return the UpdateResult
 * @since 2.0.0
 * @see MonktCollection.updateMany
 */
suspend fun MonktCollection.updateManySuspend(
    filter: BsonDocumentBlock,
    vararg update: BsonDocumentBlock,
    session: ClientSession? = null,
    block: UpdateOptionsScope.() -> Unit = {}
): UpdateResult {
    return updateManySuspend(document(filter), update.map { document(it) }, session, block)
}

/* ============ - findOneAndDelete - ============ */

/**
 * Atomically find a document and remove it.
 *
 * @param session the client session with which to associate this operation.
 * @param filter the query filter to find the document with.
 * @param options the options to apply to the operation.
 * @return the document that was removed.
 *         If no documents matched the query filter, then null will be returned.
 * @since 2.0.0
 * @see MonktCollection.findOneAndDelete
 */
suspend fun MonktCollection.findOneAndDeleteSuspend(
    filter: Bson,
    session: ClientSession? = null,
    options: FindOneAndDeleteOptions
): BsonDocument? {
    val publisher = findOneAndDelete(filter, options, session)
    return publisher.awaitFirstOrNull()
}

/**
 * Atomically find a document and remove it.
 *
 * @param session the client session with which to associate this operation.
 * @param filter the query filter to find the document with.
 * @param block the options block to apply to the operation.
 * @return the document that was removed.
 *         If no documents matched the query filter, then null will be returned.
 * @since 2.0.0
 * @see MonktCollection.findOneAndDelete
 */
suspend fun MonktCollection.findOneAndDeleteSuspend(
    filter: Bson,
    session: ClientSession? = null,
    block: FindOneAndDeleteOptionsScope.() -> Unit = { }
): BsonDocument? {
    var options = FindOneAndDeleteOptions()
    options = options.configure(block)
    return findOneAndDeleteSuspend(filter, session, options)
}

/**
 * Atomically find a document and remove it.
 *
 * @param session the client session with which to associate this operation.
 * @param filter the query filter to find the document with.
 * @param block the options block to apply to the operation.
 * @return the document that was removed.
 *         If no documents matched the query filter, then null will be returned.
 * @since 2.0.0
 * @see MonktCollection.findOneAndDelete
 */
suspend fun MonktCollection.findOneAndDeleteSuspend(
    filter: BsonDocumentBlock,
    session: ClientSession? = null,
    block: FindOneAndDeleteOptionsScope.() -> Unit = { }
): BsonDocument? {
    return findOneAndDeleteSuspend(document(filter), session, block)
}

/* =========== - findOneAndReplace  - =========== */

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
 * @see MonktCollection.findOneAndReplace
 */
suspend fun MonktCollection.findOneAndReplaceSuspend(
    filter: Bson,
    replacement: BsonDocument,
    session: ClientSession? = null,
    options: FindOneAndReplaceOptions
): BsonDocument? {
    val publisher = findOneAndReplace(filter, replacement, options, session)
    return publisher.awaitFirstOrNull()
}

/**
 * Atomically find a document and replace it.
 *
 * @param session the client session with which to associate this operation.
 * @param filter the query filter to apply the replace operation.
 * @param replacement the replacement document.
 * @param block the options block to apply to the operation.
 * @return the document that was replaced.
 *         Depending on the value of the `returnOriginal` property, this will either
 *         be the document as it was before the update or as it is after the update.
 *         If no documents matched the query filter, then null will be returned
 * @since 2.0.0
 * @see MonktCollection.findOneAndReplace
 */
suspend fun MonktCollection.findOneAndReplaceSuspend(
    filter: Bson,
    replacement: BsonDocument,
    session: ClientSession? = null,
    block: FindOneAndReplaceOptionsScope.() -> Unit = {}
): BsonDocument? {
    var options = FindOneAndReplaceOptions()
    options = options.configure(block)
    return findOneAndReplaceSuspend(filter, replacement, session, options)
}

/**
 * Atomically find a document and replace it.
 *
 * @param session the client session with which to associate this operation.
 * @param filter the query filter to apply the replace operation.
 * @param replacement the replacement document.
 * @param block the options block to apply to the operation.
 * @return the document that was replaced.
 *         Depending on the value of the `returnOriginal` property, this will either
 *         be the document as it was before the update or as it is after the update.
 *         If no documents matched the query filter, then null will be returned
 * @since 2.0.0
 * @see MonktCollection.findOneAndReplace
 */
suspend fun MonktCollection.findOneAndReplaceSuspend(
    filter: BsonDocumentBlock,
    replacement: BsonDocumentBlock,
    session: ClientSession? = null,
    block: FindOneAndReplaceOptionsScope.() -> Unit = {}
): BsonDocument? {
    return findOneAndReplaceSuspend(document(filter), document(replacement), session, block)
}

/* ============ - findOneAndUpdate - ============ */

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
 * @see MonktCollection.findOneAndUpdate
 */
suspend fun MonktCollection.findOneAndUpdateSuspend(
    filter: Bson,
    update: Bson,
    session: ClientSession? = null,
    options: FindOneAndUpdateOptions
): BsonDocument? {
    val publisher = findOneAndUpdate(filter, update, options, session)
    return publisher.awaitFirstOrNull()
}

/**
 * Atomically find a document and update it.
 *
 * @param session the client session with which to associate this operation.
 * @param filter  a document describing the query filter, which may not be null.
 * @param update  a document describing the update, which may not be null.
 *                The update to apply must include only update operators.
 * @param block the options block to apply to the operation.
 * @return the document that was updated.
 *         Depending on the value of the `returnOriginal` property, this will either be
 *         the document as it was before the update or as it is after the update.
 *         If no documents matched the query filter, then null will be returned.
 * @since 2.0.0
 * @see MonktCollection.findOneAndUpdate
 */
suspend fun MonktCollection.findOneAndUpdateSuspend(
    filter: Bson,
    update: Bson,
    session: ClientSession? = null,
    block: FindOneAndUpdateOptionsScope.() -> Unit = {}
): BsonDocument? {
    var options = FindOneAndUpdateOptions()
    options = options.configure(block)
    return findOneAndUpdateSuspend(filter, update, session, options)
}

/**
 * Atomically find a document and update it.
 *
 * @param session the client session with which to associate this operation.
 * @param filter  a document describing the query filter, which may not be null.
 * @param update  a document describing the update, which may not be null.
 *                The update to apply must include only update operators.
 * @param block the options block to apply to the operation.
 * @return the document that was updated.
 *         Depending on the value of the `returnOriginal` property, this will either be
 *         the document as it was before the update or as it is after the update.
 *         If no documents matched the query filter, then null will be returned.
 * @since 2.0.0
 * @see MonktCollection.findOneAndUpdate
 */
suspend fun MonktCollection.findOneAndUpdateSuspend(
    filter: BsonDocumentBlock,
    update: BsonDocumentBlock,
    session: ClientSession? = null,
    block: FindOneAndUpdateOptionsScope.() -> Unit = {}
): BsonDocument? {
    return findOneAndUpdateSuspend(document(filter), document(update), session, block)
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
 * @see MonktCollection.findOneAndUpdate
 */
suspend fun MonktCollection.findOneAndUpdateSuspend(
    filter: Bson,
    update: List<Bson>,
    session: ClientSession? = null,
    options: FindOneAndUpdateOptions
): BsonDocument? {
    val publisher = findOneAndUpdate(filter, update, options, session)
    return publisher.awaitFirstOrNull()
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
 * @param block the options block to apply to the operation.
 * @return the document that was updated.
 *         Depending on the value of the `returnOriginal` property, this will either be
 *         the document as it was before the update or as it is after the update.
 *         If no documents matched the query filter, then null will be returned.
 * @since 2.0.0
 * @see MonktCollection.findOneAndUpdate
 */
suspend fun MonktCollection.findOneAndUpdateSuspend(
    filter: Bson,
    update: List<Bson>,
    session: ClientSession? = null,
    block: FindOneAndUpdateOptionsScope.() -> Unit = {}
): BsonDocument? {
    var options = FindOneAndUpdateOptions()
    options = options.configure(block)
    return findOneAndUpdateSuspend(filter, update, session, options)
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
 * @param block the options block to apply to the operation.
 * @return the document that was updated.
 *         Depending on the value of the `returnOriginal` property, this will either be
 *         the document as it was before the update or as it is after the update.
 *         If no documents matched the query filter, then null will be returned.
 * @since 2.0.0
 * @see MonktCollection.findOneAndUpdate
 */
suspend fun MonktCollection.findOneAndUpdateSuspend(
    filter: BsonDocumentBlock,
    vararg update: BsonDocumentBlock,
    session: ClientSession? = null,
    block: FindOneAndUpdateOptionsScope.() -> Unit = {}
): BsonDocument? {
    return findOneAndUpdateSuspend(document(filter), update.map { document(it) }, session, block)
}

/* ============= ------ drop ------ ============= */

/**
 * Drops this collection from the Database.
 *
 * @param session the client session with which to associate this operation.
 * @since 2.0.0
 * @see MonktCollection.drop
 */
suspend fun MonktCollection.dropSuspend(
    session: ClientSession? = null
) {
    val publisher = drop(session)
    publisher.awaitFirstOrNull()
}

/* ============= -- createIndex  -- ============= */

/**
 * Creates an index.
 *
 * @param session the client session with which to associate this operation.
 * @param key an object describing the index key(s), which may not be null.
 * @param options the options for the index.
 * @since 2.0.0
 * @see MonktCollection.createIndex
 */
suspend fun MonktCollection.createIndexSuspend(
    key: Bson,
    session: ClientSession? = null,
    options: IndexOptions
): String {
    val publisher = createIndex(key, options, session)
    return publisher.awaitSingle()
}

/**
 * Creates an index.
 *
 * @param session the client session with which to associate this operation.
 * @param key an object describing the index key(s), which may not be null.
 * @param block the options block for the index.
 * @since 2.0.0
 * @see MonktCollection.createIndex
 */
suspend fun MonktCollection.createIndexSuspend(
    key: Bson,
    session: ClientSession? = null,
    block: IndexOptionsScope.() -> Unit = {}
): String {
    var options = IndexOptions()
    options = options.configure(block)
    return createIndexSuspend(key, session, options)
}

/**
 * Creates an index.
 *
 * @param session the client session with which to associate this operation.
 * @param key an object describing the index key(s), which may not be null.
 * @param block the options block for the index.
 * @since 2.0.0
 * @see MonktCollection.createIndex
 */
suspend fun MonktCollection.createIndexSuspend(
    key: BsonDocumentBlock,
    session: ClientSession? = null,
    block: IndexOptionsScope.() -> Unit = {}
): String {
    return createIndexSuspend(document(key), session, block)
}

/* ============= - createIndexes  - ============= */

/**
 * Create multiple indexes.
 *
 * @param session the client session with which to associate this operation.
 * @param indexes the list of indexes.
 * @param options options to use when creating indexes.
 * @since 2.0.0
 * @see MonktCollection.createIndexes
 */
suspend fun MonktCollection.createIndexesSuspend(
    indexes: List<IndexModel>,
    session: ClientSession? = null,
    options: CreateIndexOptions
): List<String> {
    val publisher = createIndexes(indexes, options, session)
    return publisher.asFlow().toList()
}

/**
 * Create multiple indexes.
 *
 * @param session the client session with which to associate this operation.
 * @param indexes the list of indexes.
 * @param block options block to use when creating indexes.
 * @since 2.0.0
 * @see MonktCollection.createIndexes
 */
suspend fun MonktCollection.createIndexesSuspend(
    indexes: List<IndexModel>,
    session: ClientSession? = null,
    block: CreateIndexOptionsScope.() -> Unit = {}
): List<String> {
    var options = CreateIndexOptions()
    options = options.configure(block)
    return createIndexesSuspend(indexes, session, options)
}

/**
 * Create multiple indexes.
 *
 * @param session the client session with which to associate this operation.
 * @param indexes the list of indexes.
 * @param block options block to use when creating indexes.
 * @since 2.0.0
 * @see MonktCollection.createIndexes
 */
suspend fun MonktCollection.createIndexesSuspend(
    vararg indexes: IndexModel,
    session: ClientSession? = null,
    block: CreateIndexOptionsScope.() -> Unit = {}
): List<String> {
    return createIndexesSuspend(indexes.asList(), session, block)
}

/* ============= -- listIndexes  -- ============= */

/**
 * Get all the indexes in this collection.
 *
 * @param session the client session with which to associate this operation.
 * @param block the publisher configuration block.
 * @return the indexes.
 * @since 2.0.0
 * @see MonktCollection.listIndexes
 */
suspend fun MonktCollection.listIndexesSuspend(
    session: ClientSession? = null,
    block: ListIndexesPublisherScope.() -> Unit = {}
): List<BsonDocument> {
    var publisher = listIndexes(session)
    publisher = publisher.configure(block)
    return publisher.asFlow().toList()
}

/* ============= --- dropIndex  --- ============= */

/**
 * Drops the given index.
 *
 * @param session the client session with which to associate this operation.
 * @param name the name of the index to remove.
 * @param options options to use when dropping indexes.
 * @since 2.0.0
 * @see MonktCollection.dropIndex
 */
suspend fun MonktCollection.dropIndexSuspend(
    name: String,
    session: ClientSession? = null,
    options: DropIndexOptions
) {
    val publisher = dropIndex(name, options, session)
    publisher.awaitFirstOrNull()
}

/**
 * Drops the given index.
 *
 * @param session the client session with which to associate this operation.
 * @param name the name of the index to remove.
 * @param block options block to use when dropping indexes.
 * @since 2.0.0
 * @see MonktCollection.dropIndex
 */
suspend fun MonktCollection.dropIndexSuspend(
    name: String,
    session: ClientSession? = null,
    block: DropIndexOptionsScope.() -> Unit = {}
) {
    var options = DropIndexOptions()
    options = options.configure(block)
    dropIndexSuspend(name, session, options)
}

/**
 * Drops the index given the keys used to create it.
 *
 * @param session the client session with which to associate this operation.
 * @param keys the keys of the index to remove.
 * @param options options to use when dropping indexes.
 * @since 2.0.0
 * @see MonktCollection.dropIndex
 */
suspend fun MonktCollection.dropIndexSuspend(
    keys: Bson,
    session: ClientSession? = null,
    options: DropIndexOptions
) {
    val publisher = dropIndex(keys, options, session)
    publisher.awaitFirstOrNull()
}

/**
 * Drops the index given the keys used to create it.
 *
 * @param session the client session with which to associate this operation.
 * @param keys the keys of the index to remove.
 * @param block options block to use when dropping indexes.
 * @since 2.0.0
 * @see MonktCollection.dropIndex
 */
suspend fun MonktCollection.dropIndexSuspend(
    keys: Bson,
    session: ClientSession? = null,
    block: DropIndexOptionsScope.() -> Unit = {}
) {
    var options = DropIndexOptions()
    options = options.configure(block)
    dropIndexSuspend(keys, session, options)
}

/**
 * Drops the index given the keys used to create it.
 *
 * @param session the client session with which to associate this operation.
 * @param keys the keys of the index to remove.
 * @param block options block to use when dropping indexes.
 * @since 2.0.0
 * @see MonktCollection.dropIndex
 */
suspend fun MonktCollection.dropIndexSuspend(
    keys: BsonDocumentBlock,
    session: ClientSession? = null,
    block: DropIndexOptionsScope.() -> Unit = {}
) {
    dropIndexSuspend(document(keys), session, block)
}

/* ============= -- dropIndexes  -- ============= */

/**
 * Drop all the indexes on this collection, except
 * for the default on _id.
 *
 * @param session the client session with which to associate this operation.
 * @param options options to use when dropping indexes.
 * @since 2.0.0
 * @see MonktCollection.dropIndexes
 */
suspend fun MonktCollection.dropIndexesSuspend(
    session: ClientSession? = null,
    options: DropIndexOptions
) {
    val publisher = dropIndexes(options, session)
    publisher.awaitFirstOrNull()
}

/**
 * Drop all the indexes on this collection, except
 * for the default on _id.
 *
 * @param session the client session with which to associate this operation.
 * @param block options block to use when dropping indexes.
 * @since 2.0.0
 * @see MonktCollection.dropIndexes
 */
suspend fun MonktCollection.dropIndexesSuspend(
    session: ClientSession? = null,
    block: DropIndexOptionsScope.() -> Unit = {}
) {
    var options = DropIndexOptions()
    options = options.configure(block)
    dropIndexesSuspend(session, options)
}

/* ============ - renameCollection - ============ */

/**
 * Rename the collection with oldCollectionName to
 * the newCollectionName.
 *
 * @param session the client session with which to associate this operation.
 * @param namespace the name the collection will be renamed to.
 * @param options the options for renaming a collection.
 * @since 2.0.0
 * @see MonktCollection.renameCollection
 */
suspend fun MonktCollection.renameCollectionSuspend(
    namespace: MongoNamespace,
    session: ClientSession? = null,
    options: RenameCollectionOptions
) {
    val publisher = renameCollection(namespace, options, session)
    publisher.awaitFirstOrNull()
}

/**
 * Rename the collection with oldCollectionName to
 * the newCollectionName.
 *
 * @param session the client session with which to associate this operation.
 * @param namespace the name the collection will be renamed to.
 * @param block the options block for renaming a collection.
 * @since 2.0.0
 * @see MonktCollection.renameCollection
 */
suspend fun MonktCollection.renameCollectionSuspend(
    namespace: MongoNamespace,
    session: ClientSession? = null,
    block: RenameCollectionOptionsScope.() -> Unit = {}
) {
    var options = RenameCollectionOptions()
    options = options.configure(block)
    renameCollectionSuspend(namespace, session, options)
}

/* ============= ------------------ ============= */

/* ============= ---- findOne  ---- ============= */

/**
 * Finds the first document in the collection.
 *
 * @param session the client session with which to associate this operation.
 * @param filter the query filter.
 * @param block the find options block
 * @return the first document.
 * @since 2.0.0
 * @see MonktCollection.find
 */
suspend fun MonktCollection.findOneSuspend(
    filter: Bson = bdocument,
    session: ClientSession? = null,
    block: FindPublisherScope.() -> Unit = {}
): BsonDocument? {
    var publisher = find(filter, session)
    publisher = publisher.configure(block)
    return publisher.first().awaitFirstOrNull()
}

/**
 * Finds the first document in the collection.
 *
 * @param session the client session with which to associate this operation.
 * @param filter the query filter.
 * @param block the find options block
 * @return the first document.
 * @since 2.0.0
 * @see MonktCollection.find
 */
suspend fun MonktCollection.findOneSuspend(
    filter: BsonDocumentBlock,
    session: ClientSession? = null,
    block: FindPublisherScope.() -> Unit = {}
): BsonDocument? {
    return findOneSuspend(document(filter), session, block)
}

/* ============= -- ensureIndex  -- ============= */

/**
 * Creates an index.
 * If the index already exists, remove the existing
 * index and recreate it.
 *
 * @param session the client session with which to associate this operation.
 * @param key an object describing the index key(s), which may not be null.
 * @param options the options for the index.
 * @since 2.0.0
 * @see MonktCollection.dropIndex
 * @see MonktCollection.createIndex
 */
suspend fun MonktCollection.ensureIndexSuspend(
    key: Bson,
    session: ClientSession? = null,
    options: IndexOptions
): String {
    return try {
        createIndexSuspend(key, session, options)
    } catch (e: MongoCommandException) {
        dropIndexSuspend(key)
        createIndexSuspend(key, session, options)
    }
}

/**
 * Creates an index.
 * If the index already exists, remove the existing
 * index and recreate it.
 *
 * @param session the client session with which to associate this operation.
 * @param key an object describing the index key(s), which may not be null.
 * @param block the options block for the index.
 * @since 2.0.0
 * @see MonktCollection.dropIndex
 * @see MonktCollection.createIndex
 */
suspend fun MonktCollection.ensureIndexSuspend(
    key: Bson,
    session: ClientSession? = null,
    block: IndexOptionsScope.() -> Unit = {}
): String {
    var options = IndexOptions()
    options = options.configure(block)
    return ensureIndexSuspend(key, session, options)
}

/**
 * Creates an index.
 * If the index already exists, remove the existing
 * index and recreate it.
 *
 * @param session the client session with which to associate this operation.
 * @param key an object describing the index key(s), which may not be null.
 * @param block the options block for the index.
 * @since 2.0.0
 * @see MonktCollection.dropIndex
 * @see MonktCollection.createIndex
 */
suspend fun MonktCollection.ensureIndexSuspend(
    key: BsonDocumentBlock,
    session: ClientSession? = null,
    block: IndexOptionsScope.() -> Unit = {}
): String {
    return try {
        createIndexSuspend(key, session, block)
    } catch (e: MongoCommandException) {
        dropIndexSuspend(key)
        createIndexSuspend(key, session, block)
    }
}

/* ============= --- aggregate  --- ============= */

/**
 * Perform a bulk aggregation in all the
 * collections in [this] list.
 *
 * The order of the given [pipelines] must match
 * the order of the collections in [this] list.
 *
 * The result is a list of collection-index/document
 * pairs. If a document can't be mapped to a
 * collection, it will be mapped to `-1`.
 *
 * @param pipelines the pipelines foreach collection.
 * @param pipeline the pipeline operations to be
 *                 performed on the combined
 *                 documents.
 * @return the aggregation results.
 * @since 2.0.0
 * @see MonktCollection.aggregate
 */
suspend fun List<MonktCollection>.aggregateSuspend(
    pipelines: List<List<BsonDocument>>,
    pipeline: List<BsonDocument> = emptyList(),
    session: ClientSession? = null,
    block: AggregatePublisherScope.() -> Unit = {}
): List<Pair<Int, BsonDocument>> {
    @Suppress("LocalVariableName")
    val INDEX_FIELD_NAME = "_monkt_temp_"

    require(pipelines.size == size) {
        "List aggregation pipelines size mismatch: " +
        "expected: $size; " +
        "actual: ${pipelines.size}"
    }

    val pairs = zip(pipelines).mapIndexed { index, (collection, pipeline) ->
        collection to array {
            byAll(pipeline)
            by { `$addFields` by { INDEX_FIELD_NAME by index } }
        }
    }

    val productCollection = first()
    val productPipeline = array {
        byAll(pairs.first().second)
        pairs.drop(1).forEach { (collection, pipeline) ->
            by {
                `$unionWith` by {
                    "coll" by collection.namespace.collectionName
                    "pipeline" by pipeline
                }
            }
        }
        byAll(pipeline)
    }

    @Suppress("UNCHECKED_CAST")
    val data = productCollection.aggregateSuspend(
        productPipeline as List<Bson>,
        session,
        block
    )

    return data.map {
        val index = it.remove(INDEX_FIELD_NAME) as? BsonInt32
        (index?.value ?: -1) to it
    }
}

/**
 * Perform a bulk aggregation in all the
 * collections in [this] list.
 *
 * The order of the given [pipelines] must match
 * the order of the collections in [this] list.
 *
 * @param pipelines the pipelines foreach collection.
 * @param pipeline the pipeline operations to be
 *                 performed on the combined
 *                 documents.
 * @return a list of pairs with each pair
 *         containing the index of the collection
 *         and the document.
 * @since 2.0.0
 * @see MonktCollection.aggregate
 */
suspend fun List<MonktCollection>.aggregateSuspend(
    pipelines: List<BsonArrayBlock>,
    pipeline: BsonArrayBlock = {},
    session: ClientSession? = null,
    block: AggregatePublisherScope.() -> Unit = {}
): List<Pair<Int, BsonDocument>> {
    return aggregateSuspend(
        pipelines = pipelines.map { array(it).map { it as BsonDocument } },
        pipeline = array(pipeline).map { it as BsonDocument },
        session = session,
        block = block
    )
}

/**
 * Perform a bulk aggregation in all the
 * collections in [this] list.
 *
 * The order of the given [pipelines] must match
 * the order of the collections in [this] list.
 * It is allowed to add one additional item to be
 * the pipeline for operations to be performed on
 * the combined documents.
 *
 * @param pipelines the pipelines foreach collection
 *                 and an optional item: pipeline
 *                 operations to be performed on
 *                 the combined documents.
 * @return a list of pairs with each pair
 *         containing the index of the collection
 *         and the document.
 * @since 2.0.0
 * @see MonktCollection.aggregate
 */
suspend fun List<MonktCollection>.aggregateSuspend(
    vararg pipelines: BsonArrayBlock,
    session: ClientSession? = null,
    block: AggregatePublisherScope.() -> Unit = {}
): List<Pair<Int, BsonDocument>> {
    val range = size..size + 1
    require(pipelines.size in range) {
        "List aggregation vararg pipelines size mismatch: " +
        "expected: $range ; " +
        "actual: ${pipelines.size}"
    }
    return aggregateSuspend(
        pipelines = pipelines.take(size),
        pipeline = pipelines.getOrElse(size) { {} },
        session = session,
        block = block
    )
}

/**
 * Perform a bulk aggregation passed as
 * collection/pipeline pairs [pipelines].
 *
 * @param pipelines pairs of the collection and its
 *                   aggregation.
 * @param pipeline the pipeline operations to be
 *                 performed on the combined
 *                 documents.
 * @return a list of pairs with each pair
 *         containing the index of the collection
 *         and the document.
 * @since 2.0.0
 * @see MonktCollection.aggregateSuspend
 */
suspend fun aggregateSuspend(
    pipelines: List<Pair<MonktCollection, BsonArray>>,
    pipeline: BsonArray = barray,
    session: ClientSession? = null,
    block: AggregatePublisherScope.() -> Unit = {}
): List<Pair<Int, BsonDocument>> {
    val (collectionList, pipelineList) = pipelines.unzip()
    return collectionList.aggregateSuspend(
        pipelines = pipelineList.map { it.map { it as BsonDocument } },
        pipeline = pipeline.map { it as BsonDocument },
        session = session,
        block = block
    )
}
