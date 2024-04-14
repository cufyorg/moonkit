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
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.cufy.mongodb

import com.mongodb.reactivestreams.client.MongoClients
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.cufy.bson.BsonDocument
import org.cufy.bson.java
import org.cufy.bson.kt
import java.io.Closeable

/* ============= ------------------ ============= */

actual data class MongoClient(val java: JavaMongoClient) : Closeable {
    override fun close() = java.close()
    override fun toString() = "MongoClient#${hashCode()}"
}

/* ============= ------------------ ============= */

/**
 * Create a new [MongoClient] instance wrapping
 * this client instance.
 *
 * @since 2.0.0
 */
val JavaMongoClient.kt: MongoClient
    get() = MongoClient(this)

/* ============= ------------------ ============= */

actual fun createMongoClient(connectionString: String): MongoClient {
    return MongoClients.create(connectionString).kt
}

/* ============= ------------------ ============= */

actual val MongoClient.clusterDescription: ClusterDescription
    get() = java.clusterDescription

actual operator fun MongoClient.get(name: String): MongoDatabase {
    return java.getDatabase(name).kt
}

/* ============= ------------------ ============= */

// TODO MongoClient.watch(pipeline, options, session)

actual suspend fun MongoClient.listDatabaseNames(
    session: ClientSession?,
): List<String> {
    val publisher = when (session) {
        null -> java.listDatabaseNames()
        else -> java.listDatabaseNames(session.java)
    }
    return publisher
        .asFlow()
        .toList()
}

actual suspend fun MongoClient.listDatabases(
    filter: BsonDocument,
    options: ListDatabasesOptions,
    session: ClientSession?,
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

actual suspend fun MongoClient.startSession(
    options: ClientSessionOptions,
): ClientSession {
    val publisher = java.startSession(options.java)
    return publisher.awaitSingle().kt
}

/* ============= ------------------ ============= */
