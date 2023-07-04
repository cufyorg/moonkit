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

import kotlin.Result.Companion.success

/* ============= ------------------ ============= */

/**
 * The recipe for creating a [BlockOperation].
 *
 * @author LSafer
 * @since 2.0.0
 */
data class BlockOp<T, U>(
    val dependencies: List<Op<T>>,
    val block: suspend OpClient.(List<Result<T>>) -> Result<U>
) : Op<U> {
    override fun createOperation() =
        BlockOperation(dependencies.map { it.createOperation() }, block)
}

private fun BlockOp<*, *>.inferToString(): String {
    val name = this::class.simpleName ?: "BlockOp"
    val address = System.identityHashCode(this).toString(16)
    return "$name($dependencies, ...)@$address"
}

/* ============= ------------------ ============= */

/**
 * Create an [Op] that merges the given [dependencies].
 *
 * @since 2.0.0
 */
fun <T> Op(vararg dependencies: Op<T>): Op<List<Result<T>>> {
    return Op(dependencies.asList())
}

/**
 * Create an [Op] that merges the given [dependencies].
 *
 * @since 2.0.0
 */
fun <T> Op(dependencies: List<Op<T>>): Op<List<Result<T>>> {
    return BlockOp(dependencies) { values -> success(values) }
}

/**
 * Create an [Op] that executes the given [block].
 *
 * @since 2.0.0
 */
fun <U> Op(block: suspend OpClient.() -> U): Op<U> {
    return BlockOp(emptyList<Op<Nothing>>()) { _ ->
        runCatching { block(this) }
    }
}

/**
 * Create an [Op] that executes the given [block] with the given [dependencies].
 *
 * @since 2.0.0
 */
fun <T, U> Op(dependencies: List<Op<T>>, block: suspend OpClient.(List<Result<T>>) -> U): Op<U> {
    return BlockOp(dependencies) { values ->
        runCatching { block(this, values) }
    }
}

/**
 * Create an [Op] that executes the given [block] with the given dependencies.
 *
 * @since 2.0.0
 */
@Suppress("UNCHECKED_CAST")
fun <T0, U> Op(
    dependency0: Op<T0>,
    block: suspend OpClient.(Result<T0>) -> U
): Op<U> {
    val dependencies = listOf(dependency0) as List<Op<Any?>>
    return Op(dependencies) { block(it[0] as Result<T0>) }
}

/**
 * Create an [Op] that executes the given [block] with the given dependencies.
 *
 * @since 2.0.0
 */
@Suppress("UNCHECKED_CAST")
fun <T0, T1, U> Op(
    dependency0: Op<T0>,
    dependency1: Op<T1>,
    block: suspend OpClient.(Result<T0>, Result<T1>) -> U
): Op<U> {
    val dependencies = listOf(dependency0, dependency1) as List<Op<Any?>>
    return Op(dependencies) { block(it[0] as Result<T0>, it[1] as Result<T1>) }
}

/**
 * Create an [Op] that executes the given [block] with the given dependencies.
 *
 * @since 2.0.0
 */
@Suppress("UNCHECKED_CAST")
fun <T0, T1, T2, U> Op(
    dependency0: Op<T0>,
    dependency1: Op<T1>,
    dependency2: Op<T2>,
    block: suspend OpClient.(Result<T0>, Result<T1>, Result<T2>) -> U
): Op<U> {
    val dependencies = listOf(dependency0, dependency1, dependency2) as List<Op<Any?>>
    return Op(dependencies) { block(it[0] as Result<T0>, it[1] as Result<T1>, it[2] as Result<T2>) }
}

/**
 * Create an [Op] that executes the given [block] with the given dependencies.
 *
 * @since 2.0.0
 */
@Suppress("UNCHECKED_CAST")
fun <T0, T1, T2, T3, U> Op(
    dependency0: Op<T0>,
    dependency1: Op<T1>,
    dependency2: Op<T2>,
    dependency3: Op<T3>,
    block: suspend OpClient.(Result<T0>, Result<T1>, Result<T2>, Result<T3>) -> U
): Op<U> {
    val dependencies = listOf(dependency0, dependency1, dependency2, dependency3) as List<Op<Any?>>
    return Op(dependencies) { block(it[0] as Result<T0>, it[1] as Result<T1>, it[2] as Result<T2>, it[3] as Result<T3>) }
}

/**
 * Create an [Op] that executes the given [block] with the given dependencies.
 *
 * @since 2.0.0
 */
@Suppress("UNCHECKED_CAST")
fun <T0, T1, T2, T3, T4, U> Op(
    dependency0: Op<T0>,
    dependency1: Op<T1>,
    dependency2: Op<T2>,
    dependency3: Op<T3>,
    dependency4: Op<T4>,
    block: suspend OpClient.(Result<T0>, Result<T1>, Result<T2>, Result<T3>, Result<T4>) -> U
): Op<U> {
    val dependencies = listOf(dependency0, dependency1, dependency2, dependency3, dependency4) as List<Op<Any?>>
    return Op(dependencies) { block(it[0] as Result<T0>, it[1] as Result<T1>, it[2] as Result<T2>, it[3] as Result<T3>, it[4] as Result<T4>) }
}

/* ============= ------------------ ============= */
