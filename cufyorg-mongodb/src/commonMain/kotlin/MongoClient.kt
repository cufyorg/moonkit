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
import java.io.Closeable

/* ============= ------------------ ============= */

/**
 * A generic-free coroutine dependant wrapper for
 * a mongodb client.
 *
 * @see com.mongodb.reactivestreams.client.MongoClient
 * @author LSafer
 * @since 2.0.0
 */
expect interface MongoClient : Closeable

/* ============= ------------------ ============= */

/**
 * Create a new client with the given connection string.
 *
 * @param connectionString the connection.
 * @return the client.
 * @since 2.0.0
 * @see com.mongodb.reactivestreams.client.MongoClients.create
 */
expect fun createMongoClient(connectionString: String): MongoClient

// TODO createMongoClient(...) variants

/* ============= ------------------ ============= */

/**
 * Gets the current cluster description.
 *
 * @see com.mongodb.client.MongoClient.getClusterDescription
 * @since 2.0.0
 */
expect val MongoClient.clusterDescription: ClusterDescription

/**
 * Gets the database with the given name.
 *
 * @param name the name of the database
 * @return the database
 * @since 2.0.0
 * @see com.mongodb.reactivestreams.client.MongoClient.getDatabase
 */
expect operator fun MongoClient.get(name: String): MongoDatabase

/* ============= ------------------ ============= */

// TODO MongoClient.watch(pipeline, options, session)

/**
 * Get a list of the database names
 *
 * @param session the client session with which to associate this operation
 * @return an iterable containing all the names of all the databases
 * @since 2.0.0
 * @see com.mongodb.reactivestreams.client.MongoClient.listDatabaseNames
 */
expect suspend fun MongoClient.listDatabaseNames(
    session: ClientSession? = null,
): List<String>

//

/**
 * Gets the list of databases
 *
 * @param filter the query filter to apply to the query.
 * @param options the operation options.
 * @param session the client session with which to associate this operation
 * @return the databases list
 * @since 2.0.0
 * @see com.mongodb.reactivestreams.client.MongoClient.listDatabases
 */
expect suspend fun MongoClient.listDatabases(
    filter: BsonDocument = BsonDocument.Empty,
    options: ListDatabasesOptions = ListDatabasesOptions(),
    session: ClientSession? = null,
): List<BsonDocument>

/**
 * Gets the list of databases
 *
 * @param filter the query filter to apply to the query.
 * @param options the operation options.
 * @param session the client session with which to associate this operation
 * @return the databases list
 * @since 2.0.0
 * @see com.mongodb.reactivestreams.client.MongoClient.listDatabases
 */
suspend fun MongoClient.listDatabases(
    filter: BsonDocumentBlock,
    session: ClientSession? = null,
    options: ListDatabasesOptions.() -> Unit = {},
): List<BsonDocument> {
    return listDatabases(
        filter = BsonDocument(filter),
        options = ListDatabasesOptions(options),
        session = session
    )
}

//

/**
 * Creates a client session.
 *
 * @param options the options for the client session
 * @return the client session.
 * @since 2.0.0
 * @see com.mongodb.reactivestreams.client.MongoClient.startSession
 */
expect suspend fun MongoClient.startSession(
    options: ClientSessionOptions = ClientSessionOptions(),
): ClientSession

/**
 * Creates a client session.
 *
 * @param options the options for the client session
 * @return the client session.
 * @since 2.0.0
 * @see com.mongodb.reactivestreams.client.MongoClient.startSession
 */
suspend fun MongoClient.startSession(
    options: ClientSessionOptions.() -> Unit,
): ClientSession {
    return startSession(
        options = ClientSessionOptions(options)
    )
}

/* ============= ------------------ ============= */
