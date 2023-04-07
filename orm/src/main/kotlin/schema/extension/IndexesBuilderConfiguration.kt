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
package org.cufy.monkt.schema.extension

import org.cufy.bson.*
import org.cufy.mongodb.`$type`
import org.cufy.mongodb.CreateIndexModel
import org.cufy.mongodb.CreateIndexOptions
import org.cufy.monkt.*
import org.cufy.monkt.schema.*
import kotlin.reflect.KCallable

/* ============= ---- Protocol ---- ============= */

/**
 * A builder style indexes option configuration.
 *
 * @author LSafer
 * @since 2.0.0
 */
open class IndexesBuilderConfiguration : IndexesConfiguration() {
    /**
     * The index's keys.
     */
    val keys: MutableBsonDocument = MutableBsonDocument()

    /**
     * The index's options.
     */
    val options: CreateIndexOptions = CreateIndexOptions()
}

/* ============= --- Extensions --- ============= */

// scope

/**
 * Configure the index's options with the given [block].
 */
@OptIn(AdvancedMonktApi::class)
fun <T : Any, M> OptionScope<T, M, IndexesBuilderConfiguration>.options(
    block: CreateIndexOptions.() -> Unit
) {
    configuration.options.apply(block)
}

/**
 * Configure the index's keys with the given [block].
 */
@OptIn(AdvancedMonktApi::class)
fun <T : Any, M> OptionScope<T, M, IndexesBuilderConfiguration>.keys(
    block: BsonDocumentBlock
) {
    configuration.keys.apply(block)
}

//

/**
 * Configure the `partialFilterExpression` index
 * option with the given [block].
 *
 * If the current `partialFilterExpression` is not
 * a [BsonDocument] then it will be replaced with
 * a new [BsonDocument].
 */
@OptIn(AdvancedMonktApi::class)
fun <T : Any, M> OptionScope<T, M, IndexesBuilderConfiguration>.filter(
    block: BsonDocumentBlock
) {
    val current = configuration.options.partialFilterExpression ?: emptyMap()
    val merged = current + BsonDocument(block)
    configuration.options.partialFilterExpression = merged.toBsonDocument()
}

/**
 * Configure the `partialFilterExpression` index
 * option of the given [pathname] with the given [block].
 *
 * If the current `partialFilterExpression` is not
 * a [BsonDocument] then it will be replaced with
 * a new [BsonDocument].
 *
 * If the current `partialFilterExpression[pathname]`
 * is not a [BsonDocument] then it will be replaced
 * with a new [BsonDocument].
 */
fun <T : Any, M> OptionScope<T, M, IndexesBuilderConfiguration>.filter(
    pathname: Pathname,
    block: BsonDocumentBlock
) {
    filter {
        val current = this["$pathname"] as? BsonDocument ?: emptyMap()
        this["$pathname"] = (current + BsonDocument(block)).toBsonDocument()
    }
}

/**
 * Configure the `partialFilterExpression` index
 * option of the given [relativeName] with the given [block].
 *
 * If the current `partialFilterExpression` is not
 * a [BsonDocument] then it will be replaced with
 * a new [BsonDocument].
 *
 * If the current `partialFilterExpression[relativeName]`
 * is not a [BsonDocument] then it will be replaced
 * with a new [BsonDocument].
 */
fun <T : Any, M> OptionScope<T, M, IndexesBuilderConfiguration>.filter(
    relativeName: String,
    block: BsonDocumentBlock
) {
    filter(pathname + relativeName, block)
}

/**
 * Configure the `partialFilterExpression` index
 * option of the given [relativeName] with the given [block].
 *
 * If the current `partialFilterExpression` is not
 * a [BsonDocument] then it will be replaced with
 * a new [BsonDocument].
 *
 * If the current `partialFilterExpression[relativeName]`
 * is not a [BsonDocument] then it will be replaced
 * with a new [BsonDocument].
 */
fun <T : Any, M> OptionScope<T, M, IndexesBuilderConfiguration>.filter(
    relativeName: KCallable<*>,
    block: BsonDocumentBlock
) {
    filter(relativeName.name, block)
}

//

/**
 * Add an index key with the given [pathname] and [value].
 *
 * The resultant index will have the following
 * attributes after using this function:
 *
 * ```json
 * { <pathname>: <value> },
 * { partialFilterExpression: { <pathname>: { $type: <types> } } }
 * ```
 *
 * @param pathname the index's name.
 * @param types targeted field types for this index. (using partial filter expression)
 * @since 2.0.0
 */
fun OptionScope<Unit, Unit, IndexesBuilderConfiguration>.key(
    pathname: Pathname, vararg types: BsonType, value: BsonElement = 1.b
) {
    keys { "$pathname" by value }

    if (types.isNotEmpty()) {
        filter(pathname) { `$type` by types.map { it.value.b } }
    }
}

/**
 * Add an index key with the given [relativeName] and [value].
 *
 * The resultant index will have the following
 * attributes after using this function:
 *
 * ```json
 * { <pathname + relativeName>: <value> },
 * { partialFilterExpression: { <pathname + relativeName>: { $type: <types> } } }
 * ```
 *
 * @param relativeName the index's relative name.
 * @param types targeted field types for this index. (using partial filter expression)
 * @since 2.0.0
 */
fun OptionScope<Unit, Unit, IndexesBuilderConfiguration>.key(
    relativeName: String, vararg types: BsonType, value: BsonElement = 1.b
) {
    key(pathname + relativeName, *types, value = value)
}

/**
 * Add an index key with the given [relativeName] and [value].
 *
 * The resultant index will have the following
 * attributes after using this function:
 *
 * ```json
 * { <pathname + relativeName>: <value> },
 * { partialFilterExpression: { <pathname + relativeName>: { $type: <types> } } }
 * ```
 *
 * @param relativeName the index's relative name.
 * @param types targeted field types for this index. (using partial filter expression)
 * @since 2.0.0
 */
fun OptionScope<Unit, Unit, IndexesBuilderConfiguration>.key(
    relativeName: KCallable<*>, vararg types: BsonType, value: BsonElement = 1.b
) {
    key(relativeName.name, *types, value = value)
}

// builder

/**
 * Add an index from invoking the given [block].
 *
 * @param block the index builder block.
 * @since 2.0.0
 */
fun <T : Any, M> WithOptionsBuilder<T, M>.index(
    block: OptionBlock<Unit, Unit, IndexesBuilderConfiguration>
) {
    val configuration = IndexesBuilderConfiguration()
    staticOption(configuration) {
        block(it)

        val keys = configuration.keys.toBsonDocument()
        val options = configuration.options

        val index = CreateIndexModel(keys, options)

        @Suppress("UNCHECKED_CAST")
        this as OptionScope<Unit, Unit, IndexesConfiguration>

        index(index)
    }
}

//

/**
 * Add a unique index from invoking the given [block].
 *
 * @param block the index builder block.
 * @since 2.0.0
 */
fun <T : Any, M> WithOptionsBuilder<T, M>.unique(
    block: OptionBlock<Unit, Unit, IndexesBuilderConfiguration>
) {
    index {
        options { unique = true }

        block(it)
    }
}

/**
 * Add a unique index for this field.
 *
 * @param types targeted field types for this index. (using partial filter expression)
 * @since 2.0.0
 */
fun <T : Any, M> FieldDefinitionBuilder<T, M>.unique(
    vararg types: BsonType
) {
    unique {
        key(pathname, *types)
    }
}
