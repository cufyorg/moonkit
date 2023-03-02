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
package org.cufy.monkt

import com.mongodb.CursorType
import com.mongodb.client.model.Collation
import com.mongodb.client.model.changestream.FullDocument
import com.mongodb.reactivestreams.client.*
import org.bson.BsonTimestamp
import org.cufy.bson.*
import java.util.concurrent.TimeUnit

/* =========== - DistinctPublisher  - =========== */

/**
 * A wrapper for [DistinctPublisher].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class DistinctPublisherScope(
    var publisher: DistinctPublisher<BsonDocument>
) {
    // ignored members
    // - first() : irrelevant

    /**
     * Sets the query filter to apply to the query.
     *
     * @see DistinctPublisher.filter
     */
    var filter: Bson?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.filter(value)
        }

    /**
     * Sets the maximum execution time on the server for this operation.
     *
     * @param value  the max time
     * @param unit the time unit, which may not be null
     * @see DistinctPublisher.maxTime
     */
    fun maxTime(value: Long, unit: TimeUnit) {
        publisher = publisher.maxTime(value, unit)
    }

    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see DistinctPublisher.collation
     */
    var collation: Collation?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.collation(value)
        }

    /**
     * Sets the number of documents to return per batch.
     *
     * @see FindPublisher.batchSize
     */
    var batchSize: Int
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.batchSize(value)
        }

    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see AggregatePublisher.comment
     */
    var comment: BsonValue?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.comment(value)
        }

    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see AggregatePublisher.comment
     */
    fun comment(value: String?) {
        comment = value?.let { BsonString(it) }
    }
}

/**
 * Configure [this] publisher instance using the
 * given [block] and return the configured
 * instance.
 */
fun DistinctPublisher<BsonDocument>.configure(
    block: DistinctPublisherScope.() -> Unit
): DistinctPublisher<BsonDocument> {
    return DistinctPublisherScope(this)
        .apply(block)
        .publisher
}

/* ============= - FindPublisher  - ============= */

/**
 * A wrapper for [FindPublisher].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class FindPublisherScope(
    /**
     * The current publisher instance.
     */
    var publisher: FindPublisher<BsonDocument>
) {
    // ignored members
    // - oplogReplay() : deprecation
    // - explain()     : irrelevant

    /**
     * Sets the query filter to apply to the query.
     *
     * @see FindPublisher.filter
     */
    var filter: Bson?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.filter(value)
        }

    /**
     * Sets the limit to apply.
     *
     * @see FindPublisher.limit
     */
    var limit: Int
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.limit(value)
        }

    /**
     * Sets the number of documents to skip.
     *
     * @see FindPublisher.skip
     */
    var skip: Int
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.skip(value)
        }

    /**
     * Sets the maximum execution time on the server for this operation.
     *
     * @param value  the max time
     * @param unit the time unit, which may not be null
     * @see FindPublisher.maxTime
     */
    fun maxTime(value: Long, unit: TimeUnit) {
        publisher = publisher.maxTime(value, unit)
    }

    /**
     * The maximum amount of time for the server to wait on new documents to satisfy a tailable cursor
     * query. This only applies to a TAILABLE_AWAIT cursor. When the cursor is not a TAILABLE_AWAIT cursor,
     * this option is ignored.
     *
     * On servers &gt;= 3.2, this option will be specified on the getMore command as "maxTimeMS". The default
     * is no value: no "maxTimeMS" is sent to the server with the getMore command.
     *
     * On servers &lt; 3.2, this option is ignored, and indicates that the driver should respect the server's default value
     *
     * A zero value will be ignored.
     *
     * @param value  the max await time
     * @param unit the time unit to return the result in
     * @see FindPublisher.maxAwaitTime
     */
    fun maxAwaitTime(value: Long, unit: TimeUnit) {
        publisher = publisher.maxAwaitTime(value, unit)
    }

    /**
     * Sets a document describing the fields to return for all matching documents.
     *
     * @see FindPublisher.projection
     */
    var projection: Bson?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.projection(value)
        }

    /**
     * Sets the sort criteria to apply to the query.
     *
     * @see FindPublisher.sort
     */
    var sort: Bson?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.sort(value)
        }

    /**
     * Sets the sort criteria to apply to the query.
     *
     * @see FindPublisher.sort
     */
    fun sort(document: BsonDocumentBlock) {
        sort = document(document)
    }

    /**
     * The server normally times out idle cursors after an inactivity period (10 minutes)
     * to prevent excess memory use. Set this option to prevent that.
     *
     * @see FindPublisher.noCursorTimeout
     */
    var noCursorTimeout: Boolean
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.noCursorTimeout(value)
        }

    /**
     * Get partial results from a sharded cluster if one or more shards are unreachable (instead of throwing an error).
     *
     * @see FindPublisher.partial
     */
    var partial: Boolean
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.partial(value)
        }

    /**
     * Sets the cursor type.
     *
     * @see FindPublisher.cursorType
     */
    var cursorType: CursorType
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.cursorType(value)
        }

    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see FindPublisher.collation
     */
    var collation: Collation?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.collation(value)
        }

    /**
     * Sets the comment to the query.
     * A null value means no comment is set.
     *
     * @see FindPublisher.comment
     */
    var comment: BsonValue?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.comment(value)
        }

    /**
     * Sets the comment to the query.
     * A null value means no comment is set.
     *
     * @see FindPublisher.comment
     */
    fun comment(value: String?) {
        comment = value?.let { BsonString(it) }
    }

    /**
     * Sets the hint for which index to use.
     * A null value means no hint is set.
     *
     * @see FindPublisher.hint
     */
    var hint: Bson?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.hint(value)
        }

    /**
     * Sets the hint for which index to use.
     * A null value means no hint is set.
     *
     * @see FindPublisher.hintString
     */
    var hintString: String?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.hintString(value)
        }

    /**
     * Add top-level variables to the operation.
     * A null value means no variables are set.
     *
     * Allows for improved command readability by
     * separating the variables from the query text.
     *
     * @see FindPublisher.let
     */
    var let: Bson?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.let(value)
        }

    /**
     * Sets the exclusive upper bound for a specific index.
     * A null value means no max is set.
     *
     * @see FindPublisher.max
     */
    var max: Bson?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.max(value)
        }

    /**
     * Sets the minimum inclusive lower bound for a specific index.
     * A null value means no max is set.
     *
     * @see FindPublisher.min
     */
    var min: Bson?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.min(value)
        }

    /**
     * Sets the returnKey.
     * If true the find operation will return only
     * the index keys in the resulting documents.
     *
     * @see FindPublisher.returnKey
     */
    var returnKey: Boolean
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.returnKey(value)
        }

    /**
     * Sets the showRecordId.
     * Set to true to add a field `$recordId` to the returned documents.
     *
     * @see FindPublisher.showRecordId
     */
    var showRecordId: Boolean
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.showRecordId(value)
        }

    /**
     * Sets the number of documents to return per batch.
     *
     * @see FindPublisher.batchSize
     */
    var batchSize: Int
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.batchSize(value)
        }

    /**
     * Enables writing to temporary files on the server. When set to true, the server
     * can write temporary data to disk while executing the find operation.
     *
     * This option is sent only if the caller explicitly sets it to true.
     *
     * @see FindPublisher.allowDiskUse
     */
    var allowDiskUse: Boolean?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.allowDiskUse(value)
        }
}

/**
 * Configure [this] publisher instance using the
 * given [block] and return the configured
 * instance.
 */
fun FindPublisher<BsonDocument>.configure(
    block: FindPublisherScope.() -> Unit
): FindPublisher<BsonDocument> {
    return FindPublisherScope(this)
        .apply(block)
        .publisher
}

/* =========== - AggregatePublisher - =========== */

/**
 * A wrapper for [AggregatePublisher].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class AggregatePublisherScope(
    /**
     * The current publisher instance.
     */
    var publisher: AggregatePublisher<BsonDocument>
) {
    // ignored members
    // - toCollection() : irrelevant
    // - first()        : irrelevant
    // - explain()      : irrelevant

    /**
     * Enables writing to temporary files.
     * A null value indicates that it's unspecified.
     *
     * @see AggregatePublisher.allowDiskUse
     */
    var allowDiskUse: Boolean?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.allowDiskUse(value)
        }

    /**
     * Sets the maximum execution time on the
     * server for this operation.
     *
     * @param value  the max time
     * @param unit the time unit, which may not be null
     * @see AggregatePublisher.maxTime
     */
    fun maxTime(value: Long, unit: TimeUnit) {
        publisher = publisher.maxTime(value, unit)
    }

    /**
     * The maximum amount of time for the server
     * to wait on new documents to satisfy a
     * `$changeStream` aggregation.
     *
     * A zero value will be ignored.
     *
     * @param value  the max await time
     * @param unit the time unit to return the result in
     * @see AggregatePublisher.maxAwaitTime
     */
    fun maxAwaitTime(value: Long, unit: TimeUnit) {
        publisher = publisher.maxAwaitTime(value, unit)
    }

    /**
     * Sets the bypass document level validation flag.
     *
     * @see AggregatePublisher.bypassDocumentValidation
     */
    var bypassDocumentValidation: Boolean?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.bypassDocumentValidation(value)
        }

    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see AggregatePublisher.collation
     */
    var collation: Collation?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.collation(value)
        }

    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see AggregatePublisher.comment
     */
    var comment: BsonValue?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.comment(value)
        }

    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see AggregatePublisher.comment
     */
    fun comment(value: String?) {
        comment = value?.let { BsonString(it) }
    }

    /**
     * Sets the hint for which index to use.
     * A null value means no hint is set.
     *
     * @see AggregatePublisher.hint
     */
    var hint: Bson?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.hint(value)
        }

    /**
     * Sets the hint for which index to use.
     * A null value means no hint is set.
     *
     * @see AggregatePublisher.hintString
     */
    var hintString: String?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.hintString(value)
        }

    /**
     * Add top-level variables to the aggregation.
     *
     * @see AggregatePublisher.let
     */
    var let: Bson?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.let(value)
        }

    /**
     * Sets the number of documents to return per batch.
     *
     * @see AggregatePublisher.batchSize
     */
    var batchSize: Int
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.batchSize(value)
        }
}

/**
 * Configure [this] publisher instance using the
 * given [block] and return the configured
 * instance.
 */
fun AggregatePublisher<BsonDocument>.configure(
    block: AggregatePublisherScope.() -> Unit
): AggregatePublisher<BsonDocument> {
    return AggregatePublisherScope(this)
        .apply(block)
        .publisher
}

/* ========= - ChangeStreamPublisher  - ========= */

/**
 * A wrapper for [ChangeStreamPublisher].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class ChangeStreamPublisherScope(
    /**
     * The current publisher instance.
     */
    var publisher: ChangeStreamPublisher<BsonDocument>
) {
    // ignored members
    // - withDocumentClass() : reflection
    // - first()             : irrelevant

    /**
     * Sets the fullDocument value.
     *
     * @see ChangeStreamPublisher.fullDocument
     */
    var fullDocument: FullDocument
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.fullDocument(value)
        }

    /**
     * Sets the logical starting point for the new
     * change stream.
     *
     * @see ChangeStreamPublisher.resumeAfter
     */
    var resumeAfter: BsonDocument
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.resumeAfter(value)
        }

    /**
     * The change stream will only provide changes
     * that occurred after the specified timestamp.
     *
     * Any command run against the server will
     * return an operation time that can be used
     * here.
     *
     * The default value is an operation time
     * obtained from the server before the change
     * stream was created.
     *
     * @see ChangeStreamPublisher.startAtOperationTime
     */
    var startAtOperationTime: BsonTimestamp
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.startAtOperationTime(value)
        }

    /**
     * Similar to [resumeAfter], this option takes
     * a resume token and starts a new change
     * stream returning the first notification
     * after the token.
     *
     * This will allow users to watch collections
     * that have been dropped and recreated or
     * newly renamed collections without missing
     * any notifications.
     *
     * Note: The server will report an error if
     * both [startAfter] and [resumeAfter] are
     * specified.
     *
     * @see ChangeStreamPublisher.startAfter
     */
    var startAfter: BsonDocument
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.startAfter(value)
        }

    /**
     * Sets the maximum await execution time on
     * the server for this operation.
     *
     * @param value the max await time.
     *              A zero value will be ignored,
     *              and indicates that the driver
     *              should respect the server's
     *              default value
     * @param unit the time unit, which may not be null
     * @see ChangeStreamPublisher.maxAwaitTime
     */
    fun maxAwaitTime(value: Long, unit: TimeUnit) {
        publisher = publisher.maxAwaitTime(value, unit)
    }

    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see DistinctPublisher.collation
     */
    var collation: Collation?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.collation(value)
        }

    /**
     * Sets the number of documents to return per batch.
     *
     * @see FindPublisher.batchSize
     */
    var batchSize: Int
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.batchSize(value)
        }

    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see AggregatePublisher.comment
     */
    var comment: BsonValue?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.comment(value)
        }

    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see AggregatePublisher.comment
     */
    fun comment(value: String?) {
        comment = value?.let { BsonString(it) }
    }
}

/**
 * Configure [this] publisher instance using the
 * given [block] and return the configured
 * instance.
 */
fun ChangeStreamPublisher<BsonDocument>.configure(
    block: ChangeStreamPublisherScope.() -> Unit
): ChangeStreamPublisher<BsonDocument> {
    return ChangeStreamPublisherScope(this)
        .apply(block)
        .publisher
}

/* ========== - ListIndexesPublisher - ========== */

/**
 * A wrapper for [ListIndexesPublisher].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class ListIndexesPublisherScope(
    /**
     * The current publisher instance.
     */
    var publisher: ListIndexesPublisher<BsonDocument>
) {
    // ignored members
    // - first() : irrelevant

    /**
     * Sets the maximum execution time on the
     * server for this operation.
     *
     * @param value  the max time
     * @param unit the time unit, which may not be null
     * @see ListIndexesPublisher.maxTime
     */
    fun maxTime(value: Long, unit: TimeUnit) {
        publisher = publisher.maxTime(value, unit)
    }

    /**
     * Sets the number of documents to return per batch.
     *
     * @see ListIndexesPublisher.batchSize
     */
    var batchSize: Int
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.batchSize(value)
        }

    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see ListIndexesPublisher.comment
     */
    var comment: BsonValue?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.comment(value)
        }

    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see ListIndexesPublisher.comment
     */
    fun comment(value: String?) {
        comment = value?.let { BsonString(it) }
    }
}

/**
 * Configure [this] publisher instance using the
 * given [block] and return the configured
 * instance.
 */
fun ListIndexesPublisher<BsonDocument>.configure(
    block: ListIndexesPublisherScope.() -> Unit
): ListIndexesPublisher<BsonDocument> {
    return ListIndexesPublisherScope(this)
        .apply(block)
        .publisher
}

/* ======== - ListCollectionsPublisher - ======== */

/**
 * A wrapper for [ListCollectionsPublisher].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class ListCollectionsPublisherScope(
    /**
     * The current publisher instance.
     */
    var publisher: ListCollectionsPublisher<BsonDocument>
) {
    // ignored members
    // - first() : irrelevant

    /**
     * Sets the query filter to apply to the query.
     *
     * @see ListCollectionsPublisher.filter
     */
    var filter: Bson?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.filter(value)
        }

    /**
     * Sets the maximum execution time on the server for this operation.
     *
     * @param value  the max time
     * @param unit the time unit, which may not be null
     * @see ListCollectionsPublisher.maxTime
     */
    fun maxTime(value: Long, unit: TimeUnit) {
        publisher = publisher.maxTime(value, unit)
    }

    /**
     * Sets the number of documents to return per batch.
     *
     * @see ListCollectionsPublisher.batchSize
     */
    var batchSize: Int
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.batchSize(value)
        }

    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see ListCollectionsPublisher.comment
     */
    var comment: BsonValue?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.comment(value)
        }

    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see ListCollectionsPublisher.comment
     */
    fun comment(value: String?) {
        comment = value?.let { BsonString(it) }
    }
}

/**
 * Configure [this] publisher instance using the
 * given [block] and return the configured
 * instance.
 */
fun ListCollectionsPublisher<BsonDocument>.configure(
    block: ListCollectionsPublisherScope.() -> Unit
): ListCollectionsPublisher<BsonDocument> {
    return ListCollectionsPublisherScope(this)
        .apply(block)
        .publisher
}

/* ========= - ListDatabasesPublisher - ========= */

/**
 * A wrapper for [ListDatabasesPublisher].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class ListDatabasesPublisherScope(
    /**
     * The current publisher instance.
     */
    var publisher: ListDatabasesPublisher<BsonDocument>
) {
    // ignored members
    // - first() : irrelevant

    /**
     * Sets the maximum execution time on the server for this operation.
     *
     * @param value  the max time
     * @param unit the time unit, which may not be null
     * @see ListDatabasesPublisher.maxTime
     */
    fun maxTime(value: Long, unit: TimeUnit) {
        publisher = publisher.maxTime(value, unit)
    }

    /**
     * Sets the query filter to apply to the query.
     *
     * @see ListDatabasesPublisher.filter
     */
    var filter: Bson?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.filter(value)
        }

    /**
     * Sets the nameOnly flag that indicates
     * whether the command should return just the
     * database names or return the database names
     * and size information.
     *
     * @see ListDatabasesPublisher.nameOnly
     */
    var nameOnly: Boolean?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.nameOnly(value)
        }

    /**
     * Sets the authorizedDatabasesOnly flag that
     * indicates whether the command should return
     * just the databases which the user is
     * authorized to see.
     *
     * @see ListDatabasesPublisher.authorizedDatabasesOnly
     */
    var authorizedDatabasesOnly: Boolean?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.authorizedDatabasesOnly(value)
        }

    /**
     * Sets the number of documents to return per batch.
     *
     * @see ListDatabasesPublisher.batchSize
     */
    var batchSize: Int
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.batchSize(value)
        }

    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see ListDatabasesPublisher.comment
     */
    var comment: BsonValue?
        get() = error("Inaccessible Property")
        set(value) {
            publisher = publisher.comment(value)
        }

    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see ListDatabasesPublisher.comment
     */
    fun comment(value: String?) {
        comment = value?.let { BsonString(it) }
    }
}

/**
 * Configure [this] publisher instance using the
 * given [block] and return the configured
 * instance.
 */
fun ListDatabasesPublisher<BsonDocument>.configure(
    block: ListDatabasesPublisherScope.() -> Unit
): ListDatabasesPublisher<BsonDocument> {
    return ListDatabasesPublisherScope(this)
        .apply(block)
        .publisher
}
