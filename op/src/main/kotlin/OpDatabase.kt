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

import org.cufy.mongodb.MongoDatabase
import org.cufy.mongodb.get

/* ============= ------------------ ============= */

/**
 * A convenient class that holds bare minimal
 * data needed for using some database.
 *
 * A [MongoDatabase] instance from a [OpDatabase]
 * can be obtained through [OpDatabase.get] given
 * a [OpClient].
 *
 * @author LSafer
 * @since 2.0.0
 */
interface OpDatabase {
    /**
     * The database name.
     */
    val name: String get() = inferName()
}

private fun OpDatabase.inferName(): String {
    return this::class.simpleName ?: error("Cannot infer database name for $this")
}

private fun OpDatabase.inferToString(): String {
    return "OpDatabase($name)"
}

/* ============= ------------------ ============= */

/**
 * Construct a new [OpDatabase] with the given [name].
 *
 * @since 2.0.0
 */
fun OpDatabase(name: String): OpDatabase {
    return object : OpDatabase {
        override val name = name

        override fun toString() = inferToString()
    }
}

/* ============= ------------------ ============= */

/**
 * Return a [MongoDatabase] instance corresponding
 * to this database using the given [client].
 */
suspend fun OpDatabase.get(client: OpClient = OpClient): MongoDatabase {
    return client.get()[name]
}

/**
 * Create an [Op] that executes the given [block]
 * with a [MongoDatabase] corresponding to this
 * database.
 *
 * @since 2.0.0
 */
fun <T> OpDatabase.op(block: MongoDatabase.() -> T): Op<T> {
    return DatabaseOp(name, block)
}

/* ============= ------------------ ============= */
