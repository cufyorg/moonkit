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
package org.cufy.mangaka.schema

import org.cufy.mangaka.Model

/**
 * The variables of a schema operation.
 *
 * @author LSafer
 * @since 1.0.0
 */
class SchemaScope<O, T> constructor(
    /**
     * The parent scope.
     */
    val parent: SchemaScope<*, O>?,
    /**
     * The value currently operating on.
     */
    val self: O,
    /**
     * The name of the current path.
     */
    val name: String,
    /**
     * The schema of the current path.
     *
     * @since 1.0.0
     */
    val schema: Schema<T>,
    /**
     * The model of the root document.
     *
     * @since 1.0.0
     */
    val model: Model<*>,
    /**
     * The root document.
     *
     * @since 1.0.0
     */
    val document: Any?,
    /**
     * The paths to be skipped.
     */
    val skip: List<String>,
    /**
     * Fields filter.
     */
    val filter: (String) -> Boolean,
    /**
     * Extra attributes.
     */
    val attributes: Map<String, Any> = emptyMap()
) {
    /**
     * The names to this scope.
     *
     * @since 1.0.0
     */
    val names: List<String> by lazy {
        (parent?.names ?: emptyList()) + name
    }

    /**
     * The path of this scope.
     *
     * @since 1.0.0
     */
    val path: String by lazy {
        names.joinToString(".")
    }

    /**
     * The path of this scope without the root name.
     *
     * @since 1.0.0
     */
    val pathname: String by lazy {
        names.drop(1).joinToString(".")
    }
}

/**
 * A builder for creating a new [SchemaScope].
 *
 * @author LSafer
 * @since 1.0.0
 */
open class SchemaScopeBuilder<O, T> {
    /**
     * The parent scope.
     */
    var parent: SchemaScope<*, O>? = null

    /**
     * The value currently operating on.
     */
    var self: O? = null

    /**
     * The root document.
     *
     * @since 1.0.0
     */
    var document: Any? = null

    /**
     * The paths to be skipped.
     */
    val skip: MutableList<String> = mutableListOf()

    /**
     * Fields filter.
     */
    var filter: (String) -> Boolean = { true }

    /**
     * Extra attributes.
     */
    val attributes: MutableMap<String, Any> = mutableMapOf()

    /**
     * The name of the current path.
     */
    lateinit var name: String

    /**
     * The schema of the current path.
     *
     * @since 1.0.0
     */
    lateinit var schema: Schema<T>

    /**
     * The model of the root document.
     *
     * @since 1.0.0
     */
    lateinit var model: Model<*>

    /**
     * Build the scope.
     *
     * @since 1.0.0
     */
    fun build(): SchemaScope<O, T> {
        @Suppress("UNCHECKED_CAST")
        return SchemaScope(
            parent = this.parent,
            name = this.name,
            self = this.self as O,
            schema = this.schema,
            model = this.model,
            document = this.document,
            skip = this.skip.toList(),
            filter = this.filter,
            attributes = this.attributes.toMap()
        )
    }
}

/**
 * Construct a new schema scope.
 *
 * @param parent the parent scope. (If any)
 * @param block the builder block.
 * @since 1.0.0
 */
fun <O, T> SchemaScope(
    parent: SchemaScope<*, O>? = null,
    block: SchemaScopeBuilder<O, T>.() -> Unit = {}
): SchemaScope<O, T> {
    val builder = SchemaScopeBuilder<O, T>()
    parent?.let {
        builder.parent = it
        builder.model = it.model
        builder.document = it.document
        builder.skip += it.skip
        builder.filter = it.filter
        builder.attributes += it.attributes
    }
    builder.apply(block)
    return builder.build()
}

/**
 * Add a fields filter.
 *
 * @param block the filter.
 * @since 1.1.0
 */
fun SchemaScopeBuilder<*, *>.filter(
    block: (String) -> Boolean
) {
    this.filter.let {
        this.filter = {
            it(it) && block(it)
        }
    }
}
