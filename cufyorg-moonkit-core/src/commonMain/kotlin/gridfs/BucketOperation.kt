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

import kotlinx.coroutines.*
import org.cufy.mongodb.gridfs.MongoBucket
import org.cufy.mongodb.gridfs.createMongoBucket
import org.cufy.moonkit.*

/* ============= ------------------ ============= */

/**
 * A type of operations that is focused on [MongoBucket].
 * This type of operations have a default behaviour that only
 * require an instance of [MongoBucket].
 *
 * @author LSafer
 * @since 2.0.0
 */
interface BucketOperation<T> : Operation<T> {
    /**
     * The name of the database of the bucket
     * to operate on.
     * Set to `null` to use [OpClient.defaultDatabase].
     *
     * @since 2.0.0
     */
    val database: String?

    /**
     * The name of the bucket this operation
     * is targeting.
     */
    val bucket: String

    /**
     * The default behaviour of this operation.
     *
     * **NOTE: errors throw by this function won't be caught safely.**
     *
     * @since 2.0.0
     */
    suspend fun completeWithDefaultBehaviour(bucket: MongoBucket)
}

/* ============= ------------------ ============= */

private fun BucketOperation<*>.inferToString(): String {
    val name = this::class.simpleName ?: "BucketOperation"
    val address = hashCode().toString(16)
    return "$name($database, $bucket, ...)@$address"
}

/* ============= ------------------ ============= */

/**
 * Create a custom [BucketOperation] with the given [block] being its default behaviour.
 *
 * @param bucket the name of the bucket for the operation.
 * @param database name of the database for the operation. (null for [OpClient.defaultDatabase])
 * @since 2.0.0
 */
fun <T> BucketOperation(
    bucket: String,
    database: String? = null,
    block: suspend (MongoBucket) -> T,
): BucketOperation<T> {
    return object : BucketOperation<T>, CompletableDeferred<T> by CompletableDeferred() {
        override val database = database
        override val bucket = bucket

        override fun toString() = inferToString()

        override suspend fun completeWithDefaultBehaviour(bucket: MongoBucket) {
            completeWith(runCatching { block(bucket) })
        }
    }
}

/* ============= ------------------ ============= */

/**
 * An operator performing operations of type [BucketOperation] in parallel
 * using [BucketOperation.completeWithDefaultBehaviour].
 *
 * @author LSafer
 * @since 2.0.0
 */
@ExperimentalMoonkitApi
val BucketOperator = createOperatorForType<BucketOperation<*>> { operations ->
    val leftovers = mutableSetOf<BucketOperation<*>>()
    for ((databaseName, databaseOperations) in operations.groupBy { it.database }) {
        val database = databaseOrDefaultDatabase(databaseName)

        // if databaseName is null yet no default database is set
        if (database == null) {
            leftovers += databaseOperations
            continue
        }

        for ((bucketName, bucketOperations) in databaseOperations.groupBy { it.bucket }) {
            val bucket = createMongoBucket(database, bucketName)

            bucketOperations.forEach {
                CoroutineScope(Dispatchers.IO).launch {
                    it.completeWithDefaultBehaviour(bucket)
                }
            }
        }
    }

    leftovers
}

/* ============= ------------------ ============= */
