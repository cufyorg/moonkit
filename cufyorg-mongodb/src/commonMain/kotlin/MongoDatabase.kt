/*
 *	Copyright 2022-2023 cufy.org
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
package org.cufy.mongodb

import org.cufy.bson.BsonDocument
import org.cufy.bson.BsonDocumentBlock

/* ============= ------------------ ============= */

/**
 * A generic-free coroutine dependant wrapper for
 * a mongodb database.
 *
 * @see com.mongodb.reactivestreams.client.MongoDatabase
 * @author LSafer
 * @since 2.0.0
 */
expect interface MongoDatabase

/* ============= ------------------ ============= */

/**
 * Gets the name of the database.
 *
 * @see com.mongodb.client.MongoDatabase.getName
 * @since 2.0.0
 */
expect val MongoDatabase.name: String

/**
 * Get the read preference for the MongoDatabase.
 *
 * @see com.mongodb.client.MongoDatabase.getReadPreference
 * @since 2.0.0
 */
expect val MongoDatabase.readPreference: ReadPreference

/**
 * Get the write concern for the MongoDatabase.
 *
 * @see com.mongodb.client.MongoDatabase.getWriteConcern
 * @since 2.0.0
 */
expect val MongoDatabase.writeConcern: WriteConcern

/**
 * Get the read concern for the MongoDatabase.
 *
 * @see com.mongodb.client.MongoDatabase.getWriteConcern
 * @since 2.0.0
 */
expect val MongoDatabase.readConcern: ReadConcern

/**
 * Gets a collection.
 *
 * @param name the name of the collection to return
 * @return the collection
 * @since 2.0.0
 * @see com.mongodb.reactivestreams.client.MongoDatabase.getCollection
 */
expect operator fun MongoDatabase.get(name: String): MongoCollection

/* ============= ------------------ ============= */

/**
 * Runs an aggregation framework pipeline on the
 * database for pipeline stages that do not
 * require an underlying collection, such as
 * `$currentOp` and `$listLocalSessions`.
 *
 * @param session the client session with which to associate this operation
 * @param pipeline the aggregation pipeline
 * @param options the operation options.
 * @return an iterable containing the result of the aggregation operation
 * @since 2.0.0
 * @see com.mongodb.client.MongoDatabase.aggregate
 */
expect suspend fun MongoDatabase.aggregate(
    pipeline: List<BsonDocument>,
    options: AggregateOptions = AggregateOptions(),
    session: ClientSession? = null,
): List<BsonDocument>

/**
 * Runs an aggregation framework pipeline on the
 * database for pipeline stages that do not
 * require an underlying collection, such as
 * `$currentOp` and `$listLocalSessions`.
 *
 * @param session the client session with which to associate this operation
 * @param pipeline the aggregation pipeline
 * @param options the operation options.
 * @return an iterable containing the result of the aggregation operation
 * @since 2.0.0
 * @see com.mongodb.client.MongoDatabase.aggregate
 */
suspend fun MongoDatabase.aggregate(
    vararg pipeline: BsonDocumentBlock,
    session: ClientSession? = null,
    options: AggregateOptions.() -> Unit = {},
): List<BsonDocument> {
    return aggregate(
        pipeline = pipeline.map { BsonDocument(it) },
        options = AggregateOptions(options),
        session = session
    )
}

//

/* TODO MongoDatabase.watch(pipeline, options, session) */

//

/**
 * Gets the names of all the collections in this
 * database.
 *
 * @param session the client session with which to associate this operation
 * @return all the names of all the collections in this database
 * @since 2.0.0
 * @see com.mongodb.client.MongoDatabase.listCollectionNames
 */
expect suspend fun MongoDatabase.listCollectionNames(
    session: ClientSession? = null,
): List<String>

//

/**
 * Finds all the collections in this database.
 *
 * @param session the client session with which to associate this operation.
 * @param options the operation options.
 * @return the collections list
 * @since 2.0.0
 * @see com.mongodb.client.MongoDatabase.listCollectionNames
 */
expect suspend fun MongoDatabase.listCollections(
    filter: BsonDocument = BsonDocument.Empty,
    options: ListCollectionsOptions = ListCollectionsOptions(),
    session: ClientSession? = null,
): List<BsonDocument>

/**
 * Finds all the collections in this database.
 *
 * @param session the client session with which to associate this operation.
 * @param options the operation options.
 * @return the collections list
 * @since 2.0.0
 * @see com.mongodb.client.MongoDatabase.listCollectionNames
 */
suspend fun MongoDatabase.listCollections(
    filter: BsonDocumentBlock,
    session: ClientSession? = null,
    options: ListCollectionsOptions.() -> Unit = {},
): List<BsonDocument> {
    return listCollections(
        filter = BsonDocument(filter),
        options = ListCollectionsOptions(options),
        session = session
    )
}

/* ============= ------------------ ============= */

/**
 * Executes command in the context of the current
 * database.
 *
 * @param session the client session with which to associate this operation.
 * @param command the command to be run.
 * @param preference the read preference to be used when executing the command.
 * @return the command result
 * @since 2.0.0
 * @see com.mongodb.client.MongoDatabase.runCommand
 */
expect suspend fun MongoDatabase.runCommand(
    command: BsonDocument,
    preference: ReadPreference = ReadPreference.primary(),
    session: ClientSession? = null,
): BsonDocument?

/**
 * Executes command in the context of the current
 * database.
 *
 * @param session the client session with which to associate this operation.
 * @param command the command to be run.
 * @param preference the read preference to be used when executing the command.
 * @return the command result
 * @since 2.0.0
 * @see com.mongodb.client.MongoDatabase.runCommand
 */
suspend fun MongoDatabase.runCommand(
    command: BsonDocumentBlock,
    preference: ReadPreference = ReadPreference.primary(),
    session: ClientSession? = null,
): BsonDocument? {
    return runCommand(
        command = BsonDocument(command),
        preference = preference,
        session = session
    )
}

//

/**
 * Drops this database.
 *
 * @param session the client session with which to associate this operation
 * @since 2.0.0
 * @see com.mongodb.client.MongoDatabase.drop
 */
expect suspend fun MongoDatabase.drop(
    session: ClientSession? = null,
)

//

/**
 * Create a new collection with the selected options
 *
 * @param session the client session with which to associate this operation
 * @param name the name for the new collection to create
 * @param options various options for creating the collection
 * @since 2.0.0
 * @see com.mongodb.client.MongoDatabase.createCollection
 */
expect suspend fun MongoDatabase.createCollection(
    name: String,
    options: CreateCollectionOptions = CreateCollectionOptions(),
    session: ClientSession? = null,
)

/**
 * Create a new collection with the selected options
 *
 * @param session the client session with which to associate this operation
 * @param name the name for the new collection to create
 * @param options various options for creating the collection
 * @since 2.0.0
 * @see com.mongodb.client.MongoDatabase.createCollection
 */
suspend fun MongoDatabase.createCollection(
    name: String,
    session: ClientSession? = null,
    options: CreateCollectionOptions.() -> Unit = {},
) {
    createCollection(
        name = name,
        options = CreateCollectionOptions(options),
        session = session
    )
}

//

/**
 * Creates a view with the given name, backing
 * collection/view name, aggregation pipeline, and
 * options that defines the view.
 *
 * @param session the client session with which to associate this operation
 * @param name the name of the view to create
 * @param on the backing collection/view for the view
 * @param pipeline the pipeline that defines the view
 * @param options various options for creating the view
 * @since 2.0.0
 * @see com.mongodb.client.MongoDatabase.createView
 */
expect suspend fun MongoDatabase.createView(
    name: String,
    on: String,
    pipeline: List<BsonDocument>,
    options: CreateViewOptions = CreateViewOptions(),
    session: ClientSession? = null,
)

/**
 * Creates a view with the given name, backing
 * collection/view name, aggregation pipeline, and
 * options that defines the view.
 *
 * @param session the client session with which to associate this operation
 * @param name the name of the view to create
 * @param on the backing collection/view for the view
 * @param pipeline the pipeline that defines the view
 * @param options various options for creating the view
 * @since 2.0.0
 * @see com.mongodb.client.MongoDatabase.createView
 */
suspend fun MongoDatabase.createView(
    name: String,
    on: String,
    vararg pipeline: BsonDocumentBlock,
    session: ClientSession? = null,
    options: CreateViewOptions.() -> Unit = {},
) {
    createView(
        name = name,
        on = on,
        pipeline = pipeline.map { BsonDocument(it) },
        options = CreateViewOptions(options), session = session
    )
}

/* ============= ------------------ ============= */
