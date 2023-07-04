package org.cufy.monop.internal

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.cancel
import org.cufy.mongodb.MongoClient
import org.cufy.mongodb.MongoDatabase
import org.cufy.monop.GlobalOpClient
import org.cufy.monop.OpClient
import org.cufy.monop.Operation
import org.cufy.monop.Operator

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
    private val operators: List<Operator>
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

            // cancel **old** un-executed operations
            queue.forEach {
                if (it in round) {
                    it.cancel("Operation Not Supported: $it")
                }
            }

            queue = round
        }
    }
}
