package org.cufy.mangaka.schema.extension

import org.cufy.mangaka.Model
import org.cufy.mangaka.schema.FieldDefinitionBuilder
import org.cufy.mangaka.schema.ObjectSchemaBuilder
import org.cufy.mangaka.schema.onSerialize

/**
 * This function ensures that the given [block]
 * will be invoked before any (or every) database
 * mutation on with the model of the mutation as
 * its argument (this argument).
 *
 * No further guarantees.
 */
fun FieldDefinitionBuilder<*, *>.init(
    block: suspend Model<*>.(pathname: String) -> Unit
) {
    onSerialize { _, _, _ ->
        block(this.model, this.pathname)
    }
}

/**
 * This function ensures that the given [block]
 * will be invoked before any (or every) database
 * mutation on with the model of the mutation as
 * its argument (this argument).
 *
 * No further guarantees.
 */
fun ObjectSchemaBuilder<*>.init(
    block: suspend Model<*>.(pathname: String) -> Unit
) {
    onSerialize { _ ->
        block(this.model, this.pathname)
    }
}
