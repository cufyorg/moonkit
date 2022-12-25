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

import com.mongodb.client.model.IndexModel
import com.mongodb.client.model.IndexOptions
import org.cufy.bson.*
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
    val keys: BsonDocument = BsonDocument()

    /**
     * The index's options.
     */
    val options: IndexOptions = IndexOptions()
}

/* ============= --- Extensions --- ============= */

// scope

/**
 * Configure the index's options with the given [block].
 */
@OptIn(AdvancedMonktApi::class)
fun <T : Any, M> OptionScope<T, M, IndexesBuilderConfiguration>.options(
    block: IndexOptionsScope.() -> Unit
) {
    configuration.options.configure(block)
}

/**
 * Configure the index's keys with the given [block].
 */
@OptIn(AdvancedMonktApi::class)
fun <T : Any, M> OptionScope<T, M, IndexesBuilderConfiguration>.keys(
    block: BsonDocumentBlock
) {
    configuration.keys.configure(block)
}

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
    var filter = configuration.options.partialFilterExpression
            as? BsonDocument

    if (filter == null) {
        filter = BsonDocument()
        configuration.options.partialFilterExpression(filter)
    }

    filter.configure(block)
}

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
    pathname: Pathname, vararg types: BsonType
) {
    keys { "$pathname" by bint32(1) }

    if (types.isNotEmpty()) {
        filter { "$pathname" by { `$type` by types.map { bint32(it.value) } } }
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
    relativeName: String, vararg types: BsonType
) {
    key(pathname + relativeName, *types)
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
    relativeName: KCallable<*>, vararg types: BsonType
) {
    key(relativeName.name, *types)
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

        val keys = configuration.keys
        val options = configuration.options

        val index = IndexModel(keys, options)

        @Suppress("UNCHECKED_CAST")
        this as OptionScope<Unit, Unit, IndexesConfiguration>

        index(index)
    }
}

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
