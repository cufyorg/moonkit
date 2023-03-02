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
import com.mongodb.reactivestreams.client.ChangeStreamPublisher
import com.mongodb.reactivestreams.client.ClientSession
import com.mongodb.reactivestreams.client.ListDatabasesPublisher
import com.mongodb.reactivestreams.client.MongoClient
import org.cufy.bson.Bson
import org.cufy.bson.BsonDocument
import org.reactivestreams.Publisher
import java.io.Closeable

/**
 * A generic-free coroutine dependant wrapper for
 * [MongoClient]s
 *
 * The document class will always be [BsonDocument].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class MonktClient(
    /**
     * The wrapped client.
     */
    val client: MongoClient
) : Closeable by client {
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
    val clusterDescription: ClusterDescription get() = client.clusterDescription

    /**
     * Gets the database with the given name.
     *
     * @param name the name of the database
     * @return the database
     */
    fun getDatabase(name: String): MonktDatabase =
        MonktDatabase(client.getDatabase(name))

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
        null -> client.listDatabaseNames()
        else -> client.listDatabaseNames(session)
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
        null -> client.listDatabases(BsonDocument::class.java)
        else -> client.listDatabases(session, BsonDocument::class.java)
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
        null -> client.watch(pipeline, BsonDocument::class.java)
        else -> client.watch(session, pipeline, BsonDocument::class.java)
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
        client.startSession(options)
}
