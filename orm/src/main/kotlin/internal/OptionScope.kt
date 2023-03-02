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
    val queue = mutableListOf<Pair<Signal<*>, SignalPropertyImpl<*>>>()

    return object : OptionScope<T, M, C> {
        override val model: Model<*> = option.model
        override val root: Any = option.root
        override val declaration: Any = option.declaration
        override val instance: T = option.instance
        override val value: M = option.value
        override val pathname: Pathname = option.pathname
        override val configuration: C = option.configuration

        override fun <I : Signal<O>, O> enqueue(signal: I): SignalProperty<O> {
            val property = SignalPropertyImpl<O>()

            queue += signal to property

            return property
        }

        override suspend fun wait() {
            if (queue.isNotEmpty()) {
                val (signals, properties) = queue.unzip()

                queue.clear()

                val items = onWait(signals)

                if (items.size != properties.size) {
                    error("Responded items doesn't match the signals size.")
                }

                (properties zip items).forEach { (property, item) ->
                    property.complete(item)
                }
            }
        }
    }
}

@InternalMonktApi
class SignalPropertyImpl<O> : SignalProperty<O> {
    private var isComplete: Boolean = false
    private var _value: Any? = null
    private val onComplete: MutableList<suspend (Any?) -> Any?> = mutableListOf()

    suspend fun complete(value: Any?) {
        require(!isComplete) { "Signal already completed" }
        isComplete = true
        _value = value
        onComplete.forEach { it(value) }
        onComplete.clear()
    }

    @Suppress("UNCHECKED_CAST")
    override val value: O
        get() = when {
            isComplete -> _value as O
            else -> error("Used the result of an enqueue before wait().")
        }

    override fun <R> then(block: suspend (O) -> R): SignalProperty<R> {
        val property = SignalPropertyImpl<R>()
        onComplete += {
            @Suppress("UNCHECKED_CAST")
            val mapped = block(it as O)
            property.complete(mapped)
        }
        return property
    }
}
