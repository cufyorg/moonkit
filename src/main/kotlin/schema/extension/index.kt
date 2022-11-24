package org.cufy.mangaka.schema.extension

import com.mongodb.client.model.IndexOptions
import org.cufy.mangaka.bson.by
import org.cufy.mangaka.bson.document
import org.cufy.mangaka.ensureIndex
import org.cufy.mangaka.schema.FieldDefinitionBuilder
import org.cufy.mangaka.schema.ObjectSchemaBuilder

/**
 * Add an index for this field with the given
 * [options]
 *
 * @since 1.2.0
 */
fun FieldDefinitionBuilder<*, *>.index(
    options: IndexOptions.(pathname: String) -> Unit = {}
) {
    init { pathname ->
        ensureIndex(document(pathname by 1)) {
            options(pathname)
        }
    }
}

/**
 * Add an index for the given [fields] with the
 * given [options]
 *
 * @since 1.2.0
 */
fun ObjectSchemaBuilder<*>.index(
    vararg fields: String,
    options: IndexOptions.(pathnames: List<String>) -> Unit = {}
) {
    index(fields.asList(), options)
}

/**
 * Add an index for the given [fields] with the
 * given [options]
 *
 * @since 1.2.0
 */
fun ObjectSchemaBuilder<*>.index(
    fields: List<String>,
    options: IndexOptions.(pathnames: List<String>) -> Unit = {}
) {
    init { pathname ->
        val pathnames = fields.map { name ->
            listOf(pathname, name)
                .filter { it.isNotBlank() }
                .joinToString(".")
        }

        ensureIndex(document(pathnames.map { it by 1 })) {
            options(pathnames)
        }
    }
}
