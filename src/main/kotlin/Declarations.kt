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

import org.cufy.monkt.schema.*
import org.cufy.monkt.schema.extension.*

/**
 * A class representing a single connection to some
 * database.
 *
 * @author LSafer
 * @since 2.0.0
 */
open class Monkt {
    /**
     * The client to be used by the models that
     * uses this monkt instance.
     */
    lateinit var client: MonktClient
        @InternalMonktApi set

    /**
     * The database to be used by the models that
     * uses this monkt instance.
     */
    lateinit var database: MonktDatabase
        @InternalMonktApi set

    /**
     * True, if the instance was connected.
     */
    var isConnected: Boolean = false
        @InternalMonktApi set

    /**
     * True, if the instance was initialized.
     */
    var isInitialized: Boolean = false
        @InternalMonktApi set

    /**
     * The signal handlers registered to this
     * monkt instance.
     */
    @Suppress("PropertyName")
    @InternalMonktApi
    val _handlers: MutableList<SignalHandler> = mutableListOf()

    /**
     * The models registered to this monkt instance.
     */
    @Suppress("PropertyName")
    @InternalMonktApi
    val _models: MutableList<Model<*>> = mutableListOf()

    /**
     * The signal handlers registered to this
     * monkt instance.
     *
     * @since 2.0.0
     */
    @OptIn(InternalMonktApi::class)
    @AdvancedMonktApi
    val handlers: List<SignalHandler> get() = _handlers

    /**
     * The models registered to this monkt instance.
     *
     * @since 2.0.0
     */
    @OptIn(InternalMonktApi::class)
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
     * The monkt instance.
     *
     * @since 2.0.0
     */
    lateinit var monkt: Monkt
        @InternalMonktApi set

    /**
     * The collection backing this model.
     *
     * @since 2.0.0
     */
    val collection: MonktCollection
        get() = monkt.database.getCollection(name)
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
