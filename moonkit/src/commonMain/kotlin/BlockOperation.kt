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

import kotlinx.coroutines.*
import kotlin.Result.Companion.failure

/* ============= ------------------ ============= */

/**
 * An operation for executing block after other
 * dependency operations has been executed.
 *
 * @author LSafer
 * @since 2.0.0
 */
class BlockOperation<T, U>(
    val dependencies: List<Operation<T>>,
    val block: suspend OpClient.(List<Result<T>>) -> Result<U>,
) : Operation<U>,
    CompletableDeferred<U>
    by CompletableDeferred() {
    override fun toString() = inferToString()
}

/* ============= ------------------ ============= */

private fun BlockOperation<*, *>.inferToString(): String {
    val name = "BlockOperation"
    val address = hashCode().toString(16)
    return "$name($dependencies)@$address"
}

/* ============= ------------------ ============= */

/**
 * An operator performing operations of type [BlockOperation]
 * in parallel.
 *
 * @author LSafer
 * @since 2.0.0
 */
@ExperimentalMoonkitApi
val BlockOperator = createOperatorForType<BlockOperation<Any?, Any?>> { operations ->
    operations.forEach {
        CoroutineScope(Dispatchers.IO).launch {
            val values = it.dependencies.map {
                runCatching { it.await() }
            }

            // do not trust `it.block` to not throw something
            it.completeWith(
                try {
                    it.block(this@createOperatorForType, values)
                } catch (error: Throwable) {
                    failure(error)
                }
            )
        }
    }

    operations.flatMapTo(mutableSetOf()) { it.dependencies }
}

/* ============= ------------------ ============= */
