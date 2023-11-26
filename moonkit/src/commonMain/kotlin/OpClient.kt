/*
 *	Copyright 2023 cufy.org
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
package org.cufy.moonkit

import org.cufy.mongodb.MongoClient
import org.cufy.mongodb.MongoDatabase
import org.cufy.mongodb.createMongoClient
import org.cufy.mongodb.get
import org.cufy.moonkit.internal.GlobalOpClientImpl
import org.cufy.moonkit.internal.OpClientImpl

/* ============= ------------------ ============= */

/**
 * The client that can execute [monop operations][Operation]
 * using a predefined list of [monop operators][Operator].
 *
 * The [MongoClient] instance backing an [OpClient]
 * can be obtained through [OpClient.get].
 *
 * Instances of this class are immutable and stateless.
 * Yet, its internals can be late-initialized or lazy
 * evaluated.
 *
 * @see GlobalOpClient
 * @author LSafer
 * @since 2.0.0
 */
interface OpClient {
    /**
     * Return the [MongoClient] instance backing this client.
     */
    suspend fun get(): MongoClient

    /**
     * Return the default database set for this client.
     */
    suspend fun defaultDatabase(): MongoDatabase?

    /**
     * Enqueue the given [operations].
     *
     * This function will wait for the
     * initialization of this client before
     * enqueuing the given [operations].
     * Meanwhile, it won't wait for the execution
     * of the given [operations].
     *
     * If an operation was not handled by any
     * operator the operation will be
     * [canceled][Operation.cancel] before
     * returning from this function.
     *
     * @since 2.0.0
     */
    suspend fun enqueue(operations: Set<Operation<*>>)

    /**
     * The global [OpClient] instance.
     *
     * ### Example Usage:
     *
     * ```kotlin
     * val client = createOpClient("mongodb://localhost")
     *
     * OpClient.use(client)
     * ```
     *
     * @author LSafer
     * @since 2.0.0
     */
    companion object : GlobalOpClient by GlobalOpClient()
}

/**
 * A variant of [OpClient] that can be late-initialized.
 *
 * Useful for creating global instances of [OpClient].
 *
 * @author LSafer
 * @since 2.0.0
 */
interface GlobalOpClient : OpClient {
    /**
     * Initialize this client to use the given
     * [client] as the backing instance.
     *
     * @throws IllegalStateException if already initialized.
     * @since 2.0.0
     */
    fun use(client: OpClient)
}

/**
 * If [name] is not null, return database instance with the given [name].
 * Otherwise, return the default database or null if no default database is set.
 */
internal suspend inline fun OpClient.databaseOrDefaultDatabase(name: String?): MongoDatabase? {
    return when (name) {
        null -> defaultDatabase()
        else -> get()[name]
    }
}

/* ============= ------------------ ============= */

/**
 * Create a new instance of [GlobalOpClient].
 */
fun GlobalOpClient(): GlobalOpClient {
    return GlobalOpClientImpl()
}

/**
 * Create a new [OpClient] with the given arguments.
 *
 * @param connectionString the connection.
 * @param defaultDatabaseName the name of the default database.
 * @param operators the operators to be used by the client.
 * @since 2.0.0
 */
fun createOpClient(
    connectionString: String,
    defaultDatabaseName: String? = null,
    operators: List<Operator> = DefaultOperators
): OpClient {
    val client = createMongoClient(connectionString)
    val defaultDatabase = defaultDatabaseName?.let { client[it] }
    return createOpClient(client, defaultDatabase, operators)
}

/**
 * Create a new [OpClient] with the given arguments.
 *
 * @param client the backing client.
 * @param defaultDatabase the default database instance.
 * @param operators the operators to be used by the client.
 * @since 2.0.0
 */
fun createOpClient(
    client: MongoClient,
    defaultDatabase: MongoDatabase? = null,
    operators: List<Operator> = DefaultOperators
): OpClient {
    return OpClientImpl(client, defaultDatabase, operators)
}

/* ============= ------------------ ============= */

/**
 * Enqueue the given [operation].
 *
 * @see OpClient.enqueue
 * @since 2.0.0
 */
suspend fun OpClient.enqueue(operation: Operation<*>) {
    enqueue(setOf(operation))
}

/**
 * Enqueue the given [operations].
 *
 * @see OpClient.enqueue
 * @since 2.0.0
 */
suspend fun OpClient.enqueue(vararg operations: Operation<*>) {
    enqueue(setOf(*operations))
}

/* ============= ------------------ ============= */
