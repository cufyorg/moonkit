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

import com.mongodb.ClientSessionOptions
import com.mongodb.connection.ClusterDescription
import com.mongodb.connection.ClusterSettings
import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.ChangeStreamPublisher
import com.mongodb.reactivestreams.client.ClientSession
import com.mongodb.reactivestreams.client.ListDatabasesPublisher
import com.mongodb.reactivestreams.client.MongoClient
import org.cufy.bson.Bson
import org.cufy.bson.BsonDocument
import org.reactivestreams.Publisher
import java.io.Closeable

/**
 * Create a new client with the given connection string.
 *
 * @param connectionString the connection.
 * @return the client.
 * @since 2.0.0
 * @see MongoClients.create
 */
fun createMonktClient(connectionString: String): MonktClient {
    val client = MongoClients.create(connectionString)
    return MonktClient(client)
}

/**
 * Create a new [MonktClient] instance wrapping
 * the given [client] instance.
 *
 * @param client the client to be wrapped.
 * @since 2.0.0
 */
fun MonktClient(client: MongoClient): MonktClient {
    return object : MonktClient {
        override val java = client
    }
}

/**
 * A utility interface for creating [MonktClient]
 * implementations that delegates to another
 * [MonktClient] implementation.
 *
 * @author LSafer
 * @since 2.0.0
 */
interface MonktClientDelegate : MonktClient {
    /**
     * The client delegating to.
     */
    val client: MonktClient

    override val java get() = client.java
}

/**
 * A generic-free coroutine dependant wrapper for
 * [MongoClient]s
 *
 * The document class will always be [BsonDocument].
 *
 * @author LSafer
 * @since 2.0.0
 */
interface MonktClient : Closeable {
    /**
     * The wrapped client.
     */
    val java: MongoClient

    override fun close() = java.close()

    /**
     * Gets the current cluster description.
     *
     * This method will not block, meaning that it may return a [ClusterDescription] whose `clusterType` is unknown
     * and whose [com.mongodb.connection.ServerDescription]s are all in the connecting state. If the application requires
     * notifications after the driver has connected to a member of the cluster, it should register a `ClusterListener` via
     * the [ClusterSettings] in [com.mongodb.MongoClientSettings].
     *
     * @return the current cluster description
     * @see ClusterSettings.Builder.addClusterListener
     * @see com.mongodb.MongoClientSettings.Builder.applyToClusterSettings
     * @since 4.1
     */
    val clusterDescription: ClusterDescription get() = java.clusterDescription

    /**
     * Gets the database with the given name.
     *
     * @param name the name of the database
     * @return the database
     */
    fun getDatabase(name: String): MonktDatabase =
        MonktDatabase(java.getDatabase(name))

    /**
     * Get a list of the database names
     *
     * @param session the client session with which to associate this operation
     * @return an iterable containing all the names of all the databases
     * @since 1.7
     */
    fun listDatabaseNames(
        session: ClientSession? = null
    ): Publisher<String> = when (session) {
        null -> java.listDatabaseNames()
        else -> java.listDatabaseNames(session)
    }

    /**
     * Gets the list of databases
     *
     * @param session the client session with which to associate this operation
     * @return the fluent list databases interface
     * @since 1.7
     */
    fun listDatabases(
        session: ClientSession? = null
    ): ListDatabasesPublisher<BsonDocument> = when (session) {
        null -> java.listDatabases(BsonDocument::class.java)
        else -> java.listDatabases(session, BsonDocument::class.java)
    }

    /**
     * Creates a change stream for this client.
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
        null -> java.watch(pipeline, BsonDocument::class.java)
        else -> java.watch(session, pipeline, BsonDocument::class.java)
    }

    /**
     * Creates a client session.
     *
     * @param options the options for the client session
     * @return a publisher for the client session.
     * @since 1.7
     */
    fun startSession(
        options: ClientSessionOptions = ClientSessionOptions.builder().build()
    ): Publisher<ClientSession> =
        java.startSession(options)
}
