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
 * Options to tweak the behaviour of Deletion
 * performance.
 */
class DeletionTweak :
    WithFiltersTweak<DeletionConfiguration> {

    @AdvancedMonktApi("Use `filter()` or `skip()` instead")
    override val filters: MutableList<ReturnOptionDataBlock<*, *, DeletionConfiguration, Boolean>> =
        mutableListOf()
}

/**
 * The deletion option configuration.
 *
 * @author LSafer
 * @since 2.0.0
 */
open class DeletionConfiguration {
    /**
     * The instances to be deleted.
     */
    val instances: MutableList<Any> = mutableListOf()
}

/* ============= --- Extensions --- ============= */

// scope

/**
 * Add the given [instance] to be deleted.
 */
@OptIn(AdvancedMonktApi::class)
fun <T : Any, M> OptionScope<T, M, DeletionConfiguration>.delete(instance: Any) {
    configuration.instances += instance
}

// builder

/**
 * Add the given [block] to configure the deletion
 * behaviour.
 */
fun <T : Any, M> WithOptionsBuilder<T, M>.deletion(
    block: OptionBlock<T, M, DeletionConfiguration>
) {
    val configuration = DeletionConfiguration()
    option(configuration, block)
}

// monkt

/**
 * Run the deletion options for the given [instances].
 *
 * @return the instances to be deleted.
 */
@AdvancedMonktApi("This is a part of the deletion process")
suspend fun Monkt.performDeletion(
    instances: List<Any>,
    tweak: DeletionTweak
): List<Any> {
    var options = instances.flatMap {
        Document.obtainOptions<DeletionConfiguration>(it)
    }

    options = options.filter {
        tweak.filters.all { filter ->
            filter(it)
        }
    }

    performOption(options)

    val nextInstances = options.flatMap { it.configuration.instances }
        .filter { it in instances }

    if (nextInstances.isNotEmpty()) {
        return instances + performDeletion(nextInstances, tweak)
    }

    return instances
}
