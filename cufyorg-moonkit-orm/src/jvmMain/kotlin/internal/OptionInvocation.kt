/*
 *	Copyright 2022 cufy.org
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
package org.cufy.monkt.internal

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import org.cufy.monkt.*
import org.cufy.monkt.schema.*

/**
 * Return an option invocation invoking this
 * option with the given arguments.
 */
@AdvancedMonktApi
@InternalMonktApi
suspend fun <T : Any, M, C> OptionInvocation(
    option: OptionData<T, M, C>,
): OptionInvocation<T, C> {
    var hasNext = true
    var nextSignals = CompletableDeferred<List<Signal<*>>>()
    var nextValues = CompletableDeferred<List<Any?>>()

    CoroutineScope(currentCoroutineContext()).launch {
        // wait until the first outer resume (the first call to `next()`)
        nextValues.await()
        nextValues = CompletableDeferred()

        // now the outer is waiting
        val scope = OptionScope(option) { signals ->
            // resume the outer with signals and wait for it
            // once it is responded, continue executing while
            // the outer is waiting
            nextSignals.complete(signals)
            val values = nextValues.await()
            nextValues = CompletableDeferred()
            values
        }

        option.block(scope, option.value)

        // finally, resume the outer with null to
        // indicate the end of the code.
        hasNext = false
        nextSignals.complete(emptyList())
    }

    return object : OptionInvocation<T, C> {
        override suspend fun hasNext(): Boolean {
            return hasNext
        }

        override suspend fun next(items: List<Any?>): List<Signal<*>> {
            require(hasNext) { "No more option code to be invoked" }
            nextValues.complete(items)
            val signals = nextSignals.await()
            nextSignals = CompletableDeferred()
            return signals
        }
    }
}
