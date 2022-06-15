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

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.bson.*
import org.bson.types.ObjectId
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

// Primitive Types

/**
 * Obtain String schema.
 *
 * @since 1.0.0
 */
fun StringSchema() = SchemaType {
    constructor { (it as? BsonString)?.value }
    formatter { it?.let { BsonString(it) } }
}

/**
 * Obtain Boolean schema.
 *
 * @since 1.0.0
 */
fun BooleanSchema() = SchemaType {
    constructor { (it as? BsonBoolean)?.value }
    formatter { it?.let { BsonBoolean(it) } }
}

/**
 * Obtain Int64 (Long) schema.
 *
 * @since 1.0.0
 */
fun Int64Schema() = SchemaType {
    constructor { (it as? BsonInt64)?.value }
    formatter { it?.let { BsonInt64(it) } }
}

/**
 * Obtain Int32 (Int) schema.
 *
 * @since 1.0.0
 */
fun Int32Schema() = SchemaType {
    constructor { (it as? BsonInt32)?.value }
    formatter { it?.let { BsonInt32(it) } }
}

/**
 * Create a new array schema.
 *
 * > The returned schema will NOT have any
 *   items mapped. So, you can say that the
 *   items of the array are transient by default.
 *
 * > This behaviour is explicitly chosen to mimic the
 *   mongoose behaviour.
 *
 * @since 1.0.0
 */
fun <T> ArraySchema() = SchemaType<_, _, MutableList<T>> {
    constructor { (it as? BsonArray)?.let { mutableListOf() } }
    formatter { it?.let { BsonArray() } }
}

/**
 * Obtain an Id schema.
 *
 * @since 1.0.0
 */
fun <T> IdSchema() =
    ObjectIdSchema<T>() + StringIdSchema()

/**
 * Obtain an ObjectId (Id) schema.
 *
 * @since 1.0.0
 */
fun <T> ObjectIdSchema() = SchemaType<_, _, Id<T>> {
    constructor {
        (it as? BsonObjectId)
            ?.let { Id(it.value) }
    }
    formatter {
        it?.takeIf { ObjectId.isValid(it.value) }
            ?.let { BsonObjectId(ObjectId(it.value)) }
    }
}

/**
 * Obtain a string (Id) schema.
 *
 * @since 1.0.0
 */
fun <T> StringIdSchema() = SchemaType<_, _, Id<T>> {
    constructor { (it as? BsonString)?.let { Id(it.value) } }
    formatter { it?.let { BsonString(it.value) } }
}

// Reflection

/**
 * Create a new root schema for the given [klass].
 *
 * The given [klass] must have a constructor with
 * no required arguments.
 *
 * Even if the input or output is null. The
 * functions' of the schema will always emit a
 * value.
 *
 * > The returned schema will NOT have any
 *   property registered. So, you can say that the
 *   properties of the class are transient by default.
 *
 * > This behaviour is explicitly chosen to mimic the
 *   mongoose behaviour.
 *
 * @since 1.0.0
 */
fun <T : Any> DocumentSchema(
    klass: KClass<out T>,
) = SchemaType {
    constructor { _getInstance(klass) }
    formatter { BsonDocument() }
}

/**
 * Create a new schema for the given [klass].
 *
 * The given [klass] must have a constructor with
 * no required arguments.
 *
 * > The returned schema will NOT have any
 *   property registered. So, you can say that the
 *   properties of the class are transient by default.
 *
 * > This behaviour is explicitly chosen to mimic the
 *   mongoose behaviour.
 *
 * @since 1.0.0
 */
fun <T : Any> ObjectSchema(
    klass: KClass<out T>,
) = SchemaType {
    constructor { (it as? BsonDocument)?.let { _getInstance(klass) } }
    formatter { it?.let { BsonDocument() } }
}

/**
 * Return a schema for the given enum [klass].
 *
 * @since 1.0.0
 */
fun <T : Enum<T>> EnumSchema(
    klass: KClass<T>,
) = SchemaType {
    constructor {
        it?.let { it as? BsonString }?.value
            ?.let { _getEnum(klass, it) }
    }
    formatter {
        it?.let { BsonString((it as T).name) }
    }
}

// internal

internal fun <T : Any> _getInstance(klass: KClass<out T>): T {
    @Suppress("UNCHECKED_CAST")
    return Json.decodeFromString(
        Json.serializersModule.serializer(klass.createType()),
        "{}"
    ) as T
}

internal fun <T : Enum<T>> _getEnum(klass: KClass<T>, name: String): T? {
    return klass.java.enumConstants.firstOrNull { it.name == name }
}
