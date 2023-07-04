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

import kotlinx.coroutines.Deferred

/* ============= ------------------ ============= */

/**
 * An instance that holds the necessary data to
 * perform an operation and the result of it.
 *
 * @param T the type of the operation's result.
 * @author LSafer
 * @since 2.0.0
 */
interface Operation<T> : Deferred<T>

/* ============= ------------------ ============= */

/**
 * Enqueue this operation to [client].
 *
 * @see OpClient.enqueue
 * @return this operation.
 * @since 2.0.0
 */
suspend fun <T> Operation<T>.enqueue(client: OpClient = OpClient): Operation<T> {
    client.enqueue(this)
    return this
}

/**
 * Enqueue this operation to [client] and await the output.
 *
 * @see OpClient.enqueue
 * @return the operation output.
 * @since 2.0.0
 */
suspend operator fun <T> Operation<T>.invoke(client: OpClient = OpClient): T {
    client.enqueue(this)
    return await()
}

/* ============= ------------------ ============= */
