/*
 *	Copyright 2023 cufy.org
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
package org.cufy.moonkit

import kotlinx.coroutines.*
import org.cufy.bson.BsonDocument
import org.cufy.bson.BsonElement
import org.cufy.mongodb.*

/* ============= ------------------ ============= */

/**
 * A type of operations that is focused on [MongoCollection].
 * This type of operations have a default behaviour that only
 * require an instance of [MongoCollection].
 *
 * @author LSafer
 * @since 2.0.0
 */
interface CollectionOperation<T> : Operation<T> {
    /**
     * The name of the database of the collection
     * to operate on.
     * Set to `null` to use [OpClient.defaultDatabase].
     *
     * @since 2.0.0
     */
    val database: String?

    /**
     * The name of the collection this operation
     * is targeting.
     */
    val collection: String

    /**
     * The default behaviour of this operation.
     *
     * **NOTE: errors throw by this function won't be caught safely.**
     *
     * @since 2.0.0
     */
    suspend fun completeWithDefaultBehaviour(collection: MongoCollection)
}

/* ============= ------------------ ============= */

private fun CollectionOperation<*>.inferToString(): String {
    val name = this::class.simpleName ?: "CollectionOperation"
    val address = hashCode().toString(16)
    return "$name($database, $collection, ...)@$address"
}

/* ============= ------------------ ============= */

/**
 * Create a custom [CollectionOperation] with the given [block] being its default behaviour.
 *
 * @param collection the name of the collection for the operation.
 * @param database name of the database for the operation. (null for [OpClient.defaultDatabase])
 * @since 2.0.0
 */
fun <T> CollectionOperation(
    collection: String,
    database: String? = null,
    block: suspend (MongoCollection) -> T,
): CollectionOperation<T> {
    return object : CollectionOperation<T>, CompletableDeferred<T> by CompletableDeferred() {
        override val database = database
        override val collection = collection

        override fun toString() = inferToString()

        override suspend fun completeWithDefaultBehaviour(collection: MongoCollection) {
            completeWith(runCatching { block(collection) })
        }
    }
}

/* ============= ------------------ ============= */

/**
 * An operator performing operations of type [CollectionOperation] in parallel
 * using [CollectionOperation.completeWithDefaultBehaviour].
 *
 * @author LSafer
 * @since 2.0.0
 */
@ExperimentalMoonkitApi
val CollectionOperator = createOperatorForType<CollectionOperation<*>> { operations ->
    val leftovers = mutableSetOf<CollectionOperation<*>>()
    for ((databaseName, databaseOperations) in operations.groupBy { it.database }) {
        val database = databaseOrDefaultDatabase(databaseName)

        // if databaseName is null yet no default database is set
        if (database == null) {
            leftovers += databaseOperations
            continue
        }

        for ((collectionName, collectionOperations) in databaseOperations.groupBy { it.collection }) {
            val collection = database[collectionName]

            collectionOperations.forEach {
                CoroutineScope(Dispatchers.IO).launch {
                    it.completeWithDefaultBehaviour(collection)
                }
            }
        }
    }

    leftovers
}

/* ============= ------------------ ============= */

/**
 * An operation for delete a single document in
 * some collection matching some filter with some
 * options.
 *
 * @see MongoCollection.deleteOne
 * @author LSafer
 * @since 2.0.0
 */
class DeleteOneOperation(
    override val database: String?,
    override val collection: String,
    val filter: BsonDocument,
    val options: DeleteOptions,
) : CollectionOperation<DeleteResult>,
    CompletableDeferred<DeleteResult>
    by CompletableDeferred() {
    override fun toString() = inferToString()

    override suspend fun completeWithDefaultBehaviour(collection: MongoCollection) {
        completeWith(runCatching { collection.deleteOne(filter, options) })
    }
}

/**
 * An operation for delete multiple documents in
 * some collection matching some filter with some
 * options.
 *
 * @see MongoCollection.deleteMany
 * @author LSafer
 * @since 2.0.0
 */
class DeleteManyOperation(
    override val database: String?,
    override val collection: String,
    val filter: BsonDocument,
    val options: DeleteOptions,
) : CollectionOperation<DeleteResult>,
    CompletableDeferred<DeleteResult>
    by CompletableDeferred() {
    override fun toString() = inferToString()

    override suspend fun completeWithDefaultBehaviour(collection: MongoCollection) {
        completeWith(runCatching { collection.deleteMany(filter, options) })
    }
}

/**
 * An operation for inserting a single document in
 * some collection with some options.
 *
 * @see MongoCollection.insertOne
 * @author LSafer
 * @since 2.0.0
 */
class InsertOneOperation(
    override val database: String?,
    override val collection: String,
    val document: BsonDocument,
    val options: InsertOneOptions,
) : CollectionOperation<InsertOneResult>,
    CompletableDeferred<InsertOneResult>
    by CompletableDeferred() {
    override fun toString() = inferToString()

    override suspend fun completeWithDefaultBehaviour(collection: MongoCollection) {
        completeWith(runCatching { collection.insertOne(document, options) })
    }
}

/**
 * An operation for inserting multiple documents in
 * some collection with some options.
 *
 * @see MongoCollection.insertMany
 * @author LSafer
 * @since 2.0.0
 */
class InsertManyOperation(
    override val database: String?,
    override val collection: String,
    val documents: List<BsonDocument>,
    val options: InsertManyOptions,
) : CollectionOperation<InsertManyResult>,
    CompletableDeferred<InsertManyResult>
    by CompletableDeferred() {
    override fun toString() = inferToString()

    override suspend fun completeWithDefaultBehaviour(collection: MongoCollection) {
        completeWith(runCatching { collection.insertMany(documents, options) })
    }
}

/**
 * An operation for updating a single document in
 * some collection matching some filter with some
 * options.
 *
 * @see MongoCollection.updateOne
 * @author LSafer
 * @since 2.0.0
 */
class UpdateOneOperation(
    override val database: String?,
    override val collection: String,
    val filter: BsonDocument,
    val update: BsonElement,
    val options: UpdateOptions,
) : CollectionOperation<UpdateResult>,
    CompletableDeferred<UpdateResult>
    by CompletableDeferred() {
    override fun toString() = inferToString()

    override suspend fun completeWithDefaultBehaviour(collection: MongoCollection) {
        completeWith(runCatching { collection.updateOne(filter, update, options) })
    }
}

/**
 * An operation for updating multiple documents in
 * some collection matching some filter with some
 * options.
 *
 * @see MongoCollection.updateMany
 * @author LSafer
 * @since 2.0.0
 */
class UpdateManyOperation(
    override val database: String?,
    override val collection: String,
    val filter: BsonDocument,
    val update: BsonElement,
    val options: UpdateOptions,
) : CollectionOperation<UpdateResult>,
    CompletableDeferred<UpdateResult>
    by CompletableDeferred() {
    override fun toString() = inferToString()

    override suspend fun completeWithDefaultBehaviour(collection: MongoCollection) {
        completeWith(runCatching { collection.updateMany(filter, update, options) })
    }
}

/**
 * An operation for replacing a single document in
 * some collection matching some filter with some
 * options.
 *
 * @see MongoCollection.replaceOne
 * @author LSafer
 * @since 2.0.0
 */
class ReplaceOneOperation(
    override val database: String?,
    override val collection: String,
    val filter: BsonDocument,
    val replacement: BsonDocument,
    val options: ReplaceOptions = ReplaceOptions(),
) : CollectionOperation<UpdateResult>,
    CompletableDeferred<UpdateResult>
    by CompletableDeferred() {
    override fun toString() = inferToString()

    override suspend fun completeWithDefaultBehaviour(collection: MongoCollection) {
        completeWith(runCatching { collection.replaceOne(filter, replacement, options) })
    }
}

/**
 * An operation for performing multiple operations
 * in some collection with some options.
 *
 * @see MongoCollection.bulkWrite
 * @author LSafer
 * @since 2.0.0
 */
class BulkWriteOperation(
    override val database: String?,
    override val collection: String,
    val requests: List<WriteModel>,
    val options: BulkWriteOptions,
) : CollectionOperation<BulkWriteResult>,
    CompletableDeferred<BulkWriteResult>
    by CompletableDeferred() {
    override fun toString() = inferToString()

    override suspend fun completeWithDefaultBehaviour(collection: MongoCollection) {
        completeWith(runCatching { collection.bulkWrite(requests, options) })
    }
}

/**
 * An operation for counting documents in some
 * collection matching some filter with some
 * options.
 *
 * @see MongoCollection.count
 * @author LSafer
 * @since 2.0.0
 */
class CountOperation(
    override val database: String?,
    override val collection: String,
    val filter: BsonDocument,
    val options: CountOptions,
) : CollectionOperation<Long>,
    CompletableDeferred<Long>
    by CompletableDeferred() {
    override fun toString() = inferToString()

    override suspend fun completeWithDefaultBehaviour(collection: MongoCollection) {
        completeWith(runCatching { collection.count(filter, options) })
    }
}

/**
 * An operation for getting an estimated count of
 * documents of some collection with some options.
 *
 * @see MongoCollection.estimatedCount
 * @author LSafer
 * @since 2.0.0
 */
class EstimatedCountOperation(
    override val database: String?,
    override val collection: String,
    val options: EstimatedCountOptions,
) : CollectionOperation<Long>,
    CompletableDeferred<Long>
    by CompletableDeferred() {
    override fun toString() = inferToString()

    override suspend fun completeWithDefaultBehaviour(collection: MongoCollection) {
        completeWith(runCatching { collection.estimatedCount(options) })
    }
}

/**
 * An operation for finding and deleting a single
 * document in some collection matching some
 * filter with some options.
 *
 * @see MongoCollection.findOneAndDelete
 * @author LSafer
 * @since 2.0.0
 */
class FindOneAndDeleteOperation(
    override val database: String?,
    override val collection: String,
    val filter: BsonDocument,
    val options: FindOneAndDeleteOptions,
) : CollectionOperation<BsonDocument?>,
    CompletableDeferred<BsonDocument?>
    by CompletableDeferred() {
    override fun toString() = inferToString()

    override suspend fun completeWithDefaultBehaviour(collection: MongoCollection) {
        completeWith(runCatching { collection.findOneAndDelete(filter, options) })
    }
}

/**
 * An operation for finding and replacing a single
 * document in some collection matching some
 * filter with some options.
 *
 * @see MongoCollection.findOneAndReplace
 * @author LSafer
 * @since 2.0.0
 */
class FindOneAndReplaceOperation(
    override val database: String?,
    override val collection: String,
    val filter: BsonDocument,
    val replacement: BsonDocument,
    val options: FindOneAndReplaceOptions,
) : CollectionOperation<BsonDocument?>,
    CompletableDeferred<BsonDocument?>
    by CompletableDeferred() {
    override fun toString() = inferToString()

    override suspend fun completeWithDefaultBehaviour(collection: MongoCollection) {
        completeWith(runCatching { collection.findOneAndReplace(filter, replacement, options) })
    }
}

/**
 * An operation for finding and updating a single
 * document in some collection matching some
 * filter with some options.
 *
 * @see MongoCollection.findOneAndUpdate
 * @author LSafer
 * @since 2.0.0
 */
class FindOneAndUpdateOperation(
    override val database: String?,
    override val collection: String,
    val filter: BsonDocument,
    val update: BsonElement,
    val options: FindOneAndUpdateOptions,
) : CollectionOperation<BsonDocument?>,
    CompletableDeferred<BsonDocument?>
    by CompletableDeferred() {
    override fun toString() = inferToString()

    override suspend fun completeWithDefaultBehaviour(collection: MongoCollection) {
        completeWith(runCatching { collection.findOneAndUpdate(filter, update, options) })
    }
}

/**
 * An operation for finding documents in some
 * collection matching some filter with some
 * options.
 *
 * @see MongoCollection.find
 * @author LSafer
 * @since 2.0.0
 */
class FindOperation(
    override val database: String?,
    override val collection: String,
    val filter: BsonDocument,
    val options: FindOptions,
) : CollectionOperation<List<BsonDocument>>,
    CompletableDeferred<List<BsonDocument>>
    by CompletableDeferred() {
    override fun toString() = inferToString()

    override suspend fun completeWithDefaultBehaviour(collection: MongoCollection) {
        completeWith(runCatching { collection.find(filter, options) })
    }
}

/**
 * An operation for aggregating in some collection
 * using some pipeline with some options.
 *
 * @see MongoCollection.aggregate
 * @author LSafer
 * @since 2.0.0
 */
class AggregateOperation(
    override val database: String?,
    override val collection: String,
    val pipeline: List<BsonDocument>,
    val options: AggregateOptions,
) : CollectionOperation<List<BsonDocument>>,
    CompletableDeferred<List<BsonDocument>>
    by CompletableDeferred() {
    override fun toString() = inferToString()

    override suspend fun completeWithDefaultBehaviour(collection: MongoCollection) {
        completeWith(runCatching { collection.aggregate(pipeline, options) })
    }
}

/**
 * An operation for getting distinct values of a
 * specific field in some collection matching some
 * filter with some options.
 *
 * @see MongoCollection.distinct
 * @author LSafer
 * @since 2.0.0
 */
class DistinctOperation(
    override val database: String?,
    override val collection: String,
    val field: String,
    val filter: BsonDocument,
    val options: DistinctOptions,
) : CollectionOperation<List<BsonDocument>>,
    CompletableDeferred<List<BsonDocument>>
    by CompletableDeferred() {
    override fun toString() = inferToString()

    override suspend fun completeWithDefaultBehaviour(collection: MongoCollection) {
        completeWith(runCatching { collection.distinct(field, filter, options) })
    }
}

/* ============= ------------------ ============= */
