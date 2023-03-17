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

import com.mongodb.ReadConcern
import com.mongodb.ReadPreference
import com.mongodb.WriteConcern
import com.mongodb.client.model.CreateCollectionOptions
import com.mongodb.client.model.CreateViewOptions
import com.mongodb.reactivestreams.client.*
import org.cufy.bson.Bson
import org.cufy.bson.BsonDocument
import org.reactivestreams.Publisher

/**
 * Create a new [MonktDatabase] instance wrapping
 * the given [database] instance.
 *
 * @param database the database to be wrapped.
 * @since 2.0.0
 */
fun MonktDatabase(database: MongoDatabase): MonktDatabase {
    return object : MonktDatabase {
        override val database = database
    }
}

/**
 * A generic-free coroutine dependant wrapper for
 * [MongoDatabase]s
 *
 * The document class will always be [BsonDocument].
 *
 * @author LSafer
 * @since 2.0.0
 */
interface MonktDatabase {
    /**
     * The wrapped collection.
     */
    val database: MongoDatabase

    // ignored members
    // - codecRegistry       : reflection
    // - withCodecRegistry() : reflection

    /**
     * Gets the name of the database.
     *
     * @return the database name
     */
    val name: String get() = database.name

    /**
     * Get the read preference for the MongoDatabase.
     *
     * @return the [com.mongodb.ReadPreference]
     */
    val readPreference: ReadPreference get() = database.readPreference

    /**
     * Get the read concern for the MongoCollection.
     *
     * @return the [com.mongodb.ReadConcern]
     * @since 1.2
     */
    val readConcern: ReadConcern get() = database.readConcern

    /**
     * Get the write concern for the MongoDatabase.
     *
     * @return the [com.mongodb.WriteConcern]
     */
    val writeConcern: WriteConcern get() = database.writeConcern

    // with - TODO should it be builder style?

    /**
     * Create a new MongoDatabase instance with a different read preference.
     *
     * @param preference the new [com.mongodb.ReadPreference] for the collection
     * @return a new MongoDatabase instance with the different readPreference
     */
    fun withReadPreference(preference: ReadPreference): MonktDatabase =
        MonktDatabase(database.withReadPreference(preference))

    /**
     * Create a new MongoDatabase instance with a different read concern.
     *
     * @param concern the new [ReadConcern] for the collection
     * @return a new MongoDatabase instance with the different ReadConcern
     * @since 1.2
     */
    fun withReadConcern(concern: ReadConcern): MonktDatabase =
        MonktDatabase(database.withReadConcern(concern))

    /**
     * Create a new MongoDatabase instance with a different write concern.
     *
     * @param concern the new [com.mongodb.WriteConcern] for the collection
     * @return a new MongoDatabase instance with the different writeConcern
     */
    fun withWriteConcern(concern: WriteConcern): MonktDatabase =
        MonktDatabase(database.withWriteConcern(concern))

    // get

    /**
     * Gets a collection.
     *
     * @param name the name of the collection to return
     * @return the collection
     */
    fun getCollection(name: String): MonktCollection =
        MonktCollection(database.getCollection(name, BsonDocument::class.java))

    /**
     * Executes command in the context of the current database.
     *
     * @param session the client session with which to associate this operation
     * @param command the command to be run
     * @param preference the [ReadPreference] to be used when executing the command
     * @return a publisher containing the command result
     * @since 1.7
     */
    fun runCommand(
        command: Bson,
        preference: ReadPreference = ReadPreference.primary(),
        session: ClientSession? = null
    ): Publisher<BsonDocument> = when (session) {
        null -> database.runCommand(command, preference, BsonDocument::class.java)
        else -> database.runCommand(session, command, preference, BsonDocument::class.java)
    }

    /**
     * Drops this database.
     *
     * @param session the client session with which to associate this operation
     * @return a publisher identifying when the database has been dropped
     * @since 1.7
     */
    fun drop(
        session: ClientSession? = null
    ): Publisher<Void> = when (session) {
        null -> database.drop()
        else -> database.drop(session)
    }

    /**
     * Gets the names of all the collections in this database.
     *
     * @param session the client session with which to associate this operation
     * @return a publisher with all the names of all the collections in this database
     * @since 1.7
     */
    fun listCollectionNames(
        session: ClientSession? = null
    ): Publisher<String> = when (session) {
        null -> database.listCollectionNames()
        else -> database.listCollectionNames(session)
    }

    /**
     * Finds all the collections in this database.
     *
     * @param session the client session with which to associate this operation.
     * @return the fluent list collections interface
     * @since 1.7
     */
    fun listCollections(
        session: ClientSession? = null
    ): ListCollectionsPublisher<BsonDocument> = when (session) {
        null -> database.listCollections(BsonDocument::class.java)
        else -> database.listCollections(session, BsonDocument::class.java)
    }

    /**
     * Create a new collection with the selected options
     *
     * @param session the client session with which to associate this operation
     * @param name the name for the new collection to create
     * @param options        various options for creating the collection
     * @return a publisher identifying when the collection has been created
     * @since 1.7
     */
    fun createCollection(
        name: String,
        options: CreateCollectionOptions = CreateCollectionOptions(),
        session: ClientSession? = null
    ): Publisher<Void> = when (session) {
        null -> database.createCollection(name, options)
        else -> database.createCollection(session, name, options)
    }

    /**
     * Creates a view with the given name, backing collection/view name, aggregation pipeline, and options that defines the view.
     *
     * @param session the client session with which to associate this operation
     * @param name the name of the view to create
     * @param on   the backing collection/view for the view
     * @param pipeline the pipeline that defines the view
     * @param options various options for creating the view
     * @return an observable identifying when the collection view has been created
     * @since 1.7
     */
    fun createView(
        name: String,
        on: String,
        pipeline: List<Bson>,
        options: CreateViewOptions = CreateViewOptions(),
        session: ClientSession? = null
    ): Publisher<Void> = when (session) {
        null -> database.createView(name, on, pipeline, options)
        else -> database.createView(session, name, on, pipeline, options)
    }

    /**
     * Creates a change stream for this database.
     *
     * @param session the client session with which to associate this operation
     * @param pipeline the aggregation pipeline to apply to the change stream
     * @return the change stream iterable
     * @since 1.9
     */
    fun watch(
        pipeline: List<Bson> = emptyList(),
        session: ClientSession? = null
    ): ChangeStreamPublisher<BsonDocument> = when (session) {
        null -> database.watch(pipeline, BsonDocument::class.java)
        else -> database.watch(session, pipeline, BsonDocument::class.java)
    }

    /**
     * Runs an aggregation framework pipeline on the database for pipeline stages
     * that do not require an underlying collection, such as `$currentOp` and `$listLocalSessions`.
     *
     * @param session the client session with which to associate this operation
     * @param pipeline the aggregation pipeline
     * @return an iterable containing the result of the aggregation operation
     * @since 1.11
     */
    fun aggregate(
        pipeline: List<Bson>,
        session: ClientSession? = null
    ): AggregatePublisher<BsonDocument> = when (session) {
        null -> database.aggregate(pipeline, BsonDocument::class.java)
        else -> database.aggregate(session, pipeline, BsonDocument::class.java)
    }
}
