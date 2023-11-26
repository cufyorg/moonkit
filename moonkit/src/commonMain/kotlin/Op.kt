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

import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

/* ============= ------------------ ============= */

/**
 * A stateless instructions that can be used to
 * create [Operation] instances to be then
 * executed by an [Operator].
 *
 * Op instances can be used to create multiple
 * [Operation] instances or discarded without
 * being used.
 *
 * @author LSafer
 * @since 2.0.0
 */
interface Op<T> {
    /**
     * Create a new operation from this recipe.
     *
     * @since 2.0.0
     */
    fun createOperation(): Operation<T>
}

/* ============= ------------------ ============= */

/**
 * Create a new [Operation] using [Op.createOperation]
 * then enqueue it to [client].
 *
 * @see OpClient.enqueue
 * @return the created operation.
 * @since 2.0.0
 */
suspend fun <T> Op<T>.enqueue(client: OpClient = OpClient): Operation<T> {
    return createOperation().enqueue(client)
}

/**
 * Create a new [Operation] using [Op.createOperation]
 * then enqueue it to [client] and await the output.
 *
 * @see OpClient.enqueue
 * @return the operation output.
 * @since 2.0.0
 */
suspend operator fun <T> Op<T>.invoke(client: OpClient = OpClient): T {
    return createOperation()(client)
}

/* ============= ------------------ ============= */

/**
 * Create a [BlockOp] that depends on [this] operation
 * and executes the given [block] of code.
 *
 * @param block the operation block.
 * @author LSafer
 * @since 2.0.0
 */
fun <T, U> Op<T>.tryMap(block: suspend (Result<T>) -> Result<U>): Op<U> {
    return BlockOp(listOf(this)) { values -> block(values.single()) }
}

/**
 * Create a [BlockOp] that depends on [this] operation
 * and executes the given [block] of code.
 *
 * @param block the operation block.
 * @author LSafer
 * @since 2.0.0
 */
fun <T, U> Op<T>.tryMapCatching(block: suspend (Result<T>) -> U): Op<U> {
    return tryMap { runCatching { block(it) } }
}

/**
 * Create a [BlockOp] that depends on [this] operation
 * and executes the given [block] of code.
 *
 * @param block the operation block.
 * @author LSafer
 * @since 2.0.0
 */
fun <T, U> Op<T>.map(block: suspend (T) -> Result<U>): Op<U> {
    return tryMap { it.fold({ block(it) }, { failure(it) }) }
}

/**
 * Create a [BlockOp] that depends on [this] operation
 * and executes the given [block] of code.
 *
 * @param block the operation block.
 * @author LSafer
 * @since 2.0.0
 */
fun <T, U> Op<T>.mapCatching(block: suspend (T) -> U): Op<U> {
    return tryMap { it.mapCatching { block(it) } }
}

/**
 * Create a [BlockOp] that depends on [this] operation
 * and executes the given [block] of code only if the
 * value is not `null`.
 *
 * @param block the operation block.
 * @author LSafer
 * @since 2.0.0
 */
fun <T, U> Op<T>.mapNotNull(block: suspend (T & Any) -> Result<U>): Op<U?> {
    return tryMap { it.fold({ if (it == null) success(null) else block(it) }, { failure(it) }) }
}

/**
 * Create a [BlockOp] that depends on [this] operation
 * and executes the given [block] of code only if the
 * value is not `null`.
 *
 * @param block the operation block.
 * @author LSafer
 * @since 2.0.0
 */
fun <T, U> Op<T>.mapNotNullCatching(block: suspend (T & Any) -> U): Op<U?> {
    return tryMap { it.mapCatching { it?.let { block(it) } } }
}

/* ============= ------------------ ============= */
