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
package org.cufy.mangaka

import org.bson.BsonDocument
import org.cufy.mangaka.Mangaka.Companion.model
import org.cufy.mangaka.schema.Schema
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

/**
 * The type of database client.
 *
 * @since 1.0.0
 */
typealias MangakaClient = CoroutineClient

/**
 * The type of database collection.
 *
 * @since 1.0.0
 */
typealias MangakaCollection = CoroutineCollection<BsonDocument>

/**
 * The type of a database.
 *
 * @since 1.0.0
 */
typealias MangakaDatabase = CoroutineDatabase

/**
 * A class representing a single connection to some
 * database.
 *
 * TODO: documentation for class Mangaka
 *
 * @author LSafer
 * @since 1.0.0
 */
open class Mangaka {
    /**
     * True, if this instance's [connect] function
     * has already been invoked. And it cannot be
     * invoked again.
     *
     * @since 1.0.0
     */
    var connected: Boolean = false
        protected set

    /**
     * The connection client.
     *
     * > Initialized with [connect]
     *
     * @since 1.0.0
     */
    lateinit var client: MangakaClient
        protected set

    /**
     * The connection database.
     *
     * > Initialized with [connect]
     *
     * @since 1.0.0
     */
    lateinit var database: MangakaDatabase
        protected set

    /**
     * The models initialized by this mangaka instance.
     */
    protected val models = mutableMapOf<String, Model<*>>()

    /**
     * The collections initialized by this mangaka instance.
     */
    protected val collections = mutableMapOf<String, MangakaCollection>()

    /**
     * Connect this mangaka instance to the database
     * at [uri] with the given [name]
     *
     * An exception will be thrown if this function
     * was invoked twice.
     *
     * @since 1.0.0
     */
    fun connect(uri: String, name: String) {
        require(!connected) { "Mangaka instance has already been initialized" }

        synchronized(this) {
            require(!connected) { "Mangaka instance has already been initialized" }

            client = KMongo.createClient(uri).coroutine
            database = client.getDatabase(name)
            connected = true
        }
    }

    /**
     * Get the model with the given [name].
     * Only models created via the [model]
     * function of this instance are available.
     *
     * This function was made to mimic mongoose
     * behaviour.
     *
     * An exception will be thrown if no such
     * model was found.
     *
     * @since 1.0.0
     */
    fun <T : Any> model(name: String): Model<T> {
        require(models.containsKey(name)) { "Model Not Found $name" }

        @Suppress("UNCHECKED_CAST")
        return models[name] as Model<T>
    }

    /**
     * Create a new model in this mangaka instance
     * with the given arguments and add it to the
     * registered models in this mangaka instance.
     *
     * This function was made to mimic mongoose
     * behaviour.
     *
     * Using this function or using the [Model]
     * constructor is almost equivalent.
     *
     * An exception will be thrown if this
     * function was invoked twice.
     *
     * @since 1.0.0
     */
    fun <T : Any> model(name: String, schema: Schema<T>, collection: String = name): Model<T> {
        require(!models.containsKey(name)) { "Duplicate Model $name" }

        val model = Model(name, schema, collection, this)
        models[name] = model
        return model
    }

    /**
     * Get the collection with the given [name].
     *
     * @since 1.0.0
     */
    fun collection(name: String): MangakaCollection {
        return collections.getOrPut(name) {
            database.getCollection(name)
        }
    }

    /**
     * The default mangaka instance.
     * You should use it if you deal with a single
     * database across your application.
     *
     * @author LSafer
     * @since 1.0.0
     */
    companion object : Mangaka()
}
