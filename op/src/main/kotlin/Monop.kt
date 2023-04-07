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
package org.cufy.monop

import org.cufy.mongodb.*
import org.cufy.monop.internal.MonopImpl

/* ============= ------------------ ============= */

/**
 * A database instance that uses [Operator]s to
 * execute [Operation]s.
 *
 * Initially, a Monop instance is not connected
 * and all the operations passed to it will wait
 * until it does.
 */
interface Monop {
    val client: MongoClient
    val database: MongoDatabase

    /**
     * Connect with the given [client] and [database].
     *
     * @throws IllegalStateException if already connected.
     * @since 2.0.0
     */
    fun connect(client: MongoClient, database: MongoDatabase)

    /**
     * Add the given [operators].
     *
     * @throws IllegalStateException if already connected.
     * @since 2.0.0
     */
    operator fun plusAssign(operators: Iterable<Operator>)

    /**
     * Remove the given [operators].
     *
     * @throws IllegalStateException if already connected.
     * @since 2.0.0
     */
    operator fun minusAssign(operators: Iterable<Operator>)

    /**
     * Execute the given [operations].
     *
     * If this instance has not connected yet.
     * The operations won't be handled to the
     * operators until it does.
     *
     * If an operation was not handled by any
     * operator the operation will be [canceled][Operation.cancel].
     *
     * @since 2.0.0
     */
    operator fun invoke(operations: List<Operation<*>>)

    /**
     * The global monop instance.
     *
     * @author LSafer
     * @since 2.0.0
     */
    companion object : Monop by Monop() {
        init {
            this += DefaultOperators
        }
    }
}

/**
 * Create a new monop instance.
 *
 * @since 2.0.0
 */
@OptIn(InternalMonopApi::class)
fun Monop(): Monop {
    return MonopImpl()
}

/**
 * Create a new monop instance.
 *
 * @since 2.0.0
 */
fun Monop(client: MongoClient, database: MongoDatabase): Monop {
    return Monop().apply { connect(client, database) }
}

/**
 * Create a new monop instance.
 *
 * @since 2.0.0
 */
fun Monop(connectionString: String, name: String): Monop {
    return Monop().apply { connect(connectionString, name) }
}

/* ============= ------------------ ============= */

/**
 * Connect with the given [connectionString] and [name].
 *
 * @throws IllegalStateException if already connected.
 * @since 2.0.0
 */
fun Monop.connect(connectionString: String, name: String) {
    val client = createMongoClient(connectionString)
    connect(client, client[name])
}

/**
 * Add the given [operator].
 *
 * @throws IllegalStateException if already connected.
 * @sin 2.0.0
 */
operator fun Monop.plusAssign(operator: Operator) {
    this += listOf(operator)
}

/**
 * Remove the given [operator].
 *
 * @throws IllegalStateException if already connected.
 * @since 2.0.0
 */
operator fun Monop.minusAssign(operator: Operator) {
    this -= listOf(operator)
}

/**
 * Execute the given [operations].
 *
 * If this instance has not connected yet.
 * The operations won't be handled to the
 * operators until it does.
 *
 * If an operation was not handled by any
 * operator the operation will be [canceled][Operation.cancel].
 *
 * @since 2.0.0
 */
operator fun Monop.invoke(vararg operations: Operation<*>) {
    this(operations.asList())
}

/**
 * A shortcut to get a [MongoCollection] instance
 * from a monop instance.
 *
 * @since 2.0.0
 */
operator fun Monop.get(collection: MonopCollection): MongoCollection {
    return database[collection.name]
}

/* ============= ------------------ ============= */
