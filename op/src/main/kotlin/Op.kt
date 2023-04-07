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

import org.cufy.bson.BsonDocument
import org.cufy.bson.BsonElement
import org.cufy.mongodb.*
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

/* ============= ------------------ ============= */

/**
 * A stateless instructions that can be executed
 * by an [Operator].
 *
 * Operations can be executed multiple times or
 * left executed without any consequences.
 *
 * The execution of an [op] instance is left for
 * the used [Operator].
 *
 * @author LSafer
 * @since 2.0.0
 */
interface Op<T> {
    /**
     * Create a new operation from this recipe.
     *
     * @since 2.0.0
     */
    operator fun invoke(): Operation<T>
}

/* ============= ------------------ ============= */

/**
 * The recipe for creating a [BlockOperation].
 *
 * @author LSafer
 * @since 2.0.0
 */
data class BlockOp<T, U>(
    val dependencies: List<Op<T>>,
    val block: suspend Monop.(List<Result<T>>) -> Result<U>
) : Op<U> {
    override fun invoke() =
        BlockOperation(dependencies.map { it() }, block)
}

/**
 * Create a [BlockOp] that executes the given [block].
 *
 * @since 2.0.0
 */
@OperationKeywordMarker
fun <T> op(block: suspend Monop.() -> T): Op<T> {
    return BlockOp(emptyList<Op<Unit>>()) {
        runCatching { block() }
    }
}

/**
 * Create a [BlockOp] that executes the given [ops].
 *
 * @param ops the dependency operations.
 * @since 2.0.0
 */
@OperationKeywordMarker
fun <T> opOf(ops: List<Op<T>>): BlockOp<T, List<Result<T>>> {
    return BlockOp(ops) { success(it) }
}

/**
 * Create a [BlockOp] that executes the given [ops].
 *
 * @param ops the dependency operations.
 * @since 2.0.0
 */
@OperationKeywordMarker
fun <T> opOf(vararg ops: Op<T>): Op<List<Result<T>>> {
    return opOf(ops.asList())
}

/**
 * Create a [BlockOp] that depends on [this] operation
 * and executes the given [block] of code.
 *
 * @param block the operation block.
 * @author LSafer
 * @since 2.0.0
 */
fun <T, U> Op<T>.tryMap(block: suspend (Result<T>) -> Result<U>): BlockOp<T, U> {
    return BlockOp(listOf(this)) { block(it.single()) }
}

/**
 * Create a [BlockOp] that depends on [this] operation
 * and executes the given [block] of code.
 *
 * @param block the operation block.
 * @author LSafer
 * @since 2.0.0
 */
fun <T, U> Op<T>.tryMapCatching(block: suspend (Result<T>) -> U): BlockOp<T, U> {
    return tryMap { runCatching { block(it) } }
}

/**
 * Create a [BlockOp] that depends on [this] operation
 * and executes the given [block] of code.
 *
 * @param block the operation block.
 * @author LSafer
 * @since 2.0.0
 */
fun <T, U> Op<T>.map(block: suspend (T) -> Result<U>): Op<U> {
    return tryMap { it.fold({ block(it) }, { failure(it) }) }
}

/**
 * Create a [BlockOp] that depends on [this] operation
 * and executes the given [block] of code.
 *
 * @param block the operation block.
 * @author LSafer
 * @since 2.0.0
 */
fun <T, U> Op<T>.mapCatching(block: suspend (T) -> U): Op<U> {
    return tryMap { it.mapCatching { block(it) } }
}

/* ============= ------------------ ============= */

/**
 * The recipe for creating a [DeleteOneOperation].
 *
 * @see MongoCollection.deleteOne
 * @author LSafer
 * @since 2.0.0
 */
data class DeleteOneOp(
    val collection: MonopCollection,
    val filter: BsonDocument,
    val options: DeleteOptions
) : Op<DeleteResult> {
    override fun invoke() =
        DeleteOneOperation(collection, filter, options)
}

/**
 * The recipe for creating a [DeleteManyOperation].
 *
 * @see MongoCollection.deleteMany
 * @author LSafer
 * @since 2.0.0
 */
data class DeleteManyOp(
    val collection: MonopCollection,
    val filter: BsonDocument,
    val options: DeleteOptions
) : Op<DeleteResult> {
    override fun invoke() =
        DeleteManyOperation(collection, filter, options)
}

/**
 * The recipe for creating a [InsertOneOperation].
 *
 * @see MongoCollection.insertOne
 * @author LSafer
 * @since 2.0.0
 */
data class InsertOneOp(
    val collection: MonopCollection,
    val document: BsonDocument,
    val options: InsertOneOptions
) : Op<InsertOneResult> {
    override fun invoke() =
        InsertOneOperation(collection, document, options)
}

/**
 * The recipe for creating a [InsertManyOperation].
 *
 * @see MongoCollection.insertMany
 * @author LSafer
 * @since 2.0.0
 */
data class InsertManyOp(
    val collection: MonopCollection,
    val documents: List<BsonDocument>,
    val options: InsertManyOptions
) : Op<InsertManyResult> {
    override fun invoke() =
        InsertManyOperation(collection, documents, options)
}

/**
 * The recipe for creating a [UpdateOneOperation].
 *
 * @see MongoCollection.updateOne
 * @author LSafer
 * @since 2.0.0
 */
data class UpdateOneOp(
    val collection: MonopCollection,
    val filter: BsonDocument,
    val update: BsonElement,
    val options: UpdateOptions
) : Op<UpdateResult> {
    override fun invoke() =
        UpdateOneOperation(collection, filter, update, options)
}

/**
 * The recipe for creating a [UpdateManyOperation].
 *
 * @see MongoCollection.updateMany
 * @author LSafer
 * @since 2.0.0
 */
data class UpdateManyOp(
    val collection: MonopCollection,
    val filter: BsonDocument,
    val update: BsonElement,
    val options: UpdateOptions
) : Op<UpdateResult> {
    override fun invoke() =
        UpdateManyOperation(collection, filter, update, options)
}

/**
 * The recipe for creating a [ReplaceOneOperation].
 *
 * @see MongoCollection.replaceOne
 * @author LSafer
 * @since 2.0.0
 */
data class ReplaceOneOp(
    val collection: MonopCollection,
    val filter: BsonDocument,
    val replacement: BsonDocument,
    val options: ReplaceOptions = ReplaceOptions()
) : Op<UpdateResult> {
    override fun invoke() =
        ReplaceOneOperation(collection, filter, replacement, options)
}

/**
 * The recipe for creating a [BulkWriteOperation].
 *
 * @see MongoCollection.bulkWrite
 * @author LSafer
 * @since 2.0.0
 */
data class BulkWriteOp(
    val collection: MonopCollection,
    val requests: List<WriteModel>,
    val options: BulkWriteOptions
) : Op<BulkWriteResult> {
    override fun invoke() =
        BulkWriteOperation(collection, requests, options)
}

/**
 * The recipe for creating a [CountOperation].
 *
 * @see MongoCollection.count
 * @author LSafer
 * @since 2.0.0
 */
data class CountOp(
    val collection: MonopCollection,
    val filter: BsonDocument,
    val options: CountOptions,
) : Op<Long> {
    override fun invoke() =
        CountOperation(collection, filter, options)
}

/**
 * The recipe for creating a [EstimatedCountOperation].
 *
 * @see MongoCollection.estimatedCount
 * @author LSafer
 * @since 2.0.0
 */
data class EstimatedCountOp(
    val collection: MonopCollection,
    val options: EstimatedCountOptions
) : Op<Long> {
    override fun invoke() =
        EstimatedCountOperation(collection, options)
}

/**
 * The recipe for creating a [FindOneAndDeleteOperation].
 *
 * @see MongoCollection.findOneAndDelete
 * @author LSafer
 * @since 2.0.0
 */
data class FindOneAndDeleteOp(
    val collection: MonopCollection,
    val filter: BsonDocument,
    val options: FindOneAndDeleteOptions
) : Op<BsonDocument?> {
    override fun invoke() =
        FindOneAndDeleteOperation(collection, filter, options)
}

/**
 * The recipe for creating a [FindOneAndReplaceOperation].
 *
 * @see MongoCollection.findOneAndReplace
 * @author LSafer
 * @since 2.0.0
 */
data class FindOneAndReplaceOp(
    val collection: MonopCollection,
    val filter: BsonDocument,
    val replacement: BsonDocument,
    val options: FindOneAndReplaceOptions
) : Op<BsonDocument?> {
    override fun invoke() =
        FindOneAndReplaceOperation(collection, filter, replacement, options)
}

/**
 * The recipe for creating a [FindOneAndUpdateOperation].
 *
 * @see MongoCollection.findOneAndUpdate
 * @author LSafer
 * @since 2.0.0
 */
data class FindOneAndUpdateOp(
    val collection: MonopCollection,
    val filter: BsonDocument,
    val update: BsonElement,
    val options: FindOneAndUpdateOptions
) : Op<BsonDocument?> {
    override fun invoke() =
        FindOneAndUpdateOperation(collection, filter, update, options)
}

/**
 * The recipe for creating a [FindOperation].
 *
 * @see MongoCollection.find
 * @author LSafer
 * @since 2.0.0
 */
data class FindOp(
    val collection: MonopCollection,
    val filter: BsonDocument,
    val options: FindOptions
) : Op<List<BsonDocument>> {
    override fun invoke() =
        FindOperation(collection, filter, options)
}

/**
 * The recipe for creating an [AggregateOperation].
 *
 * @see MongoCollection.aggregate
 * @author LSafer
 * @since 2.0.0
 */
data class AggregateOp(
    val collection: MonopCollection,
    val pipeline: List<BsonDocument>,
    val options: AggregateOptions
) : Op<List<BsonDocument>> {
    override fun invoke() =
        AggregateOperation(collection, pipeline, options)
}

/**
 * The recipe for creating a [DistinctOperation].
 *
 * @see MongoCollection.distinct
 * @author LSafer
 * @since 2.0.0
 */
data class DistinctOp(
    val collection: MonopCollection,
    val field: String,
    val filter: BsonDocument,
    val options: DistinctOptions
) : Op<List<BsonDocument>> {
    override fun invoke() =
        DistinctOperation(collection, field, filter, options)
}

/* ============= ------------------ ============= */
