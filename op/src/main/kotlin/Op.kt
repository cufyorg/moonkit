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
import org.cufy.bson.BsonDocumentBlock
import org.cufy.bson.BsonElement
import org.cufy.bson.plus
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
    fun createOperation(): Operation<T>
}

/**
 * Execute a new operation with the given [Monop]
 * and don't await the result.
 *
 * @return the operation.
 * @since 2.0.0
 */
fun <T> Op<T>.enqueue(monop: Monop = Monop): Operation<T> {
    return createOperation().enqueue(monop)
}

/**
 * Execute a new operation with the given [Monop]
 * and await the result.
 *
 * @return the awaited result.
 * @since 2.0.0
 */
suspend operator fun <T> Op<T>.invoke(monop: Monop = Monop): T {
    return createOperation().invoke(monop)
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
    override fun createOperation() =
        BlockOperation(dependencies.map { it.createOperation() }, block)
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
fun <T> opOf(ops: List<Op<T>>): BlockOp<T, List<Result<T>>> {
    return BlockOp(ops) { success(it) }
}

/**
 * Create a [BlockOp] that executes the given [ops].
 *
 * @param ops the dependency operations.
 * @since 2.0.0
 */
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

/**
 * Create a [BlockOp] that depends on [this] operation
 * and executes the given [block] of code only if the
 * value is not `null`.
 *
 * @param block the operation block.
 * @author LSafer
 * @since 2.0.0
 */
fun <T, U> Op<T>.mapNotNull(block: suspend (T & Any) -> Result<U>): BlockOp<T, U?> {
    return tryMap { it.fold({ if (it == null) success(null) else block(it) }, { failure(it) }) }
}

/**
 * Create a [BlockOp] that depends on [this] operation
 * and executes the given [block] of code only if the
 * value is not `null`.
 *
 * @param block the operation block.
 * @author LSafer
 * @since 2.0.0
 */
fun <T, U> Op<T>.mapNotNullCatching(block: suspend (T & Any) -> U): BlockOp<T, U?> {
    return tryMap { it.mapCatching { it?.let { block(it) } } }
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
    val collection: String,
    val filter: BsonDocument,
    val options: DeleteOptions
) : Op<DeleteResult> {
    override fun createOperation() =
        DeleteOneOperation(collection, filter, options)
}

fun DeleteOneOp.withFilter(filter: BsonDocumentBlock): DeleteOneOp {
    return copy(filter = this.filter + filter)
}

fun DeleteOneOp.withOptions(options: DeleteOptions.() -> Unit): DeleteOneOp {
    return copy(options = this.options.copy().apply(options))
}

/**
 * The recipe for creating a [DeleteManyOperation].
 *
 * @see MongoCollection.deleteMany
 * @author LSafer
 * @since 2.0.0
 */
data class DeleteManyOp(
    val collection: String,
    val filter: BsonDocument,
    val options: DeleteOptions
) : Op<DeleteResult> {
    override fun createOperation() =
        DeleteManyOperation(collection, filter, options)
}

fun DeleteManyOp.withFilter(filter: BsonDocumentBlock): DeleteManyOp {
    return copy(filter = this.filter + filter)
}

fun DeleteManyOp.withOptions(options: DeleteOptions.() -> Unit): DeleteManyOp {
    return copy(options = this.options.copy().apply(options))
}

/**
 * The recipe for creating a [InsertOneOperation].
 *
 * @see MongoCollection.insertOne
 * @author LSafer
 * @since 2.0.0
 */
data class InsertOneOp(
    val collection: String,
    val document: BsonDocument,
    val options: InsertOneOptions
) : Op<InsertOneResult> {
    override fun createOperation() =
        InsertOneOperation(collection, document, options)
}

fun InsertOneOp.withDocument(document: BsonDocumentBlock): InsertOneOp {
    return copy(document = this.document + document)
}

fun InsertOneOp.withOptions(options: InsertOneOptions.() -> Unit): InsertOneOp {
    return copy(options = this.options.copy().apply(options))
}

/**
 * The recipe for creating a [InsertManyOperation].
 *
 * @see MongoCollection.insertMany
 * @author LSafer
 * @since 2.0.0
 */
data class InsertManyOp(
    val collection: String,
    val documents: List<BsonDocument>,
    val options: InsertManyOptions
) : Op<InsertManyResult> {
    override fun createOperation() =
        InsertManyOperation(collection, documents, options)
}

fun InsertManyOp.withOptions(options: InsertManyOptions.() -> Unit): InsertManyOp {
    return copy(options = this.options.copy().apply(options))
}

/**
 * The recipe for creating a [UpdateOneOperation].
 *
 * @see MongoCollection.updateOne
 * @author LSafer
 * @since 2.0.0
 */
data class UpdateOneOp(
    val collection: String,
    val filter: BsonDocument,
    val update: BsonElement,
    val options: UpdateOptions
) : Op<UpdateResult> {
    override fun createOperation() =
        UpdateOneOperation(collection, filter, update, options)
}

fun UpdateOneOp.withFilter(filter: BsonDocumentBlock): UpdateOneOp {
    return copy(filter = this.filter + filter)
}

fun UpdateOneOp.withOptions(options: UpdateOptions.() -> Unit): UpdateOneOp {
    return copy(options = this.options.copy().apply(options))
}

/**
 * The recipe for creating a [UpdateManyOperation].
 *
 * @see MongoCollection.updateMany
 * @author LSafer
 * @since 2.0.0
 */
data class UpdateManyOp(
    val collection: String,
    val filter: BsonDocument,
    val update: BsonElement,
    val options: UpdateOptions
) : Op<UpdateResult> {
    override fun createOperation() =
        UpdateManyOperation(collection, filter, update, options)
}

fun UpdateManyOp.withFilter(filter: BsonDocumentBlock): UpdateManyOp {
    return copy(filter = this.filter + filter)
}

fun UpdateManyOp.withOptions(options: UpdateOptions.() -> Unit): UpdateManyOp {
    return copy(options = this.options.copy().apply(options))
}

/**
 * The recipe for creating a [ReplaceOneOperation].
 *
 * @see MongoCollection.replaceOne
 * @author LSafer
 * @since 2.0.0
 */
data class ReplaceOneOp(
    val collection: String,
    val filter: BsonDocument,
    val replacement: BsonDocument,
    val options: ReplaceOptions = ReplaceOptions()
) : Op<UpdateResult> {
    override fun createOperation() =
        ReplaceOneOperation(collection, filter, replacement, options)
}

fun ReplaceOneOp.withFilter(filter: BsonDocumentBlock): ReplaceOneOp {
    return copy(filter = this.filter + filter)
}

fun ReplaceOneOp.withReplacement(replacement: BsonDocumentBlock): ReplaceOneOp {
    return copy(replacement = this.replacement + replacement)
}

fun ReplaceOneOp.withOptions(options: ReplaceOptions.() -> Unit): ReplaceOneOp {
    return copy(options = this.options.copy().apply(options))
}

/**
 * The recipe for creating a [BulkWriteOperation].
 *
 * @see MongoCollection.bulkWrite
 * @author LSafer
 * @since 2.0.0
 */
data class BulkWriteOp(
    val collection: String,
    val requests: List<WriteModel>,
    val options: BulkWriteOptions
) : Op<BulkWriteResult> {
    override fun createOperation() =
        BulkWriteOperation(collection, requests, options)
}

fun BulkWriteOp.withOptions(options: BulkWriteOptions.() -> Unit): BulkWriteOp {
    return copy(options = this.options.copy().apply(options))
}

/**
 * The recipe for creating a [CountOperation].
 *
 * @see MongoCollection.count
 * @author LSafer
 * @since 2.0.0
 */
data class CountOp(
    val collection: String,
    val filter: BsonDocument,
    val options: CountOptions,
) : Op<Long> {
    override fun createOperation() =
        CountOperation(collection, filter, options)
}

fun CountOp.withFilter(filter: BsonDocumentBlock): CountOp {
    return copy(filter = this.filter + filter)
}

fun CountOp.withOptions(options: CountOptions.() -> Unit): CountOp {
    return copy(options = this.options.copy().apply(options))
}

/**
 * The recipe for creating a [EstimatedCountOperation].
 *
 * @see MongoCollection.estimatedCount
 * @author LSafer
 * @since 2.0.0
 */
data class EstimatedCountOp(
    val collection: String,
    val options: EstimatedCountOptions
) : Op<Long> {
    override fun createOperation() =
        EstimatedCountOperation(collection, options)
}

fun EstimatedCountOp.withOptions(options: EstimatedCountOptions.() -> Unit): EstimatedCountOp {
    return copy(options = this.options.copy().apply(options))
}

/**
 * The recipe for creating a [FindOneAndDeleteOperation].
 *
 * @see MongoCollection.findOneAndDelete
 * @author LSafer
 * @since 2.0.0
 */
data class FindOneAndDeleteOp(
    val collection: String,
    val filter: BsonDocument,
    val options: FindOneAndDeleteOptions
) : Op<BsonDocument?> {
    override fun createOperation() =
        FindOneAndDeleteOperation(collection, filter, options)
}

fun FindOneAndDeleteOp.withFilter(filter: BsonDocumentBlock): FindOneAndDeleteOp {
    return copy(filter = this.filter + filter)
}

fun FindOneAndDeleteOp.withOptions(options: FindOneAndDeleteOptions.() -> Unit): FindOneAndDeleteOp {
    return copy(options = this.options.copy().apply(options))
}

/**
 * The recipe for creating a [FindOneAndReplaceOperation].
 *
 * @see MongoCollection.findOneAndReplace
 * @author LSafer
 * @since 2.0.0
 */
data class FindOneAndReplaceOp(
    val collection: String,
    val filter: BsonDocument,
    val replacement: BsonDocument,
    val options: FindOneAndReplaceOptions
) : Op<BsonDocument?> {
    override fun createOperation() =
        FindOneAndReplaceOperation(collection, filter, replacement, options)
}

fun FindOneAndReplaceOp.withFilter(filter: BsonDocumentBlock): FindOneAndReplaceOp {
    return copy(filter = this.filter + filter)
}

fun FindOneAndReplaceOp.withReplacement(replacement: BsonDocumentBlock): FindOneAndReplaceOp {
    return copy(replacement = this.replacement + replacement)
}

fun FindOneAndReplaceOp.withOptions(options: FindOneAndReplaceOptions.() -> Unit): FindOneAndReplaceOp {
    return copy(options = this.options.copy().apply(options))
}

/**
 * The recipe for creating a [FindOneAndUpdateOperation].
 *
 * @see MongoCollection.findOneAndUpdate
 * @author LSafer
 * @since 2.0.0
 */
data class FindOneAndUpdateOp(
    val collection: String,
    val filter: BsonDocument,
    val update: BsonElement,
    val options: FindOneAndUpdateOptions
) : Op<BsonDocument?> {
    override fun createOperation() =
        FindOneAndUpdateOperation(collection, filter, update, options)
}

fun FindOneAndUpdateOp.withFilter(filter: BsonDocumentBlock): FindOneAndUpdateOp {
    return copy(filter = this.filter + filter)
}

fun FindOneAndUpdateOp.withOptions(options: FindOneAndUpdateOptions.() -> Unit): FindOneAndUpdateOp {
    return copy(options = this.options.copy().apply(options))
}

/**
 * The recipe for creating a [FindOperation].
 *
 * @see MongoCollection.find
 * @author LSafer
 * @since 2.0.0
 */
data class FindOp(
    val collection: String,
    val filter: BsonDocument,
    val options: FindOptions
) : Op<List<BsonDocument>> {
    override fun createOperation() =
        FindOperation(collection, filter, options)
}

fun FindOp.withFilter(filter: BsonDocumentBlock): FindOp {
    return copy(filter = this.filter + filter)
}

fun FindOp.withOptions(options: FindOptions.() -> Unit): FindOp {
    return copy(options = this.options.copy().apply(options))
}

/**
 * The recipe for creating an [AggregateOperation].
 *
 * @see MongoCollection.aggregate
 * @author LSafer
 * @since 2.0.0
 */
data class AggregateOp(
    val collection: String,
    val pipeline: List<BsonDocument>,
    val options: AggregateOptions
) : Op<List<BsonDocument>> {
    override fun createOperation() =
        AggregateOperation(collection, pipeline, options)
}

fun AggregateOp.withOptions(options: AggregateOptions.() -> Unit): AggregateOp {
    return copy(options = this.options.copy().apply(options))
}

/**
 * The recipe for creating a [DistinctOperation].
 *
 * @see MongoCollection.distinct
 * @author LSafer
 * @since 2.0.0
 */
data class DistinctOp(
    val collection: String,
    val field: String,
    val filter: BsonDocument,
    val options: DistinctOptions
) : Op<List<BsonDocument>> {
    override fun createOperation() =
        DistinctOperation(collection, field, filter, options)
}

fun DistinctOp.withFilter(filter: BsonDocumentBlock): DistinctOp {
    return copy(filter = this.filter + filter)
}

fun DistinctOp.withOptions(options: DistinctOptions.() -> Unit): DistinctOp {
    return copy(options = this.options.copy().apply(options))
}

/* ============= ------------------ ============= */
