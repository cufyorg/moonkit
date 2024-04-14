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

import org.cufy.mongodb.MongoDatabase

/* ============= ------------------ ============= */

/**
 * A type of ops that is focused on [MongoDatabase].
 * This type of operation only produces operations
 * that are of type [DatabaseOperation].
 *
 * @author LSafer
 * @since 2.0.0
 */
interface DatabaseOp<T> : Op<T> {
    /**
     * The name of the database of the collection
     * to operate on.
     * Set to `null` to use [OpClient.defaultDatabase].
     *
     * @since 2.0.0
     */
    val database: String?

    override fun createOperation(): DatabaseOperation<T>
}

/* ============= ------------------ ============= */

private fun DatabaseOp<*>.inferToString(): String {
    val name = this::class.simpleName ?: "DatabaseOp"
    val address = hashCode().toString(16)
    return "$name($database, ...)@$address"
}

/* ============= ------------------ ============= */

/**
 * Create a custom [DatabaseOp] with the given [block] being its default behaviour.
 *
 * @param database name of the database for the operation. (null for [OpClient.defaultDatabase])
 * @since 2.0.0
 */
fun <T> DatabaseOp(
    database: String? = null,
    block: suspend (MongoDatabase) -> T,
): DatabaseOp<T> {
    return object : DatabaseOp<T> {
        override val database = database

        override fun toString() = inferToString()

        override fun createOperation(): DatabaseOperation<T> =
            DatabaseOperation(database, block)
    }
}

/* ============= ------------------ ============= */
