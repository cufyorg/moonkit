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

import org.cufy.mangaka.internal.mangakaAlreadyInitializedError
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

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
    lateinit var client: CoroutineClient
        protected set

    /**
     * The connection database.
     *
     * > Initialized with [connect]
     *
     * @since 1.0.0
     */
    lateinit var database: CoroutineDatabase
        protected set

    /**
     * Connect this mangaka instance to the database
     * at [uri] with the given [name]
     *
     * @since 1.0.0
     */
    fun connect(uri: String, name: String) {
        if (connected)
            mangakaAlreadyInitializedError()

        synchronized(this) {
            if (connected)
                mangakaAlreadyInitializedError()

            client = KMongo.createClient(uri).coroutine
            database = client.getDatabase(name)
            connected = true
        }
    }

    // TODO: plugins?

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
