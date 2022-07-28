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
package org.cufy.mangaka

import com.mongodb.client.model.*
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertManyResult
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult
import org.bson.BsonDocument
import org.bson.BsonObjectId
import org.bson.BsonValue
import org.bson.conversions.Bson
import org.cufy.mangaka.bson.`$in`
import org.cufy.mangaka.bson.`$set`
import org.cufy.mangaka.bson.by
import org.cufy.mangaka.bson.document
import org.cufy.mangaka.schema.Schema
import org.cufy.mangaka.schema.SchemaScope
import org.cufy.mangaka.schema.SchemaScopeBuilder
import org.litote.kmongo.EMPTY_BSON
import org.litote.kmongo.util.KMongoUtil

/**
 * A model is a wrapper over a collection providing
 * more utility.
 *
 * TODO: documentation for class Model
 *
 * @author LSafer
 * @since 1.0.0
 */
open class Model<T : Any>(
    /**
     * The name of the model.
     *
     * @since 1.0.0
     */
    val name: String,
    /**
     * The schema to be used to construct,
     * validate and format instances of [T].
     *
     * @since 1.0.0
     */
    val schema: Schema<T>,
    /**
     * The name of the collection backing this model.
     *
     * @since 1.0.0
     */
    val collectionName: String = name,
    /**
     * The mangaka instance used to construct
     * this model.
     *
     * @since 1.0.0
     */
    val mangaka: Mangaka = Mangaka
) {
    /**
     * The collection backing this mode.
     *
     * @since 1.0.0
     */
    val collection by lazy {
        mangaka.collection(collectionName)
    }

    /**
     * Create a new instance of [T] from the given
     * [bson] value using the schema.
     *
     * The created instance will be considered new
     * if the given [bson] is null.
     *
     * @param bson the source bson.
     * @param block the schema scope builder.
     * @since 1.0.0
     */
    suspend operator fun invoke(
        bson: Bson? = null,
        block: SchemaScopeBuilder<*, T>.() -> Unit = {}
    ): T {
        val document = bson?.toBsonDocument() ?: BsonDocument()
        val value = deserialize(document, block)
        val valueId = KMongoUtil.getIdValue(value)
        val documentId = document["_id"] as? BsonObjectId
        value._id = valueId?.let { Id(it) }
            ?: documentId?.let { Id(it) }
                    ?: Id()
        value.model = this
        value.isNew = bson == null
        value.isDeleted = false
        return value
    }

    /**
     * Validate the given [value] with the schema.
     *
     * @param value the value to be validated.
     * @param block the schema scope builder.
     * @since 1.0.0
     */
    suspend fun validate(
        value: T,
        block: SchemaScopeBuilder<*, T>.() -> Unit = {}
    ) {
        val scope = SchemaScope<Any?, T> {
            name = this@Model.name
            schema = this@Model.schema
            model = this@Model
            document = value
            block(this)
        }
        val errors = schema.validate(scope, value)
        if (errors.isNotEmpty())
            throw errors.first()
    }

    /**
     * Serialize the given [value] with the schema.
     *
     * @param value the value to be serialized.
     * @param block the schema scope builder.
     * @return the serialized bson.
     * @since 1.0.0
     */
    suspend fun serialize(
        value: T,
        block: SchemaScopeBuilder<*, T>.() -> Unit = {}
    ): BsonValue {
        val scope = SchemaScope<Any?, T> {
            name = this@Model.name
            schema = this@Model.schema
            model = this@Model
            document = value
            block(this)
        }
        return schema.serialize(scope, value)
    }

    /**
     * Deserialize the given [document] with the schema.
     *
     * @param document the bson to be deserialized.
     * @param block the schema scope builder.
     * @return the deserialized value.
     * @since 1.0.0
     */
    suspend fun deserialize(
        document: BsonDocument,
        block: SchemaScopeBuilder<*, T>.() -> Unit = {}
    ): T {
        val scope = SchemaScope<Any?, T> {
            name = this@Model.name
            schema = this@Model.schema
            model = this@Model
            block(this)
        }
        return schema.deserialize(scope, document)
    }
}

/* =============== DOCUMENT     =============== */

/**
 * Create and save a new instance of [T] from
 * the given [bson] value using the schema.
 *
 * @param bson the source bson.
 * @param options the update options.
 * @param validate the validation schema scope.
 *                 Pass `null` to skip validation.
 * @param block the schema scope builder.
 * @return the created value.
 * @since 1.0.0
 */
suspend fun <T : Any> Model<T>.create(
    bson: Bson = BsonDocument(),
    options: UpdateOptions.() -> Unit = {},
    validate: (SchemaScopeBuilder<*, T>.() -> Unit)? = {},
    block: SchemaScopeBuilder<*, T>.() -> Unit = {}
): T {
    val value = this(bson)
    save(value, options, validate, block)
    return value
}

/**
 * Save the given [value] to the collection.
 *
 * @param value the value to be saved.
 * @param options the update options.
 * @param validate the validation schema scope.
 *                 Pass `null` to skip validation.
 * @param block the schema scope builder.
 * @since 1.0.0
 */
suspend fun <T : Any> Model<T>.save(
    value: T,
    options: UpdateOptions.() -> Unit = {},
    validate: (SchemaScopeBuilder<*, T>.() -> Unit)? = {},
    block: SchemaScopeBuilder<*, T>.() -> Unit = {}
): UpdateResult {
    if (value.isDeleted)
        throw MangakaException("The document already deleted")
    if (validate != null)
        validate(value, validate)
    val document = serialize(value, block)
    val update = document(`$set` by document)
    val result = updateOneById(value._id, update) {
        upsert(true)
        options()
    }
    value.isNew = false
    return result
}

/**
 * Delete the given [value] from the collection.
 *
 * @param value the value to be deleted.
 * @param options the deletion options.
 * @since 1.0.0
 */
suspend fun <T : Any> Model<T>.remove(
    value: T,
    options: DeleteOptions.() -> Unit = {}
): DeleteResult {
    if (value.isDeleted)
        throw MangakaException("The document already deleted")
    val result = deleteOneById(value._id) {
        options()
    }
    value.isDeleted = true
    return result
}

/* =============== COLLECTION   =============== */

/**
 * Find a document with the given [id]
 * and convert it to an instance of type [T].
 *
 * @param id the id of the document.
 * @param block the schema scope builder.
 * @return the value. Or `null` if not found.
 * @since 1.0.0
 */
suspend fun <T : Any> Model<T>.findOneById(
    id: Id<T>,
    block: SchemaScopeBuilder<*, T>.() -> Unit = {}
): T? {
    val result = this.collection.findOneById(id.bson)
    result ?: return null
    return this(result, block)
}

/**
 * Find a document with any of the given [ids]
 * and convert it to an instance of type [T].
 *
 * @param ids the ids of the document.
 * @param block the schema scope builder.
 * @return the value. Or `null` if not found.
 * @since 1.1.0
 */
suspend fun <T : Any> Model<T>.findOneById(
    vararg ids: Id<T>,
    block: SchemaScopeBuilder<*, T>.() -> Unit = {}
): T? {
    return findOneById(ids.asList(), block)
}

/**
 * Find a document with any of the given [ids]
 * and convert it to an instance of type [T].
 *
 * @param ids the ids of the document.
 * @param block the schema scope builder.
 * @return the value. Or `null` if not found.
 * @since 1.1.0
 */
suspend fun <T : Any> Model<T>.findOneById(
    ids: List<Id<T>>,
    block: SchemaScopeBuilder<*, T>.() -> Unit = {}
): T? {
    return findOne(document(
        "_id" by document(
            `$in` by ids.map { it.bson }
        )
    ), block)
}

/**
 * Find a document matching the given [filter]
 * and convert it to an instance of type [T].
 *
 * @param filter the search filter.
 * @param block the schema scope builder.
 * @return the value. Or `null` if not found.
 * @since 1.0.0
 */
suspend fun <T : Any> Model<T>.findOne(
    filter: Bson = EMPTY_BSON,
    block: SchemaScopeBuilder<*, T>.() -> Unit = {}
): T? {
    val result = this.collection.findOne(filter)
    result ?: return null
    return this(result, block)
}

/**
 * Find documents with any of the given [ids]
 * and convert them to instances of type [T].
 *
 * @param ids the ids of the documents.
 * @param block the schema scope builder.
 * @return the values.
 * @since 1.1.0
 */
suspend fun <T : Any> Model<T>.findById(
    vararg ids: Id<T>,
    block: SchemaScopeBuilder<*, T>.() -> Unit = {}
): List<T> {
    return findById(ids.asList(), block)
}

/**
 * Find documents with any of the given [ids]
 * and convert them to instances of type [T].
 *
 * @param ids the ids of the documents.
 * @param block the schema scope builder.
 * @return the values.
 * @since 1.1.0
 */
suspend fun <T : Any> Model<T>.findById(
    ids: List<Id<T>>,
    block: SchemaScopeBuilder<*, T>.() -> Unit = {}
): List<T> {
    return find(document(
        "_id" by document(
            `$in` by ids.map { it.bson }
        )
    ), block)
}

/**
 * Find documents matching the given [filter]
 * and convert them to instances of type [T].
 *
 * @param filter the search filter.
 * @param block the schema scope builder.
 * @return the values.
 * @since 1.0.0
 */
suspend fun <T : Any> Model<T>.find(
    filter: Bson = EMPTY_BSON,
    block: SchemaScopeBuilder<*, T>.() -> Unit = {}
): List<T> {
    val results = this.collection.find(filter).toList()
    return results.map { this(it, block) }
}

//

/**
 * Aggregates documents according to the specified
 * aggregation pipeline.
 *
 * @param pipeline the aggregate pipeline
 * @return the aggregation result.
 * @since 1.0.0
 */
suspend fun Model<*>.aggregate(
    vararg pipeline: Bson
): List<BsonDocument> {
    return aggregate(pipeline.asList())
}

/**
 * Aggregates documents according to the specified
 * aggregation pipeline.
 *
 * @param pipeline the aggregate pipeline
 * @return the aggregation result.
 * @since 1.0.0
 */
suspend fun Model<*>.aggregate(
    pipeline: List<Bson>
): List<BsonDocument> {
    return this.collection.aggregate<BsonDocument>(pipeline)
        .toList()
}

//

/**
 * Removes at most one document from the
 * collection that has the given id.
 * If no documents match, the collection is not
 * modified.
 *
 * @param id      the id of the document.
 * @param options the options to apply to the
 *                delete operation.
 * @return the deletion result.
 * @since 1.0.0
 */
suspend fun <T : Any> Model<T>.deleteOneById(
    id: Id<T>,
    options: DeleteOptions.() -> Unit = {}
): DeleteResult {
    return deleteOne(document("_id" by id.bson), options)
}

/**
 * Removes at most one document from the
 * collection that matches the given filter.
 * If no documents match, the collection is not
 * modified.
 *
 * @param filter  the query filter to apply the
 *                delete operation.
 * @param options the options to apply to the
 *                delete operation.
 * @return the deletion result.
 * @since 1.0.0
 */
suspend fun Model<*>.deleteOne(
    filter: Bson = EMPTY_BSON,
    options: DeleteOptions.() -> Unit = {}
): DeleteResult {
    return this.collection.deleteOne(filter, DeleteOptions().apply(options))
}

/**
 * Removes all documents from the collection that
 * match the given query filter. If no documents
 * match, the collection is not modified.
 *
 * @param filter  the query filter to apply the
 *                delete operation.
 * @param options the options to apply to the
 *                delete operation.
 * @return the deletion result.
 * @since 1.0.0
 */
suspend fun Model<*>.deleteMany(
    filter: Bson = EMPTY_BSON,
    options: DeleteOptions.() -> Unit = {}
): DeleteResult {
    return this.collection.deleteMany(filter, DeleteOptions().apply(options))
}

//

/**
 * Update a single document in the collection
 * according to the specified arguments.
 *
 * @param id      the id of the document.
 * @param update  a document describing the update.
 *                The update to apply must include
 *                only update operators.
 * @param options the options to apply to the
 *                update operation.
 * @return the update result.
 * @since 1.0.0
 */
suspend fun <T : Any> Model<T>.updateOneById(
    id: Id<T>,
    update: Bson,
    options: UpdateOptions.() -> Unit = {}
): UpdateResult {
    return updateOne(document("_id" by id.bson), update, options)
}

/**
 * Update a single document in the collection
 * according to the specified arguments.
 *
 * @param filter  a document describing the query
 *                filter.
 * @param update  a document describing the update.
 *                The update to apply must include
 *                only update operators.
 * @param options the options to apply to the
 *                update operation.
 * @return the update result.
 * @since 1.0.0
 */
suspend fun Model<*>.updateOne(
    filter: Bson,
    update: Bson,
    options: UpdateOptions.() -> Unit = {}
): UpdateResult {
    return this.collection.updateOne(filter, update, UpdateOptions().apply(options))
}

/**
 * Update all documents in the collection according
 * to the specified arguments.
 *
 * @param filter  a document describing the query
 *                filter.
 * @param update  a document describing the update.
 *                The update to apply must include
 *                only update operators.
 * @param options the options to apply to the
 *                update operation.
 * @return the update result.
 * @since 1.0.0
 */
suspend fun Model<*>.updateMany(
    filter: Bson,
    update: Bson,
    options: UpdateOptions.() -> Unit = {}
): UpdateResult {
    return this.collection.updateMany(filter, update, UpdateOptions().apply(options))
}

//

/**
 * Inserts the provided document. If the document
 * is missing an identifier, the driver should
 * generate one.
 *
 * @param document the document to insert.
 * @param options  the options to apply to the
 *                 operation.
 * @return the insertion result.
 * @since 1.0.0
 */
suspend fun Model<*>.insertOne(
    document: BsonDocument,
    options: InsertOneOptions.() -> Unit = {}
): InsertOneResult {
    return collection.insertOne(document, InsertOneOptions().apply(options))
}

/**
 * Inserts a batch of documents. The preferred way
 * to perform bulk inserts is to use the BulkWrite
 * API. However, when talking with a server &lt; 2.6,
 * using this method will be faster due to constraints
 * in the bulk API related to error handling.
 *
 * @param documents the documents to insert.
 * @param options   the options to apply to the
 *                  operation.
 * @return the insertion result.
 * @since 1.0.0
 */
suspend fun Model<*>.insertMany(
    vararg documents: BsonDocument,
    options: InsertManyOptions.() -> Unit = {}
): InsertManyResult {
    return insertMany(documents.asList(), options)
}

/**
 * Inserts a batch of documents. The preferred way
 * to perform bulk inserts is to use the BulkWrite
 * API. However, when talking with a server &lt; 2.6,
 * using this method will be faster due to constraints
 * in the bulk API related to error handling.
 *
 * @param documents the documents to insert.
 * @param options   the options to apply to the
 *                  operation.
 * @return the insertion result.
 * @since 1.0.0
 */
suspend fun Model<*>.insertMany(
    documents: List<BsonDocument>,
    options: InsertManyOptions.() -> Unit = {}
): InsertManyResult {
    return collection.insertMany(documents, InsertManyOptions().apply(options))
}

//

/**
 * Check if the collection has a document
 * matching the given [filter]. If so, return
 * true. Otherwise, return false.
 *
 * @param filter the filter to match the
 *               documents with.
 * @return true, if a matching document was
 *         found. False, otherwise.
 * @since 1.0.0
 */
suspend fun Model<*>.exists(
    filter: Bson = EMPTY_BSON,
    options: CountOptions.() -> Unit = {}
): Boolean {
    return count(filter, options) > 0L
}

/**
 * Return the number of documents matching the
 * given [filter].
 *
 * @param filter filter to match with.
 * @return the number of documents.
 * @since 1.0.0
 */
suspend fun Model<*>.count(
    filter: Bson = EMPTY_BSON,
    options: CountOptions.() -> Unit = {}
): Long {
    return this.collection.countDocuments(filter, CountOptions().apply(options))
}

//

/**
 * Create an index with the given keys and options.
 * If the creation of the index is not doable
 * because an index with the same keys but with
 * different [IndexOptions] already exists, then
 * drop the existing index and create a new one.
 *
 * @param keys          an object describing the
 *                      index key(s)
 * @param options       the options for the index
 * @return the index name
 * @since 1.0.0
 */
suspend fun Model<*>.ensureIndex(
    keys: Bson,
    options: IndexOptions.() -> Unit = {}
): String? {
    return this.collection.ensureIndex(keys, IndexOptions().apply(options))
}
