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

import org.cufy.monop.gridfs.BucketOperator

/* ============= ------------------ ============= */

/**
 * A function that can be used to execute multiple
 * [Operation] instances in the background.
 *
 * Can be used to override default execution
 * strategies. Like transforming multiple read or
 * write operations into a single bulk read or write
 * operation.
 *
 * @author LSafer
 * @since 2.0.0
 */
fun interface Operator {
    /**
     * Execute all the given [operations] (non-suspending).
     * Return a list containing operations this
     * operator cannot execute and new operations
     * to be executed.
     *
     * This function should only suspend for required
     * values of the operator itself and not for the
     * execution of the given [operations].
     *
     * For example: awaiting the database instance
     * to be available is allowed. Meanwhile,
     * awaiting creating an index or inserting a
     * document should be done in the background.
     *
     * **DO NOT CALL DIRECTLY**
     *
     * @receiver the client to be used for the execution.
     * @param operations the operations to be executed.
     * @return more operations to execute.
     * @since 2.0.0
     */
    suspend operator fun OpClient.invoke(operations: Set<Operation<*>>): Set<Operation<*>>
}

/**
 * Create an [Operator] instance that only accepts
 * operations of type [T] and returns any operation
 * that is not of type [T] to be processed by a
 * different operator.
 *
 * @param block the operation block. (has [Operator.invoke] semantics)
 * @since 2.0.0
 */
inline fun <reified T : Operation<*>> createOperatorForType(
    crossinline block: suspend OpClient.(Set<T>) -> Iterable<Operation<*>>
): Operator {
    return Operator { operations ->
        val remaining = mutableSetOf<Operation<*>>()
        val matching = mutableSetOf<T>()
        operations.forEach { if (it is T) matching.add(it) else remaining.add(it) }
        remaining + block(this, matching)
    }
}

/* ============= ------------------ ============= */

/**
 * A list of all built-in operators.
 */
@OptIn(ExperimentalMonopApi::class)
val DefaultOperators = listOf(
    BlockOperator,
    CollectionOperator,
    DatabaseOperator,
    BucketOperator,
)

/* ============= ------------------ ============= */
