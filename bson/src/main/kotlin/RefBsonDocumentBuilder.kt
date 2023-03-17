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
package org.cufy.bson

/**
 * A block of code building a bson document with a generic `ref`.
 *
 * @since 2.0.0
 */
typealias RefBsonDocumentBlock<T> = RefBsonDocumentBuilder<T>.() -> Unit

/**
 * A builder building a [BsonDocument] with a generic [ref].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class RefBsonDocumentBuilder<T>(
    @BsonBuildMarker
    val ref: T,
    document: BsonDocument = BsonDocument()
) : BsonDocumentBuilder(document)

/**
 * Construct a new bson document using the given
 * builder [block] with [ref].
 */
fun <T> document(
    ref: T,
    block: RefBsonDocumentBlock<T>
): BsonDocument {
    val builder = RefBsonDocumentBuilder(ref)
    builder.apply(block)
    return builder.document
}

/**
 * Return a pair containing [this] and the result
 * from creating a document with the given [block].
 */
@BsonBuildMarker
infix fun <T> T.tobdocument(
    block: RefBsonDocumentBlock<T>
): Pair<T, BsonDocument> {
    return this to document(this, block)
}

/**
 * Apply the given [block] to this document with [ref].
 */
fun <T> BsonDocument.configure(
    ref: T,
    block: RefBsonDocumentBlock<T>
) {
    val builder = RefBsonDocumentBuilder(ref, this)
    builder.apply(block)
}
