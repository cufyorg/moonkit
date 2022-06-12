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
package org.cufy.mangaka.internal

import org.cufy.mangaka.Id
import org.cufy.mangaka.Model
import java.lang.ref.WeakReference
import java.util.*

internal val __metadata = IdentityHashMap<WeakReference<Any>, MetaData<Any>>()

/**
 * An instance holding database internal state of
 * some object.
 */
data class MetaData<T>(
    /**
     * The id of the target object.
     */
    var id: Id<T>,
    /**
     * The model created the target object.
     */
    var model: Model<T & Any>,
    /**
     * True, if the object is not present in the
     * database.
     */
    var isNew: Boolean,
    /**
     * True, if the object is deleted from the
     * database.
     */
    var isDeleted: Boolean
) {
    companion object {
        /**
         * Internal cleanup. Call whenever. Does
         * nothing special. No side effects.
         */
        internal fun clean() {
            __metadata.entries.removeIf { it.key.get() === null }
        }

        /**
         * Get the metadata set for the given [value].
         *
         * @since 1.0.0
         */
        fun <T> get(value: T): MetaData<T>? {
            clean()

            val entry = __metadata.entries.firstOrNull { it.key.get() === value }

            @Suppress("UNCHECKED_CAST")
            return entry?.value as? MetaData<T>
        }

        /**
         * Set the metadata set for the given [value].
         *
         * @since 1.0.0
         */
        fun <T> set(value: T, metadata: MetaData<T>) {
            clean()

            val entry = __metadata.entries.firstOrNull { it.key.get() === value }

            if (entry != null)
                metadataAlreadyInitializedError(value)

            @Suppress("UNCHECKED_CAST")
            __metadata[WeakReference(value)] = metadata as MetaData<Any>
        }
    }
}
