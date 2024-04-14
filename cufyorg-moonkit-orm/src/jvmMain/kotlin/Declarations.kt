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
package org.cufy.monkt

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.cufy.mongodb.MongoClient
import org.cufy.mongodb.MongoCollection
import org.cufy.mongodb.MongoDatabase
import org.cufy.mongodb.get
import org.cufy.monkt.schema.ObjectSchema
import org.cufy.monkt.schema.ObjectSchemaBuilderBlock
import org.cufy.monkt.schema.ObjectSchemaConstructor
import org.cufy.monkt.schema.SignalHandler
import org.cufy.monkt.schema.extension.AggregateSignalHandler

/**
 * A class representing a single connection to some
 * database.
 *
 * @author LSafer
 * @since 2.0.0
 */
open class Monkt {
    /**
     * The monkt client completable deferred.
     */
    internal val deferredClient = CompletableDeferred<MongoClient>()

    /**
     * The monkt database completable deferred.
     */
    internal val deferredDatabase = CompletableDeferred<MongoDatabase>()

    /**
     * The client to be used by the models that
     * uses this monkt instance.
     */
    suspend fun client(): MongoClient {
        return deferredClient.await()
    }

    /**
     * The database to be used by the models that
     * uses this monkt instance.
     */
    suspend fun database(): MongoDatabase {
        return deferredDatabase.await()
    }

    /**
     * The client to be used by the models that
     * uses this monkt instance.
     */
    @ExperimentalMonktApi("Blocking property")
    val client: MongoClient get() = runBlocking { client() }

    /**
     * The database to be used by the models that
     * uses this monkt instance.
     */
    @ExperimentalMonktApi("Blocking property")
    val database: MongoDatabase get() = runBlocking { database() }

    /**
     * True, if this instance was shutdown.
     */
    var isShutdown: Boolean = false
        internal set

    /**
     * True, if the instance was connected.
     */
    var isConnected: Boolean = false
        internal set

    /**
     * True, if the instance was initialized.
     */
    var isInitialized: Boolean = false
        internal set

    /**
     * The signal handlers registered to this
     * monkt instance.
     */
    @Suppress("PropertyName")
    internal val _handlers: MutableList<SignalHandler> = mutableListOf()

    /**
     * The models registered to this monkt instance.
     */
    @Suppress("PropertyName")
    internal val _models: MutableList<Model<*>> = mutableListOf()

    /**
     * The signal handlers registered to this
     * monkt instance.
     *
     * @since 2.0.0
     */
    @AdvancedMonktApi
    val handlers: List<SignalHandler> get() = _handlers

    /**
     * The models registered to this monkt instance.
     *
     * @since 2.0.0
     */
    @AdvancedMonktApi
    val models: List<Model<*>> get() = _models

    init {
        @Suppress("LeakingThis")
        this += AggregateSignalHandler
    }

    /**
     * The default monkt instance.
     *
     * @since 2.0.0
     */
    companion object : Monkt()
}

/**
 * A model is a schema driven collection
 * implementation.
 *
 * You must add this to a [Monkt Instance][Monkt.plusAssign].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class Model<T : Any>(
    /**
     * The name of the model.
     *
     * @since 2.0.0
     */
    val name: String,
    /**
     * The schema to be used to construct,
     * validate and format instances of [T].
     *
     * @since 2.0.0
     */
    val schema: ObjectSchema<T>
) {
    /**
     * Construct a new model with the given
     * arguments.
     *
     * @param name the model's name.
     * @param constructor the constructor of its schema.
     * @param block the schema definition block.
     * @since 2.0.0
     */
    constructor(
        name: String,
        constructor: ObjectSchemaConstructor<T>? = null,
        block: ObjectSchemaBuilderBlock<T> = {}
    ) : this(name, ObjectSchema(constructor, block))

    /**
     * The monkt instance completable deferred.
     */
    internal val deferredMonkt = CompletableDeferred<Monkt>()

    /**
     * The monkt instance.
     *
     * @since 2.0.0
     */
    suspend fun monkt(): Monkt {
        return deferredMonkt.await()
    }

    /**
     * The collection backing this model.
     *
     * @since 2.0.0
     */
    suspend fun collection(): MongoCollection {
        return monkt().database()[name]
    }

    /**
     * The monkt instance.
     *
     * @since 2.0.0
     */
    @ExperimentalMonktApi("Blocking property")
    val monkt: Monkt get() = runBlocking { monkt() }

    /**
     * The monkt instance.
     *
     * @since 2.0.0
     */
    @ExperimentalMonktApi("Blocking property")
    val collection: MongoCollection get() = runBlocking { collection() }

    override fun toString(): String = "Model($name)"
}

/**
 * A marker/utility interface to add to model
 * classes to make utility extension functions
 * work with them.
 *
 * To use the utilities offered by this marker.
 * The instances of the marker MUST be constructed
 * by a model.
 *
 * @author LSafer
 * @since 2.0.0
 */
interface Document {
    /**
     * Document utilities.
     *
     * @author LSafer
     * @since 2.0.0
     */
    companion object
}
