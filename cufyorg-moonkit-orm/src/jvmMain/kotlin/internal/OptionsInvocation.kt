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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.currentCoroutineContext
import org.cufy.monkt.*
import org.cufy.monkt.schema.*

@InternalMonktApi
private data class InvocationOrderSignalData(
    val invocation: OptionInvocation<*, *>,
    val order: Int,
    val signal: Signal<*>
)

@InternalMonktApi
private data class InvocationOrderItemData(
    val invocation: OptionInvocation<*, *>,
    val order: Int,
    val item: Any?
)

@InternalMonktApi
private data class InvocationItemsData(
    val invocation: OptionInvocation<*, *>,
    val items: List<Any?>
)

@InternalMonktApi
private data class HandlerInvocationOrderSignalListData(
    val handler: SignalHandler,
    val list: List<InvocationOrderSignalData>
)

@AdvancedMonktApi
@InternalMonktApi
suspend fun OptionsInvocation(
    options: List<OptionData<*, *, *>>
): OptionsInvocation {
    val invocations = options.map { OptionInvocation(it) }
    var data = invocations.map { InvocationItemsData(it, emptyList()) }

    return object : OptionsInvocation {
        override suspend fun hasNext(): Boolean {
            return data.isNotEmpty()
        }

        override suspend fun next(handlers: List<SignalHandler>) {
            require(data.isNotEmpty()) { "No more option code to be invoked" }
            data = data
                .invokeAll()
                .toHandlerInvocationOrderSignalListDataList(handlers)
                .handleAll()
                .toInvocationItemsDataList(invocations)
                .filter { it.invocation.hasNext() }
        }
    }
}

@InternalMonktApi
private suspend fun List<InvocationItemsData>.invokeAll(): List<InvocationOrderSignalData> {
    return flatMap { (invocation, items) ->
        invocation.next(items).mapIndexed { order, signal ->
            InvocationOrderSignalData(invocation, order, signal)
        }
    }
}

@AdvancedMonktApi
@InternalMonktApi
private fun List<InvocationOrderSignalData>.toHandlerInvocationOrderSignalListDataList(
    handlers: List<SignalHandler>
): List<HandlerInvocationOrderSignalListData> {
    return groupBy { data ->
        handlers.firstOrNull { it.canHandle(data.signal) }
            ?: error("Unhandled Signal: ${data.signal}")
    }.map { (handler, list) ->
        HandlerInvocationOrderSignalListData(handler, list)
    }
}

@AdvancedMonktApi
@InternalMonktApi
private suspend fun List<HandlerInvocationOrderSignalListData>.handleAll(): List<InvocationOrderItemData> {
    val coroutineScope = CoroutineScope(currentCoroutineContext())
    return map { (handler, list) ->
        coroutineScope.async {
            handler.handle(list.map { it.signal })
                .zip(list)
                .map { (item, data) ->
                    InvocationOrderItemData(data.invocation, data.order, item)
                }
        }
    }.awaitAll().flatten()
}

@InternalMonktApi
private fun List<InvocationOrderItemData>.toInvocationItemsDataList(
    invocations: List<OptionInvocation<*, *>>
): List<InvocationItemsData> {
    return invocations.map { invocation ->
        val items = this
            .filter { it.invocation === invocation }
            .sortedBy { it.order }
            .map { it.item }
        InvocationItemsData(invocation, items)
    }
}
