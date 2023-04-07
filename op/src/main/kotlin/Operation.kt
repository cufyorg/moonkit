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
package org.cufy.monop

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import org.cufy.bson.BsonDocument
import org.cufy.bson.BsonElement
import org.cufy.mongodb.*

/* ============= ------------------ ============= */

/**
 * An instance that holds the necessary data to
 * perform an operation and the result of it.
 *
 * @param T the type of the operation's result.
 * @author LSafer
 * @since 2.0.0
 */
interface Operation<T> : Deferred<T>

/**
 * Execute this operation with the given [Monop]
 * and await the result.
 *
 * @since 2.0.0
 */
suspend operator fun <T> Operation<T>.invoke(monop: Monop = Monop): T {
    monop(this)
    return await()
}

/* ============= ------------------ ============= */

/**
 * An operation for executing block after other
 * dependency operations has been executed.
 *
 * @author LSafer
 * @since 2.0.0
 */
class BlockOperation<T, U>(
    val dependencies: List<Operation<T>>,
    val block: suspend Monop.(List<Result<T>>) -> Result<U>
) : Operation<U>,
    CompletableDeferred<U>
    by CompletableDeferred()

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
    val collection: MonopCollection,
    val filter: BsonDocument,
    val options: DeleteOptions
) : Operation<DeleteResult>,
    CompletableDeferred<DeleteResult>
    by CompletableDeferred()

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
    val collection: MonopCollection,
    val filter: BsonDocument,
    val options: DeleteOptions
) : Operation<DeleteResult>,
    CompletableDeferred<DeleteResult>
    by CompletableDeferred()

/**
 * An operation for inserting a single document in
 * some collection with some options.
 *
 * @see MongoCollection.insertOne
 * @author LSafer
 * @since 2.0.0
 */
class InsertOneOperation(
    val collection: MonopCollection,
    val document: BsonDocument,
    val options: InsertOneOptions
) : Operation<InsertOneResult>,
    CompletableDeferred<InsertOneResult>
    by CompletableDeferred()

/**
 * An operation for inserting multiple documents in
 * some collection with some options.
 *
 * @see MongoCollection.insertMany
 * @author LSafer
 * @since 2.0.0
 */
class InsertManyOperation(
    val collection: MonopCollection,
    val documents: List<BsonDocument>,
    val options: InsertManyOptions
) : Operation<InsertManyResult>,
    CompletableDeferred<InsertManyResult>
    by CompletableDeferred()

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
    val collection: MonopCollection,
    val filter: BsonDocument,
    val update: BsonElement,
    val options: UpdateOptions
) : Operation<UpdateResult>,
    CompletableDeferred<UpdateResult>
    by CompletableDeferred()

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
    val collection: MonopCollection,
    val filter: BsonDocument,
    val update: BsonElement,
    val options: UpdateOptions
) : Operation<UpdateResult>,
    CompletableDeferred<UpdateResult>
    by CompletableDeferred()

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
    val collection: MonopCollection,
    val filter: BsonDocument,
    val replacement: BsonDocument,
    val options: ReplaceOptions = ReplaceOptions()
) : Operation<UpdateResult>,
    CompletableDeferred<UpdateResult>
    by CompletableDeferred()

/**
 * An operation for performing multiple operations
 * in some collection with some options.
 *
 * @see MongoCollection.bulkWrite
 * @author LSafer
 * @since 2.0.0
 */
class BulkWriteOperation(
    val collection: MonopCollection,
    val requests: List<WriteModel>,
    val options: BulkWriteOptions
) : Operation<BulkWriteResult>,
    CompletableDeferred<BulkWriteResult>
    by CompletableDeferred()

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
    val collection: MonopCollection,
    val filter: BsonDocument,
    val options: CountOptions
) : Operation<Long>,
    CompletableDeferred<Long>
    by CompletableDeferred()

/**
 * An operation for getting an estimated count of
 * documents of some collection with some options.
 *
 * @see MongoCollection.estimatedCount
 * @author LSafer
 * @since 2.0.0
 */
class EstimatedCountOperation(
    val collection: MonopCollection,
    val options: EstimatedCountOptions
) : Operation<Long>,
    CompletableDeferred<Long>
    by CompletableDeferred()

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
    val collection: MonopCollection,
    val filter: BsonDocument,
    val options: FindOneAndDeleteOptions
) : Operation<BsonDocument?>,
    CompletableDeferred<BsonDocument?>
    by CompletableDeferred()

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
    val collection: MonopCollection,
    val filter: BsonDocument,
    val replacement: BsonDocument,
    val options: FindOneAndReplaceOptions
) : Operation<BsonDocument?>,
    CompletableDeferred<BsonDocument?>
    by CompletableDeferred()

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
    val collection: MonopCollection,
    val filter: BsonDocument,
    val update: BsonElement,
    val options: FindOneAndUpdateOptions
) : Operation<BsonDocument?>,
    CompletableDeferred<BsonDocument?>
    by CompletableDeferred()

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
    val collection: MonopCollection,
    val filter: BsonDocument,
    val options: FindOptions
) : Operation<List<BsonDocument>>,
    CompletableDeferred<List<BsonDocument>>
    by CompletableDeferred()

/**
 * An operation for aggregating in some collection
 * using some pipeline with some options.
 *
 * @see MongoCollection.aggregate
 * @author LSafer
 * @since 2.0.0
 */
class AggregateOperation(
    val collection: MonopCollection,
    val pipeline: List<BsonDocument>,
    val options: AggregateOptions
) : Operation<List<BsonDocument>>,
    CompletableDeferred<List<BsonDocument>>
    by CompletableDeferred()

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
    val collection: MonopCollection,
    val field: String,
    val filter: BsonDocument,
    val options: DistinctOptions
) : Operation<List<BsonDocument>>,
    CompletableDeferred<List<BsonDocument>>
    by CompletableDeferred()

/* ============= ------------------ ============= */
