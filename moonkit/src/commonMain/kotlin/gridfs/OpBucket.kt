/*
 *	Copyright 2023 cufy.org and meemer.com
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
package org.cufy.moonkit.gridfs

import org.cufy.mongodb.gridfs.MongoBucket
import org.cufy.mongodb.gridfs.createMongoBucket
import org.cufy.moonkit.Op
import org.cufy.moonkit.OpClient
import org.cufy.moonkit.databaseOrDefaultDatabase

/* ============= ------------------ ============= */

/**
 * A convenient class that holds bare minimal
 * data needed for using some bucket.
 *
 * @author LSafer
 * @since 2.0.0
 */
interface OpBucket {
    /**
     * The default bucket.
     */
    companion object : OpBucket {
        override val name = "fs"
    }

    /**
     * The name of the database of the bucket
     * to operate on.
     * Set to `null` to use [OpClient.defaultDatabase].
     *
     * @since 2.0.0
     */
    val database: String? get() = null

    /**
     * The bucket name.
     *
     * @since 2.0.0
     */
    val name: String get() = inferName()
}

/* ============= ------------------ ============= */

private fun OpBucket.inferName(): String {
    return this::class.simpleName ?: error("Cannot infer bucket name for $this")
}

private fun OpBucket.inferToString(): String {
    return "OpBucket($database, $name)"
}

/* ============= ------------------ ============= */

/**
 * Construct a new [OpBucket] with the given [name] and [database].
 *
 * @since 2.0.0
 */
fun OpBucket(name: String, database: String? = null): OpBucket {
    return object : OpBucket {
        override val database = database
        override val name = name

        override fun toString() = inferToString()
    }
}

/* ============= ------------------ ============= */

/**
 * Return a [MongoBucket] instance corresponding
 * to this bucket using the given [client].
 */
suspend fun OpBucket.get(client: OpClient = OpClient): MongoBucket {
    val database = client.databaseOrDefaultDatabase(database)
    // if this.database is null yet no default database is set
    database ?: error("Bucket requires default database yet default database not set.")
    return createMongoBucket(database, name)
}

/**
 * Create an [Op] that executes the given [block]
 * with a [MongoBucket] corresponding to this
 * bucket.
 *
 * @since 2.0.0
 */
fun <T> OpBucket.op(block: suspend MongoBucket.() -> T): Op<T> {
    return BucketOp(name, database, block)
}

/* ============= ------------------ ============= */
