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

import org.cufy.bson.*
import org.cufy.mongodb.*
import java.util.*

/* ============= ------------------ ============= */

/**
 * A convenient class that holds bare minimal
 * data needed for using some collection.
 *
 * The important parts of this class are [name]
 * which is the name of the collection and [init]
 * which is a function to be invoked when the
 * collection is about to be used.
 *
 * @author LSafer
 * @since 2.0.0
 */
open class MonopCollection(val name: String) {
    private var dejaVu = Collections.newSetFromMap<Monop>(WeakHashMap())

    override fun toString(): String {
        return "MonopCollection($name)"
    }

    /**
     * Called only once per [monop] instance
     * and only before this instance being used.
     *
     * Call [initOnce] instead.
     */
    protected open suspend fun init(monop: Monop) {}

    /**
     * If not initialized, initialize this
     * instance with the given [monop].
     *
     * @since 2.0.0
     */
    suspend fun initOnce(monop: Monop) {
        if (!dejaVu.add(monop))
            return

        init(monop)
    }
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
fun MonopCollection.deleteOne(
    filter: BsonDocument,
    options: DeleteOptions = DeleteOptions()
): DeleteOneOp {
    return DeleteOneOp(this, filter, options)
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
fun MonopCollection.deleteOne(
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
fun MonopCollection.deleteOneById(
    id: Id<*>,
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
fun MonopCollection.deleteOneById(
    id: Id<*>,
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
fun MonopCollection.deleteMany(
    filter: BsonDocument,
    options: DeleteOptions = DeleteOptions()
) = DeleteManyOp(this, filter, options)

/**
 * Create a [DeleteManyOp] with the given arguments.
 *
 * @receiver the collection to delete in.
 * @param filter the document filter.
 * @param options the operation options.
 * @see MongoCollection.deleteMany
 * @since 2.0.0
 */
fun MonopCollection.deleteMany(
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
fun MonopCollection.insertOne(
    document: BsonDocument,
    options: InsertOneOptions = InsertOneOptions()
): InsertOneOp {
    return InsertOneOp(this, document, options)
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
fun MonopCollection.insertOne(
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
fun MonopCollection.insertMany(
    documents: List<BsonDocument>,
    options: InsertManyOptions = InsertManyOptions()
): InsertManyOp {
    return InsertManyOp(this, documents, options)
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
fun MonopCollection.insertMany(
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
fun MonopCollection.updateOne(
    filter: BsonDocument,
    update: BsonDocument,
    options: UpdateOptions = UpdateOptions()
): UpdateOneOp {
    return UpdateOneOp(this, filter, update, options)
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
fun MonopCollection.updateOne(
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
fun MonopCollection.updateOne(
    filter: BsonDocument,
    update: List<BsonDocument>,
    options: UpdateOptions = UpdateOptions()
): UpdateOneOp {
    return UpdateOneOp(this, filter, BsonArray(update), options)
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
fun MonopCollection.updateOne(
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
fun MonopCollection.updateOneById(
    id: Id<*>,
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
fun MonopCollection.updateOneById(
    id: Id<*>,
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
fun MonopCollection.updateOneById(
    id: Id<*>,
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
fun MonopCollection.updateOneById(
    id: Id<*>,
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
fun MonopCollection.updateMany(
    filter: BsonDocument,
    update: BsonDocument,
    options: UpdateOptions = UpdateOptions()
): UpdateManyOp {
    return UpdateManyOp(this, filter, update, options)
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
fun MonopCollection.updateMany(
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
fun MonopCollection.updateMany(
    filter: BsonDocument,
    update: List<BsonDocument>,
    options: UpdateOptions = UpdateOptions()
): UpdateManyOp {
    return UpdateManyOp(this, filter, BsonArray(update), options)
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
fun MonopCollection.updateMany(
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
fun MonopCollection.replaceOne(
    filter: BsonDocument,
    replacement: BsonDocument,
    options: ReplaceOptions = ReplaceOptions()
): ReplaceOneOp {
    return ReplaceOneOp(this, filter, replacement, options)
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
fun MonopCollection.replaceOne(
    filter: BsonDocumentBlock,
    replacement: BsonDocumentBlock,
    options: ReplaceOptions.() -> Unit = {}
) = replaceOne(BsonDocument(filter), BsonDocument(filter), ReplaceOptions(options))

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
fun MonopCollection.replaceOneById(
    id: Id<*>,
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
fun MonopCollection.replaceOneById(
    id: Id<*>,
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
fun MonopCollection.bulkWrite(
    requests: List<WriteModel>,
    options: BulkWriteOptions = BulkWriteOptions()
): BulkWriteOp {
    return BulkWriteOp(this, requests, options)
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
fun MonopCollection.bulkWrite(
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
fun MonopCollection.count(
    filter: BsonDocument = EmptyBsonDocument,
    options: CountOptions = CountOptions()
): CountOp {
    return CountOp(this, filter, options)
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
fun MonopCollection.count(
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
fun MonopCollection.estimatedCount(
    options: EstimatedCountOptions = EstimatedCountOptions()
): EstimatedCountOp {
    return EstimatedCountOp(this, options)
}

/**
 * Create an [EstimatedCountOp] with the given arguments.
 *
 * @receiver the collection to update in.
 * @param options the operation options.
 * @see MongoCollection.estimatedCount
 * @since 2.0.0
 */
fun MonopCollection.estimatedCount(
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
fun MonopCollection.findOneAndDelete(
    filter: BsonDocument,
    options: FindOneAndDeleteOptions = FindOneAndDeleteOptions()
): FindOneAndDeleteOp {
    return FindOneAndDeleteOp(this, filter, options)
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
fun MonopCollection.findOneAndDelete(
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
fun MonopCollection.findOneByIdAndDelete(
    id: Id<*>,
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
fun MonopCollection.findOneByIdAndDelete(
    id: Id<*>,
    options: FindOneAndDeleteOptions.() -> Unit = {}
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
fun MonopCollection.findOneAndReplace(
    filter: BsonDocument,
    replacement: BsonDocument,
    options: FindOneAndReplaceOptions = FindOneAndReplaceOptions()
): FindOneAndReplaceOp {
    return FindOneAndReplaceOp(this, filter, replacement, options)
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
fun MonopCollection.findOneAndReplace(
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
fun MonopCollection.findOneByIdAndReplace(
    id: Id<*>,
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
fun MonopCollection.findOneByIdAndReplace(
    id: Id<*>,
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
fun MonopCollection.findOneAndUpdate(
    filter: BsonDocument,
    update: BsonDocument,
    options: FindOneAndUpdateOptions = FindOneAndUpdateOptions()
): FindOneAndUpdateOp {
    return FindOneAndUpdateOp(this, filter, update, options)
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
fun MonopCollection.findOneAndUpdate(
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
fun MonopCollection.findOneAndUpdate(
    filter: BsonDocument,
    update: List<BsonDocument>,
    options: FindOneAndUpdateOptions = FindOneAndUpdateOptions()
): FindOneAndUpdateOp {
    return FindOneAndUpdateOp(this, filter, BsonArray(update), options)
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
fun MonopCollection.findOneAndUpdate(
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
fun MonopCollection.findOneByIdAndUpdate(
    id: Id<*>,
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
fun MonopCollection.findOneByIdAndUpdate(
    id: Id<*>,
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
fun MonopCollection.findOneByIdAndUpdate(
    id: Id<*>,
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
fun MonopCollection.findOneByIdAndUpdate(
    id: Id<*>,
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
fun MonopCollection.find(
    filter: BsonDocument = EmptyBsonDocument,
    options: FindOptions = FindOptions()
): FindOp {
    return FindOp(this, filter, options)
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
fun MonopCollection.find(
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
fun MonopCollection.findOne(
    filter: BsonDocument = EmptyBsonDocument,
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
fun MonopCollection.findOne(
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
fun MonopCollection.findOneById(
    id: Id<*>,
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
fun MonopCollection.findOneById(
    id: Id<*>,
    options: FindOptions.() -> Unit = {}
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
fun MonopCollection.aggregate(
    pipeline: List<BsonDocument>,
    options: AggregateOptions = AggregateOptions()
): AggregateOp {
    return AggregateOp(this, pipeline, options)
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
fun MonopCollection.aggregate(
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
fun MonopCollection.distinct(
    field: String,
    filter: BsonDocument = EmptyBsonDocument,
    options: DistinctOptions = DistinctOptions()
): DistinctOp {
    return DistinctOp(this, field, filter, options)
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
fun MonopCollection.distinct(
    field: String,
    filter: BsonDocumentBlock,
    options: DistinctOptions.() -> Unit = {}
) = distinct(field, BsonDocument(filter), DistinctOptions(options))

/* ============= ------------------ ============= */