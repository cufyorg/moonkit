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

import com.mongodb.reactivestreams.client.MongoClients
import org.cufy.bson.*
import org.cufy.monkt.internal.*
import org.cufy.monkt.schema.*
import org.cufy.monkt.schema.extension.*

/*==================================================
================ Options Operations ================
==================================================*/

/* ============= - obtainOptions  - ============= */

/**
 * Return the options of type [C] that was applied
 * to [this] document in its schema.
 */
inline fun <reified C> Document.obtainOptions(): List<OptionData<*, *, C>> {
    return Document.obtainOptions(this)
}

/**
 * Return the options of type [C] that was applied
 * to the given [instance] in its schema.
 */
@OptIn(AdvancedMonktApi::class)
inline fun <reified C> Document.Companion.obtainOptions(instance: Any): List<OptionData<*, *, C>> {
    @Suppress("UNCHECKED_CAST")
    return obtainAllOptions(instance)
        .filter { C::class.isInstance(it.configuration) }
            as List<OptionData<*, *, C>>
}

/**
 * Return the options of type [C] that was applied
 * to this model in its schema.
 */
@OptIn(AdvancedMonktApi::class)
inline fun <reified C> Model<*>.obtainOptions(): List<OptionData<Unit, Unit, C>> {
    @Suppress("UNCHECKED_CAST")
    return obtainAllOptions()
        .filter { C::class.isInstance(it.configuration) }
            as List<OptionData<Unit, Unit, C>>
}

/* ============ - obtainAllOptions - ============ */

/**
 * Obtain a list of the options applied to the
 * given [instance] in its schema.
 */
@AdvancedMonktApi("Use `obtainOptions()` instead")
fun <T : Any> Document.Companion.obtainAllOptions(instance: T): List<OptionData<*, *, *>> {
    val model = Document.getModel(instance)
    return model.schema.obtainOptions(model, instance, Pathname(), instance)
}

/**
 * Obtain a list of the options applied to this
 * model in its schema.
 */
@AdvancedMonktApi("Use `obtainOptions()` instead")
fun <T : Any> Model<T>.obtainAllOptions(): List<OptionData<Unit, Unit, *>> {
    return schema.obtainStaticOptions(this, Pathname(), emptySet())
}

/* ============= - performOption  - ============= */

/**
 * Perform the given [options] using the handlers
 * of this monkt instance.
 */
@AdvancedMonktApi("This usually used as a part of a bigger operation.")
@OptIn(InternalMonktApi::class)
suspend fun Monkt.performOption(options: List<OptionData<*, *, *>>) {
    val invocation = OptionsInvocation(options)

    while (invocation.hasNext()) {
        invocation.next(handlers)
    }
}

/*==================================================
================= Codec Operations =================
==================================================*/

/* ============ - performDecoding  - ============ */

/**
 * Perform the decoding stage for the given [document].
 *
 * Note: The [instance id][Document.Companion.setId] of
 * the instance will be set from the document's `_id`.
 * If the document doesn't have an `_id` set. A new
 * [object id][org.cufy.bson.ObjectId] will be used.
 *
 * @since 2.0.0
 */
@AdvancedMonktApi("This is a part of the decoding process")
fun <T : Any> Document.Companion.performDecoding(model: Model<T>, document: BsonDocument): T {
    val instance = model.schema.decode(document)

    val bsonId = document["_id"] ?: bundefined
    val id = LenientIdCoercer.decode(bsonId)

    Document.setId(instance, id)
    Document.setModel(instance, model)

    return instance
}

/* ============ - performEncoding  - ============ */

/**
 * Perform the encoding stage for the given [instance].
 *
 * @since 2.0.0
 */
@AdvancedMonktApi("This is a part of the encoding process")
fun <T : Any> Document.Companion.performEncoding(instance: T): BsonDocument {
    val id = Document.getId<Any>(instance)
    val model = Document.getModel(instance)

    val document = model.schema.encode(instance)

    if ("_id" !in document)
        document["_id"] = IdSchema.encode(id)

    return document
}

/*==================================================
============== Connection Operations  ==============
==================================================*/

/* ============= ---- Register ---- ============= */

/**
 * Register the given [model] to this monkt
 * instance.
 */
@OptIn(InternalMonktApi::class)
operator fun Monkt.plusAssign(model: Model<*>) {
    _models += model
}

/**
 * Register the given [handler] to this monkt
 * instance.
 */
@OptIn(InternalMonktApi::class)
operator fun Monkt.plusAssign(handler: SignalHandler) {
    _handlers += handler
}

/* ============= ---- connect ---- ============= */

/**
 * Set the client and database to the given
 * [client] and [database] and close the
 * previous client.
 *
 * @param client the new client.
 * @param database the new database.
 * @throws IllegalStateException if already connected
 */
@OptIn(InternalMonktApi::class)
fun Monkt.connect(client: MonktClient, database: MonktDatabase) {
    require(!isConnected) { "Already Connected" }
    isConnected = true
    deferredClient.complete(client)
    deferredDatabase.complete(database)
    if (isShutdown) {
        client.use { }
    }
}

/**
 * Set the client and database using the
 * given [uri] and [name] and close the
 * previous client.
 *
 * @param uri the connection string.
 * @param name the database name.
 * @since 2.0.0
 */
fun Monkt.connect(uri: String, name: String) {
    val client = MongoClients.create(uri)
    val database = client.getDatabase(name)
    connect(MonktClient(client), MonktDatabase(database))
}

/**
 * Shutdown the current connection. (If connected)
 */
@OptIn(ExperimentalMonktApi::class, InternalMonktApi::class)
fun Monkt.shutdown() {
    if (isConnected && !isShutdown) {
        this.client.use { }
    }
    isShutdown = true
}
