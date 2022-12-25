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

import org.cufy.monkt.*
import org.cufy.monkt.schema.*

/* ============= ---- Protocol ---- ============= */

/**
 * Options to tweak the behaviour of migration
 * performance.
 */
class MigrationTweak :
    WithFiltersTweak<MigrationConfiguration> {

    @AdvancedMonktApi("Use `filter()` or `skip()` instead")
    override val filters: MutableList<ReturnOptionDataBlock<*, *, MigrationConfiguration, Boolean>> =
        mutableListOf()
}

/**
 * The configuration of an option invoked after
 * the instance is constructed.
 *
 * @author LSafer
 * @since 2.0.0
 */
open class MigrationConfiguration

/* ============= --- Extensions --- ============= */

// builder

/**
 * Add the given [block] to be invoked at on
 * instance initialization for migration.
 */
fun <T : Any, M> WithOptionsBuilder<T, M>.migration(
    block: OptionBlock<T, M, MigrationConfiguration>
) {
    val configuration = MigrationConfiguration()
    option(configuration, block)
}

// monkt

/**
 * Run the migration options for the given [instances].
 */
@AdvancedMonktApi("This is a part of the decoding process")
suspend fun Monkt.performMigration(
    instances: List<Any>,
    tweak: MigrationTweak
) {
    var options = instances.flatMap {
        Document.obtainOptions<MigrationConfiguration>(it)
    }

    options = options.filter {
        tweak.filters.all { filter ->
            filter(it)
        }
    }

    performOption(options)
}
