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
package org.cufy.mangaka.bson

import com.mongodb.client.model.Filters
import org.bson.*
import kotlin.reflect.KProperty

/**
 * A marker for bson builder functions.
 *
 * @author LSafer
 * @since 1.0.0
 */
@DslMarker
annotation class BsonBuilderDsl

/**
 * Create a [BsonElement] from associating the
 * receiver string with the given [item].
 *
 * @since 1.0.0
 */
@BsonBuilderDsl
infix fun String.by(item: Any?): BsonElement {
    val bson = Filters.eq("", item).toBsonDocument()[""]
    return BsonElement(this, bson ?: BsonNull.VALUE)
}

/**
 * Create a [BsonElement] from associating the
 * name of the receiver property with the given
 * [item].
 *
 * @since 1.0.0
 */
@BsonBuilderDsl
infix fun <T> KProperty<T>.by(item: T): BsonElement {
    val bson = Filters.eq("", item).toBsonDocument()[""]
    return BsonElement(name, bson ?: BsonNull.VALUE)
}

/**
 * Create a [BsonDocument] with the given [elements].
 *
 * @since 1.0.0
 */
@BsonBuilderDsl
fun document(vararg elements: BsonElement): BsonDocument {
    return BsonDocument(elements.toList())
}

/**
 * Create a [BsonDocument] with the given [elements].
 *
 * @since 1.1.0
 */
@BsonBuilderDsl
fun document(elements: List<BsonElement>): BsonDocument {
    return BsonDocument(elements)
}

/**
 * Create a [BsonArray] with the given [items].
 *
 * @since 1.0.0
 */
@BsonBuilderDsl
fun array(vararg items: BsonValue): BsonArray {
    return BsonArray(items.toList())
}

/**
 * Create a [BsonArray] with the given [items].
 *
 * @since 1.1.0
 */
@BsonBuilderDsl
fun array(items: List<BsonValue>): BsonArray {
    return BsonArray(items)
}
