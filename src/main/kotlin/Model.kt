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

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import org.bson.BsonDocument
import org.bson.BsonObjectId
import org.bson.conversions.Bson
import org.cufy.mangaka.internal.*
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.upsert
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
    val schema: Schema<in T, in Unit, T>,
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
    val collection: CoroutineCollection<BsonDocument> by lazy {
        mangaka.database.database
            .getCollection(collectionName, BsonDocument::class.java)
            .coroutine
    }

    /* Document */

    /**
     * Create a new instance of [T] from the given
     * [bson] value using the schema.
     *
     * The created instance will be considered new
     * if the given [bson] is null.
     *
     * @since 1.0.0
     */
    suspend operator fun invoke(bson: Bson? = null): T {
        val document = bson?.toBsonDocument()
        val value = construct(document)
        val valueId = KMongoUtil.getIdValue(value)
        val documentId = document?.get("_id") as? BsonObjectId
        val metadata = MetaData(
            id = valueId?.let { Id.normalize(it) }
                ?: documentId?.let { Id(it.value) }
                ?: Id(),
            model = this,
            isNew = document == null,
            isDeleted = false
        )
        MetaData.set(value, metadata)
        return value
    }

    /**
     * Create and save a new instance of [T] from
     * the given [bson] value using the schema.
     *
     * @since 1.0.0
     */
    suspend fun create(bson: Bson? = null): T {
        val document = bson?.toBsonDocument()
        val value = construct(document)
        val valueId = KMongoUtil.getIdValue(value)
        val documentId = document?.get("_id") as? BsonObjectId
        val metadata = MetaData(
            id = valueId?.let { Id.normalize(it) }
                ?: documentId?.let { Id(it.value) }
                ?: Id(),
            model = this,
            isNew = true,
            isDeleted = false
        )
        MetaData.set(value, metadata)
        save(value)
        return value
    }

    /**
     * Save the given [value] to the collection.
     *
     * @since 1.0.0
     */
    suspend fun save(value: T): UpdateResult {
        val metadata = MetaData.get(value)
            ?: missingMetadataError(value)
        if (metadata.isDeleted)
            valueIsDeletedError(value)
        validate(value)
        val document = format(value)
        val result = this.collection.updateOneById(
            id = metadata.id.normal,
            update = BsonDocument().apply {
                put("\$set", document)
            },
            options = upsert()
        )
        metadata.isNew = false
        return result
    }

    /**
     * Delete the given [value] from the collection.
     *
     * @since 1.0.0
     */
    suspend fun remove(value: T): DeleteResult {
        val metadata = MetaData.get(value)
            ?: missingMetadataError(value)
        if (metadata.isDeleted)
            valueIsDeletedError(value)
        val result = this.collection.deleteOneById(metadata.id.normal)
        metadata.isDeleted = true
        return result
    }

    /**
     * Validate the given [value] with the schema.
     *
     * @since 1.0.0
     */
    suspend fun validate(value: T) {
        val validator = schema.validator.safeCast()
        val errors = validator(SchemaScope(
            name = name,
            path = name,
            model = this,
            document = value,
            self = Unit
        ), value)
        if (errors.isNotEmpty())
            throw errors.first()
    }

    /* Override */

    /**
     * Find a document matching the given [filter]
     * and convert it to an instance of type [T].
     *
     * @since 1.0.0
     */
    suspend fun findOne(vararg filter: Bson = emptyArray()): T? {
        val result = this.collection.findOne(and(*filter))
        result ?: return null
        return this(result)
    }

    /**
     * Find a document with the given [id]
     * and convert it to an instance of type [T].
     *
     * @since 1.0.0
     */
    suspend fun findOneById(id: Id<T>): T? {
        val result = this.collection.findOneById(id.normal)
        result ?: return null
        return this(result)
    }

    /**
     * Find documents matching the given [filter]
     * and convert them to instances of type [T].
     *
     * @since 1.0.0
     */
    suspend fun find(vararg filter: Bson = emptyArray()): List<T> {
        val results = this.collection.find(and(*filter)).toList()
        return results.map { this(it) }
    }

    /* Delegate */

    // todo: Model delegate to collection

    /* Mongoose */

    /**
     * Return the number of documents matching the
     * given [filter].
     *
     * @param filter filter to match with.
     * @return the number of documents.
     * @since 1.0.0
     */
    suspend fun count(vararg filter: Bson = emptyArray()): Long {
        return this.collection.countDocuments(and(*filter))
    }

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
    suspend fun exists(vararg filter: Bson = emptyArray()): Boolean {
        return count(*filter) > 0L
    }

    /* Internal */

    private suspend fun format(value: T): BsonDocument {
        val formatter = schema.formatter.safeCast()
        val document = formatter(SchemaScope(
            name = name,
            path = name,
            model = this,
            document = value,
            self = Unit
        ), value)
        document as? BsonDocument ?: formatFailureError(name)
        return document
    }

    private suspend fun construct(bson: BsonDocument?): T {
        val constructor = schema.constructor.safeCast()
        val value = constructor(SchemaScope(
            name = name,
            path = name,
            model = this,
            document = null,
            self = Unit
        ), bson)
        value ?: constructorFailureError(name)
        return value
    }
}
