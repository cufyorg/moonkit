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
import org.cufy.mongodb.CreateIndexModel
import org.cufy.monkt.*
import org.cufy.monkt.schema.*

/* ============= ---- Protocol ---- ============= */

/**
 * Options to tweak the behaviour of index
 * performance.
 */
class IndexesTweak :
    WithFiltersTweak<IndexesConfiguration> {

    @AdvancedMonktApi("Use `filter()` or `skip()` instead")
    override val filters: MutableList<ReturnOptionDataBlock<*, *, IndexesConfiguration, Boolean>> =
        mutableListOf()
}

/**
 * The indexes option configuration.
 *
 * @author LSafer
 * @since 2.0.0
 */
open class IndexesConfiguration {
    /**
     * The indexes to be added.
     */
    val indexes: MutableList<CreateIndexModel> = mutableListOf()
}

/* ============= --- Extensions --- ============= */

// scope

/**
 * Add the given [index].
 */
@OptIn(AdvancedMonktApi::class)
fun <T : Any, M> OptionScope<T, M, IndexesConfiguration>.index(
    index: CreateIndexModel
) {
    configuration.indexes += index
}

// builder

/**
 * Add the given index [block].
 *
 * @param block the index block.
 * @return 2.0.0
 */
fun <T : Any, M> WithOptionsBuilder<T, M>.indexes(
    block: OptionBlock<Unit, Unit, IndexesConfiguration>
) {
    val configuration = IndexesConfiguration()
    staticOption(configuration, block)
}

// monkt

/**
 * Run the indexes options for the given [models].
 *
 * @return the indexes by model.
 */
@AdvancedMonktApi("This is a part of the model initialization process")
suspend fun Monkt.performIndexes(
    models: List<Model<*>>,
    tweak: IndexesTweak
): Map<Model<*>, List<CreateIndexModel>> {
    var options = models.flatMap {
        it.obtainOptions<IndexesConfiguration>()
    }

    options = options.filter {
        tweak.filters.all { filter ->
            filter(it)
        }
    }

    performOption(options)

    return options
        .groupBy { it.model }
        .mapValues { (_, value) ->
            value.flatMap { it.configuration.indexes }
        }
}
