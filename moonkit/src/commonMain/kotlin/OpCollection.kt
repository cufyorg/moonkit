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

import org.cufy.bson.AnyId
import org.cufy.bson.BsonDocument
import org.cufy.bson.BsonDocumentBlock
import org.cufy.bson.toBsonArray
import org.cufy.mongodb.*

/* ============= ------------------ ============= */

/**
 * A convenient class that holds bare minimal
 * data needed for using some collection.
 *
 * @author LSafer
 * @since 2.0.0
 */
interface OpCollection {
    /**
     * The name of the database of the collection
     * to operate on.
     * Set to `null` to use [OpClient.defaultDatabase].
     *
     * @since 2.0.0
     */
    val database: String? get() = null

    /**
     * The collection name.
     *
     * @since 2.0.0
     */
    val name: String get() = inferName()
}

private fun OpCollection.inferName(): String {
    return this::class.simpleName ?: error("Cannot infer collection name for $this")
}

private fun OpCollection.inferToString(): String {
    return "OpCollection($database, $name)"
}

/* ============= ------------------ ============= */

/**
 * Construct a new [OpCollection] with the given [name] and [database].
 *
 * @since 2.0.0
 */
fun OpCollection(name: String, database: String? = null): OpCollection {
    return object : OpCollection {
        override val database = database
        override val name = name

        override fun toString() = inferToString()
    }
}

/* ============= ------------------ ============= */

/**
 * Return a [MongoCollection] instance corresponding
 * to this collection using the given [client].
 */
suspend fun OpCollection.get(client: OpClient = OpClient): MongoCollection {
    val database = client.databaseOrDefaultDatabase(database)
    // if this.database is null yet no default database is set
    database ?: error("Collection requires default database yet default database not set.")
    return database[name]
}

/**
 * Create an [Op] that executes the given [block]
 * with a [MongoCollection] corresponding to this
 * collection.
 *
 * @since 2.0.0
 */
fun <T> OpCollection.op(block: suspend MongoCollection.() -> T): Op<T> {
    return CollectionOp(name, database, block)
}

/* ============= ------------------ ============= */

/**
 * Create a [DeleteOneOp] with the given arguments.
 *
 * @receiver the collection to delete in.
 * @param filter the document filter.
 * @param options the operation options.
 * @see MongoCollection.deleteOne
 * @since 2.0.0
 */
fun OpCollection.deleteOne(
    filter: BsonDocument,
    options: DeleteOptions = DeleteOptions()
): DeleteOneOp {
    return DeleteOneOp(database, name, filter, options)
}

/**
 * Create a [DeleteOneOp] with the given arguments.
 *
 * @receiver the collection to delete in.
 * @param filter the document filter.
 * @param options the operation options.
 * @see MongoCollection.deleteOne
 * @since 2.0.0
 */
fun OpCollection.deleteOne(
    filter: BsonDocumentBlock,
    options: DeleteOptions.() -> Unit = {}
) = deleteOne(BsonDocument(filter), DeleteOptions(options))

//

/**
 * Create a [DeleteOneOp] with the given arguments.
 *
 * @receiver the collection to delete in.
 * @param id the id of the document to be deleted.
 * @param options the operation options.
 * @see MongoCollection.deleteOne
 * @since 2.0.0
 */
fun OpCollection.deleteOneById(
    id: AnyId,
    options: DeleteOptions = DeleteOptions()
): DeleteOneOp {
    return deleteOne(
        BsonDocument { "_id" by id },
        options
    )
}

/**
 * Create a [DeleteOneOp] with the given arguments.
 *
 * @receiver the collection to delete in.
 * @param id the id of the document to be deleted.
 * @param options the operation options.
 * @see MongoCollection.deleteOne
 * @since 2.0.0
 */
fun OpCollection.deleteOneById(
    id: AnyId,
    options: DeleteOptions.() -> Unit
) = deleteOneById(id, DeleteOptions(options))

/* ============= ------------------ ============= */

/**
 * Create a [DeleteManyOp] with the given arguments.
 *
 * @receiver the collection to delete in.
 * @param filter the document filter.
 * @param options the operation options.
 * @see MongoCollection.deleteMany
 * @since 2.0.0
 */
fun OpCollection.deleteMany(
    filter: BsonDocument,
    options: DeleteOptions = DeleteOptions()
) = DeleteManyOp(database, name, filter, options)

/**
 * Create a [DeleteManyOp] with the given arguments.
 *
 * @receiver the collection to delete in.
 * @param filter the document filter.
 * @param options the operation options.
 * @see MongoCollection.deleteMany
 * @since 2.0.0
 */
fun OpCollection.deleteMany(
    filter: BsonDocumentBlock,
    options: DeleteOptions.() -> Unit = {}
) = deleteMany(BsonDocument(filter), DeleteOptions(options))

/* ============= ------------------ ============= */

/**
 * Create a [InsertOneOp] with the given arguments.
 *
 * @receiver the collection to insert in.
 * @param document the document insert.
 * @param options the operation options.
 * @see MongoCollection.insertOne
 * @since 2.0.0
 */
fun OpCollection.insertOne(
    document: BsonDocument,
    options: InsertOneOptions = InsertOneOptions()
): InsertOneOp {
    return InsertOneOp(database, name, document, options)
}

/**
 * Create a [InsertOneOp] with the given arguments.
 *
 * @receiver the collection to insert in.
 * @param document the document insert.
 * @param options the operation options.
 * @see MongoCollection.insertOne
 * @since 2.0.0
 */
fun OpCollection.insertOne(
    document: BsonDocumentBlock,
    options: InsertOneOptions.() -> Unit = {}
) = insertOne(BsonDocument(document), InsertOneOptions(options))

/* ============= ------------------ ============= */

/**
 * Create a [InsertManyOp] with the given arguments.
 *
 * @receiver the collection to insert in.
 * @param documents the document filter.
 * @param options the operation options.
 * @see MongoCollection.insertMany
 * @since 2.0.0
 */
fun OpCollection.insertMany(
    documents: List<BsonDocument>,
    options: InsertManyOptions = InsertManyOptions()
): InsertManyOp {
    return InsertManyOp(database, name, documents, options)
}

/**
 * Create a [InsertManyOp] with the given arguments.
 *
 * @receiver the collection to insert in.
 * @param documents the documents to insert.
 * @param options the operation options.
 * @see MongoCollection.insertMany
 * @since 2.0.0
 */
fun OpCollection.insertMany(
    vararg documents: BsonDocumentBlock,
    options: InsertManyOptions.() -> Unit = {}
) = insertMany(documents.map { BsonDocument(it) }, InsertManyOptions(options))

/* ============= ------------------ ============= */

/**
 * Create an [UpdateOneOp] with the given arguments.
 *
 * @receiver the collection to update in.
 * @param filter the document filter.
 * @param update the update to be applied.
 * @param options the operation options.
 * @see MongoCollection.updateOne
 * @since 2.0.0
 */
fun OpCollection.updateOne(
    filter: BsonDocument,
    update: BsonDocument,
    options: UpdateOptions = UpdateOptions()
): UpdateOneOp {
    return UpdateOneOp(database, name, filter, update, options)
}

/**
 * Create an [UpdateOneOp] with the given arguments.
 *
 * @receiver the collection to update in.
 * @param filter the document filter.
 * @param update the update to be applied.
 * @param options the operation options.
 * @see MongoCollection.updateOne
 * @since 2.0.0
 */
fun OpCollection.updateOne(
    filter: BsonDocumentBlock,
    update: BsonDocumentBlock,
    options: UpdateOptions.() -> Unit = {}
) = updateOne(BsonDocument(filter), BsonDocument(update), UpdateOptions(options))

//

/**
 * Create an [UpdateOneOp] with the given arguments.
 *
 * @receiver the collection to update in.
 * @param filter the document filter.
 * @param update the update to be applied.
 * @param options the operation options.
 * @see MongoCollection.updateOne
 * @since 2.0.0
 */
fun OpCollection.updateOne(
    filter: BsonDocument,
    update: List<BsonDocument>,
    options: UpdateOptions = UpdateOptions()
): UpdateOneOp {
    return UpdateOneOp(database, name, filter, update.toBsonArray(), options)
}

/**
 * Create an [UpdateOneOp] with the given arguments.
 *
 * @receiver the collection to update in.
 * @param filter the document filter.
 * @param update the update to be applied.
 * @param options the operation options.
 * @see MongoCollection.updateOne
 * @since 2.0.0
 */
fun OpCollection.updateOne(
    filter: BsonDocumentBlock,
    vararg update: BsonDocumentBlock,
    options: UpdateOptions.() -> Unit = {}
) = updateOne(BsonDocument(filter), update.map { BsonDocument(it) }, UpdateOptions(options))

//

/**
 * Create an [UpdateOneOp] with the given arguments.
 *
 * @receiver the collection to find in.
 * @param id the id of the document to be updated.
 * @param update the update to be applied.
 * @param options the operation options.
 * @see MongoCollection.updateOneById
 * @since 2.0.0
 */
fun OpCollection.updateOneById(
    id: AnyId,
    update: BsonDocument,
    options: UpdateOptions = UpdateOptions()
): UpdateOneOp {
    return updateOne(
        BsonDocument { "_id" by id },
        update,
        options
    )
}

/**
 * Create an [UpdateOneOp] with the given arguments.
 *
 * @receiver the collection to find in.
 * @param id the id of the document to be updated.
 * @param update the update to be applied.
 * @param options the operation options.
 * @see MongoCollection.updateOne
 * @since 2.0.0
 */
fun OpCollection.updateOneById(
    id: AnyId,
    update: BsonDocumentBlock,
    options: UpdateOptions.() -> Unit = {}
) = updateOneById(id, BsonDocument(update), UpdateOptions(options))

//

/**
 * Create an [UpdateOneOp] with the given arguments.
 *
 * @receiver the collection to find in.
 * @param id the id of the document to be updated.
 * @param update the update to be applied.
 * @param options the operation options.
 * @see MongoCollection.updateOne
 * @since 2.0.0
 */
fun OpCollection.updateOneById(
    id: AnyId,
    update: List<BsonDocument>,
    options: UpdateOptions = UpdateOptions()
): UpdateOneOp {
    return updateOne(
        BsonDocument { "_id" by id },
        update,
        options
    )
}

/**
 * Create an [UpdateOneOp] with the given arguments.
 *
 * @receiver the collection to find in.
 * @param id the id of the document to be updated.
 * @param update the update to be applied.
 * @param options the operation options.
 * @see MongoCollection.updateOne
 * @since 2.0.0
 */
fun OpCollection.updateOneById(
    id: AnyId,
    vararg update: BsonDocumentBlock,
    options: UpdateOptions.() -> Unit = {}
) = updateOneById(id, update.map { BsonDocument(it) }, UpdateOptions(options))

/* ============= ------------------ ============= */

/**
 * Create an [UpdateManyOp] with the given arguments.
 *
 * @receiver the collection to find in.
 * @param filter the document filter.
 * @param update the update to be applied.
 * @param options the operation options.
 * @see MongoCollection.updateMany
 * @since 2.0.0
 */
fun OpCollection.updateMany(
    filter: BsonDocument,
    update: BsonDocument,
    options: UpdateOptions = UpdateOptions()
): UpdateManyOp {
    return UpdateManyOp(database, name, filter, update, options)
}

/**
 * Create an [UpdateManyOp] with the given arguments.
 *
 * @receiver the collection to find in.
 * @param filter the document filter.
 * @param update the update to be applied.
 * @param options the operation options.
 * @see MongoCollection.updateMany
 * @since 2.0.0
 */
fun OpCollection.updateMany(
    filter: BsonDocumentBlock,
    update: BsonDocumentBlock,
    options: UpdateOptions.() -> Unit = {}
) = updateMany(BsonDocument(filter), BsonDocument(update), UpdateOptions(options))

//

/**
 * Create an [UpdateManyOp] with the given arguments.
 *
 * @receiver the collection to find in.
 * @param filter the document filter.
 * @param update the update to be applied.
 * @param options the operation options.
 * @see MongoCollection.updateMany
 * @since 2.0.0
 */
fun OpCollection.updateMany(
    filter: BsonDocument,
    update: List<BsonDocument>,
    options: UpdateOptions = UpdateOptions()
): UpdateManyOp {
    return UpdateManyOp(database, name, filter, update.toBsonArray(), options)
}

/**
 * Create an [UpdateManyOp] with the given arguments.
 *
 * @receiver the collection to find in.
 * @param filter the document filter.
 * @param update the update to be applied.
 * @param options the operation options.
 * @see MongoCollection.updateMany
 * @since 2.0.0
 */
fun OpCollection.updateMany(
    filter: BsonDocumentBlock,
    vararg update: BsonDocumentBlock,
    options: UpdateOptions.() -> Unit = {}
) = updateMany(BsonDocument(filter), update.map { BsonDocument(it) }, UpdateOptions(options))

/* ============= ------------------ ============= */

/**
 * Create an [ReplaceOneOp] with the given arguments.
 *
 * @receiver the collection to replace in.
 * @param filter the document filter.
 * @param replacement the replacement document.
 * @param options the operation options.
 * @see MongoCollection.replaceOne
 * @since 2.0.0
 */
fun OpCollection.replaceOne(
    filter: BsonDocument,
    replacement: BsonDocument,
    options: ReplaceOptions = ReplaceOptions()
): ReplaceOneOp {
    return ReplaceOneOp(database, name, filter, replacement, options)
}

/**
 * Create an [ReplaceOneOp] with the given arguments.
 *
 * @receiver the collection to replace in.
 * @param filter the document filter.
 * @param replacement the replacement document.
 * @param options the operation options.
 * @see MongoCollection.replaceOne
 * @since 2.0.0
 */
fun OpCollection.replaceOne(
    filter: BsonDocumentBlock,
    replacement: BsonDocumentBlock,
    options: ReplaceOptions.() -> Unit = {}
) = replaceOne(BsonDocument(filter), BsonDocument(replacement), ReplaceOptions(options))

//

/**
 * Create an [ReplaceOneOp] with the given arguments.
 *
 * @receiver the collection to replace in.
 * @param id the document's id.
 * @param replacement the replacement document.
 * @param options the operation options.
 * @see MongoCollection.replaceOneById
 * @since 2.0.0
 */
fun OpCollection.replaceOneById(
    id: AnyId,
    replacement: BsonDocument,
    options: ReplaceOptions = ReplaceOptions()
): ReplaceOneOp {
    return replaceOne(
        BsonDocument { "_id" by id },
        replacement,
        options
    )
}

/**
 * Create an [ReplaceOneOp] with the given arguments.
 *
 * @receiver the collection to replace in.
 * @param id the document's id.
 * @param replacement the replacement document.
 * @param options the operation options.
 * @see MongoCollection.replaceOneById
 * @since 2.0.0
 */
fun OpCollection.replaceOneById(
    id: AnyId,
    replacement: BsonDocumentBlock,
    options: ReplaceOptions.() -> Unit = {}
) = replaceOneById(id, BsonDocument(replacement), ReplaceOptions(options))

/* ============= ------------------ ============= */

/**
 * Create an [BulkWriteOp] with the given arguments.
 *
 * @receiver the collection to update in.
 * @param requests the writes to execute.
 * @param options the operation options.
 * @see MongoCollection.bulkWrite
 * @since 2.0.0
 */
fun OpCollection.bulkWrite(
    requests: List<WriteModel>,
    options: BulkWriteOptions = BulkWriteOptions()
): BulkWriteOp {
    return BulkWriteOp(database, name, requests, options)
}

/**
 * Create an [BulkWriteOp] with the given arguments.
 *
 * @receiver the collection to update in.
 * @param requests the writes to execute.
 * @param options the operation options.
 * @see MongoCollection.bulkWrite
 * @since 2.0.0
 */
fun OpCollection.bulkWrite(
    vararg requests: WriteModel,
    options: BulkWriteOptions.() -> Unit = {}
) = bulkWrite(requests.asList(), BulkWriteOptions(options))

/* ============= ------------------ ============= */

/**
 * Create a [CountOp] with the given arguments.
 *
 * @receiver the collection to count in.
 * @param filter the document filter.
 * @param options the operation options.
 * @see MongoCollection.count
 * @since 2.0.0
 */
fun OpCollection.count(
    filter: BsonDocument = BsonDocument.Empty,
    options: CountOptions = CountOptions()
): CountOp {
    return CountOp(database, name, filter, options)
}

/**
 * Create a [CountOp] with the given arguments.
 *
 * @receiver the collection to count in.
 * @param filter the document filter.
 * @param options the operation options.
 * @see MongoCollection.count
 * @since 2.0.0
 */
fun OpCollection.count(
    filter: BsonDocumentBlock,
    options: CountOptions.() -> Unit = {}
) = count(BsonDocument(filter), CountOptions(options))

/* ============= ------------------ ============= */

/**
 * Create an [EstimatedCountOp] with the given arguments.
 *
 * @receiver the collection to update in.
 * @param options the operation options.
 * @see MongoCollection.estimatedCount
 * @since 2.0.0
 */
fun OpCollection.estimatedCount(
    options: EstimatedCountOptions = EstimatedCountOptions()
): EstimatedCountOp {
    return EstimatedCountOp(database, name, options)
}

/**
 * Create an [EstimatedCountOp] with the given arguments.
 *
 * @receiver the collection to update in.
 * @param options the operation options.
 * @see MongoCollection.estimatedCount
 * @since 2.0.0
 */
fun OpCollection.estimatedCount(
    options: EstimatedCountOptions.() -> Unit = {}
) = estimatedCount(EstimatedCountOptions(options))

/* ============= ------------------ ============= */

/**
 * Create a [FindOneAndDeleteOp] with the given arguments.
 *
 * @receiver the collection to delete in.
 * @param filter the document filter.
 * @param options the operation options.
 * @see MongoCollection.findOneAndDelete
 * @since 2.0.0
 */
fun OpCollection.findOneAndDelete(
    filter: BsonDocument,
    options: FindOneAndDeleteOptions = FindOneAndDeleteOptions()
): FindOneAndDeleteOp {
    return FindOneAndDeleteOp(database, name, filter, options)
}

/**
 * Create a [FindOneAndDeleteOp] with the given arguments.
 *
 * @receiver the collection to delete in.
 * @param filter the document filter.
 * @param options the operation options.
 * @see MongoCollection.findOneAndDelete
 * @since 2.0.0
 */
fun OpCollection.findOneAndDelete(
    filter: BsonDocumentBlock,
    options: FindOneAndDeleteOptions.() -> Unit = {}
) = findOneAndDelete(BsonDocument(filter), FindOneAndDeleteOptions(options))

//

/**
 * Create a [FindOneAndDeleteOp] with the given arguments.
 *
 * @receiver the collection to delete in.
 * @param id the id of the document to be deleted.
 * @param options the operation options.
 * @see MongoCollection.findOneByIdAndDelete
 * @since 2.0.0
 */
fun OpCollection.findOneByIdAndDelete(
    id: AnyId,
    options: FindOneAndDeleteOptions = FindOneAndDeleteOptions()
): FindOneAndDeleteOp {
    return findOneAndDelete(
        BsonDocument { "_id" by id },
        options
    )
}

/**
 * Create a [FindOneAndDeleteOp] with the given arguments.
 *
 * @receiver the collection to delete in.
 * @param id the id of the document to be deleted.
 * @param options the operation options.
 * @see MongoCollection.findOneByIdAndDelete
 * @since 2.0.0
 */
fun OpCollection.findOneByIdAndDelete(
    id: AnyId,
    options: FindOneAndDeleteOptions.() -> Unit
) = findOneByIdAndDelete(id, FindOneAndDeleteOptions(options))

/* ============= ------------------ ============= */

/**
 * Create a [FindOneAndReplaceOp] with the given arguments.
 *
 * @receiver the collection to replace in.
 * @param filter the document filter.
 * @param replacement the replacement.
 * @param options the operation options.
 * @see MongoCollection.findOneAndReplace
 * @since 2.0.0
 */
fun OpCollection.findOneAndReplace(
    filter: BsonDocument,
    replacement: BsonDocument,
    options: FindOneAndReplaceOptions = FindOneAndReplaceOptions()
): FindOneAndReplaceOp {
    return FindOneAndReplaceOp(database, name, filter, replacement, options)
}

/**
 * Create a [FindOneAndReplaceOp] with the given arguments.
 *
 * @receiver the collection to replace in.
 * @param filter the document filter.
 * @param replacement the replacement.
 * @param options the operation options.
 * @see MongoCollection.findOneAndReplace
 * @since 2.0.0
 */
fun OpCollection.findOneAndReplace(
    filter: BsonDocumentBlock,
    replacement: BsonDocumentBlock,
    options: FindOneAndReplaceOptions.() -> Unit = {}
) = findOneAndReplace(BsonDocument(filter), BsonDocument(replacement), FindOneAndReplaceOptions(options))

//

/**
 * Create a [FindOneAndReplaceOp] with the given arguments.
 *
 * @receiver the collection to replace in.
 * @param id the id of the document to be replaced.
 * @param replacement the replacement.
 * @param options the operation options.
 * @see MongoCollection.findOneByIdAndReplace
 * @since 2.0.0
 */
fun OpCollection.findOneByIdAndReplace(
    id: AnyId,
    replacement: BsonDocument,
    options: FindOneAndReplaceOptions = FindOneAndReplaceOptions()
): FindOneAndReplaceOp {
    return findOneAndReplace(
        BsonDocument { "_id" by id },
        replacement,
        options
    )
}

/**
 * Create a [FindOneAndReplaceOp] with the given arguments.
 *
 * @receiver the collection to replace in.
 * @param id the id of the document to be replaced.
 * @param replacement the replacement.
 * @param options the operation options.
 * @see MongoCollection.findOneByIdAndReplace
 * @since 2.0.0
 */
fun OpCollection.findOneByIdAndReplace(
    id: AnyId,
    replacement: BsonDocumentBlock,
    options: FindOneAndReplaceOptions.() -> Unit = {}
) = findOneByIdAndReplace(id, BsonDocument(replacement), FindOneAndReplaceOptions(options))

/* ============= ------------------ ============= */

/**
 * Create a [FindOneAndUpdateOp] with the given arguments.
 *
 * @receiver the collection to update in.
 * @param filter the document filter.
 * @param update the update to be applied.
 * @param options the operation options.
 * @see MongoCollection.findOneAndUpdate
 * @since 2.0.0
 */
fun OpCollection.findOneAndUpdate(
    filter: BsonDocument,
    update: BsonDocument,
    options: FindOneAndUpdateOptions = FindOneAndUpdateOptions()
): FindOneAndUpdateOp {
    return FindOneAndUpdateOp(database, name, filter, update, options)
}

/**
 * Create a [FindOneAndUpdateOp] with the given arguments.
 *
 * @receiver the collection to update in.
 * @param filter the document filter.
 * @param update the update to be applied.
 * @param options the operation options.
 * @see MongoCollection.findOneAndUpdate
 * @since 2.0.0
 */
fun OpCollection.findOneAndUpdate(
    filter: BsonDocumentBlock,
    update: BsonDocumentBlock,
    options: FindOneAndUpdateOptions.() -> Unit = {}
) = findOneAndUpdate(BsonDocument(filter), BsonDocument(update), FindOneAndUpdateOptions(options))

//

/**
 * Create a [FindOneAndUpdateOp] with the given arguments.
 *
 * @receiver the collection to update in.
 * @param filter the document filter.
 * @param update the update to be applied.
 * @param options the operation options.
 * @see MongoCollection.findOneAndUpdate
 * @since 2.0.0
 */
fun OpCollection.findOneAndUpdate(
    filter: BsonDocument,
    update: List<BsonDocument>,
    options: FindOneAndUpdateOptions = FindOneAndUpdateOptions()
): FindOneAndUpdateOp {
    return FindOneAndUpdateOp(database, name, filter, update.toBsonArray(), options)
}

/**
 * Create a [FindOneAndUpdateOp] with the given arguments.
 *
 * @receiver the collection to update in.
 * @param filter the document filter.
 * @param update the update to be applied.
 * @param options the operation options.
 * @see MongoCollection.findOneAndUpdate
 * @since 2.0.0
 */
fun OpCollection.findOneAndUpdate(
    filter: BsonDocumentBlock,
    vararg update: BsonDocumentBlock,
    options: FindOneAndUpdateOptions.() -> Unit = {}
) = findOneAndUpdate(BsonDocument(filter), update.map { BsonDocument(it) }, FindOneAndUpdateOptions(options))

//

/**
 * Create a [FindOneAndUpdateOp] with the given arguments.
 *
 * @receiver the collection to update in.
 * @param id the id of the document to be deleted.
 * @param update the update to be applied.
 * @param options the operation options.
 * @see MongoCollection.findOneAndUpdate
 * @since 2.0.0
 */
fun OpCollection.findOneByIdAndUpdate(
    id: AnyId,
    update: BsonDocument,
    options: FindOneAndUpdateOptions = FindOneAndUpdateOptions()
): FindOneAndUpdateOp {
    return findOneAndUpdate(
        BsonDocument { "_id" by id },
        update,
        options
    )
}

/**
 * Create a [FindOneAndUpdateOp] with the given arguments.
 *
 * @receiver the collection to update in.
 * @param id the id of the document to be deleted.
 * @param update the update to be applied.
 * @param options the operation options.
 * @see MongoCollection.findOneAndUpdate
 * @since 2.0.0
 */
fun OpCollection.findOneByIdAndUpdate(
    id: AnyId,
    update: BsonDocumentBlock,
    options: FindOneAndUpdateOptions.() -> Unit = {}
) = findOneByIdAndUpdate(id, BsonDocument(update), FindOneAndUpdateOptions(options))

//

/**
 * Create a [FindOneAndUpdateOp] with the given arguments.
 *
 * @receiver the collection to update in.
 * @param id the id of the document to be deleted.
 * @param update the update to be applied.
 * @param options the operation options.
 * @see MongoCollection.findOneAndUpdate
 * @since 2.0.0
 */
fun OpCollection.findOneByIdAndUpdate(
    id: AnyId,
    update: List<BsonDocument>,
    options: FindOneAndUpdateOptions = FindOneAndUpdateOptions()
): FindOneAndUpdateOp {
    return findOneAndUpdate(
        BsonDocument { "_id" by id },
        update,
        options
    )
}

/**
 * Create a [FindOneAndUpdateOp] with the given arguments.
 *
 * @receiver the collection to update in.
 * @param id the id of the document to be deleted.
 * @param update the update to be applied.
 * @param options the operation options.
 * @see MongoCollection.findOneAndUpdate
 * @since 2.0.0
 */
fun OpCollection.findOneByIdAndUpdate(
    id: AnyId,
    vararg update: BsonDocumentBlock,
    options: FindOneAndUpdateOptions.() -> Unit = {}
) = findOneByIdAndUpdate(id, update.map { BsonDocument(it) }, FindOneAndUpdateOptions(options))

/* ============= ------------------ ============= */

/**
 * Create a [FindOp] with the given arguments.
 *
 * @receiver the collection to find in.
 * @param filter the documents filter.
 * @param options the operation options.
 * @see MongoCollection.find
 * @since 2.0.0
 */
fun OpCollection.find(
    filter: BsonDocument = BsonDocument.Empty,
    options: FindOptions = FindOptions()
): FindOp {
    return FindOp(database, name, filter, options)
}

/**
 * Create a [FindOp] with the given arguments.
 *
 * @receiver the collection to find in.
 * @param filter the documents filter.
 * @param options the operation options.
 * @see MongoCollection.find
 * @since 2.0.0
 */
fun OpCollection.find(
    filter: BsonDocumentBlock,
    options: FindOptions.() -> Unit = {}
) = find(BsonDocument(filter), FindOptions(options))

//

/**
 * Create a [FindOp] that returns the first document.
 *
 * @receiver the collection to find in.
 * @param filter the documents filter.
 * @param options the operation options.
 * @see MongoCollection.findOne
 * @since 2.0.0
 */
fun OpCollection.findOne(
    filter: BsonDocument = BsonDocument.Empty,
    options: FindOptions = FindOptions()
): Op<BsonDocument?> {
    return find(
        filter,
        options.copy(limit = 1)
    ).map { Result.success(it.firstOrNull()) }
}

/**
 * Create a [FindOp] that returns a single document.
 *
 * @receiver the collection to find in.
 * @param filter the documents filter.
 * @param options the operation options.
 * @see MongoCollection.findOne
 * @since 2.0.0
 */
fun OpCollection.findOne(
    filter: BsonDocumentBlock,
    options: FindOptions.() -> Unit = {}
) = findOne(BsonDocument(filter), FindOptions(options))

//

/**
 * Create a [FindOp] that returns a single document.
 *
 * @receiver the collection to find in.
 * @param id the id of the document to be deleted.
 * @param options the operation options.
 * @see MongoCollection.findOneById
 * @since 2.0.0
 */
fun OpCollection.findOneById(
    id: AnyId,
    options: FindOptions = FindOptions()
): Op<BsonDocument?> {
    return findOne(
        BsonDocument { "_id" by id },
        options
    )
}

/**
 * Create a [FindOp] that returns a single document.
 *
 * @receiver the collection to find in.
 * @param id the id of the document to be deleted.
 * @param options the operation options.
 * @see MongoCollection.findOneById
 * @since 2.0.0
 */
fun OpCollection.findOneById(
    id: AnyId,
    options: FindOptions.() -> Unit
) = findOneById(id, FindOptions(options))

/* ============= ------------------ ============= */

/**
 * Create an [AggregateOp] with the given arguments.
 *
 * @receiver the collection to aggregate in.
 * @param pipeline the aggregation pipeline.
 * @param options the operation options.
 * @see MongoCollection.aggregate
 * @since 2.0.0
 */
fun OpCollection.aggregate(
    pipeline: List<BsonDocument>,
    options: AggregateOptions = AggregateOptions()
): AggregateOp {
    return AggregateOp(database, name, pipeline, options)
}

/**
 * Create an [AggregateOp] with the given arguments.
 *
 * @receiver the collection to aggregate in.
 * @param pipeline the aggregation pipeline.
 * @param options the operation options.
 * @see MongoCollection.aggregate
 * @since 2.0.0
 */
fun OpCollection.aggregate(
    vararg pipeline: BsonDocumentBlock,
    options: AggregateOptions.() -> Unit = {}
) = aggregate(pipeline.map { BsonDocument(it) }, AggregateOptions(options))

/* ============= ------------------ ============= */

/**
 * Create a [DistinctOp] with the given arguments.
 *
 * @receiver the collection to get in.
 * @param field the field name.
 * @param filter the document filter.
 * @param options the operation options.
 * @see MongoCollection.distinct
 * @since 2.0.0
 */
fun OpCollection.distinct(
    field: String,
    filter: BsonDocument = BsonDocument.Empty,
    options: DistinctOptions = DistinctOptions()
): DistinctOp {
    return DistinctOp(database, name, field, filter, options)
}

/**
 * Create a [DistinctOp] with the given arguments.
 *
 * @receiver the collection to get in.
 * @param field the field name.
 * @param filter the document filter.
 * @param options the operation options.
 * @see MongoCollection.distinct
 * @since 2.0.0
 */
fun OpCollection.distinct(
    field: String,
    filter: BsonDocumentBlock,
    options: DistinctOptions.() -> Unit = {}
) = distinct(field, BsonDocument(filter), DistinctOptions(options))

/* ============= ------------------ ============= */
