/*
 *	Copyright 2022-2023 cufy.org
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
package org.cufy.mongodb

import org.cufy.bson.BsonDocument
import org.cufy.bson.BsonElement
import kotlin.time.Duration

/* ============= ------------------ ============= */

/**
 * Create a new options instance from the given [block].
 */
fun DeleteOptions(
    block: DeleteOptions.() -> Unit
) = DeleteOptions().apply(block)

/**
 * The options that can be applied to a delete operations.
 *
 * @see com.mongodb.client.model.DeleteOptions
 * @author LSafer
 * @since 2.0.0
 */
data class DeleteOptions(
    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see com.mongodb.client.model.DeleteOptions.collation
     * @since 2.0.0
     */
    var collation: Collation? = null,
    /**
     * Sets the hint to apply.
     *
     * @see com.mongodb.client.model.DeleteOptions.hint
     * @since 2.0.0
     */
    var hint: BsonDocument? = null,
    /**
     * Sets the hint to apply.
     *
     * @see com.mongodb.client.model.DeleteOptions.hintString
     * @since 2.0.0
     */
    var hintString: String? = null,
    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see com.mongodb.client.model.DeleteOptions.comment
     * @since 2.0.0
     */
    var comment: BsonElement? = null,
    /**
     * Add top-level variables for the operation
     *
     * Allows for improved command readability by
     * separating the variables from the query text.
     *
     * @see com.mongodb.client.model.DeleteOptions.let
     * @since 2.0.0
     */
    var let: BsonDocument? = null
)

//

/**
 * Create a new options instance from the given [block].
 */
fun InsertOneOptions(
    block: InsertOneOptions.() -> Unit
) = InsertOneOptions().apply(block)

/**
 * The options that can be applied to an insert one operation.
 *
 * @see com.mongodb.client.model.InsertOneOptions
 * @author LSafer
 * @since 2.0.0
 */
data class InsertOneOptions(
    /**
     * Sets the bypass document level validation flag.
     *
     * @see com.mongodb.client.model.InsertOneOptions.bypassDocumentValidation
     * @since 2.0.0
     */
    var bypassDocumentValidation: Boolean? = null,
    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see com.mongodb.client.model.InsertOneOptions.comment
     * @since 2.0.0
     */
    var comment: BsonElement? = null
)

//

/**
 * Create a new options instance from the given [block].
 */
fun InsertManyOptions(
    block: InsertManyOptions.() -> Unit
) = InsertManyOptions().apply(block)

/**
 * The options that can be applied to an insert many operation.
 *
 * @see com.mongodb.client.model.InsertManyOptions
 * @author LSafer
 * @since 2.0.0
 */
data class InsertManyOptions(
    /**
     * Sets whether the server should insert the
     * documents in the order provided.
     *
     * @see com.mongodb.client.model.InsertManyOptions.ordered
     * @since 2.0.0
     */
    var ordered: Boolean = true,
    /**
     * Sets the bypass document level validation flag.
     *
     * @see com.mongodb.client.model.InsertManyOptions.bypassDocumentValidation
     * @since 2.0.0
     */
    var bypassDocumentValidation: Boolean? = null,
    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see com.mongodb.client.model.InsertManyOptions.comment
     * @since 2.0.0
     */
    var comment: BsonElement? = null
)

//

/**
 * Create a new options instance from the given [block].
 */
fun UpdateOptions(
    block: UpdateOptions.() -> Unit
) = UpdateOptions().apply(block)

/**
 * The options that can be applied to an update one operation.
 *
 * @see com.mongodb.client.model.UpdateOptions
 * @author LSafer
 * @since 2.0.0
 */
data class UpdateOptions(
    /**
     * Set to true if a new document should be
     * inserted if there are no matches to the
     * query filter.
     *
     * @see com.mongodb.client.model.UpdateOptions.upsert
     * @since 2.0.0
     */
    var upsert: Boolean = false,
    /**
     * Sets the bypass document level validation flag.
     *
     * @see com.mongodb.client.model.UpdateOptions.bypassDocumentValidation
     * @since 2.0.0
     */
    var bypassDocumentValidation: Boolean? = null,
    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see com.mongodb.client.model.UpdateOptions.collation
     * @since 2.0.0
     */
    var collation: Collation? = null,
    /**
     * Sets the array filters option.
     *
     * @see com.mongodb.client.model.UpdateOptions.arrayFilters
     * @since 2.0.0
     */
    var arrayFilters: List<BsonDocument>? = null,
    /**
     * Sets the hint for which index to use.
     * A null value means no hint is set.
     *
     * @see com.mongodb.client.model.UpdateOptions.hint
     * @since 2.0.0
     */
    var hint: BsonDocument? = null,
    /**
     * Sets the hint to apply.
     *
     * @see com.mongodb.client.model.UpdateOptions.hintString
     * @since 2.0.0
     */
    var hintString: String? = null,
    /**
     * Sets the comment for this operation. A null
     * value means no comment is set.
     *
     * @see com.mongodb.client.model.UpdateOptions.comment
     * @since 2.0.0
     */
    var comment: BsonElement? = null,
    /**
     * Add top-level variables for the operation
     *
     * Allows for improved command readability by
     * separating the variables from the query text.
     *
     * @see com.mongodb.client.model.UpdateOptions.let
     * @since 2.0.0
     */
    var let: BsonDocument? = null
)

//

/**
 * Create a new options instance from the given [block].
 */
fun ReplaceOptions(
    block: ReplaceOptions.() -> Unit
) = ReplaceOptions().apply(block)

/**
 * The options that can be applied to a replace one operation.
 *
 * @see com.mongodb.client.model.ReplaceOptions
 * @author LSafer
 * @since 2.0.0
 */
data class ReplaceOptions(
    /**
     * Set to true if a new document should be
     * inserted if there are no matches to the
     * query filter.
     *
     * @see com.mongodb.client.model.ReplaceOptions.upsert
     * @since 2.0.0
     */
    var upsert: Boolean = false,
    /**
     * Sets the bypass document level validation flag.
     *
     * @see com.mongodb.client.model.ReplaceOptions.bypassDocumentValidation
     * @since 2.0.0
     */
    var bypassDocumentValidation: Boolean? = null,
    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see com.mongodb.client.model.ReplaceOptions.collation
     * @since 2.0.0
     */
    var collation: Collation? = null,
    /**
     * Sets the hint to apply.
     *
     * @see com.mongodb.client.model.ReplaceOptions.hint
     * @since 2.0.0
     */
    var hint: BsonDocument? = null,
    /**
     * Sets the hint to apply.
     *
     * @see com.mongodb.client.model.ReplaceOptions.hintString
     * @since 2.0.0
     */
    var hintString: String? = null,
    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see com.mongodb.client.model.ReplaceOptions.comment
     * @since 2.0.0
     */
    var comment: BsonElement? = null,
    /**
     * Add top-level variables for the operation
     *
     * Allows for improved command readability by
     * separating the variables from the query text.
     *
     * @see com.mongodb.client.model.ReplaceOptions.let
     * @since 2.0.0
     */
    var let: BsonDocument? = null
)

//

/**
 * Create a new options instance from the given [block].
 */
fun BulkWriteOptions(
    block: BulkWriteOptions.() -> Unit
) = BulkWriteOptions().apply(block)

/**
 * The options that can be applied to a bulk write operation.
 *
 * @see com.mongodb.client.model.BulkWriteOptions
 * @author LSafer
 * @since 2.0.0
 */
data class BulkWriteOptions(
    /**
     * If true, then when a write fails, return without performing the remaining
     * writes. If false, then when a write fails, continue with the remaining writes, if any.
     * Defaults to true.
     *
     * @see com.mongodb.client.model.BulkWriteOptions.ordered
     * @since 2.0.0
     */
    var ordered: Boolean = true,
    /**
     * If true, allows the write to opt-out of document level validation.
     *
     * @see com.mongodb.client.model.BulkWriteOptions.bypassDocumentValidation
     * @since 2.0.0
     */
    var bypassDocumentValidation: Boolean? = null,
    /**
     * The comment to send with the query. The
     * default is not to include a comment with
     * the query.
     *
     * @see com.mongodb.client.model.BulkWriteOptions.comment
     * @since 2.0.0
     */
    var comment: BsonElement? = null,
    /**
     * Add top-level variables for the operation
     *
     * Allows for improved command readability by
     * separating the variables from the query text.
     * The value of let will be passed to all update
     * and delete, but not insert, commands.
     *
     * @see com.mongodb.client.model.BulkWriteOptions.let
     * @since 2.0.0
     */
    var let: BsonDocument? = null
)

/* ============= ------------------ ============= */

/**
 * Create a new options instance from the given [block].
 */
fun CountOptions(
    block: CountOptions.() -> Unit
) = CountOptions().apply(block)

/**
 * The options that can be applied to a count documents operation.
 *
 * @see com.mongodb.client.model.CountOptions
 * @author LSafer
 * @since 2.0.0
 */
data class CountOptions(
    /**
     * Sets the hint to apply.
     *
     * @see com.mongodb.client.model.CountOptions.hint
     * @since 2.0.0
     */
    var hint: BsonDocument? = null,
    /**
     * Sets the hint to apply.
     *
     * @see com.mongodb.client.model.CountOptions.hintString
     * @since 2.0.0
     */
    var hintString: String? = null,
    /**
     * Sets the limit to apply.
     *
     * @see com.mongodb.client.model.CountOptions.limit
     * @since 2.0.0
     */
    var limit: Int = 0,
    /**
     * Sets the number of documents to skip.
     *
     * @see com.mongodb.client.model.CountOptions.skip
     * @since 2.0.0
     */
    var skip: Int = 0,
    /**
     * Sets the maximum execution time on the server for this operation.
     *
     * @see com.mongodb.client.model.CountOptions.maxTime
     * @since 2.0.0
     */
    var maxTime: Duration = Duration.ZERO,
    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see com.mongodb.client.model.CountOptions.collation
     * @since 2.0.0
     */
    var collation: Collation? = null,
    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see com.mongodb.client.model.CountOptions.comment
     * @since 2.0.0
     */
    var comment: BsonElement? = null
)

//

/**
 * Create a new options instance from the given [block].
 */
fun EstimatedCountOptions(
    block: EstimatedCountOptions.() -> Unit
) = EstimatedCountOptions().apply(block)

/**
 * The options that can be applied to an estimated document count operation.
 *
 * @see com.mongodb.client.model.EstimatedDocumentCountOptions
 * @author LSafer
 * @since 2.0.0
 */
data class EstimatedCountOptions(
    /**
     * Sets the maximum execution time on the server for this operation.
     *
     * @see com.mongodb.client.model.EstimatedDocumentCountOptions.maxTime
     * @since 2.0.0
     */
    var maxTime: Duration = Duration.ZERO,
    /**
     * Sets the comment for this operation. A null value means no comment is set.
     *
     * @see com.mongodb.client.model.EstimatedDocumentCountOptions.comment
     * @since 2.0.0
     */
    var comment: BsonElement? = null
)

//

/**
 * Create a new options instance from the given [block].
 */
fun FindOneAndDeleteOptions(
    block: FindOneAndDeleteOptions.() -> Unit
) = FindOneAndDeleteOptions().apply(block)

/**
 * The options that can be applied to a find one and delete operation.
 *
 * @see com.mongodb.client.model.FindOneAndDeleteOptions
 * @author LSafer
 * @since 2.0.0
 */
data class FindOneAndDeleteOptions(
    /**
     * Sets a document describing the fields to
     * return for all matching documents.
     *
     * @see com.mongodb.client.model.FindOneAndDeleteOptions.projection
     * @since 2.0.0
     */
    var projection: BsonDocument? = null,
    /**
     * Sets the sort criteria to apply to the query.
     *
     * @see com.mongodb.client.model.FindOneAndDeleteOptions.sort
     * @since 2.0.0
     */
    var sort: BsonDocument? = null,
    /**
     * Sets the maximum execution time on the server for this operation.
     *
     * @see com.mongodb.client.model.FindOneAndDeleteOptions.maxTime
     * @since 2.0.0
     */
    var maxTime: Duration = Duration.ZERO,
    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see com.mongodb.client.model.FindOneAndDeleteOptions.collation
     * @since 2.0.0
     */
    var collation: Collation? = null,
    /**
     * Sets the hint for which index to use.
     * A null value means no hint is set.
     *
     * @see com.mongodb.client.model.FindOneAndDeleteOptions.hint
     * @since 2.0.0
     */
    var hint: BsonDocument? = null,
    /**
     * Sets the hint to apply.
     *
     * @see com.mongodb.client.model.FindOneAndDeleteOptions.hintString
     * @since 2.0.0
     */
    var hintString: String? = null,
    /**
     * Sets the comment for this operation. A null
     * value means no comment is set.
     *
     * @see com.mongodb.client.model.FindOneAndDeleteOptions.comment
     * @since 2.0.0
     */
    var comment: BsonElement? = null,
    /**
     * Add top-level variables for the operation
     *
     * Allows for improved command readability by
     * separating the variables from the query text.
     *
     * @see com.mongodb.client.model.FindOneAndDeleteOptions.let
     * @since 2.0.0
     */
    var let: BsonDocument? = null
)

//

/**
 * Create a new options instance from the given [block].
 */
fun FindOneAndReplaceOptions(
    block: FindOneAndReplaceOptions.() -> Unit
) = FindOneAndReplaceOptions().apply(block)

/**
 * The options that can be applied to a find one and replace operation.
 *
 * @see com.mongodb.client.model.FindOneAndReplaceOptions
 * @author LSafer
 * @since 2.0.0
 */
data class FindOneAndReplaceOptions(
    /**
     * Sets a document describing the fields to
     * return for all matching documents.
     *
     * @see com.mongodb.client.model.FindOneAndReplaceOptions.projection
     * @since 2.0.0
     */
    var projection: BsonDocument? = null,
    /**
     * Sets the sort criteria to apply to the query.
     *
     * @see com.mongodb.client.model.FindOneAndReplaceOptions.sort
     * @since 2.0.0
     */
    var sort: BsonDocument? = null,
    /**
     * Set to true if a new document should be
     * inserted if there are no matches to the
     * query filter.
     *
     * @see com.mongodb.client.model.FindOneAndReplaceOptions.upsert
     * @since 2.0.0
     */
    var upsert: Boolean = false,
    /**
     * Set whether to return the document before
     * it was replaced or after
     *
     * @see com.mongodb.client.model.FindOneAndReplaceOptions.returnDocument
     * @since 2.0.0
     */
    var returnDocument: ReturnDocument = ReturnDocument.Before,
    /**
     * Sets the maximum execution time on the server for this operation.
     *
     * @see com.mongodb.client.model.FindOneAndReplaceOptions.maxTime
     * @since 2.0.0
     */
    var maxTime: Duration = Duration.ZERO,
    /**
     * Sets the bypass document level validation flag.
     *
     * @see com.mongodb.client.model.FindOneAndReplaceOptions.bypassDocumentValidation
     * @since 2.0.0
     */
    var bypassDocumentValidation: Boolean? = null,
    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see com.mongodb.client.model.FindOneAndReplaceOptions.collation
     * @since 2.0.0
     */
    var collation: Collation? = null,
    /**
     * Sets the hint for which index to use.
     * A null value means no hint is set.
     *
     * @see com.mongodb.client.model.FindOneAndReplaceOptions.hint
     * @since 2.0.0
     */
    var hint: BsonDocument? = null,
    /**
     * Sets the hint to apply.
     *
     * @see com.mongodb.client.model.FindOneAndReplaceOptions.hintString
     * @since 2.0.0
     */
    var hintString: String? = null,
    /**
     * Sets the comment for this operation. A null
     * value means no comment is set.
     *
     * @see com.mongodb.client.model.FindOneAndReplaceOptions.comment
     * @since 2.0.0
     */
    var comment: BsonElement? = null,
    /**
     * Add top-level variables for the operation
     *
     * Allows for improved command readability by
     * separating the variables from the query text.
     *
     * @see com.mongodb.client.model.FindOneAndReplaceOptions.let
     * @since 2.0.0
     */
    var let: BsonDocument? = null
)

//

/**
 * Create a new options instance from the given [block].
 */
fun FindOneAndUpdateOptions(
    block: FindOneAndUpdateOptions.() -> Unit
) = FindOneAndUpdateOptions().apply(block)

/**
 * The options that can be applied to a find one and update operation.
 *
 * @see com.mongodb.client.model.FindOneAndUpdateOptions
 * @author LSafer
 * @since 2.0.0
 */
data class FindOneAndUpdateOptions(
    /**
     * Sets a document describing the fields to
     * return for all matching documents.
     *
     * @see com.mongodb.client.model.FindOneAndUpdateOptions.projection
     * @since 2.0.0
     */
    var projection: BsonDocument? = null,
    /**
     * Sets the sort criteria to apply to the query.
     *
     * @see com.mongodb.client.model.FindOneAndUpdateOptions.sort
     * @since 2.0.0
     */
    var sort: BsonDocument? = null,
    /**
     * Set to true if a new document should be
     * inserted if there are no matches to the
     * query filter.
     *
     * @see com.mongodb.client.model.FindOneAndUpdateOptions.upsert
     * @since 2.0.0
     */
    var upsert: Boolean = false,
    /**
     * Set whether to return the document before
     * it was replaced or after
     *
     * @see com.mongodb.client.model.FindOneAndUpdateOptions.returnDocument
     * @since 2.0.0
     */
    var returnDocument: ReturnDocument = ReturnDocument.Before,
    /**
     * Sets the maximum execution time on the server for this operation.
     *
     * @see com.mongodb.client.model.FindOneAndUpdateOptions.maxTime
     * @since 2.0.0
     */
    var maxTime: Duration = Duration.ZERO,
    /**
     * Sets the bypass document level validation flag.
     *
     * @see com.mongodb.client.model.FindOneAndUpdateOptions.bypassDocumentValidation
     * @since 2.0.0
     */
    var bypassDocumentValidation: Boolean? = null,
    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see com.mongodb.client.model.FindOneAndUpdateOptions.collation
     * @since 2.0.0
     */
    var collation: Collation? = null,
    /**
     * Sets the array filters option.
     *
     * @see com.mongodb.client.model.FindOneAndUpdateOptions.arrayFilters
     * @since 2.0.0
     */
    var arrayFilters: List<BsonDocument>? = null,
    /**
     * Sets the hint for which index to use.
     * A null value means no hint is set.
     *
     * @see com.mongodb.client.model.FindOneAndUpdateOptions.hint
     * @since 2.0.0
     */
    var hint: BsonDocument? = null,
    /**
     * Sets the hint to apply.
     *
     * @see com.mongodb.client.model.FindOneAndUpdateOptions.hintString
     * @since 2.0.0
     */
    var hintString: String? = null,
    /**
     * Sets the comment for this operation. A null
     * value means no comment is set.
     *
     * @see com.mongodb.client.model.FindOneAndUpdateOptions.comment
     * @since 2.0.0
     */
    var comment: BsonElement? = null,
    /**
     * Add top-level variables for the operation
     *
     * Allows for improved command readability by
     * separating the variables from the query text.
     *
     * @see com.mongodb.client.model.FindOneAndUpdateOptions.let
     * @since 2.0.0
     */
    var let: BsonDocument? = null
)

/* ============= ------------------ ============= */

/**
 * Create a new options instance from the given [block].
 */
fun FindOptions(
    block: FindOptions.() -> Unit
) = FindOptions().apply(block)

/**
 * The options that can be applied to a find operation.
 *
 * @see com.mongodb.reactivestreams.client.FindPublisher
 * @author LSafer
 * @since 2.0.0
 */
data class FindOptions(
    /**
     * Sets the limit to apply.
     *
     * @see com.mongodb.reactivestreams.client.FindPublisher.limit
     * @since 2.0.0
     */
    var limit: Int = 0,
    /**
     * Sets the number of documents to skip.
     *
     * @see com.mongodb.reactivestreams.client.FindPublisher.skip
     * @since 2.0.0
     */
    var skip: Int = 0,
    /**
     * Sets the maximum execution time on the server for this operation.
     *
     * @see com.mongodb.reactivestreams.client.FindPublisher.maxTime
     * @since 2.0.0
     */
    var maxTime: Duration = Duration.ZERO,
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
     * @see com.mongodb.reactivestreams.client.FindPublisher.maxAwaitTime
     * @since 2.0.0
     */
    var maxAwaitTime: Duration = Duration.ZERO,
    /**
     * Sets a document describing the fields to return for all matching documents.
     *
     * @see com.mongodb.reactivestreams.client.FindPublisher.projection
     * @since 2.0.0
     */
    var projection: BsonDocument? = null,
    /**
     * Sets the sort criteria to apply to the query.
     *
     * @see com.mongodb.reactivestreams.client.FindPublisher.sort
     * @since 2.0.0
     */
    var sort: BsonDocument? = null,
    /**
     * The server normally times out idle cursors after an inactivity period (10 minutes)
     * to prevent excess memory use. Set this option to prevent that.
     *
     * @see com.mongodb.reactivestreams.client.FindPublisher.noCursorTimeout
     * @since 2.0.0
     */
    var noCursorTimeout: Boolean = false,
    /**
     * Get partial results from a sharded cluster if one or more shards are unreachable (instead of throwing an error).
     *
     * @see com.mongodb.reactivestreams.client.FindPublisher.partial
     * @since 2.0.0
     */
    var partial: Boolean = false,
    /**
     * Sets the cursor type.
     *
     * @see com.mongodb.reactivestreams.client.FindPublisher.cursorType
     * @since 2.0.0
     */
    var cursorType: CursorType = CursorType.NonTailable,
    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see com.mongodb.reactivestreams.client.FindPublisher.collation
     * @since 2.0.0
     */
    var collation: Collation? = null,
    /**
     * Sets the comment to the query.
     * A null value means no comment is set.
     *
     * @see com.mongodb.reactivestreams.client.FindPublisher.comment
     * @since 2.0.0
     */
    var comment: BsonElement? = null,
    /**
     * Sets the hint for which index to use.
     * A null value means no hint is set.
     *
     * @see com.mongodb.reactivestreams.client.FindPublisher.hint
     * @since 2.0.0
     */
    var hint: BsonDocument? = null,
    /**
     * Sets the hint for which index to use.
     * A null value means no hint is set.
     *
     * @see com.mongodb.reactivestreams.client.FindPublisher.hintString
     * @since 2.0.0
     */
    var hintString: String? = null,
    /**
     * Add top-level variables to the operation.
     * A null value means no variables are set.
     *
     * Allows for improved command readability by
     * separating the variables from the query text.
     *
     * @see com.mongodb.reactivestreams.client.FindPublisher.let
     * @since 2.0.0
     */
    var let: BsonDocument? = null,
    /**
     * Sets the exclusive upper bound for a specific index.
     * A null value means no max is set.
     *
     * @see com.mongodb.reactivestreams.client.FindPublisher.max
     * @since 2.0.0
     */
    var max: BsonDocument? = null,
    /**
     * Sets the minimum inclusive lower bound for a specific index.
     * A null value means no max is set.
     *
     * @see com.mongodb.reactivestreams.client.FindPublisher.min
     * @since 2.0.0
     */
    var min: BsonDocument? = null,
    /**
     * Sets the returnKey.
     * If true the find operation will return only
     * the index keys in the resulting documents.
     *
     * @see com.mongodb.reactivestreams.client.FindPublisher.returnKey
     * @since 2.0.0
     */
    var returnKey: Boolean = false,
    /**
     * Sets the showRecordId.
     * Set to true to add a field `$recordId` to the returned documents.
     *
     * @see com.mongodb.reactivestreams.client.FindPublisher.showRecordId
     * @since 2.0.0
     */
    var showRecordId: Boolean = false,
    /**
     * Sets the number of documents to return per batch.
     *
     * Overrides the value for setting the batch
     * size, allowing for fine-grained control
     * over the underlying cursor.
     *
     * @see com.mongodb.reactivestreams.client.FindPublisher.batchSize
     * @since 2.0.0
     */
    var batchSize: Int? = null,
    /**
     * Enables writing to temporary files on the server. When set to true, the server
     * can write temporary data to disk while executing the find operation.
     *
     * This option is sent only if the caller explicitly sets it to true.
     *
     * @see com.mongodb.reactivestreams.client.FindPublisher.allowDiskUse
     * @since 2.0.0
     */
    var allowDiskUse: Boolean? = null
)

//

/**
 * Create a new options instance from the given [block].
 */
fun AggregateOptions(
    block: AggregateOptions.() -> Unit
) = AggregateOptions().apply(block)

/**
 * The options that can be applied to an aggregation operation.
 *
 * @see com.mongodb.reactivestreams.client.AggregatePublisher
 * @author LSafer
 * @since 2.0.0
 */
data class AggregateOptions(
    /**
     * Enables writing to temporary files.
     * A null value indicates that it's unspecified.
     *
     * @see com.mongodb.reactivestreams.client.AggregatePublisher.allowDiskUse
     * @since 2.0.0
     */
    var allowDiskUse: Boolean? = null,
    /**
     * Sets the maximum execution time on the
     * server for this operation.
     *
     * @see com.mongodb.reactivestreams.client.AggregatePublisher.maxTime
     * @since 2.0.0
     */
    var maxTime: Duration = Duration.ZERO,
    /**
     * The maximum amount of time for the server
     * to wait on new documents to satisfy a
     * `$changeStream` aggregation.
     *
     * A zero value will be ignored.
     *
     * @see com.mongodb.reactivestreams.client.AggregatePublisher.maxAwaitTime
     * @since 2.0.0
     */
    var maxAwaitTime: Duration = Duration.ZERO,
    /**
     * Sets the bypass document level validation flag.
     *
     * @see com.mongodb.reactivestreams.client.AggregatePublisher.bypassDocumentValidation
     * @since 2.0.0
     */
    var bypassDocumentValidation: Boolean? = null,
    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see com.mongodb.reactivestreams.client.AggregatePublisher.collation
     * @since 2.0.0
     */
    var collation: Collation? = null,
    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see com.mongodb.reactivestreams.client.AggregatePublisher.comment
     * @since 2.0.0
     */
    var comment: BsonElement? = null,
    /**
     * Sets the hint for which index to use.
     * A null value means no hint is set.
     *
     * @see com.mongodb.reactivestreams.client.AggregatePublisher.hint
     * @since 2.0.0
     */
    var hint: BsonDocument? = null,
    /**
     * Sets the hint for which index to use.
     * A null value means no hint is set.
     *
     * @see com.mongodb.reactivestreams.client.AggregatePublisher.hintString
     * @since 2.0.0
     */
    var hintString: String? = null,
    /**
     * Add top-level variables to the aggregation.
     *
     * @see com.mongodb.reactivestreams.client.AggregatePublisher.let
     * @since 2.0.0
     */
    var let: BsonDocument? = null,
    /**
     * Sets the number of documents to return per batch.
     *
     * @see com.mongodb.reactivestreams.client.AggregatePublisher.batchSize
     * @since 2.0.0
     */
    var batchSize: Int? = null
)

//

/**
 * Create a new options instance from the given [block].
 */
fun DistinctOptions(
    block: DistinctOptions.() -> Unit
) = DistinctOptions().apply(block)

/**
 * The options that can be applied to a distinct operation.
 *
 * @see com.mongodb.reactivestreams.client.DistinctPublisher
 * @author LSafer
 * @since 2.0.0
 */
data class DistinctOptions(
    /**
     * Sets the maximum execution time on the server for this operation.
     *
     * @see com.mongodb.reactivestreams.client.DistinctPublisher.maxTime
     * @since 2.0.0
     */
    var maxTime: Duration = Duration.ZERO,
    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see com.mongodb.reactivestreams.client.DistinctPublisher.collation
     * @since 2.0.0
     */
    var collation: Collation? = null,
    /**
     * Sets the number of documents to return per batch.
     *
     * @see com.mongodb.reactivestreams.client.DistinctPublisher.batchSize
     * @since 2.0.0
     */
    var batchSize: Int? = null,
    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see com.mongodb.reactivestreams.client.DistinctPublisher.comment
     * @since 2.0.0
     */
    var comment: BsonElement? = null,
)

//

/**
 * Create a new options instance from the given [block].
 */
fun WatchOptions(
    block: WatchOptions.() -> Unit
) = WatchOptions().apply(block)

/**
 * The options that can be applied to a watch operation.
 *
 * @see com.mongodb.reactivestreams.client.ChangeStreamPublisher
 * @author LSafer
 * @since 2.0.0
 */
data class WatchOptions(
    /**
     * Sets the fullDocument value.
     *
     * @see com.mongodb.reactivestreams.client.ChangeStreamPublisher.fullDocument
     * @since 2.0.0
     */
    var fullDocument: FullDocument = FullDocument.Default,
    /**
     * Sets the logical starting point for the new
     * change stream.
     *
     * @see com.mongodb.reactivestreams.client.ChangeStreamPublisher.resumeAfter
     * @since 2.0.0
     */
    var resumeAfter: BsonDocument? = null,
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
     * @see com.mongodb.reactivestreams.client.ChangeStreamPublisher.startAtOperationTime
     * @since 2.0.0
     */
    var startAtOperationTime: Long? = null,
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
     * @see com.mongodb.reactivestreams.client.ChangeStreamPublisher.startAfter
     * @since 2.0.0
     */
    var startAfter: BsonDocument? = null,
    /**
     * Sets the maximum await execution time on
     * the server for this operation.
     *
     * @see com.mongodb.reactivestreams.client.ChangeStreamPublisher.maxAwaitTime
     * @since 2.0.0
     */
    var maxAwaitTime: Duration = Duration.ZERO,
    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see com.mongodb.reactivestreams.client.ChangeStreamPublisher.collation
     * @since 2.0.0
     */
    var collation: Collation? = null,
    /**
     * Sets the number of documents to return per batch.
     *
     * @see com.mongodb.reactivestreams.client.ChangeStreamPublisher.batchSize
     * @since 2.0.0
     */
    var batchSize: Int? = null,
    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see com.mongodb.reactivestreams.client.ChangeStreamPublisher.comment
     * @since 2.0.0
     */
    var comment: BsonElement? = null,
    /**
     * Sets whether to include expanded change stream events, which are:
     * createIndexes, dropIndexes, modify, create, shardCollection,
     * reshardCollection, refineCollectionShardKey.
     *
     * @see com.mongodb.reactivestreams.client.ChangeStreamPublisher.showExpandedEvents
     * @since 2.0.0
     */
    var showExpandedEvents: Boolean = false
)

//

/**
 * Create a new options instance from the given [block].
 */
fun ListDatabasesOptions(
    block: ListDatabasesOptions.() -> Unit
) = ListDatabasesOptions().apply(block)

/**
 * The options that can be applied to a list databases operation.
 *
 * @see com.mongodb.reactivestreams.client.ListDatabasesPublisher
 * @author LSafer
 * @since 2.0.0
 */
data class ListDatabasesOptions(
    /**
     * Sets the maximum execution time on the server for this operation.
     *
     * @see com.mongodb.reactivestreams.client.ListDatabasesPublisher.maxTime
     * @since 2.0.0
     */
    var maxTime: Duration = Duration.ZERO,
    /**
     * Sets the nameOnly flag that indicates
     * whether the command should return just the
     * database names or return the database names
     * and size information.
     *
     * @see com.mongodb.reactivestreams.client.ListDatabasesPublisher.nameOnly
     * @since 2.0.0
     */
    var nameOnly: Boolean? = null,
    /**
     * Sets the authorizedDatabasesOnly flag that
     * indicates whether the command should return
     * just the databases which the user is
     * authorized to see.
     *
     * @see com.mongodb.reactivestreams.client.ListDatabasesPublisher.authorizedDatabasesOnly
     * @since 2.0.0
     */
    var authorizedDatabasesOnly: Boolean? = null,
    /**
     * Sets the number of documents to return per batch.
     *
     * @see com.mongodb.reactivestreams.client.ListDatabasesPublisher.batchSize
     * @since 2.0.0
     */
    var batchSize: Int? = null,
    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see com.mongodb.reactivestreams.client.ListDatabasesPublisher.comment
     * @since 2.0.0
     */
    var comment: BsonElement? = null
)

//

/**
 * Create a new options instance from the given [block].
 */
fun ListCollectionsOptions(
    block: ListCollectionsOptions.() -> Unit
) = ListCollectionsOptions().apply(block)

/**
 * The options that can be applied to a list collections operation.
 *
 * @see com.mongodb.reactivestreams.client.ListCollectionsPublisher
 * @author LSafer
 * @since 2.0.0
 */
data class ListCollectionsOptions(
    /**
     * Sets the maximum execution time on the server for this operation.
     *
     * @see com.mongodb.reactivestreams.client.ListCollectionsPublisher.maxTime
     * @since 2.0.0
     */
    var maxTime: Duration = Duration.ZERO,
    /**
     * Sets the number of documents to return per batch.
     *
     * @see com.mongodb.reactivestreams.client.ListCollectionsPublisher.batchSize
     * @since 2.0.0
     */
    var batchSize: Int? = null,
    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see com.mongodb.reactivestreams.client.ListCollectionsPublisher.comment
     * @since 2.0.0
     */
    var comment: BsonElement? = null
)

//

/**
 * Create a new options instance from the given [block].
 */
fun ListIndexesOptions(
    block: ListIndexesOptions.() -> Unit
) = ListIndexesOptions().apply(block)

/**
 * The options that can be applied to a list indexes operation.
 *
 * @see com.mongodb.reactivestreams.client.ListIndexesPublisher
 * @author LSafer
 * @since 2.0.0
 */
data class ListIndexesOptions(
    /**
     * Sets the maximum execution time on the
     * server for this operation.
     *
     * @see com.mongodb.reactivestreams.client.ListIndexesPublisher.maxTime
     * @since 2.0.0
     */
    var maxTime: Duration = Duration.ZERO,
    /**
     * Sets the number of documents to return per batch.
     *
     * @see com.mongodb.reactivestreams.client.ListIndexesPublisher.batchSize
     * @since 2.0.0
     */
    var batchSize: Int? = null,
    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see com.mongodb.reactivestreams.client.ListIndexesPublisher.comment
     * @since 2.0.0
     */
    var comment: BsonElement? = null
)

/* ============= ------------------ ============= */

/**
 * Create a new options instance from the given [block].
 */
fun CreateIndexOptions(
    block: CreateIndexOptions.() -> Unit
) = CreateIndexOptions().apply(block)

/**
 * The options that can be applied to a create index operation.
 *
 * @see com.mongodb.client.model.IndexOptions
 * @author LSafer
 * @since 2.0.0
 */
data class CreateIndexOptions(
    /**
     * Should the index should be created in the
     * background
     *
     * @see com.mongodb.client.model.IndexOptions.background
     */
    var background: Boolean = false,
    /**
     * Should the index should be unique.
     *
     * @see com.mongodb.client.model.IndexOptions.unique
     */
    var unique: Boolean = false,
    /**
     * Sets the name of the index.
     *
     * @see com.mongodb.client.model.IndexOptions.name
     */
    var name: String? = null,
    /**
     * Should the index only references documents
     * with the specified field
     *
     * @see com.mongodb.client.model.IndexOptions.sparse
     */
    var sparse: Boolean = false,
    /**
     * Sets the time to live for documents in the collection
     *
     * @see com.mongodb.client.model.IndexOptions.expireAfter
     */
    var expireAfter: Duration? = null,
    /**
     * Sets the index version number.
     *
     * @see com.mongodb.client.model.IndexOptions.version
     */
    var version: Int? = null,
    /**
     * Sets the weighting object for use with a text index.
     *
     * @see com.mongodb.client.model.IndexOptions.weights
     */
    var weights: BsonDocument? = null,
    /**
     * Sets the language for the text index.
     *
     * @see com.mongodb.client.model.IndexOptions.defaultLanguage
     */
    var defaultLanguage: String? = null,
    /**
     * Sets the name of the field that contains the language string.
     *
     * @see com.mongodb.client.model.IndexOptions.languageOverride
     */
    var languageOverride: String? = null,
    /**
     * Set the text index version number.
     *
     * @see com.mongodb.client.model.IndexOptions.textVersion
     */
    var textVersion: Int? = null,
    /**
     * Sets the 2dsphere index version number.
     *
     * @see com.mongodb.client.model.IndexOptions.sphereVersion
     */
    var sphereVersion: Int? = null,
    /**
     * Sets the number of precision of the stored
     * geohash value of the location data in 2d
     * indexes.
     *
     * @see com.mongodb.client.model.IndexOptions.bits
     */
    var bits: Int? = null,
    /**
     * Sets the lower inclusive boundary for the
     * longitude and latitude values for 2d indexes.
     *
     * @see com.mongodb.client.model.IndexOptions.min
     */
    var min: Double? = null,
    /**
     * Sets the upper inclusive boundary for the
     * longitude and latitude values for 2d indexes.
     *
     * @see com.mongodb.client.model.IndexOptions.max
     */
    var max: Double? = null,
    /**
     * Sets the storage engine options document for this index.
     *
     * @see com.mongodb.client.model.IndexOptions.storageEngine
     */
    var storageEngine: BsonDocument? = null,
    /**
     * Sets the filter expression for the documents
     * to be included in the index
     *
     * @see com.mongodb.client.model.IndexOptions.partialFilterExpression
     */
    var partialFilterExpression: BsonDocument? = null,
    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see com.mongodb.client.model.IndexOptions.collation
     */
    var collation: Collation? = null,
    /**
     * Sets the wildcard projection of a wildcard index
     *
     * @see com.mongodb.client.model.IndexOptions.wildcardProjection
     */
    var wildcardProjection: BsonDocument? = null,
    /**
     * Should the index not be used by the query
     * planner when executing operations.
     *
     * @see com.mongodb.client.model.IndexOptions.hidden
     */
    var hidden: Boolean = false
)

//

/**
 * Create a new options instance from the given [block].
 */
fun CreateIndexesOptions(
    block: CreateIndexesOptions.() -> Unit
) = CreateIndexesOptions().apply(block)

/**
 * The options that can be applied to a create indexes operation.
 *
 * @see com.mongodb.client.model.CreateIndexOptions
 * @author LSafer
 * @since 2.0.0
 */
data class CreateIndexesOptions(
    /**
     * Sets the maximum execution time on the server for this operation.
     *
     * @see com.mongodb.client.model.CreateIndexOptions.maxTime
     * @since 2.0.0
     */
    var maxTime: Duration = Duration.ZERO,
    /**
     * Sets the create index commit quorum for this operation.
     *
     * @see com.mongodb.client.model.CreateIndexOptions.commitQuorum
     */
    var commitQuorum: CommitQuorum? = null
)

//

/**
 * Create a new options instance from the given [block].
 */
fun DropIndexOptions(
    block: DropIndexOptions.() -> Unit
) = DropIndexOptions().apply(block)

/**
 * The options that can be applied to a drop index operation.
 *
 * @see com.mongodb.client.model.DropIndexOptions
 * @author LSafer
 * @since 2.0.0
 */
data class DropIndexOptions(
    /**
     * Sets the maximum execution time on the
     * server for this operation.
     *
     * @see com.mongodb.client.model.DropIndexOptions.maxTime
     * @since 2.0.0
     */
    var maxTime: Duration = Duration.ZERO
)

//

/**
 * Create a new options instance from the given [block].
 */
fun CreateCollectionOptions(
    block: CreateCollectionOptions.() -> Unit
) = CreateCollectionOptions().apply(block)

/**
 * The options that can be applied to a create collection operation.
 *
 * @see com.mongodb.client.model.CreateCollectionOptions
 * @author LSafer
 * @since 2.0.0
 */
data class CreateCollectionOptions(
    /**
     * Sets the maximum number of documents allowed in a capped collection.
     *
     * @see com.mongodb.client.model.CreateCollectionOptions.maxDocuments
     * @since 2.0.0
     */
    var maxDocuments: Long = 0,
    /**
     * sets whether the collection is capped.
     *
     * @see com.mongodb.client.model.CreateCollectionOptions.capped
     * @since 2.0.0
     */
    var capped: Boolean = false,
    /**
     * Gets the maximum size of in bytes of a capped collection.
     *
     * @see com.mongodb.client.model.CreateCollectionOptions.sizeInBytes
     * @since 2.0.0
     */
    var sizeInBytes: Long = 0,
    /**
     * Sets the storage engine options document defaults for the collection
     *
     * @see com.mongodb.client.model.CreateCollectionOptions.storageEngineOptions
     * @since 2.0.0
     */
    var storageEngineOptions: BsonDocument? = null,
    /**
     * Sets the index option defaults for the collection.
     *
     * @see com.mongodb.client.model.CreateCollectionOptions.indexOptionDefaults
     * @since 2.0.0
     */
    var indexOptionDefaults: IndexOptionDefaults? = null,
    /**
     * Sets the validation options for documents
     * being inserted or updated in a collection
     *
     * @see com.mongodb.client.model.CreateCollectionOptions.validationOptions
     * @since 2.0.0
     */
    var validationOptions: ValidationOptions = ValidationOptions(),
    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see com.mongodb.client.model.CreateCollectionOptions.collation
     * @since 2.0.0
     */
    var collation: Collation? = null,
    /**
     * Sets the expire-after option.
     *
     * @see com.mongodb.client.model.CreateCollectionOptions.expireAfter
     * @since 2.0.0
     */
    var expireAfter: Duration = Duration.ZERO,
    /**
     * Sets the time-series collection options.
     *
     * @see com.mongodb.client.model.CreateCollectionOptions.timeSeriesOptions
     * @since 2.0.0
     */
    var timeSeriesOptions: TimeSeriesOptions? = null,
    /**
     * Sets the clustered index collection options.
     *
     * @see com.mongodb.client.model.CreateCollectionOptions.clusteredIndexOptions
     * @since 2.0.0
     */
    var clusteredIndexOptions: ClusteredIndexOptions? = null,
    /**
     * Sets the change stream pre- and post-
     * images options.
     *
     * @see com.mongodb.client.model.CreateCollectionOptions.changeStreamPreAndPostImagesOptions
     * @since 2.0.0
     */
    var changeStreamPreAndPostImagesOptions: ChangeStreamPreAndPostImagesOptions? = null,
    /**
     * Sets the encrypted fields
     *
     * @see com.mongodb.client.model.CreateCollectionOptions.encryptedFields
     * @since 2.0.0
     */
    var encryptedFields: BsonDocument? = null
)

//

/**
 * Create a new options instance from the given [block].
 */
fun RenameCollectionOptions(
    block: RenameCollectionOptions.() -> Unit
) = RenameCollectionOptions().apply(block)

/**
 * The options that can be applied to a rename collection operation.
 *
 * @see com.mongodb.client.model.RenameCollectionOptions
 * @author LSafer
 * @since 2.0.0
 */
data class RenameCollectionOptions(
    /**
     * Sets if mongod should drop the target of
     * renameCollection prior to renaming the
     * collection.
     *
     * @see com.mongodb.client.model.RenameCollectionOptions.dropTarget
     * @since 2.0.0
     */
    var dropTarget: Boolean = true
)

//

/**
 * Create a new options instance from the given [block].
 */
fun CreateViewOptions(
    block: CreateViewOptions.() -> Unit
) = CreateViewOptions().apply(block)

/**
 * The options that can be applied to a create view operation.
 *
 * @see com.mongodb.client.model.CreateViewOptions
 * @author LSafer
 * @since 2.0.0
 */
data class CreateViewOptions(
    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see com.mongodb.client.model.CreateViewOptions
     * @since 2.0.0
     */
    var collation: Collation? = null
)

//

/**
 * Create a new options instance from the given [block].
 */
fun ClientSessionOptions(
    block: ClientSessionOptions.() -> Unit
) = ClientSessionOptions().apply(block)

/**
 * The options to apply to a [ClientSession].
 *
 * @see com.mongodb.ClientSessionOptions
 * @since 2.0.0
 */
data class ClientSessionOptions(
    /**
     * Sets whether operations using the session
     * should causally consistent with each other.
     *
     * @see com.mongodb.ClientSessionOptions.isCausallyConsistent
     * @since 2.0.0
     */
    var causallyConsistent: Boolean? = null,
    /**
     * Sets whether read operations using the
     * session should share the same snapshot.
     *
     * The default value is unset, in which case
     * the driver will use the global default
     * value, which is currently false.
     *
     * @see com.mongodb.ClientSessionOptions.isSnapshot
     * @since 2.0.0
     */
    var snapshot: Boolean? = null,
    /**
     * Sets whether operations using the session
     * should causally consistent with each other.
     *
     * @see com.mongodb.ClientSessionOptions.getDefaultTransactionOptions
     * @since 2.0.0
     */
    var defaultTransactionOptions: TransactionOptions = TransactionOptions()
)

/* ============= ------------------ ============= */
