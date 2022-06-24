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
package org.cufy.mangaka

import org.cufy.weakness.WeakProperty

/**
 * A marker/utility interface to add to model
 * classes to make utility extension functions
 * work with them.
 *
 * To use the utilities offered by this marker.
 * The instances of the marker MUST be constructed
 * by a model.
 *
 * @author LSafer
 * @since 1.0.0
 */
interface Document

/**
 * Return the id of the document.
 *
 * @since 1.0.0
 */
var <T : Document> T._id: Id<T> by WeakProperty()

/**
 * Return the model created the document.
 *
 * @since 1.0.0
 */
var <T : Document> T.model: Model<T> by WeakProperty()

/**
 * Return true if the document is flagged not
 * present in the database.
 *
 * @since 1.0.0
 */
var <T : Document> T.isNew: Boolean by WeakProperty()

/**
 * Return true if the document is flagged deleted
 * from the database.
 *
 * @since 1.0.0
 */
var <T : Document> T.isDeleted: Boolean by WeakProperty()

/**
 * Return the id of the document.
 */
internal var <T : Any> T._id: Id<T> by WeakProperty()

/**
 * Return the model created the document.
 */
internal var <T : Any> T.model: Model<T> by WeakProperty()

/**
 * Return true if the document is flagged not
 * present in the database.
 */
internal var <T : Any> T.isNew: Boolean by WeakProperty()

/**
 * Return true if the document is flagged deleted
 * from the database.
 */
internal var <T : Any> T.isDeleted: Boolean by WeakProperty()

/**
 * Saves this document by inserting a new document
 * into the database if the document is new, or
 * updates the existing document.
 *
 * This function will validate the document before
 * executing.
 *
 * @since 1.0.0
 */
suspend fun Document.save() {
    this.model.save(this)
}

/**
 * Removes this document from the db.
 */
suspend fun Document.remove() {
    this.model.remove(this)
}

/**
 * Executes the schema validator for this document.
 */
suspend fun Document.validate() {
    this.model.validate(this)
}
