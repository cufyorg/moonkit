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

import com.mongodb.ClientSessionOptions
import com.mongodb.CreateIndexCommitQuorum
import com.mongodb.TransactionOptions
import com.mongodb.client.model.*
import org.cufy.bson.*
import java.util.concurrent.TimeUnit

/* ===== - EstimatedDocumentCountOptions  - ===== */

/**
 * A wrapper for [EstimatedDocumentCountOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class EstimatedDocumentCountOptionsScope(
    /**
     * The current options instance.
     */
    var options: EstimatedDocumentCountOptions
) {
    /**
     * Gets the maximum execution time on the server for this operation.  The default is 0, which places no limit on the execution time.
     *
     * @param unit the time unit to return the result in
     * @return the maximum execution time in the given time unit
     * @see EstimatedDocumentCountOptions.getMaxTime
     */
    fun getMaxTime(unit: TimeUnit): Long {
        return options.getMaxTime(unit)
    }

    /**
     * Sets the maximum execution time on the server for this operation.
     *
     * @param value  the max time
     * @param unit the time unit, which may not be null
     * @see EstimatedDocumentCountOptions.maxTime
     */
    fun maxTime(value: Long, unit: TimeUnit) {
        options = options.maxTime(value, unit)
    }
}

/**
 * Configure [this] options instance using the
 * given [block] and return the configured
 * instance.
 */
inline fun EstimatedDocumentCountOptions.configure(
    block: EstimatedDocumentCountOptionsScope.() -> Unit
): EstimatedDocumentCountOptions {
    return EstimatedDocumentCountOptionsScope(this)
        .apply(block)
        .options
}

/* ============= -- CountOptions -- ============= */

/**
 * A wrapper for [CountOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class CountOptionsScope(
    /**
     * The current options instance.
     */
    var options: CountOptions
) {
    /**
     * Sets the hint to apply.
     *
     * @see CountOptions.hint
     */
    var hint: Bson?
        get() = options.hint
        set(value) {
            options = options.hint(value)
        }

    /**
     * Sets the hint to apply.
     *
     * @see CountOptions.hintString
     */
    var hintString: String?
        get() = options.hintString
        set(value) {
            options = options.hintString(value)
        }

    /**
     * Sets the limit to apply.
     *
     * @see CountOptions.limit
     */
    var limit: Int
        get() = options.limit
        set(value) {
            options = options.limit(value)
        }

    /**
     * Sets the number of documents to skip.
     *
     * @see CountOptions.skip
     */
    var skip: Int
        get() = options.skip
        set(value) {
            options = options.skip(value)
        }

    /**
     * Gets the maximum execution time on the server for this operation.  The default is 0, which places no limit on the execution time.
     *
     * @param unit the time unit to return the result in
     * @return the maximum execution time in the given time unit
     * @see CountOptions.getMaxTime
     */
    fun getMaxTime(unit: TimeUnit): Long {
        return options.getMaxTime(unit)
    }

    /**
     * Sets the maximum execution time on the server for this operation.
     *
     * @param value  the max time
     * @param unit the time unit, which may not be null
     * @see CountOptions.maxTime
     */
    fun maxTime(value: Long, unit: TimeUnit) {
        options = options.maxTime(value, unit)
    }

    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see CountOptions.collation
     */
    var collation: Collation?
        get() = options.collation
        set(value) {
            options = options.collation(value)
        }

    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see CountOptions.comment
     */
    var comment: BsonValue?
        get() = options.comment
        set(value) {
            options = options.comment(value)
        }

    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see CountOptions.comment
     */
    fun comment(value: String?) {
        comment = value?.let { BsonString(it) }
    }
}

/**
 * Configure [this] options instance using the
 * given [block] and return the configured
 * instance.
 */
inline fun CountOptions.configure(
    block: CountOptionsScope.() -> Unit
): CountOptions {
    return CountOptionsScope(this)
        .apply(block)
        .options
}

/* ============ - BulkWriteOptions - ============ */

/**
 * A wrapper for [BulkWriteOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class BulkWriteOptionsScope(
    /**
     * The current options instance.
     */
    var options: BulkWriteOptions
) {
    /**
     * If true, then when a write fails, return without performing the remaining
     * writes. If false, then when a write fails, continue with the remaining writes, if any.
     * Defaults to true.
     *
     * @see BulkWriteOptions.ordered
     */
    var ordered: Boolean
        get() = options.isOrdered
        set(value) {
            options = options.ordered(value)
        }

    /**
     * If true, allows the write to opt-out of document level validation.
     *
     * @see BulkWriteOptions.bypassDocumentValidation
     */
    var bypassDocumentValidation: Boolean?
        get() = options.bypassDocumentValidation
        set(value) {
            options = options.bypassDocumentValidation(value)
        }

    /**
     * The comment to send with the query. The
     * default is not to include a comment with
     * the query.
     *
     * @see BulkWriteOptions.comment
     */
    var comment: BsonValue?
        get() = options.comment
        set(value) {
            options = options.comment(value)
        }

    /**
     * Sets the comment for this operation. A null
     * value means no comment is set.
     *
     * @see BulkWriteOptions.comment
     */
    fun comment(value: String?) {
        this.comment = value?.let { BsonString(it) }
    }

    /**
     * Add top-level variables for the operation
     *
     * Allows for improved command readability by
     * separating the variables from the query text.
     * The value of let will be passed to all update
     * and delete, but not insert, commands.
     *
     * @see BulkWriteOptions.let
     */
    var let: Bson?
        get() = options.let
        set(value) {
            options = options.let(value)
        }
}

/**
 * Configure [this] options instance using the
 * given [block] and return the configured
 * instance.
 */
inline fun BulkWriteOptions.configure(
    block: BulkWriteOptionsScope.() -> Unit
): BulkWriteOptions {
    return BulkWriteOptionsScope(this)
        .apply(block)
        .options
}

/* ============ - InsertOneOptions - ============ */

/**
 * A wrapper for [InsertOneOptions]
 *
 * @author LSafer
 * @since 2.0.0
 */
open class InsertOneOptionsScope(
    /**
     * The current options instance.
     */
    var options: InsertOneOptions
) {
    /**
     * Sets the bypass document level validation flag.
     *
     * @see InsertOneOptions.bypassDocumentValidation
     */
    var bypassDocumentValidation: Boolean?
        get() = options.bypassDocumentValidation
        set(value) {
            options = options.bypassDocumentValidation(value)
        }

    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see InsertOneOptions.comment
     */
    var comment: BsonValue?
        get() = options.comment
        set(value) {
            options = options.comment(value)
        }

    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see InsertOneOptions.comment
     */
    fun comment(value: String?) {
        comment = value?.let { BsonString(it) }
    }
}

/**
 * Configure [this] options instance using the
 * given [block] and return the configured
 * instance.
 */
inline fun InsertOneOptions.configure(
    block: InsertOneOptionsScope.() -> Unit
): InsertOneOptions {
    return InsertOneOptionsScope(this)
        .apply(block)
        .options
}

/* =========== - InsertManyOptions  - =========== */

/**
 * A wrapper for [InsertManyOptions]
 *
 * @author LSafer
 * @since 2.0.0
 */
open class InsertManyOptionsScope(
    /**
     * The current options instance.
     */
    var options: InsertManyOptions
) {
    /**
     * Sets whether the server should insert the
     * documents in the order provided.
     *
     * @see InsertManyOptions.ordered
     */
    var ordered: Boolean
        get() = options.isOrdered
        set(value) {
            options = options.ordered(value)
        }

    /**
     * Sets the bypass document level validation flag.
     *
     * @see InsertManyOptions.bypassDocumentValidation
     */
    var bypassDocumentValidation: Boolean?
        get() = options.bypassDocumentValidation
        set(value) {
            options = options.bypassDocumentValidation(value)
        }

    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see InsertManyOptions.comment
     */
    var comment: BsonValue?
        get() = options.comment
        set(value) {
            options = options.comment(value)
        }

    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see InsertManyOptions.comment
     */
    fun comment(value: String?) {
        comment = value?.let { BsonString(it) }
    }
}

/**
 * Configure [this] options instance using the
 * given [block] and return the configured
 * instance.
 */
inline fun InsertManyOptions.configure(
    block: InsertManyOptionsScope.() -> Unit
): InsertManyOptions {
    return InsertManyOptionsScope(this)
        .apply(block)
        .options
}

/* ============= - DeleteOptions  - ============= */

/**
 * A wrapper for [DeleteOptions]
 *
 * @author LSafer
 * @since 2.0.0
 */
open class DeleteOptionsScope(
    /**
     * The current options instance.
     */
    var options: DeleteOptions
) {
    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see DeleteOptions.collation
     */
    var collation: Collation?
        get() = options.collation
        set(value) {
            options = options.collation(value)
        }

    /**
     * Sets the hint to apply.
     *
     * @see DeleteOptions.hint
     */
    var hint: Bson?
        get() = options.hint
        set(value) {
            options = options.hint(value)
        }

    /**
     * Sets the hint to apply.
     *
     * @see DeleteOptions.hintString
     */
    var hintString: String?
        get() = options.hintString
        set(value) {
            options = options.hintString(value)
        }

    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see DeleteOptions.comment
     */
    var comment: BsonValue?
        get() = options.comment
        set(value) {
            options = options.comment(value)
        }

    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see DeleteOptions.comment
     */
    fun comment(value: String?) {
        comment = value?.let { BsonString(it) }
    }

    /**
     * Add top-level variables for the operation
     *
     * Allows for improved command readability by
     * separating the variables from the query text.
     *
     * @see DeleteOptions.let
     */
    var let: Bson?
        get() = options.let
        set(value) {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            options = options.let(value)
        }
}

/**
 * Configure [this] options instance using the
 * given [block] and return the configured
 * instance.
 */
inline fun DeleteOptions.configure(
    block: DeleteOptionsScope.() -> Unit
): DeleteOptions {
    return DeleteOptionsScope(this)
        .apply(block)
        .options
}

/* ============= - ReplaceOptions - ============= */

/**
 * A wrapper for [ReplaceOptions]
 *
 * @author LSafer
 * @since 2.0.0
 */
open class ReplaceOptionsScope(
    /**
     * The current options instance.
     */
    var options: ReplaceOptions
) {
    /**
     * Set to true if a new document should be
     * inserted if there are no matches to the
     * query filter.
     *
     * @see ReplaceOptions.upsert
     */
    var upsert: Boolean
        get() = options.isUpsert
        set(value) {
            options = options.upsert(value)
        }

    /**
     * Sets the bypass document level validation flag.
     *
     * @see ReplaceOptions.bypassDocumentValidation
     */
    var bypassDocumentValidation: Boolean?
        get() = options.bypassDocumentValidation
        set(value) {
            options = options.bypassDocumentValidation(value)
        }

    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see ReplaceOptions.collation
     */
    var collation: Collation?
        get() = options.collation
        set(value) {
            options = options.collation(value)
        }

    /**
     * Sets the hint to apply.
     *
     * @see ReplaceOptions.hint
     */
    var hint: Bson?
        get() = options.hint
        set(value) {
            options = options.hint(value)
        }

    /**
     * Sets the hint to apply.
     *
     * @see ReplaceOptions.hintString
     */
    var hintString: String?
        get() = options.hintString
        set(value) {
            options = options.hintString(value)
        }

    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see ReplaceOptions.comment
     */
    var comment: BsonValue?
        get() = options.comment
        set(value) {
            options = options.comment(value)
        }

    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see ReplaceOptions.comment
     */
    fun comment(value: String?) {
        comment = value?.let { BsonString(it) }
    }

    /**
     * Add top-level variables for the operation
     *
     * Allows for improved command readability by
     * separating the variables from the query text.
     *
     * @see ReplaceOptions.let
     */
    var let: Bson?
        get() = options.let
        set(value) {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            options = options.let(value)
        }
}

/**
 * Configure [this] options instance using the
 * given [block] and return the configured
 * instance.
 */
inline fun ReplaceOptions.configure(
    block: ReplaceOptionsScope.() -> Unit
): ReplaceOptions {
    return ReplaceOptionsScope(this)
        .apply(block)
        .options
}

/* ============= - UpdateOptions  - ============= */

/**
 * A wrapper for [UpdateOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class UpdateOptionsScope(
    /**
     * The current options instance.
     */
    var options: UpdateOptions
) {
    /**
     * Set to true if a new document should be
     * inserted if there are no matches to the
     * query filter.
     *
     * @see UpdateOptions.upsert
     */
    var upsert: Boolean
        get() = options.isUpsert
        set(value) {
            options = options.upsert(value)
        }

    /**
     * Sets the bypass document level validation flag.
     *
     * @see UpdateOptions.bypassDocumentValidation
     */
    var bypassDocumentValidation: Boolean?
        get() = options.bypassDocumentValidation
        set(value) {
            options = options.bypassDocumentValidation(value)
        }

    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see UpdateOptions.collation
     */
    var collation: Collation?
        get() = options.collation
        set(value) {
            options = options.collation(value)
        }

    /**
     * Sets the array filters option.
     *
     * @see UpdateOptions.arrayFilters
     */
    var arrayFilters: List<Bson>?
        get() = options.arrayFilters
        set(value) {
            options = options.arrayFilters(value)
        }

    /**
     * Sets the hint for which index to use.
     * A null value means no hint is set.
     *
     * @see UpdateOptions.hint
     */
    var hint: Bson?
        get() = options.hint
        set(value) {
            options = options.hint(value)
        }

    /**
     * Sets the hint to apply.
     *
     * @see UpdateOptions.hintString
     */
    var hintString: String?
        get() = options.hintString
        set(value) {
            options = options.hintString(value)
        }

    /**
     * Sets the comment for this operation. A null
     * value means no comment is set.
     *
     * @see UpdateOptions.comment
     */
    var comment: BsonValue?
        get() = options.comment
        set(value) {
            options = options.comment(value)
        }

    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see UpdateOptions.comment
     */
    fun comment(value: String?) {
        comment = value?.let { BsonString(it) }
    }

    /**
     * Add top-level variables for the operation
     *
     * Allows for improved command readability by
     * separating the variables from the query text.
     *
     * @see UpdateOptions.let
     */
    var let: Bson?
        get() = options.let
        set(value) {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            options = options.let(value)
        }
}

/**
 * Configure [this] options instance using the
 * given [block] and return the configured
 * instance.
 */
inline fun UpdateOptions.configure(
    block: UpdateOptionsScope.() -> Unit
): UpdateOptions {
    return UpdateOptionsScope(this)
        .apply(block)
        .options
}

/* ======== - FindOneAndDeleteOptions  - ======== */

/**
 * A wrapper for [FindOneAndDeleteOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class FindOneAndDeleteOptionsScope(
    /**
     * The current options instance.
     */
    var options: FindOneAndDeleteOptions
) {
    /**
     * Sets a document describing the fields to
     * return for all matching documents.
     *
     * @see FindOneAndDeleteOptions.projection
     */
    var projection: Bson?
        get() = options.projection
        set(value) {
            options = options.projection(value)
        }

    /**
     * Sets the sort criteria to apply to the query.
     *
     * @see FindOneAndDeleteOptions.sort
     */
    var sort: Bson?
        get() = options.sort
        set(value) {
            options = options.sort(value)
        }

    /**
     * Gets the maximum execution time on the server for this operation.  The default is 0, which places no limit on the execution time.
     *
     * @param unit the time unit to return the result in
     * @return the maximum execution time in the given time unit
     * @see FindOneAndDeleteOptions.getMaxTime
     */
    fun getMaxTime(unit: TimeUnit): Long {
        return options.getMaxTime(unit)
    }

    /**
     * Sets the maximum execution time on the server for this operation.
     *
     * @param value  the max time
     * @param unit the time unit, which may not be null
     * @see FindOneAndDeleteOptions.maxTime
     */
    fun maxTime(value: Long, unit: TimeUnit) {
        options = options.maxTime(value, unit)
    }

    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see FindOneAndDeleteOptions.collation
     */
    var collation: Collation?
        get() = options.collation
        set(value) {
            options = options.collation(value)
        }

    /**
     * Sets the hint for which index to use.
     * A null value means no hint is set.
     *
     * @see FindOneAndDeleteOptions.hint
     */
    var hint: Bson?
        get() = options.hint
        set(value) {
            options = options.hint(value)
        }

    /**
     * Sets the hint to apply.
     *
     * @see FindOneAndDeleteOptions.hintString
     */
    var hintString: String?
        get() = options.hintString
        set(value) {
            options = options.hintString(value)
        }

    /**
     * Sets the comment for this operation. A null
     * value means no comment is set.
     *
     * @see FindOneAndDeleteOptions.comment
     */
    var comment: BsonValue?
        get() = options.comment
        set(value) {
            options = options.comment(value)
        }

    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see FindOneAndDeleteOptions.comment
     */
    fun comment(value: String?) {
        comment = value?.let { BsonString(it) }
    }

    /**
     * Add top-level variables for the operation
     *
     * Allows for improved command readability by
     * separating the variables from the query text.
     *
     * @see FindOneAndDeleteOptions.let
     */
    var let: Bson?
        get() = options.let
        set(value) {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            options = options.let(value)
        }
}

/**
 * Configure [this] options instance using the
 * given [block] and return the configured
 * instance.
 */
inline fun FindOneAndDeleteOptions.configure(
    block: FindOneAndDeleteOptionsScope.() -> Unit
): FindOneAndDeleteOptions {
    return FindOneAndDeleteOptionsScope(this)
        .apply(block)
        .options
}

/* ======== - FindOneAndReplaceOptions - ======== */

/**
 * A wrapper for [FindOneAndReplaceOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class FindOneAndReplaceOptionsScope(
    /**
     * The current options instance.
     */
    var options: FindOneAndReplaceOptions
) {
    /**
     * Sets a document describing the fields to
     * return for all matching documents.
     *
     * @see FindOneAndReplaceOptions.projection
     */
    var projection: Bson?
        get() = options.projection
        set(value) {
            options = options.projection(value)
        }

    /**
     * Sets the sort criteria to apply to the query.
     *
     * @see FindOneAndReplaceOptions.sort
     */
    var sort: Bson?
        get() = options.sort
        set(value) {
            options = options.sort(value)
        }

    /**
     * Set to true if a new document should be
     * inserted if there are no matches to the
     * query filter.
     *
     * @see FindOneAndReplaceOptions.upsert
     */
    var upsert: Boolean
        get() = options.isUpsert
        set(value) {
            options = options.upsert(value)
        }

    /**
     * Set whether to return the document before
     * it was replaced or after
     *
     * @see FindOneAndReplaceOptions.returnDocument
     */
    var returnDocument: ReturnDocument
        get() = options.returnDocument
        set(value) {
            options = options.returnDocument(value)
        }

    /**
     * Gets the maximum execution time on the server for this operation.  The default is 0, which places no limit on the execution time.
     *
     * @param unit the time unit to return the result in
     * @return the maximum execution time in the given time unit
     * @see FindOneAndReplaceOptions.getMaxTime
     */
    fun getMaxTime(unit: TimeUnit): Long {
        return options.getMaxTime(unit)
    }

    /**
     * Sets the maximum execution time on the server for this operation.
     *
     * @param value  the max time
     * @param unit the time unit, which may not be null
     * @see FindOneAndReplaceOptions.maxTime
     */
    fun maxTime(value: Long, unit: TimeUnit) {
        options = options.maxTime(value, unit)
    }

    /**
     * Sets the bypass document level validation flag.
     *
     * @see FindOneAndReplaceOptions.bypassDocumentValidation
     */
    var bypassDocumentValidation: Boolean?
        get() = options.bypassDocumentValidation
        set(value) {
            options = options.bypassDocumentValidation(value)
        }

    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see FindOneAndReplaceOptions.collation
     */
    var collation: Collation?
        get() = options.collation
        set(value) {
            options = options.collation(value)
        }

    /**
     * Sets the hint for which index to use.
     * A null value means no hint is set.
     *
     * @see FindOneAndReplaceOptions.hint
     */
    var hint: Bson?
        get() = options.hint
        set(value) {
            options = options.hint(value)
        }

    /**
     * Sets the hint to apply.
     *
     * @see FindOneAndReplaceOptions.hintString
     */
    var hintString: String?
        get() = options.hintString
        set(value) {
            options = options.hintString(value)
        }

    /**
     * Sets the comment for this operation. A null
     * value means no comment is set.
     *
     * @see FindOneAndReplaceOptions.comment
     */
    var comment: BsonValue?
        get() = options.comment
        set(value) {
            options = options.comment(value)
        }

    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see FindOneAndReplaceOptions.comment
     */
    fun comment(value: String?) {
        comment = value?.let { BsonString(it) }
    }

    /**
     * Add top-level variables for the operation
     *
     * Allows for improved command readability by
     * separating the variables from the query text.
     *
     * @see FindOneAndReplaceOptions.let
     */
    var let: Bson?
        get() = options.let
        set(value) {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            options = options.let(value)
        }
}

/**
 * Configure [this] options instance using the
 * given [block] and return the configured
 * instance.
 */
inline fun FindOneAndReplaceOptions.configure(
    block: FindOneAndReplaceOptionsScope.() -> Unit
): FindOneAndReplaceOptions {
    return FindOneAndReplaceOptionsScope(this)
        .apply(block)
        .options
}

/* ======== - FindOneAndUpdateOptions  - ======== */

/**
 * A wrapper for [FindOneAndUpdateOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class FindOneAndUpdateOptionsScope(
    /**
     * The current options instance.
     */
    var options: FindOneAndUpdateOptions
) {
    /**
     * Sets a document describing the fields to
     * return for all matching documents.
     *
     * @see FindOneAndUpdateOptions.projection
     */
    var projection: Bson?
        get() = options.projection
        set(value) {
            options = options.projection(value)
        }

    /**
     * Sets the sort criteria to apply to the query.
     *
     * @see FindOneAndUpdateOptions.sort
     */
    var sort: Bson?
        get() = options.sort
        set(value) {
            options = options.sort(value)
        }

    /**
     * Set to true if a new document should be
     * inserted if there are no matches to the
     * query filter.
     *
     * @see FindOneAndUpdateOptions.upsert
     */
    var upsert: Boolean
        get() = options.isUpsert
        set(value) {
            options = options.upsert(value)
        }

    /**
     * Set whether to return the document before
     * it was replaced or after
     *
     * @see FindOneAndUpdateOptions.returnDocument
     */
    var returnDocument: ReturnDocument
        get() = options.returnDocument
        set(value) {
            options = options.returnDocument(value)
        }

    /**
     * Gets the maximum execution time on the server for this operation.  The default is 0, which places no limit on the execution time.
     *
     * @param unit the time unit to return the result in
     * @return the maximum execution time in the given time unit
     * @see FindOneAndUpdateOptions.getMaxTime
     */
    fun getMaxTime(unit: TimeUnit): Long {
        return options.getMaxTime(unit)
    }

    /**
     * Sets the maximum execution time on the server for this operation.
     *
     * @param value  the max time
     * @param unit the time unit, which may not be null
     * @see FindOneAndUpdateOptions.maxTime
     */
    fun maxTime(value: Long, unit: TimeUnit) {
        options = options.maxTime(value, unit)
    }

    /**
     * Sets the bypass document level validation flag.
     *
     * @see FindOneAndUpdateOptions.bypassDocumentValidation
     */
    var bypassDocumentValidation: Boolean?
        get() = options.bypassDocumentValidation
        set(value) {
            options = options.bypassDocumentValidation(value)
        }

    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see FindOneAndUpdateOptions.collation
     */
    var collation: Collation?
        get() = options.collation
        set(value) {
            options = options.collation(value)
        }

    /**
     * Sets the array filters option.
     *
     * @see FindOneAndUpdateOptions.arrayFilters
     */
    var arrayFilters: List<Bson>?
        get() = options.arrayFilters
        set(value) {
            options = options.arrayFilters(value)
        }

    /**
     * Sets the hint for which index to use.
     * A null value means no hint is set.
     *
     * @see FindOneAndUpdateOptions.hint
     */
    var hint: Bson?
        get() = options.hint
        set(value) {
            options = options.hint(value)
        }

    /**
     * Sets the hint to apply.
     *
     * @see FindOneAndUpdateOptions.hintString
     */
    var hintString: String?
        get() = options.hintString
        set(value) {
            options = options.hintString(value)
        }

    /**
     * Sets the comment for this operation. A null
     * value means no comment is set.
     *
     * @see FindOneAndUpdateOptions.comment
     */
    var comment: BsonValue?
        get() = options.comment
        set(value) {
            options = options.comment(value)
        }

    /**
     * Sets the comment for this operation.
     * A null value means no comment is set.
     *
     * @see FindOneAndUpdateOptions.comment
     */
    fun comment(value: String?) {
        comment = value?.let { BsonString(it) }
    }

    /**
     * Add top-level variables for the operation
     *
     * Allows for improved command readability by
     * separating the variables from the query text.
     *
     * @see FindOneAndUpdateOptions.let
     */
    var let: Bson?
        get() = options.let
        set(value) {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            options = options.let(value)
        }
}

/**
 * Configure [this] options instance using the
 * given [block] and return the configured
 * instance.
 */
inline fun FindOneAndUpdateOptions.configure(
    block: FindOneAndUpdateOptionsScope.() -> Unit
): FindOneAndUpdateOptions {
    return FindOneAndUpdateOptionsScope(this)
        .apply(block)
        .options
}

/* ============= -- IndexOptions -- ============= */

/**
 * A wrapper for [IndexOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class IndexOptionsScope(
    /**
     * The current options instance.
     */
    var options: IndexOptions
) {
    // ignored members
    // - bucketSize : deprecation

    /**
     * Should the index should be created in the
     * background
     *
     * @see IndexOptions.background
     */
    var background: Boolean
        get() = options.isBackground
        set(value) {
            options = options.background(value)
        }

    /**
     * Should the index should be unique.
     *
     * @see IndexOptions.unique
     */
    var unique: Boolean
        get() = options.isUnique
        set(value) {
            options = options.unique(value)
        }

    /**
     * Sets the name of the index.
     *
     * @see IndexOptions.name
     */
    var name: String?
        get() = options.name
        set(value) {
            options = options.name(value)
        }

    /**
     * Should the index only references documents
     * with the specified field
     *
     * @see IndexOptions.sparse
     */
    var sparse: Boolean
        get() = options.isSparse
        set(value) {
            options = options.sparse(value)
        }

    /**
     * Gets the time to live for documents in the collection
     *
     * @param unit the time unit
     * @return the time to live for documents in the collection
     * @see IndexOptions.getExpireAfter
     */
    fun getExpireAfter(unit: TimeUnit): Long? {
        return options.getExpireAfter(unit)
    }

    /**
     * Sets the time to live for documents in the collection
     *
     * @param value the time to live for documents in the collection
     * @param unit the time unit for expireAfter
     * @see IndexOptions.expireAfter
     */
    fun expireAfter(value: Long?, unit: TimeUnit) {
        options = options.expireAfter(value, unit)
    }

    /**
     * Sets the index version number.
     *
     * @see IndexOptions.version
     */
    var version: Int?
        get() = options.version
        set(value) {
            options = options.version(value)
        }

    /**
     * Sets the weighting object for use with a text index.
     *
     * @see IndexOptions.weights
     */
    var weights: Bson?
        get() = options.weights
        set(value) {
            options = options.weights(value)
        }

    /**
     * Sets the language for the text index.
     *
     * @see IndexOptions.defaultLanguage
     */
    var defaultLanguage: String?
        get() = options.defaultLanguage
        set(value) {
            options = options.defaultLanguage(value)
        }

    /**
     * Sets the name of the field that contains the language string.
     *
     * @see IndexOptions.languageOverride
     */
    var languageOverride: String?
        get() = options.languageOverride
        set(value) {
            options = options.languageOverride(value)
        }

    /**
     * Set the text index version number.
     *
     * @see IndexOptions.textVersion
     */
    var textVersion: Int?
        get() = options.textVersion
        set(value) {
            options = options.textVersion(value)
        }

    /**
     * Sets the 2dsphere index version number.
     *
     * @see IndexOptions.sphereVersion
     */
    var sphereVersion: Int?
        get() = options.sphereVersion
        set(value) {
            options = options.sphereVersion(value)
        }

    /**
     * Sets the number of precision of the stored
     * geohash value of the location data in 2d
     * indexes.
     *
     * @see IndexOptions.bits
     */
    var bits: Int?
        get() = options.bits
        set(value) {
            options = options.bits(value)
        }

    /**
     * Sets the lower inclusive boundary for the
     * longitude and latitude values for 2d indexes.
     *
     * @see IndexOptions.min
     */
    var min: Double?
        get() = options.min
        set(value) {
            options = options.min(value)
        }

    /**
     * Sets the upper inclusive boundary for the
     * longitude and latitude values for 2d indexes.
     *
     * @see IndexOptions.max
     */
    var max: Double?
        get() = options.max
        set(value) {
            options = options.max(value)
        }

    /**
     * Sets the storage engine options document for this index.
     *
     * @see IndexOptions.storageEngine
     */
    var storageEngine: Bson?
        get() = options.storageEngine
        set(value) {
            options = options.storageEngine(value)
        }

    /**
     * Sets the filter expression for the documents
     * to be included in the index
     *
     * @see IndexOptions.partialFilterExpression
     */
    var partialFilterExpression: Bson?
        get() = options.partialFilterExpression
        set(value) {
            options = options.partialFilterExpression(value)
        }

    /**
     * Sets the filter expression for the documents
     * to be included in the index
     *
     * @see IndexOptions.partialFilterExpression
     */
    fun partialFilterExpression(block: BsonDocumentBlock) {
        partialFilterExpression = document(block)
    }

    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see IndexOptions.collation
     */
    var collation: Collation?
        get() = options.collation
        set(value) {
            options = options.collation(value)
        }

    /**
     * Sets the wildcard projection of a wildcard index
     *
     * @see IndexOptions.wildcardProjection
     */
    var wildcardProjection: Bson?
        get() = options.wildcardProjection
        set(value) {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            options = options.wildcardProjection(value)
        }

    /**
     * Should the index not be used by the query
     * planner when executing operations.
     *
     * @see IndexOptions.hidden
     */
    var hidden: Boolean
        get() = options.isHidden
        set(value) {
            options = options.hidden(value)
        }
}

/**
 * Configure [this] options instance using the
 * given [block] and return the configured
 * instance.
 */
inline fun IndexOptions.configure(
    block: IndexOptionsScope.() -> Unit
): IndexOptions {
    return IndexOptionsScope(this)
        .apply(block)
        .options
}

/* =========== - CreateIndexOptions - =========== */

/**
 * A wrapper for [CreateIndexOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class CreateIndexOptionsScope(
    /**
     * The current options instance.
     */
    var options: CreateIndexOptions
) {
    /**
     * Gets the maximum execution time on the server for this operation.  The default is 0, which places no limit on the execution time.
     *
     * @param unit the time unit to return the result in
     * @return the maximum execution time in the given time unit
     * @see CreateIndexOptions.getMaxTime
     */
    fun getMaxTime(unit: TimeUnit): Long {
        return options.getMaxTime(unit)
    }

    /**
     * Sets the maximum execution time on the server for this operation.
     *
     * @param value  the max time
     * @param unit the time unit, which may not be null
     * @see CreateIndexOptions.maxTime
     */
    fun maxTime(value: Long, unit: TimeUnit) {
        options = options.maxTime(value, unit)
    }

    /**
     * Sets the create index commit quorum for this operation.
     *
     * @see CreateIndexOptions.commitQuorum
     */
    var commitQuorum: CreateIndexCommitQuorum?
        get() = options.commitQuorum
        set(value) {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            options = options.commitQuorum(value)
        }
}

/**
 * Configure [this] options instance using the
 * given [block] and return the configured
 * instance.
 */
inline fun CreateIndexOptions.configure(
    block: CreateIndexOptionsScope.() -> Unit
): CreateIndexOptions {
    return CreateIndexOptionsScope(this)
        .apply(block)
        .options
}

/* ============ - DropIndexOptions - ============ */

/**
 * A wrapper for [DropIndexOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class DropIndexOptionsScope(
    /**
     * The current options instance.
     */
    var options: DropIndexOptions
) {
    /**
     * Gets the maximum execution time on the server for this operation.  The default is 0, which places no limit on the execution time.
     *
     * @param unit the time unit to return the result in
     * @return the maximum execution time in the given time unit
     * @see DropIndexOptions.getMaxTime
     */
    fun getMaxTime(unit: TimeUnit): Long {
        return options.getMaxTime(unit)
    }

    /**
     * Sets the maximum execution time on the server for this operation.
     *
     * @param value  the max time
     * @param unit the time unit, which may not be null
     * @see DropIndexOptions.maxTime
     */
    fun maxTime(value: Long, unit: TimeUnit) {
        options = options.maxTime(value, unit)
    }
}

/**
 * Configure [this] options instance using the
 * given [block] and return the configured
 * instance.
 */
inline fun DropIndexOptions.configure(
    block: DropIndexOptionsScope.() -> Unit
): DropIndexOptions {
    return DropIndexOptionsScope(this)
        .apply(block)
        .options
}

/* ======== - RenameCollectionOptions  - ======== */

/**
 * A wrapper for [RenameCollectionOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class RenameCollectionOptionsScope(
    /**
     * The current options instance.
     */
    var options: RenameCollectionOptions
) {
    /**
     * Sets if mongod should drop the target of
     * renameCollection prior to renaming the
     * collection.
     *
     * @see RenameCollectionOptions.dropTarget
     */
    var dropTarget: Boolean
        get() = options.isDropTarget
        set(value) {
            options = options.dropTarget(value)
        }
}

/**
 * Configure [this] options instance using the
 * given [block] and return the configured
 * instance.
 */
inline fun RenameCollectionOptions.configure(
    block: RenameCollectionOptionsScope.() -> Unit
): RenameCollectionOptions {
    return RenameCollectionOptionsScope(this)
        .apply(block)
        .options
}

/* ======== - CreateCollectionOptions  - ======== */

/**
 * A wrapper for [CreateCollectionOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class CreateCollectionOptionsScope(
    /**
     * The current options instance.
     */
    var options: CreateCollectionOptions
) {
    /**
     * Sets the maximum number of documents allowed in a capped collection.
     *
     * @see CreateCollectionOptions.maxDocuments
     */
    var maxDocuments: Long
        get() = options.maxDocuments
        set(value) {
            options = options.maxDocuments(value)
        }

    /**
     * sets whether the collection is capped.
     *
     * @see CreateCollectionOptions.capped
     */
    var capped: Boolean
        get() = options.isCapped
        set(value) {
            options = options.capped(value)
        }

    /**
     * Gets the maximum size of in bytes of a capped collection.
     *
     * @see CreateCollectionOptions.sizeInBytes
     */
    var sizeInBytes: Long
        get() = options.sizeInBytes
        set(value) {
            options = options.sizeInBytes(value)
        }

    /**
     * Sets the storage engine options document defaults for the collection
     *
     * @see CreateCollectionOptions.storageEngineOptions
     */
    var storageEngineOptions: Bson?
        get() = options.storageEngineOptions
        set(value) {
            options = options.storageEngineOptions(value)
        }

    /**
     * Sets the index option defaults for the collection.
     *
     * @see CreateCollectionOptions.indexOptionDefaults
     */
    var indexOptionDefaults: IndexOptionDefaults
        get() = options.indexOptionDefaults
        set(value) {
            options = options.indexOptionDefaults(value)
        }

    /**
     * Sets the validation options for documents being inserted or updated in a collection
     *
     * @see CreateCollectionOptions.validationOptions
     */
    var validationOptions: ValidationOptions
        get() = options.validationOptions
        set(value) {
            options = options.validationOptions(value)
        }

    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see CreateCollectionOptions.collation
     */
    var collation: Collation?
        get() = options.collation
        set(value) {
            options = options.collation(value)
        }

    /**
     * Returns the expire-after option.
     *
     * @param unit the time unit
     * @return the expire-after option.
     * @see CreateCollectionOptions.getExpireAfter
     */
    fun getExpireAfter(unit: TimeUnit): Long {
        return options.getExpireAfter(unit)
    }

    /**
     * Sets the expire-after option.
     *
     * @param value the expire-after duration.
     * @param unit the time unit
     * @see CreateCollectionOptions.expireAfter
     */
    fun expireAfter(value: Long, unit: TimeUnit) {
        options = options.expireAfter(value, unit)
    }

    /**
     * Sets the time-series collection options.
     *
     * @see CreateCollectionOptions.timeSeriesOptions
     */
    var timeSeriesOptions: TimeSeriesOptions?
        get() = options.timeSeriesOptions
        set(value) {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            options = options.timeSeriesOptions(value)
        }

    /**
     * Sets the clustered index collection options.
     *
     * @see CreateCollectionOptions.clusteredIndexOptions
     */
    var clusteredIndexOptions: ClusteredIndexOptions?
        get() = options.clusteredIndexOptions
        set(value) {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            options = options.clusteredIndexOptions(value)
        }

    /**
     * Sets the change stream pre- and post- images options.
     *
     * @see CreateCollectionOptions.changeStreamPreAndPostImagesOptions
     */
    var changeStreamPreAndPostImagesOptions: ChangeStreamPreAndPostImagesOptions?
        get() = options.changeStreamPreAndPostImagesOptions
        set(value) {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            options = options.changeStreamPreAndPostImagesOptions(value)
        }

    /**
     * Sets the encrypted fields
     *
     * @see CreateCollectionOptions.encryptedFields
     */
    var encryptedFields: Bson?
        get() = options.encryptedFields
        set(value) {
            options = options.encryptedFields(value)
        }
}

/**
 * Configure [this] options instance using the
 * given [block] and return the configured
 * instance.
 */
inline fun CreateCollectionOptions.configure(
    block: CreateCollectionOptionsScope.() -> Unit
): CreateCollectionOptions {
    return CreateCollectionOptionsScope(this)
        .apply(block)
        .options
}

/* =========== - CreateViewOptions  - =========== */

/**
 * A wrapper for [CreateViewOptions].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class CreateViewOptionsScope(
    /**
     * The current options instance.
     */
    var options: CreateViewOptions
) {
    /**
     * Sets the collation options
     *
     * A null value represents the server default.
     *
     * @see CreateViewOptions.collation
     */
    var collation: Collation?
        get() = options.collation
        set(value) {
            options = options.collation(value)
        }
}

/**
 * Configure [this] options instance using the
 * given [block] and return the configured
 * instance.
 */
inline fun CreateViewOptions.configure(
    block: CreateViewOptionsScope.() -> Unit
): CreateViewOptions {
    return CreateViewOptionsScope(this)
        .apply(block)
        .options
}

/* ========== - ClientSessionOptions - ========== */

/**
 * A wrapper for [ClientSessionOptions.Builder].
 *
 * @author LSafer
 * @since 2.0.0
 */
open class ClientSessionOptionsScope(
    /**
     * The current options instance.
     */
    var options: ClientSessionOptions.Builder
) {
    /**
     * Sets whether operations using the session
     * should causally consistent with each other.
     *
     * @see ClientSessionOptions.Builder.causallyConsistent
     */
    var causallyConsistent: Boolean
        get() = error("Inaccessible Property")
        set(value) {
            options = options.causallyConsistent(value)
        }

    /**
     * Sets whether read operations using the
     * session should share the same snapshot.
     *
     * The default value is unset, in which case
     * the driver will use the global default
     * value, which is currently false.
     *
     * @see ClientSessionOptions.Builder.snapshot
     */
    var snapshot: Boolean
        get() = error("Inaccessible Property")
        set(value) {
            options = options.snapshot(value)
        }

    /**
     * Sets whether operations using the session
     * should causally consistent with each other.
     *
     * @see ClientSessionOptions.defaultTransactionOptions
     */
    var defaultTransactionOptions: TransactionOptions
        get() = error("Inaccessible Property")
        set(value) {
            options = options.defaultTransactionOptions(value)
        }
}

/**
 * Configure [this] options instance using the
 * given [block] and return the configured
 * instance.
 */
inline fun ClientSessionOptions.Builder.configure(
    block: ClientSessionOptionsScope.() -> Unit
): ClientSessionOptions.Builder {
    return ClientSessionOptionsScope(this)
        .apply(block)
        .options
}
