package org.cufy.mangaka.extension

import org.cufy.mangaka.Schema
import org.cufy.mangaka.SchemaScope

/**
 * Add the given validator [function] to this
 * schema and apply it only when the value is
 * present.
 *
 * @since 1.0.0
 */
fun <D, O, T> Schema<D, O, T>.insure(
    message: String,
    function: suspend SchemaScope<D, O, T>.(T) -> Boolean
) {
    validate(message) { it == null || function(it) }
}

/**
 * Add the given validator [function] to this
 * schema and apply it only when the value is
 * present.
 *
 * @since 1.0.0
 */
fun <D, O, T> Schema<D, O, T>.insure(
    message: suspend SchemaScope<D, O, T>.(T) -> String = {
        "Validation failed for path $path"
    },
    function: suspend SchemaScope<D, O, T>.(T) -> Boolean
) {
    validate({ message(it!!) }) { it == null || function(it) }
}
