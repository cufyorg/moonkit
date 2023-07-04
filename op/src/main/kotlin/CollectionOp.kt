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

/* ============= ------------------ ============= */

/**
 * A type of ops that is focused on [MongoCollection].
 * This type of operations only produces operations
 * that are of type [CollectionOperation].
 *
 * @author LSafer
 * @since 2.0.0
 */
interface CollectionOp<T> : Op<T> {
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

    override fun createOperation(): CollectionOperation<T>
}

private fun CollectionOp<*>.inferToString(): String {
    val name = this::class.simpleName ?: "CollectionOp"
    val address = System.identityHashCode(this).toString(16)
    return "$name($database, $collection, ...)@$address"
}

/* ============= ------------------ ============= */

/**
 * Create a custom [CollectionOp] with the given [block] being its default behaviour.
 *
 * @param collection the name of the collection for the operation.
 * @param database name of the database for the operation. (null for [OpClient.defaultDatabase])
 * @since 2.0.0
 */
fun <T> CollectionOp(collection: String, database: String? = null, block: suspend (MongoCollection) -> T): CollectionOp<T> {
    return object : CollectionOp<T> {
        override val database = database
        override val collection = collection

        override fun toString() = inferToString()

        override fun createOperation(): CollectionOperation<T> =
            CollectionOperation(collection, database, block)
    }
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
    override val database: String?,
    override val collection: String,
    val filter: BsonDocument,
    val options: DeleteOptions
) : CollectionOp<DeleteResult> {
    override fun toString() = inferToString()

    override fun createOperation() =
        DeleteOneOperation(database, collection, filter, options)
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
    override val database: String?,
    override val collection: String,
    val filter: BsonDocument,
    val options: DeleteOptions
) : CollectionOp<DeleteResult> {
    override fun toString() = inferToString()

    override fun createOperation() =
        DeleteManyOperation(database, collection, filter, options)
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
    override val database: String?,
    override val collection: String,
    val document: BsonDocument,
    val options: InsertOneOptions
) : CollectionOp<InsertOneResult> {
    override fun toString() = inferToString()

    override fun createOperation() =
        InsertOneOperation(database, collection, document, options)
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
    override val database: String?,
    override val collection: String,
    val documents: List<BsonDocument>,
    val options: InsertManyOptions
) : CollectionOp<InsertManyResult> {
    override fun toString() = inferToString()

    override fun createOperation() =
        InsertManyOperation(database, collection, documents, options)
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
    override val database: String?,
    override val collection: String,
    val filter: BsonDocument,
    val update: BsonElement,
    val options: UpdateOptions
) : CollectionOp<UpdateResult> {
    override fun toString() = inferToString()

    override fun createOperation() =
        UpdateOneOperation(database, collection, filter, update, options)
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
    override val database: String?,
    override val collection: String,
    val filter: BsonDocument,
    val update: BsonElement,
    val options: UpdateOptions
) : CollectionOp<UpdateResult> {
    override fun toString() = inferToString()

    override fun createOperation() =
        UpdateManyOperation(database, collection, filter, update, options)
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
    override val database: String?,
    override val collection: String,
    val filter: BsonDocument,
    val replacement: BsonDocument,
    val options: ReplaceOptions = ReplaceOptions()
) : CollectionOp<UpdateResult> {
    override fun toString() = inferToString()

    override fun createOperation() =
        ReplaceOneOperation(database, collection, filter, replacement, options)
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
    override val database: String?,
    override val collection: String,
    val requests: List<WriteModel>,
    val options: BulkWriteOptions
) : CollectionOp<BulkWriteResult> {
    override fun toString() = inferToString()

    override fun createOperation() =
        BulkWriteOperation(database, collection, requests, options)
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
    override val database: String?,
    override val collection: String,
    val filter: BsonDocument,
    val options: CountOptions,
) : CollectionOp<Long> {
    override fun toString() = inferToString()

    override fun createOperation() =
        CountOperation(database, collection, filter, options)
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
    override val database: String?,
    override val collection: String,
    val options: EstimatedCountOptions
) : CollectionOp<Long> {
    override fun toString() = inferToString()

    override fun createOperation() =
        EstimatedCountOperation(database, collection, options)
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
    override val database: String?,
    override val collection: String,
    val filter: BsonDocument,
    val options: FindOneAndDeleteOptions
) : CollectionOp<BsonDocument?> {
    override fun toString() = inferToString()

    override fun createOperation() =
        FindOneAndDeleteOperation(database, collection, filter, options)
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
    override val database: String?,
    override val collection: String,
    val filter: BsonDocument,
    val replacement: BsonDocument,
    val options: FindOneAndReplaceOptions
) : CollectionOp<BsonDocument?> {
    override fun toString() = inferToString()

    override fun createOperation() =
        FindOneAndReplaceOperation(database, collection, filter, replacement, options)
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
    override val database: String?,
    override val collection: String,
    val filter: BsonDocument,
    val update: BsonElement,
    val options: FindOneAndUpdateOptions
) : CollectionOp<BsonDocument?> {
    override fun toString() = inferToString()

    override fun createOperation() =
        FindOneAndUpdateOperation(database, collection, filter, update, options)
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
    override val database: String?,
    override val collection: String,
    val filter: BsonDocument,
    val options: FindOptions
) : CollectionOp<List<BsonDocument>> {
    override fun toString() = inferToString()

    override fun createOperation() =
        FindOperation(database, collection, filter, options)
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
    override val database: String?,
    override val collection: String,
    val pipeline: List<BsonDocument>,
    val options: AggregateOptions
) : CollectionOp<List<BsonDocument>> {
    override fun toString() = inferToString()

    override fun createOperation() =
        AggregateOperation(database, collection, pipeline, options)
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
    override val database: String?,
    override val collection: String,
    val field: String,
    val filter: BsonDocument,
    val options: DistinctOptions
) : CollectionOp<List<BsonDocument>> {
    override fun toString() = inferToString()

    override fun createOperation() =
        DistinctOperation(database, collection, field, filter, options)
}

fun DistinctOp.withFilter(filter: BsonDocumentBlock): DistinctOp {
    return copy(filter = this.filter + filter)
}

fun DistinctOp.withOptions(options: DistinctOptions.() -> Unit): DistinctOp {
    return copy(options = this.options.copy().apply(options))
}

/* ============= ------------------ ============= */
