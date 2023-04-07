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

import com.mongodb.reactivestreams.client.MongoClients
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.cufy.bson.BsonDocument
import org.cufy.bson.BsonDocumentBlock
import org.cufy.bson.EmptyBsonDocument
import org.cufy.bson.java.java
import org.cufy.bson.java.kt
import org.cufy.mongodb.java.JavaMongoClient
import org.cufy.mongodb.java.apply
import org.cufy.mongodb.java.java
import org.cufy.mongodb.java.kt
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
interface MongoClient : Closeable {
    /**
     * The wrapped client.
     */
    val java: JavaMongoClient

    override fun close() {
        java.close()
    }
}

/**
 * Create a new client with the given connection string.
 *
 * @param connectionString the connection.
 * @return the client.
 * @since 2.0.0
 * @see com.mongodb.reactivestreams.client.MongoClients.create
 */
fun createMongoClient(connectionString: String): MongoClient {
    return MongoClients.create(connectionString).kt
}

// TODO createMongoClient(...) variants

/* ============= ------------------ ============= */

/**
 * Gets the current cluster description.
 *
 * @see com.mongodb.client.MongoClient.getClusterDescription
 * @since 2.0.0
 */
val MongoClient.clusterDescription: ClusterDescription
    get() = java.clusterDescription

/**
 * Gets the database with the given name.
 *
 * @param name the name of the database
 * @return the database
 * @since 2.0.0
 * @see com.mongodb.reactivestreams.client.MongoClient.getDatabase
 */
operator fun MongoClient.get(name: String): MongoDatabase {
    return java.getDatabase(name).kt
}

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
suspend fun MongoClient.listDatabaseNames(
    session: ClientSession? = null
): List<String> {
    val publisher = when (session) {
        null -> java.listDatabaseNames()
        else -> java.listDatabaseNames(session.java)
    }
    return publisher
        .asFlow()
        .toList()
}

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
suspend fun MongoClient.listDatabases(
    filter: BsonDocument = EmptyBsonDocument,
    options: ListDatabasesOptions = ListDatabasesOptions(),
    session: ClientSession? = null
): List<BsonDocument> {
    val publisher = when (session) {
        null -> java.listDatabases(org.bson.BsonDocument::class.java)
        else -> java.listDatabases(session.java, org.bson.BsonDocument::class.java)
    }
    return publisher
        .filter(filter.java)
        .apply(options)
        .asFlow()
        .toList()
        .map { it.kt }
}

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
    options: ListDatabasesOptions.() -> Unit = {}
) = listDatabases(BsonDocument(filter), ListDatabasesOptions(options), session)

/* ============= ------------------ ============= */

/**
 * Creates a client session.
 *
 * @param options the options for the client session
 * @return the client session.
 * @since 2.0.0
 * @see com.mongodb.reactivestreams.client.MongoClient.startSession
 */
suspend fun MongoClient.startSession(
    options: ClientSessionOptions = ClientSessionOptions()
): ClientSession {
    val publisher = java.startSession(options.java)
    return publisher.awaitSingle().kt
}

/**
 * Creates a client session.
 *
 * @param options the options for the client session
 * @return the client session.
 * @since 2.0.0
 * @see com.mongodb.reactivestreams.client.MongoClient.startSession
 */
suspend fun MongoClient.startSession(
    options: ClientSessionOptions.() -> Unit
) = startSession(ClientSessionOptions(options))

/* ============= ------------------ ============= */
