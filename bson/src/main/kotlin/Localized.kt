package org.cufy.bson

/**
 * A data class encapsulated an element with its language.
 */
data class Localized<T>(
    val element: T,
    val lang: String,
)

/**
 * Return a string derived from this one with
 * its content tagged with the given language [tag].
 */
@BsonKeywordMarker
infix fun String.lang(tag: String): String {
    if (tag.isEmpty()) return this
    return "$this#$tag"
}
