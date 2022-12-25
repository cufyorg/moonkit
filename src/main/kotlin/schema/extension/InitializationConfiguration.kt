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
 * Options to tweak the behaviour of initialization
 * performance.
 */
class InitializationTweak :
    WithFiltersTweak<InitializationConfiguration> {

    @AdvancedMonktApi("Use `filter()` or `skip()` instead")
    override val filters: MutableList<ReturnOptionDataBlock<*, *, InitializationConfiguration, Boolean>> =
        mutableListOf()
}

/**
 * The init option configuration.
 *
 * Covers both [initImpl] and [staticInit].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class InitializationConfiguration

/* ============= --- Extensions --- ============= */

// builder

/**
 * Add the given [block] to be invoked on instance
 * initialization.
 *
 * @param block the option block.
 * @since 2.0.0
 * @see InitializationConfiguration
 */
fun <T : Any, M> WithOptionsBuilder<T, M>.init(
    block: OptionBlock<T, M, InitializationConfiguration>
) {
    option(InitializationConfiguration(), block)
}

/**
 * Add the given [block] to be invoked on model
 * initialization.
 *
 * @param block the option block.
 * @since 2.0.0
 * @see InitializationConfiguration
 */
fun <T : Any, M> WithOptionsBuilder<T, M>.staticInit(
    block: OptionBlock<Unit, Unit, InitializationConfiguration>
) {
    staticOption(InitializationConfiguration(), block)
}

// monkt

/**
 * Run the static init options for the given [models].
 */
@AdvancedMonktApi("This is a part of the model initialization process")
suspend fun Monkt.performStaticInitialization(
    models: List<Model<*>>,
    tweak: InitializationTweak?
) {
    var options = models.flatMap {
        it.obtainOptions<InitializationConfiguration>()
    }

    if (tweak != null) {
        options = options.filter {
            tweak.filters.all { filter ->
                filter(it)
            }
        }
    }

    performOption(options)
}

/**
 * Run the init options for the given [instances].
 */
@AdvancedMonktApi("This is a part of the decoding process")
suspend fun Monkt.performInitialization(
    instances: List<Any>,
    tweak: InitializationTweak
) {
    var options = instances.flatMap {
        Document.obtainOptions<InitializationConfiguration>(it)
    }

    options = options.filter {
        tweak.filters.all { filter ->
            filter(it)
        }
    }

    performOption(options)
}
