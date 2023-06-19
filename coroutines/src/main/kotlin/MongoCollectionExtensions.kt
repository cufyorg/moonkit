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

import org.cufy.bson.*

/* ============= ------------------ ============= */

/**
 * Removes at most one document from the
 * collection that has the given id.
 *
 * If no documents match, the collection is not
 * modified.
 *
 * @param session the client session with which to associate this operation.
 * @param id the id of the document to be deleted.
 * @param options the options to apply to the delete operation.
 * @return the DeleteResult.
 * @throws com.mongodb.MongoException
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.deleteOne
 */
suspend fun MongoCollection.deleteOneById(
    id: AnyId,
    options: DeleteOptions = DeleteOptions(),
    session: ClientSession? = null
): DeleteResult {
    return deleteOne(
        BsonDocument { "_id" by id },
        options,
        session
    )
}

/**
 * Removes at most one document from the
 * collection that has the given id.
 *
 * If no documents match, the collection is not
 * modified.
 *
 * @param session the client session with which to associate this operation.
 * @param id the id of the document to be deleted.
 * @param options the options to apply to the delete operation.
 * @return the DeleteResult.
 * @throws com.mongodb.MongoException
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.deleteOne
 */
suspend fun MongoCollection.deleteOneById(
    id: AnyId,
    session: ClientSession? = null,
    options: DeleteOptions.() -> Unit = {}
) = deleteOneById(id, DeleteOptions(options), session)

//

/**
 * Update a single document in the collection
 * according to the specified arguments.
 *
 * @param session the client session with which to associate this operation.
 * @param id the id of the document.
 * @param update a document describing the update, which may not be null.
 *               The update to apply must include only update operators.
 * @param options the options to apply to the update operation.
 * @return the UpdateResult
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.updateOne
 */
suspend fun MongoCollection.updateOneById(
    id: AnyId,
    update: BsonDocument,
    options: UpdateOptions = UpdateOptions(),
    session: ClientSession? = null
): UpdateResult {
    return updateOne(
        BsonDocument { "_id" by id },
        update,
        options,
        session
    )
}

/**
 * Update a single document in the collection
 * according to the specified arguments.
 *
 * @param session the client session with which to associate this operation.
 * @param id the id of the document.
 * @param update a document describing the update, which may not be null.
 *               The update to apply must include only update operators.
 * @param options the options to apply to the update operation.
 * @return the UpdateResult
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.updateOne
 */
suspend fun MongoCollection.updateOneById(
    id: AnyId,
    update: BsonDocumentBlock,
    session: ClientSession? = null,
    options: UpdateOptions.() -> Unit = {}
) = updateOneById(id, BsonDocument(update), UpdateOptions(options), session)

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
 * @param id the id of the document.
 * @param update a pipeline describing the update, which may not be null.
 * @param options the options to apply to the update operation
 * @return the UpdateResult
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.updateOne
 */
suspend fun MongoCollection.updateOneById(
    id: AnyId,
    update: List<BsonDocument>,
    options: UpdateOptions = UpdateOptions(),
    session: ClientSession? = null
): UpdateResult {
    return updateOne(
        BsonDocument { "_id" by id },
        update,
        options,
        session
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
 * @param id the id of the document.
 * @param update a pipeline describing the update, which may not be null.
 * @param options the options to apply to the update operation
 * @return the UpdateResult
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.updateOne
 */
suspend fun MongoCollection.updateOneById(
    id: AnyId,
    vararg update: BsonDocumentBlock,
    session: ClientSession? = null,
    options: UpdateOptions.() -> Unit = {}
) = updateOneById(id, update.map { BsonDocument(it) }, UpdateOptions(options), session)

//

/**
 * Replace a document in the collection according
 * to the specified arguments.
 *
 * @param session the client session with which to associate this operation
 * @param id the id of the document
 * @param replacement the replacement document
 * @param options the options to apply to the replace operation
 * @return the UpdateResult
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.replaceOne
 */
suspend fun MongoCollection.replaceOneById(
    id: AnyId,
    replacement: BsonDocument,
    options: ReplaceOptions = ReplaceOptions(),
    session: ClientSession? = null
): UpdateResult {
    return replaceOne(
        BsonDocument { "_id" by id },
        replacement,
        options,
        session
    )
}

/**
 * Replace a document in the collection according
 * to the specified arguments.
 *
 * @param session the client session with which to associate this operation
 * @param id the id of the document
 * @param replacement the replacement document
 * @param options the options to apply to the replace operation
 * @return the UpdateResult
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.replaceOne
 */
suspend fun MongoCollection.replaceOneById(
    id: AnyId,
    replacement: BsonDocumentBlock,
    session: ClientSession? = null,
    options: ReplaceOptions.() -> Unit = {}
) = replaceOneById(id, BsonDocument(replacement), ReplaceOptions(options), session)

/* ============= ------------------ ============= */

/**
 * Atomically find a document and remove it.
 *
 * @param session the client session with which to associate this operation.
 * @param id the id of the document.
 * @param options the options to apply to the operation.
 * @return the document that was removed.
 *         If no documents matched the query filter, then null will be returned.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.findOneAndDelete
 */
suspend fun MongoCollection.findOneByIdAndDelete(
    id: AnyId,
    options: FindOneAndDeleteOptions = FindOneAndDeleteOptions(),
    session: ClientSession? = null
): BsonDocument? {
    return findOneAndDelete(
        BsonDocument { "_id" by id },
        options,
        session
    )
}

/**
 * Atomically find a document and remove it.
 *
 * @param session the client session with which to associate this operation.
 * @param id the id of the document.
 * @param options the options to apply to the operation.
 * @return the document that was removed.
 *         If no documents matched the query filter, then null will be returned.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.findOneAndDelete
 */
suspend fun MongoCollection.findOneByIdAndDelete(
    id: AnyId,
    session: ClientSession? = null,
    options: FindOneAndDeleteOptions.() -> Unit
) = findOneByIdAndDelete(id, FindOneAndDeleteOptions(options), session)

//

/**
 * Atomically find a document and replace it.
 *
 * @param session the client session with which to associate this operation.
 * @param id the id of the document.
 * @param replacement the replacement document.
 * @param options the options to apply to the operation.
 * @return the document that was replaced.
 *         Depending on the value of the `returnOriginal` property, this will either
 *         be the document as it was before the update or as it is after the update.
 *         If no documents matched the query filter, then null will be returned
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.findOneAndReplace
 */
suspend fun MongoCollection.findOneByIdAndReplace(
    id: AnyId,
    replacement: BsonDocument,
    options: FindOneAndReplaceOptions = FindOneAndReplaceOptions(),
    session: ClientSession? = null
): BsonDocument? {
    return findOneAndReplace(
        BsonDocument { "_id" by id },
        replacement,
        options,
        session
    )
}

/**
 * Atomically find a document and replace it.
 *
 * @param session the client session with which to associate this operation.
 * @param id the id of the document.
 * @param replacement the replacement document.
 * @param options the options to apply to the operation.
 * @return the document that was replaced.
 *         Depending on the value of the `returnOriginal` property, this will either
 *         be the document as it was before the update or as it is after the update.
 *         If no documents matched the query filter, then null will be returned
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.findOneAndReplace
 */
suspend fun MongoCollection.findOneByIdAndReplace(
    id: AnyId,
    replacement: BsonDocumentBlock,
    session: ClientSession? = null,
    options: FindOneAndReplaceOptions.() -> Unit = {}
) = findOneByIdAndReplace(id, BsonDocument(replacement), FindOneAndReplaceOptions(options), session)

//

/**
 * Atomically find a document and update it.
 *
 * @param session the client session with which to associate this operation.
 * @param id the id of the document.
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
suspend fun MongoCollection.findOneByIdAndUpdate(
    id: AnyId,
    update: BsonDocument,
    options: FindOneAndUpdateOptions = FindOneAndUpdateOptions(),
    session: ClientSession? = null
): BsonDocument? {
    return findOneAndUpdate(
        BsonDocument { "_id" by id },
        update,
        options,
        session
    )
}

/**
 * Atomically find a document and update it.
 *
 * @param session the client session with which to associate this operation.
 * @param id the id of the document.
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
suspend fun MongoCollection.findOneByIdAndUpdate(
    id: AnyId,
    update: BsonDocumentBlock,
    session: ClientSession? = null,
    options: FindOneAndUpdateOptions.() -> Unit = {}
) = findOneByIdAndUpdate(id, BsonDocument(update), FindOneAndUpdateOptions(options), session)

//

/**
 * Atomically find a document and update it.
 *
 * Note: Supports retryable writes on MongoDB
 * server versions 3.6 or higher when the
 * retryWrites setting is enabled.
 *
 * @param session the client session with which to associate this operation.
 * @param id the id of the document.
 * @param update a pipeline describing the update, which may not be null.
 * @param options the options to apply to the operation.
 * @return the document that was updated.
 *         Depending on the value of the `returnOriginal` property, this will either be
 *         the document as it was before the update or as it is after the update.
 *         If no documents matched the query filter, then null will be returned.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.findOneAndUpdate
 */
suspend fun MongoCollection.findOneByIdAndUpdate(
    id: AnyId,
    update: List<BsonDocument>,
    options: FindOneAndUpdateOptions = FindOneAndUpdateOptions(),
    session: ClientSession? = null
): BsonDocument? {
    return findOneAndUpdate(
        BsonDocument { "_id" by id },
        update,
        options,
        session
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
 * @param id the id of the document.
 * @param update a pipeline describing the update, which may not be null.
 * @param options the options to apply to the operation.
 * @return the document that was updated.
 *         Depending on the value of the `returnOriginal` property, this will either be
 *         the document as it was before the update or as it is after the update.
 *         If no documents matched the query filter, then null will be returned.
 * @since 2.0.0
 * @see com.mongodb.client.MongoCollection.findOneAndUpdate
 */
suspend fun MongoCollection.findOneByIdAndUpdate(
    id: AnyId,
    vararg update: BsonDocumentBlock,
    session: ClientSession? = null,
    options: FindOneAndUpdateOptions.() -> Unit = {}
) = findOneByIdAndUpdate(id, update.map { BsonDocument(it) }, FindOneAndUpdateOptions(options), session)

/* ============= ------------------ ============= */

/**
 * Finds the first document in the collection.
 *
 * @param session the client session with which to associate this operation.
 * @param filter the query filter.
 * @param options the operation options.
 * @return the first document.
 * @since 2.0.0
 * @see MongoCollection.find
 */
suspend fun MongoCollection.findOne(
    filter: BsonDocument = EmptyBsonDocument,
    options: FindOptions = FindOptions(),
    session: ClientSession? = null
): BsonDocument? {
    return find(
        filter,
        options.copy(limit = 1),
        session
    ).firstOrNull()
}

/**
 * Finds the first document in the collection.
 *
 * @param session the client session with which to associate this operation.
 * @param filter the query filter.
 * @param options the operation options.
 * @return the first document.
 * @since 2.0.0
 * @see MongoCollection.find
 */
suspend fun MongoCollection.findOne(
    filter: BsonDocumentBlock,
    session: ClientSession? = null,
    options: FindOptions.() -> Unit = {}
) = findOne(BsonDocument(filter), FindOptions(options), session)

//

/**
 * Finds the first document in the collection.
 *
 * @param session the client session with which to associate this operation.
 * @param id the id of the document.
 * @param options the operation options.
 * @return the first document.
 * @since 2.0.0
 * @see MongoCollection.find
 */
suspend fun MongoCollection.findOneById(
    id: AnyId,
    options: FindOptions = FindOptions(),
    session: ClientSession? = null
): BsonDocument? {
    return findOne(
        BsonDocument { "_id" by id },
        options,
        session
    )
}

/**
 * Finds the first document in the collection.
 *
 * @param session the client session with which to associate this operation.
 * @param id the id of the document.
 * @param options the operation options.
 * @return the first document.
 * @since 2.0.0
 * @see MongoCollection.find
 */
suspend fun MongoCollection.findOneById(
    id: AnyId,
    session: ClientSession? = null,
    options: FindOptions.() -> Unit
) = findOneById(id, FindOptions(options), session)

/* ============= ------------------ ============= */

/**
 * Creates an index.
 * If the index already exists, remove the existing
 * index and recreate it.
 *
 * @param keys an object describing the index key(s), which may not be null.
 * @param options the options for the index.
 * @param session the client session with which to associate this operation.
 * @since 2.0.0
 * @see MongoCollection.dropIndex
 * @see MongoCollection.createIndex
 */
suspend fun MongoCollection.ensureIndex(
    keys: BsonDocument,
    options: CreateIndexOptions = CreateIndexOptions(),
    session: ClientSession? = null
): String {
    return try {
        createIndex(keys, options, session)
    } catch (e: com.mongodb.MongoCommandException) {
        dropIndex(keys)
        createIndex(keys, options, session)
    }
}

/**
 * Creates an index.
 * If the index already exists, remove the existing
 * index and recreate it.
 *
 * @param keys an object describing the index key(s), which may not be null.
 * @param options the options for the index.
 * @param session the client session with which to associate this operation.
 * @since 2.0.0
 * @see MongoCollection.dropIndex
 * @see MongoCollection.createIndex
 */
suspend fun MongoCollection.ensureIndex(
    keys: BsonDocumentBlock,
    session: ClientSession? = null,
    options: CreateIndexOptions.() -> Unit = {}
) = ensureIndex(BsonDocument(keys), CreateIndexOptions(options), session)

/* ============= ------------------ ============= */
