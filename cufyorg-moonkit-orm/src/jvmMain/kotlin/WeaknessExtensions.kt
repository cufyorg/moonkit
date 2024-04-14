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

import org.cufy.bson.ID
import org.cufy.weakness.Weakness
import org.cufy.weakness.weak
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.typeOf

/**
 * A weakness instance for document fantom properties.
 */
private val fantomWeakness = Weakness()

/**
 * A weakness instance for document internal properties.
 */
private val internalWeakness = Weakness()

// Delegates

/**
 * Return a delegate to the weak property `_id`.
 */
@Suppress("FunctionName")
fun <T : Any> Document.Companion._id(): ReadWriteProperty<T, ID<T>> {
    return weak(internalWeakness, "_id", typeOf<ID<T>>())
}

/**
 * Return a delegate to the weak property `model`.
 */
fun <T : Any> Document.Companion.model(): ReadWriteProperty<T, Model<T>> {
    return weak(internalWeakness, "model", typeOf<Model<T>>())
}

/**
 * Return a delegate to the weak property `isNew`.
 */
fun <T : Any> Document.Companion.isNew(): ReadWriteProperty<T, Boolean> {
    return weak(internalWeakness, "isNew", typeOf<Boolean>())
}

/**
 * Return a delegate to the weak property `isDeleted`.
 */
fun <T : Any> Document.Companion.isDeleted(): ReadWriteProperty<T, Boolean> {
    return weak(internalWeakness, "isDeleted", typeOf<Boolean>())
}

/**
 * Return a delegate to a fantom property with the given [name].
 *
 * If [name] was not provided, the name of the property will be used.
 */
fun <T : Any, V> Document.Companion.fantom(name: String? = null): ReadWriteProperty<T, V> {
    return weak(fantomWeakness, name)
}

// Extensions

/**
 * Return the id of the document.
 *
 * @since 2.0.0
 */
@Suppress("ObjectPropertyName")
val <T : Document> T._id: ID<T> by Document._id()

/**
 * Return the model created the document.
 *
 * @since 2.0.0
 */
val <T : Document> T.model: Model<T> by Document.model()

/**
 * Return true if the document is flagged not
 * present in the database.
 *
 * @since 2.0.0
 */
val <T : Document> T.isNew: Boolean by Document.isNew()

/**
 * Return true if the document is flagged deleted
 * from the database.
 *
 * @since 2.0.0
 */
val <T : Document> T.isDeleted: Boolean by Document.isDeleted()

/**
 * Return the id of the document.
 */
@Suppress("ObjectPropertyName")
private var <T : Any> T._id: ID<T> by Document._id()

/**
 * Return the model created the document.
 */
private var <T : Any> T.model: Model<T> by Document.model()

/**
 * Return true if the document is flagged not
 * present in the database.
 */
private var <T : Any> T.isNew: Boolean by Document.isNew()

/**
 * Return true if the document is flagged deleted
 * from the database.
 */
private var <T : Any> T.isDeleted: Boolean by Document.isDeleted()

// Getters and Setters

/**
 * Return the id of the document.
 *
 * @since 2.0.0
 */
fun <T : Any> Document.Companion.getId(instance: T): ID<T> {
    return instance._id
}

/**
 * Set the `id` weak property of the given
 * [instance] to the given [value]
 */
@AdvancedMonktApi("Manually setting the id may cause misbehaviour")
fun <T : Any> Document.Companion.setId(instance: T, value: ID<T>) {
    instance._id = value
}

/**
 * Return the model created the document.
 *
 * @since 2.0.0
 */
fun <T : Any> Document.Companion.getModel(instance: T): Model<T> {
    return instance.model
}

/**
 * Set the `model` weak property of the given
 * [instance] to the given [value]
 */
@AdvancedMonktApi("Manually setting the model may cause misbehaviour")
fun <T : Any> Document.Companion.setModel(instance: T, value: Model<T>) {
    instance.model = value
}

/**
 * Return true if the document is flagged not
 * present in the database.
 *
 * @since 2.0.0
 */
fun <T : Any> Document.Companion.isNew(instance: T): Boolean {
    return instance.isNew
}

/**
 * Set the `isNew` weak property of the given
 * [instance] to the given [value]
 */
@AdvancedMonktApi("Manually setting 'isNew' may cause misbehaviour")
fun <T : Any> Document.Companion.setNew(instance: T, value: Boolean) {
    instance.isNew = value
}

/**
 * Return true if the document is flagged deleted
 * from the database.
 *
 * @since 2.0.0
 */
fun <T : Any> Document.Companion.isDeleted(instance: T): Boolean {
    return instance.isDeleted
}

/**
 * Set the `deleted` weak property of the given
 * [instance] to the given [value]
 */
@AdvancedMonktApi("Manually setting 'isDeleted' may cause misbehaviour")
fun <T : Any> Document.Companion.setDeleted(instance: T, value: Boolean) {
    instance.isDeleted = value
}

/**
 * Return the weak property of [instance] with the
 * given [name].
 *
 * Type safety is the responsibility of the user.
 * The type parameter [V] was put as a helping
 * mechanism and not as a guarantee.
 *
 * Also, it is allowed to provide any [instance].
 */
operator fun <T : Any, V> Document.Companion.get(instance: T, name: String): V {
    @Suppress("UNCHECKED_CAST")
    return fantomWeakness[instance][name] as V
}

/**
 * Set the weak property at [instance] with the
 * given [name] to the given [value].
 *
 * Type safety is the responsibility of the user.
 * The type parameter [V] was put as a helping
 * mechanism and not as a guarantee.
 *
 * Also, it is allowed to provide any [instance].
 */
operator fun <T : Any, V> Document.Companion.set(instance: T, name: String, value: V) {
    fantomWeakness[instance][name] = value
}
