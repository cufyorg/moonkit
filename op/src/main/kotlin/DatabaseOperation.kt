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

import kotlinx.coroutines.*
import org.cufy.mongodb.MongoDatabase

/* ============= ------------------ ============= */

/**
 * A type of operations that is focused on [MongoDatabase].
 * This type of operations have a default behaviour that only
 * require an instance of [MongoDatabase].
 *
 * @author LSafer
 * @since 2.0.0
 */
interface DatabaseOperation<T> : Operation<T> {
    /**
     * The name of the database.
     * Set to `null` to use the database provided
     * when the operation is enqueued.
     *
     * @since 2.0.0
     */
    val database: String?

    /**
     * The default behaviour of this operation.
     *
     * **NOTE: errors throw by this function won't be caught safely.**
     *
     * @since 2.0.0
     */
    suspend fun completeWithDefaultBehaviour(database: MongoDatabase)
}

private fun DatabaseOperation<*>.inferToString(): String {
    val name = this::class.simpleName ?: "DatabaseOperation"
    val address = System.identityHashCode(this).toString(16)
    return "$name($database, ...)@$address"
}

/* ============= ------------------ ============= */

/**
 * Create a custom [DatabaseOperation] with the given [block] being its default behaviour.
 *
 * @param database name of the database for the operation. (null for [OpClient.defaultDatabase])
 * @since 2.0.0
 */
fun <T> DatabaseOperation(database: String? = null, block: suspend (MongoDatabase) -> T): DatabaseOperation<T> {
    return object : DatabaseOperation<T>, CompletableDeferred<T> by CompletableDeferred() {
        override val database = database

        override fun toString() = inferToString()

        override suspend fun completeWithDefaultBehaviour(database: MongoDatabase) {
            completeWith(runCatching { block(database) })
        }
    }
}

/* ============= ------------------ ============= */

@ExperimentalMonopApi
val DatabaseOperator = createOperatorForType<DatabaseOperation<*>> { operations ->
    val leftovers = mutableSetOf<DatabaseOperation<*>>()
    for ((databaseName, databaseOperations) in operations.groupBy { it.database }) {
        val database = databaseOrDefaultDatabase(databaseName)

        // if databaseName is null yet no default database is set
        if (database == null) {
            leftovers += databaseOperations
            continue
        }

        databaseOperations.forEach {
            CoroutineScope(Dispatchers.IO).launch {
                it.completeWithDefaultBehaviour(database)
            }
        }
    }

    leftovers
}

/* ============= ------------------ ============= */
