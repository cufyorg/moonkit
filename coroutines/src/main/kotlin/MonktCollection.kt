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
package org.cufy.monkt

import com.mongodb.MongoNamespace
import com.mongodb.ReadConcern
import com.mongodb.ReadPreference
import com.mongodb.WriteConcern
import com.mongodb.bulk.BulkWriteResult
import com.mongodb.client.model.*
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertManyResult
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult
import com.mongodb.reactivestreams.client.*
import org.cufy.bson.Bson
import org.cufy.bson.BsonDocument
import org.cufy.bson.bdocument
import org.reactivestreams.Publisher

/**
 * A generic-free coroutine dependant wrapper for
 * [MongoCollection]s
 *
 * The document class will always be [BsonDocument].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class MonktCollection(
    /**
     * The wrapped collection.
     */
    val collection: MongoCollection<BsonDocument>
) {
    // ignored members
    // - documentClass       : reflection
    // - codecRegistry       : reflection
    // - withDocumentClass() : reflection
    // - withCodecRegistry() : reflection
    // - mapReduce()         : deprecation

    /**
     * Gets the namespace of this collection.
     *
     * @return the namespace
     */
    val namespace: MongoNamespace get() = collection.namespace

    /**
     * Get the read preference for the MongoCollection.
     *
     * @return the [com.mongodb.ReadPreference]
     */
    val readPreference: ReadPreference get() = collection.readPreference

    /**
     * Get the read concern for the MongoCollection.
     *
     * @return the [com.mongodb.ReadConcern]
     * @since 1.2
     */
    val readConcern: ReadConcern get() = collection.readConcern

    /**
     * Get the write concern for the MongoCollection.
     *
     * @return the [com.mongodb.WriteConcern]
     */
    val writeConcern: WriteConcern get() = collection.writeConcern

    // with - TODO should it be builder style?

    /**
     * Create a new MongoCollection instance with a different read preference.
     *
     * @param preference the new [com.mongodb.ReadPreference] for the collection
     * @return a new MongoCollection instance with the different readPreference
     */
    fun withReadPreference(preference: ReadPreference): MonktCollection =
        MonktCollection(collection.withReadPreference(preference))

    /**
     * Create a new MongoCollection instance with a different read concern.
     *
     * @param concern the new [ReadConcern] for the collection
     * @return a new MongoCollection instance with the different ReadConcern
     * @since 1.2
     */
    fun withReadConcern(concern: ReadConcern): MonktCollection =
        MonktCollection(collection.withReadConcern(concern))

    /**
     * Create a new MongoCollection instance with a different write concern.
     *
     * @param concern the new [com.mongodb.WriteConcern] for the collection
     * @return a new MongoCollection instance with the different writeConcern
     */
    fun withWriteConcern(concern: WriteConcern): MonktCollection =
        MonktCollection(collection.withWriteConcern(concern))

    // count

    /**
     * Gets an estimate of the count of documents in a collection using collection metadata.
     *
     * @param options the options describing the count
     * @return a publisher with a single element indicating the estimated number of documents
     * @since 1.9
     */
    fun estimatedDocumentCount(
        options: EstimatedDocumentCountOptions =
            EstimatedDocumentCountOptions()
    ): Publisher<Long> =
        collection.estimatedDocumentCount(options)

    /**
     * Counts the number of documents in the collection according to the given options.
     *
     * Note: For a fast count of the total documents in a collection see [estimatedDocumentCountSuspend].
     *
     * Note: When migrating from `count()` to `countDocuments()` the following query operators must be replaced:
     *
     * ```
     *  +-------------+--------------------------------+
     *  | Operator    | Replacement                    |
     *  +=============+================================+
     *  | $where      |  $expr                         |
     *  +-------------+--------------------------------+
     *  | $near       |  $geoWithin with $center       |
     *  +-------------+--------------------------------+
     *  | $nearSphere |  $geoWithin with $centerSphere |
     *  +-------------+--------------------------------+
     * ```
     *
     * @param session the client session with which to associate this operation
     * @param filter the query filter
     * @param options the options describing the count
     * @return a publisher with a single element indicating the number of documents
     * @since 1.9
     */
    fun countDocuments(
        filter: Bson = bdocument,
        options: CountOptions = CountOptions(),
        session: ClientSession? = null
    ): Publisher<Long> = when (session) {
        null -> collection.countDocuments(filter, options)
        else -> collection.countDocuments(session, filter, options)
    }

    // READ

    /**
     * Gets the distinct values of the specified
     * field name.
     *
     * @param session the client session with
     *                which to associate this
     *                operation
     * @param field   the field name
     * @param filter  the query filter
     * @return an iterable of distinct values
     * @since 1.7
     */
    fun distinct(
        field: String,
        filter: Bson = bdocument,
        session: ClientSession? = null
    ): DistinctPublisher<BsonDocument> = when (session) {
        null -> collection.distinct(field, filter, BsonDocument::class.java)
        else -> collection.distinct(session, field, filter, BsonDocument::class.java)
    }

    /**
     * Finds all documents in the collection.
     *
     * @param session the client session with which to associate this operation
     * @param filter the query filter
     * @return the fluent find interface
     * @since 1.7
     */
    fun find(
        filter: Bson = bdocument,
        session: ClientSession? = null
    ): FindPublisher<BsonDocument> = when (session) {
        null -> collection.find(filter, BsonDocument::class.java)
        else -> collection.find(session, filter, BsonDocument::class.java)
    }

    /**
     * Aggregates documents according to the specified aggregation pipeline.
     *
     * @param session the client session with which to associate this operation
     * @param pipeline  the aggregate pipeline
     * @return a publisher containing the result of the aggregation operation
     * @since 1.7
     */
    fun aggregate(
        pipeline: List<Bson>,
        session: ClientSession? = null
    ): AggregatePublisher<BsonDocument> = when (session) {
        null -> collection.aggregate(pipeline, BsonDocument::class.java)
        else -> collection.aggregate(session, pipeline, BsonDocument::class.java)
    }

    /**
     * Creates a change stream for this collection.
     *
     * @param session the client session with which to associate this operation
     * @param pipeline the aggregation pipeline to apply to the change stream
     * @return the change stream iterable
     * @since 1.7
     */
    fun watch(
        pipeline: List<Bson> = emptyList(),
        session: ClientSession? = null
    ): ChangeStreamPublisher<BsonDocument> = when (session) {
        null -> collection.watch(pipeline, BsonDocument::class.java)
        else -> collection.watch(session, pipeline, BsonDocument::class.java)
    }

    // WRITE

    /**
     * Executes a mix of inserts, updates, replaces, and deletes.
     *
     * @param session the client session with which to associate this operation
     * @param requests the writes to execute
     * @param options  the options to apply to the bulk write operation
     * @return the BulkWriteResult
     * @since 1.7
     */
    fun bulkWrite(
        requests: List<WriteModel<BsonDocument>>,
        options: BulkWriteOptions = BulkWriteOptions(),
        session: ClientSession? = null
    ): Publisher<BulkWriteResult> = when (session) {
        null -> collection.bulkWrite(requests, options)
        else -> collection.bulkWrite(session, requests, options)
    }

    /**
     * Inserts the provided document. If the document is missing an identifier, the driver should generate one.
     *
     * @param session the client session with which to associate this operation
     * @param document the document to insert
     * @param options  the options to apply to the operation
     * @return a publisher with a single element with the InsertOneResult or with either a
     * com.mongodb.DuplicateKeyException or com.mongodb.MongoException
     * @since 1.7
     */
    fun insertOne(
        document: BsonDocument,
        options: InsertOneOptions = InsertOneOptions(),
        session: ClientSession? = null
    ): Publisher<InsertOneResult> = when (session) {
        null -> collection.insertOne(document, options)
        else -> collection.insertOne(session, document, options)
    }

    /**
     * Inserts a batch of documents.
     *
     * @param session the client session with which to associate this operation
     * @param documents the documents to insert
     * @param options   the options to apply to the operation
     * @return a publisher with a single element with the InsertManyResult or with either a
     * com.mongodb.DuplicateKeyException or com.mongodb.MongoException
     * @since 1.7
     */
    fun insertMany(
        documents: List<BsonDocument>,
        options: InsertManyOptions = InsertManyOptions(),
        session: ClientSession? = null
    ): Publisher<InsertManyResult> = when (session) {
        null -> collection.insertMany(documents, options)
        else -> collection.insertMany(session, documents, options)
    }

    /**
     * Removes at most one document from the collection that matches the given filter.  If no documents match, the collection is not
     * modified.
     *
     * @param session the client session with which to associate this operation
     * @param filter the query filter to apply the delete operation
     * @param options the options to apply to the delete operation
     * @return a publisher with a single element the DeleteResult or with an com.mongodb.MongoException
     * @since 1.7
     */
    fun deleteOne(
        filter: Bson,
        options: DeleteOptions = DeleteOptions(),
        session: ClientSession? = null
    ): Publisher<DeleteResult> = when (session) {
        null -> collection.deleteOne(filter, options)
        else -> collection.deleteOne(session, filter, options)
    }

    /**
     * Removes all documents from the collection that match the given query filter.  If no documents match, the collection is not modified.
     *
     * @param session the client session with which to associate this operation
     * @param filter the query filter to apply the delete operation
     * @param options the options to apply to the delete operation
     * @return a publisher with a single element the DeleteResult or with an com.mongodb.MongoException
     * @since 1.7
     */
    fun deleteMany(
        filter: Bson,
        options: DeleteOptions = DeleteOptions(),
        session: ClientSession? = null
    ): Publisher<DeleteResult> = when (session) {
        null -> collection.deleteMany(filter, options)
        else -> collection.deleteMany(session, filter, options)
    }

    /**
     * Replace a document in the collection according to the specified arguments.
     *
     * @param session the client session with which to associate this operation
     * @param filter      the query filter to apply the replace operation
     * @param replacement the replacement document
     * @param options     the options to apply to the replace operation
     * @return a publisher with a single element the UpdateResult
     * @since 1.8
     */
    fun replaceOne(
        filter: Bson,
        replacement: BsonDocument,
        options: ReplaceOptions = ReplaceOptions(),
        session: ClientSession? = null
    ): Publisher<UpdateResult> = when (session) {
        null -> collection.replaceOne(filter, replacement, options)
        else -> collection.replaceOne(session, filter, replacement, options)
    }

    /**
     * Update a single document in the collection according to the specified arguments.
     *
     * @param session the client session with which to associate this operation
     * @param filter  a document describing the query filter, which may not be null.
     * @param update  a document describing the update, which may not be null. The update to apply must include only update operators.
     * @param options the options to apply to the update operation
     * @return a publisher with a single element the UpdateResult
     * @since 1.7
     */
    fun updateOne(
        filter: Bson,
        update: Bson,
        options: UpdateOptions = UpdateOptions(),
        session: ClientSession? = null
    ): Publisher<UpdateResult> = when (session) {
        null -> collection.updateOne(filter, update, options)
        else -> collection.updateOne(session, filter, update, options)
    }

    /**
     * Update a single document in the collection according to the specified arguments.
     *
     * Note: Supports retryable writes on MongoDB server versions 3.6 or higher when the retryWrites setting is enabled.
     *
     * @param session the client session with which to associate this operation
     * @param filter        a document describing the query filter, which may not be null.
     * @param update        a pipeline describing the update, which may not be null.
     * @param options the options to apply to the update operation
     * @return a publisher with a single element the UpdateResult
     * @since 1.12
     */
    fun updateOne(
        filter: Bson,
        update: List<Bson>,
        options: UpdateOptions = UpdateOptions(),
        session: ClientSession? = null
    ): Publisher<UpdateResult> = when (session) {
        null -> collection.updateOne(filter, update, options)
        else -> collection.updateOne(session, filter, update, options)
    }

    /**
     * Update all documents in the collection according to the specified arguments.
     *
     * @param session the client session with which to associate this operation
     * @param filter  a document describing the query filter, which may not be null.
     * @param update  a document describing the update, which may not be null. The update to apply must include only update operators.
     * @param options the options to apply to the update operation
     * @return a publisher with a single element the UpdateResult
     * @since 1.7
     */
    fun updateMany(
        filter: Bson,
        update: Bson,
        options: UpdateOptions = UpdateOptions(),
        session: ClientSession? = null
    ): Publisher<UpdateResult> = when (session) {
        null -> collection.updateMany(filter, update, options)
        else -> collection.updateMany(session, filter, update, options)
    }

    /**
     * Update all documents in the collection according to the specified arguments.
     *
     * @param session the client session with which to associate this operation
     * @param filter a document describing the query filter, which may not be null.
     * @param update a pipeline describing the update, which may not be null.
     * @param options the options to apply to the update operation
     * @return a publisher with a single element the UpdateResult
     * @since 1.12
     */
    fun updateMany(
        filter: Bson,
        update: List<Bson>,
        options: UpdateOptions = UpdateOptions(),
        session: ClientSession? = null
    ): Publisher<UpdateResult> = when (session) {
        null -> collection.updateMany(filter, update, options)
        else -> collection.updateMany(session, filter, update, options)
    }

    // ATOMIC

    /**
     * Atomically find a document and remove it.
     *
     * @param session the client session with which to associate this operation
     * @param filter  the query filter to find the document with
     * @param options the options to apply to the operation
     * @return a publisher with a single element the document that was removed.  If no documents matched the query filter, then null will be
     * returned
     * @since 1.7
     */
    fun findOneAndDelete(
        filter: Bson,
        options: FindOneAndDeleteOptions = FindOneAndDeleteOptions(),
        session: ClientSession? = null
    ): Publisher<BsonDocument> = when (session) {
        null -> collection.findOneAndDelete(filter, options)
        else -> collection.findOneAndDelete(session, filter, options)
    }

    /**
     * Atomically find a document and replace it.
     *
     * @param session the client session with which to associate this operation
     * @param filter      the query filter to apply the replace operation
     * @param replacement the replacement document
     * @param options     the options to apply to the operation
     * @return a publisher with a single element the document that was replaced.  Depending on the value of the {@code returnOriginal}
     * property, this will either be the document as it was before the update or as it is after the update.  If no documents matched the
     * query filter, then null will be returned
     * @since 1.7
     */
    fun findOneAndReplace(
        filter: Bson,
        replacement: BsonDocument,
        options: FindOneAndReplaceOptions = FindOneAndReplaceOptions(),
        session: ClientSession? = null
    ): Publisher<BsonDocument> = when (session) {
        null -> collection.findOneAndReplace(filter, replacement, options)
        else -> collection.findOneAndReplace(session, filter, replacement, options)
    }

    /**
     * Atomically find a document and update it.
     *
     * @param session the client session with which to associate this operation
     * @param filter  a document describing the query filter, which may not be null.
     * @param update  a document describing the update, which may not be null. The update to apply must include only update operators.
     * @param options the options to apply to the operation
     * @return a publisher with a single element the document that was updated.  Depending on the value of the {@code returnOriginal}
     * property, this will either be the document as it was before the update or as it is after the update.  If no documents matched the
     * query filter, then null will be returned
     * @since 1.7
     */
    fun findOneAndUpdate(
        filter: Bson,
        update: Bson,
        options: FindOneAndUpdateOptions = FindOneAndUpdateOptions(),
        session: ClientSession? = null
    ): Publisher<BsonDocument> = when (session) {
        null -> collection.findOneAndUpdate(filter, update, options)
        else -> collection.findOneAndUpdate(session, filter, update, options)
    }

    /**
     * Atomically find a document and update it.
     *
     * Note: Supports retryable writes on MongoDB server versions 3.6 or higher when the retryWrites setting is enabled.
     *
     * @param session the client session with which to associate this operation
     * @param filter  a document describing the query filter, which may not be null.
     * @param update  a pipeline describing the update, which may not be null.
     * @param options the options to apply to the operation
     * @return a publisher with a single element the document that was updated.  Depending on the value of the {@code returnOriginal}
     * property, this will either be the document as it was before the update or as it is after the update.  If no documents matched the
     * query filter, then null will be returned
     * @since 1.12
     */
    fun findOneAndUpdate(
        filter: Bson,
        update: List<Bson>,
        options: FindOneAndUpdateOptions = FindOneAndUpdateOptions(),
        session: ClientSession? = null
    ): Publisher<BsonDocument> = when (session) {
        null -> collection.findOneAndUpdate(filter, update, options)
        else -> collection.findOneAndUpdate(session, filter, update, options)
    }

    // COLLECTION

    /**
     * Drops this collection from the Database.
     *
     * @param session the client session with which to associate this operation
     * @return an empty publisher that indicates when the operation has completed
     * @since 1.7
     */
    fun drop(
        session: ClientSession? = null
    ): Publisher<Void> = when (session) {
        null -> collection.drop()
        else -> collection.drop(session)
    }

    /**
     * Creates an index.
     *
     * @param session the client session with which to associate this operation
     * @param key     an object describing the index key(s), which may not be null.
     * @param options the options for the index
     * @return an empty publisher that indicates when the operation has completed
     * @since 1.7
     */
    fun createIndex(
        key: Bson,
        options: IndexOptions = IndexOptions(),
        session: ClientSession? = null
    ): Publisher<String> = when (session) {
        null -> collection.createIndex(key, options)
        else -> collection.createIndex(session, key, options)
    }

    /**
     * Create multiple indexes.
     *
     * @param session the client session with which to associate this operation
     * @param indexes the list of indexes
     * @param options options to use when creating indexes
     * @return an empty publisher that indicates when the operation has completed
     * @since 1.7
     */
    fun createIndexes(
        indexes: List<IndexModel>,
        options: CreateIndexOptions = CreateIndexOptions(),
        session: ClientSession? = null
    ): Publisher<String> = when (session) {
        null -> collection.createIndexes(indexes, options)
        else -> collection.createIndexes(session, indexes, options)
    }

    /**
     * Get all the indexes in this collection.
     *
     * @param session the client session with which to associate this operation
     * @return the fluent list indexes interface
     * @since 1.7
     */
    fun listIndexes(
        session: ClientSession? = null
    ): ListIndexesPublisher<BsonDocument> = when (session) {
        null -> collection.listIndexes(BsonDocument::class.java)
        else -> collection.listIndexes(session, BsonDocument::class.java)
    }

    /**
     * Drops the given index.
     *
     * @param session the client session with which to associate this operation
     * @param name the name of the index to remove
     * @param options options to use when dropping indexes
     * @return an empty publisher that indicates when the operation has completed
     * @since 1.7
     */
    fun dropIndex(
        name: String,
        options: DropIndexOptions = DropIndexOptions(),
        session: ClientSession? = null
    ): Publisher<Void> = when (session) {
        null -> collection.dropIndex(name, options)
        else -> collection.dropIndex(session, name, options)
    }

    /**
     * Drops the index given the keys used to create it.
     *
     * @param session the client session with which to associate this operation
     * @param keys the keys of the index to remove
     * @param options options to use when dropping indexes
     * @return an empty publisher that indicates when the operation has completed
     * @since 1.7
     */
    fun dropIndex(
        keys: Bson,
        options: DropIndexOptions = DropIndexOptions(),
        session: ClientSession? = null
    ): Publisher<Void> = when (session) {
        null -> collection.dropIndex(keys, options)
        else -> collection.dropIndex(session, keys, options)
    }

    /**
     * Drop all the indexes on this collection, except for the default on _id.
     *
     * @param session the client session with which to associate this operation
     * @param options options to use when dropping indexes
     * @return an empty publisher that indicates when the operation has completed
     * @since 1.7
     */
    fun dropIndexes(
        options: DropIndexOptions = DropIndexOptions(),
        session: ClientSession? = null
    ): Publisher<Void> = when (session) {
        null -> collection.dropIndexes(options)
        else -> collection.dropIndexes(session, options)
    }

    /**
     * Rename the collection with oldCollectionName to the newCollectionName.
     *
     * @param session the client session with which to associate this operation
     * @param namespace the name the collection will be renamed to
     * @param options the options for renaming a collection
     * @return an empty publisher that indicates when the operation has completed
     * @since 1.7
     */
    fun renameCollection(
        namespace: MongoNamespace,
        options: RenameCollectionOptions = RenameCollectionOptions(),
        session: ClientSession? = null
    ): Publisher<Void> = when (session) {
        null -> collection.renameCollection(namespace, options)
        else -> collection.renameCollection(session, namespace, options)
    }
}
