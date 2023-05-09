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
package org.cufy.monop.internal

import kotlinx.coroutines.*
import org.cufy.mongodb.MongoClient
import org.cufy.mongodb.MongoDatabase
import org.cufy.monop.*

@InternalMonopApi
class MonopImpl : Monop {
    override lateinit var client: MongoClient
    override lateinit var database: MongoDatabase

    private val operators = DefaultOperators.toMutableList()
    private val deferred = CompletableDeferred<Unit>()

    override fun connect(client: MongoClient, database: MongoDatabase) {
        ensureNotConnected()
        this.client = client
        this.database = database
        deferred.complete(Unit)
    }

    override operator fun plusAssign(operators: Iterable<Operator>) {
        ensureNotConnected()
        this.operators += operators
    }

    override operator fun minusAssign(operators: Iterable<Operator>) {
        ensureNotConnected()
        @Suppress("ConvertArgumentToSet")
        this.operators -= operators
    }

    override fun enqueue(operations: List<Operation<*>>) {
        CoroutineScope(Dispatchers.IO).launch {
            deferred.await()

            OperatorScopeImpl(this@MonopImpl, operations)
                .execute(operators)
        }
    }

    private fun ensureNotConnected() {
        if (deferred.isCompleted)
            error("Monop Already Connected")
    }
}

@InternalMonopApi
class OperatorScopeImpl(
    override val monop: Monop,
    initial: List<Operation<*>>
) : OperatorScope {
    private var queue = initial.toMutableList()
    private var next = mutableListOf<Operation<*>>()

    override fun accept(predicate: (Operation<*>) -> Boolean): List<Operation<*>> {
        return buildList {
            listOf(queue, next).forEach {
                val iterator = it.iterator()

                while (iterator.hasNext()) {
                    val next = iterator.next()

                    if (predicate(next)) {
                        iterator.remove()
                        add(next)
                    }
                }
            }
        }
    }

    override fun enqueue(operations: List<Operation<*>>) {
        next += operations
    }

    internal suspend fun execute(operators: List<Operator>) {
        while (queue.isNotEmpty()) {
            operators.forEach { with(it) { invoke() } }
            queue.forEach { it.cancel("Operation Not Supported: $it") }
            queue = next
            next = mutableListOf()
        }
    }
}
