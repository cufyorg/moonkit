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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.completeWith
import kotlinx.coroutines.launch
import org.cufy.mongodb.*

/* ============= ------------------ ============= */

/**
 * A function that can execute some operations in
 * an [OperatorScope].
 */
fun interface Operator {
    /**
     * Execute the operation on the [receiver][this] scope.
     *
     * DO NOT CALL DIRECTLY
     *
     * @since 2.0.0
     */
    suspend operator fun OperatorScope.invoke()
}

/**
 * A stateful instance that holds a set of
 * operations to be completed with a set of
 * [Operator]s.
 *
 * @author LSafer
 * @since 2.0.0
 */
interface OperatorScope {
    /**
     * The monop instance.
     *
     * @since 2.0.0
     */
    val monop: Monop

    /**
     * Add the given [operations] to the queue.
     *
     * @since 2.0.0
     */
    fun enqueue(operations: List<Operation<*>>)

    /**
     * Get and remove the operations that matches
     * the given [predicate] from the queue.
     *
     * @since 2.0.0
     */
    fun accept(predicate: (Operation<*>) -> Boolean): List<Operation<*>>
}

/**
 * Add the given [operations] to the queue.
 *
 * @since 2.0.0
 */
fun OperatorScope.enqueue(vararg operations: Operation<*>) {
    enqueue(operations.asList())
}

/**
 * Get and remove the operations that are
 * instances of type [O] from the queue.
 *
 * @since 2.0.0
 */
inline fun <reified O : Operation<*>> OperatorScope.accept(): List<O> {
    @Suppress("UNCHECKED_CAST")
    return accept { it is O } as List<O>
}

/* ============= ------------------ ============= */

/**
 * An operator performing operations of type [BlockOperation]
 * in parallel.
 *
 * @author LSafer
 * @since 2.0.0
 */
val BlockOperator = Operator {
    accept<BlockOperation<Any?, Any?>>().forEach {
        enqueue(it.dependencies)

        CoroutineScope(Dispatchers.IO).launch {
            val values = it.dependencies.map {
                runCatching { it.await() }
            }

            val out = it.block(monop, values)

            it.completeWith(out)
        }
    }
}

/**
 * An operator performing operations of type [DeleteOneOperation]
 * in parallel using [MongoCollection.deleteOne]
 *
 * This operator supports all the options in [DeleteOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
val DeleteOneOperator = Operator {
    accept<DeleteOneOperation>().forEach {
        it.collection.initOnce(monop)

        CoroutineScope(Dispatchers.IO).launch {
            it.completeWith(runCatching {
                monop[it.collection].run {
                    deleteOne(it.filter, it.options)
                }
            })
        }
    }
}

/**
 * An operator performing operations of type [DeleteManyOperation]
 * in parallel using [MongoCollection.deleteMany]
 *
 * This operator supports all the options in [DeleteOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
val DeleteManyOperator = Operator {
    accept<DeleteManyOperation>().forEach {
        it.collection.initOnce(monop)

        CoroutineScope(Dispatchers.IO).launch {
            it.completeWith(runCatching {
                monop[it.collection].run {
                    deleteMany(it.filter, it.options)
                }
            })
        }
    }
}

/**
 * An operator performing operations of type [InsertOneOperation]
 * in parallel using [MongoCollection.insertOne].
 *
 * This operator supports all the options in [InsertOneOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
val InsertOneOperator = Operator {
    accept<InsertOneOperation>().forEach {
        it.collection.initOnce(monop)

        CoroutineScope(Dispatchers.IO).launch {
            it.completeWith(runCatching {
                monop[it.collection].run {
                    insertOne(it.document, it.options)
                }
            })
        }
    }
}

/**
 * An operator performing operations of type [InsertManyOperation]
 * in parallel using [MongoCollection.insertMany]
 *
 * This operator supports all the options in [InsertManyOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
val InsertManyOperator = Operator {
    accept<InsertManyOperation>().forEach {
        it.collection.initOnce(monop)

        CoroutineScope(Dispatchers.IO).launch {
            it.completeWith(runCatching {
                monop[it.collection].run {
                    insertMany(it.documents, it.options)
                }
            })
        }
    }
}

/**
 * An operator performing operations of type [UpdateOneOperation]
 * in parallel using [MongoCollection.updateOne]
 *
 * This operator supports all the options in [UpdateOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
val UpdateOneOperator = Operator {
    accept<UpdateOneOperation>().forEach {
        it.collection.initOnce(monop)

        CoroutineScope(Dispatchers.IO).launch {
            it.completeWith(runCatching {
                monop[it.collection].run {
                    updateOne(it.filter, it.update, it.options)
                }
            })
        }
    }
}

/**
 * An operator performing operations of type [UpdateManyOperation]
 * in parallel using [MongoCollection.updateMany]
 *
 * This operator supports all the options in [UpdateOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
val UpdateManyOperator = Operator {
    accept<UpdateManyOperation>().forEach {
        it.collection.initOnce(monop)

        CoroutineScope(Dispatchers.IO).launch {
            it.completeWith(runCatching {
                monop[it.collection].run {
                    updateMany(it.filter, it.update, it.options)
                }
            })
        }
    }
}

/**
 * An operator performing operations of type [ReplaceOneOperator]
 * in parallel using [MongoCollection.replaceOne]
 *
 * This operator supports all the options in [ReplaceOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
val ReplaceOneOperator = Operator {
    accept<ReplaceOneOperation>().forEach {
        it.collection.initOnce(monop)

        CoroutineScope(Dispatchers.IO).launch {
            it.completeWith(runCatching {
                monop[it.collection].run {
                    replaceOne(it.filter, it.replacement, it.options)
                }
            })
        }
    }
}

/**
 * An operator performing operations of type [BulkWriteOperation]
 * in parallel using [MongoCollection.bulkWrite]
 *
 * This operator supports all the options in [BulkWriteOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
val BulkWriteOperator = Operator {
    accept<BulkWriteOperation>().forEach {
        it.collection.initOnce(monop)

        CoroutineScope(Dispatchers.IO).launch {
            it.completeWith(runCatching {
                monop[it.collection].run {
                    bulkWrite(it.requests, it.options)
                }
            })
        }
    }
}

/**
 * An operator performing operations of type [CountOperation]
 * in parallel using [MongoCollection.count]
 *
 * This operator supports all the options in [CountOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
val CountOperator = Operator {
    accept<CountOperation>().forEach {
        it.collection.initOnce(monop)

        CoroutineScope(Dispatchers.IO).launch {
            it.completeWith(runCatching {
                monop[it.collection].run {
                    count(it.filter, it.options)
                }
            })
        }
    }
}

/**
 * An operator performing operations of type [EstimatedCountOperation]
 * in parallel using [MongoCollection.estimatedCount]
 *
 * This operator supports all the options in [EstimatedCountOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
val EstimatedCountOperator = Operator {
    accept<EstimatedCountOperation>().forEach {
        it.collection.initOnce(monop)

        CoroutineScope(Dispatchers.IO).launch {
            it.completeWith(runCatching {
                monop[it.collection].run {
                    estimatedCount(it.options)
                }
            })
        }
    }
}

/**
 * An operator performing operations of type [FindOneAndDeleteOperation]
 * in parallel using [MongoCollection.findOneAndDelete]
 *
 * This operator supports all the options in [FindOneAndDeleteOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
val FindOneAndDeleteOperator = Operator {
    accept<FindOneAndDeleteOperation>().forEach {
        it.collection.initOnce(monop)

        CoroutineScope(Dispatchers.IO).launch {
            it.completeWith(runCatching {
                monop[it.collection].run {
                    findOneAndDelete(it.filter, it.options)
                }
            })
        }
    }
}

/**
 * An operator performing operations of type [FindOneAndReplaceOperation]
 * in parallel using [MongoCollection.findOneAndReplace]
 *
 * This operator supports all the options in [FindOneAndReplaceOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
val FindOneAndReplaceOperator = Operator {
    accept<FindOneAndReplaceOperation>().forEach {
        it.collection.initOnce(monop)

        CoroutineScope(Dispatchers.IO).launch {
            it.completeWith(runCatching {
                monop[it.collection].run {
                    findOneAndReplace(it.filter, it.replacement, it.options)
                }
            })
        }
    }
}

/**
 * An operator performing operations of type [FindOneAndUpdateOperation]
 * in parallel using [MongoCollection.findOneAndUpdate]
 *
 * This operator supports all the options in [FindOneAndUpdateOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
val FindOneAndUpdateOperator = Operator {
    accept<FindOneAndUpdateOperation>().forEach {
        it.collection.initOnce(monop)

        CoroutineScope(Dispatchers.IO).launch {
            it.completeWith(runCatching {
                monop[it.collection].run {
                    findOneAndUpdate(it.filter, it.update, it.options)
                }
            })
        }
    }
}

/**
 * An operator performing operations of type [FindOperation]
 * in parallel using [MongoCollection.find]
 *
 * This operator supports all the options in [FindOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
val FindOperator = Operator {
    accept<FindOperation>().forEach {
        it.collection.initOnce(monop)

        CoroutineScope(Dispatchers.IO).launch {
            it.completeWith(runCatching {
                monop[it.collection].run {
                    find(it.filter, it.options)
                }
            })
        }
    }
}

/**
 * An operator performing operations of type [AggregateOperation]
 * in parallel using [MongoCollection.aggregate]
 *
 * This operator supports all the options in [AggregateOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
val AggregateOperator = Operator {
    accept<AggregateOperation>().forEach {
        it.collection.initOnce(monop)

        CoroutineScope(Dispatchers.IO).launch {
            it.completeWith(runCatching {
                monop[it.collection].run {
                    aggregate(it.pipeline, it.options)
                }
            })
        }
    }
}

/**
 * An operator performing operations of type [DistinctOperation]
 * in parallel using [MongoCollection.distinct]
 *
 * This operator supports all the options in [DistinctOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
val DistinctOperator = Operator {
    accept<DistinctOperation>().forEach {
        it.collection.initOnce(monop)

        CoroutineScope(Dispatchers.IO).launch {
            it.completeWith(runCatching {
                monop[it.collection].run {
                    distinct(it.field, it.filter, it.options)
                }
            })
        }
    }
}

/* ============= ------------------ ============= */

/**
 * A list of all built-in operators.
 */
val DefaultOperators = listOf(
    BlockOperator,
    DeleteOneOperator,
    DeleteManyOperator,
    InsertOneOperator,
    InsertManyOperator,
    UpdateOneOperator,
    UpdateManyOperator,
    ReplaceOneOperator,
    BulkWriteOperator,
    CountOperator,
    EstimatedCountOperator,
    FindOneAndDeleteOperator,
    FindOneAndReplaceOperator,
    FindOneAndUpdateOperator,
    FindOperator,
    AggregateOperator,
    DistinctOperator
)

/* ============= ------------------ ============= */
