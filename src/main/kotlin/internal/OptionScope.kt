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

import org.cufy.bson.Pathname
import org.cufy.monkt.*
import org.cufy.monkt.schema.*

/**
 * Construct a new option scope with the given
 * parameters.
 *
 * @param onWait a function to be invoked when the
 *                  scope needs to wait for signals
 *                  to be resolved.
 */
@AdvancedMonktApi
@InternalMonktApi
fun <T : Any, M, C> OptionScope(
    option: OptionData<T, M, C>,
    onWait: suspend (signals: List<Signal<*>>) -> List<Any?>
): OptionScope<T, M, C> {
    var nextSignalIndex = 0
    val enqueuedSignals = mutableListOf<Signal<*>>()
    val items = mutableListOf<Any?>()

    return object : OptionScope<T, M, C> {
        override val model: Model<*> = option.model
        override val root: Any = option.root
        override val schema: Schema<M> = option.schema
        override val instance: T = option.instance
        override val value: M = option.value
        override val pathname: Pathname = option.pathname
        override val configuration: C = option.configuration

        override fun <I : Signal<O>, O> enqueue(signal: I): SignalProperty<O> {
            val index = nextSignalIndex++

            enqueuedSignals += signal

            return SignalProperty {
                if (index > items.lastIndex) {
                    error("Used the result of an enqueue before wait().")
                }

                @Suppress("UNCHECKED_CAST")
                items[index] as O
            }
        }

        override suspend fun wait() {
            if (enqueuedSignals.isNotEmpty()) {
                val currentSignals = enqueuedSignals.toList()
                enqueuedSignals.clear()
                val currentItems = onWait(currentSignals)
                if (currentItems.size != currentSignals.size) {
                    error("Responded items doesn't match the signals size.")
                }
                items.addAll(currentItems)
            }
        }
    }
}
