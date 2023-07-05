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
package org.cufy.monop.gridfs

import org.cufy.mongodb.gridfs.MongoBucket
import org.cufy.monop.Op
import org.cufy.monop.OpClient

/* ============= ------------------ ============= */

/**
 * A type of ops that is focused on [MongoBucket].
 * This type of operations only produces operations
 * that are of type [BucketOperation].
 *
 * @author LSafer
 * @since 2.0.0
 */
interface BucketOp<T> : Op<T> {
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

    override fun createOperation(): BucketOperation<T>
}

private fun BucketOp<*>.inferToString(): String {
    val name = this::class.simpleName ?: "BucketOp"
    val address = System.identityHashCode(this).toString(16)
    return "$name($database, $bucket, ...)@$address"
}

/* ============= ------------------ ============= */

/**
 * Create a custom [BucketOp] with the given [block] being its default behaviour.
 *
 * @param bucket the name of the bucket for the operation.
 * @param database name of the database for the operation. (null for [OpClient.defaultDatabase])
 * @since 2.0.0
 */
fun <T> BucketOp(bucket: String, database: String? = null, block: suspend (MongoBucket) -> T): BucketOp<T> {
    return object : BucketOp<T> {
        override val database = database
        override val bucket = bucket

        override fun toString() = inferToString()

        override fun createOperation(): BucketOperation<T> =
            BucketOperation(bucket, database, block)
    }
}

/* ============= ------------------ ============= */
