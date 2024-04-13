package org.cufy.moonkit.internal

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.cancel
import org.cufy.mongodb.MongoClient
import org.cufy.mongodb.MongoDatabase
import org.cufy.moonkit.GlobalOpClient
import org.cufy.moonkit.OpClient
import org.cufy.moonkit.Operation
import org.cufy.moonkit.Operator

internal class GlobalOpClientImpl : GlobalOpClient {
    private val deferred = CompletableDeferred<OpClient>()

    override fun use(client: OpClient) {
        check(deferred.complete(client)) {
            "Attempting to initialize an already initialized client: $this"
        }
    }

    override suspend fun get(): MongoClient {
        return deferred.await().get()
    }

    override suspend fun defaultDatabase(): MongoDatabase? {
        return deferred.await().defaultDatabase()
    }

    override suspend fun enqueue(operations: Set<Operation<*>>) {
        deferred.await().enqueue(operations)
    }
}

internal class OpClientImpl(
    private val client: MongoClient,
    private val defaultDatabase: MongoDatabase?,
    private val operators: List<Operator>,
) : OpClient {
    override suspend fun get(): MongoClient {
        return client
    }

    override suspend fun defaultDatabase(): MongoDatabase? {
        return defaultDatabase
    }

    override suspend fun enqueue(operations: Set<Operation<*>>) {
        var queue = operations

        while (queue.isNotEmpty()) {
            var round = queue

            for (operator in operators) {
                // calling operator(this, round)
                round = with(operator) { this@OpClientImpl(round) }

                // all operations are done; continuation is redundant
                if (round.isEmpty()) return
            }

            val nextQueue = round.toMutableSet()

            // cancel **old** un-executed operations
            nextQueue.iterator().apply {
                forEach {
                    if (it in queue) {
                        it.cancel("Operation Not Supported: $it")
                        remove()
                    }
                }
            }

            queue = nextQueue
        }
    }
}
