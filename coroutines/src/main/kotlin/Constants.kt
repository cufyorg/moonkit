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
@file:Suppress("ObjectPropertyName")

package org.cufy.mongodb

/* ===== Common =============================== */

/**
 * Selects documents if element in the array field
 * matches all the specified $elemMatch conditions.
 *
 * [$elemMatch](https://www.mongodb.com/docs/manual/reference/operator/query/elemMatch/#mongodb-query-op.-elemMatch)
 *
 * ---
 *
 * Projects the first element in an array that
 * matches the specified $elemMatch condition.
 *
 * [$elemMatch](https://www.mongodb.com/docs/manual/reference/operator/projection/elemMatch/#mongodb-projection-proj.-elemMatch)
 *
 * @since 2.0.0
 */
// Query and Projection Operators / Query Selectors / Array
// Query and Projection Operators / Projection Operators
const val `$elemMatch` = "\$elemMatch"

/**
 * Projects the first element in an array that
 * matches the query condition.
 *
 * [$](https://www.mongodb.com/docs/manual/reference/operator/projection/positional/#mongodb-projection-proj.-)
 *
 * ---
 *
 * Acts as a placeholder to update the first
 * element that matches the query condition.
 *
 * [$](https://www.mongodb.com/docs/manual/reference/operator/update/positional/#mongodb-update-up.-)
 *
 * @since 2.0.0
 */
// Query and Projection Operators / Projection Operators
// Update Operators / Array / Operators
const val `$` = "\$"

/**
 * Limits the number of elements projected from an
 * array. Supports skip and limit slices.
 *
 * [$slice](https://www.mongodb.com/docs/manual/reference/operator/projection/slice/#mongodb-projection-proj.-slice)
 *
 * ---
 *
 * Modifies the $push operator to limit the size
 * of updated arrays.
 *
 * [$slice](https://www.mongodb.com/docs/manual/reference/operator/update/slice/#mongodb-update-up.-slice)
 *
 * @since 2.0.0
 */
// Query and Projection Operators / Projection Operators
// Update Operators / Array / Modifiers
const val `$slice` = "\$slice"

/**
 * Sets the value of a field in a document.
 *
 * [$set](https://www.mongodb.com/docs/manual/reference/operator/update/set/#mongodb-update-up.-set)
 *
 * ---
 *
 * Adds new fields to documents. Similar to
 * $project, $set reshapes each document in the
 * stream; specifically, by adding new fields to
 * output documents that contain both the existing
 * fields from the input documents and the newly
 * added fields.
 *
 * $set is an alias for $addFields stage.
 *
 * [$set](https://www.mongodb.com/docs/manual/reference/operator/aggregation/set/#mongodb-pipeline-pipe.-set)
 *
 * @since 2.0.0
 */
// Update Operators / Fields
// Aggregation Pipeline Stages / Stages
const val `$set` = "\$set"

/**
 * Modifies the $push operator to reorder
 * documents stored in an array.
 *
 * [$sort](https://www.mongodb.com/docs/manual/reference/operator/update/sort/#mongodb-update-up.-sort)
 *
 * ---
 *
 * Reorders the document stream by a specified
 * sort key. Only the order changes; the documents
 * remain unmodified. For each input document,
 * outputs one document.
 *
 * [$sort](https://www.mongodb.com/docs/manual/reference/operator/aggregation/sort/#mongodb-pipeline-pipe.-sort)
 *
 * @since 2.0.0
 */
// Update Operators / Array / Modifiers
const val `$sort` = "\$sort"

/**
 * Removes the specified field from a document.
 *
 * [$unset](https://www.mongodb.com/docs/manual/reference/operator/update/unset/#mongodb-update-up.-unset)
 *
 * ---
 *
 * Removes/excludes fields from documents.
 *
 * $unset is an alias for $project stage that
 * removes fields.
 *
 * [$unset](https://www.mongodb.com/docs/manual/reference/operator/aggregation/unset/#mongodb-pipeline-pipe.-unset)
 *
 * @since 2.0.0
 */
// Update Operators / Fields
// Aggregation Pipeline Stages / Stages
const val `$unset` = "\$unset"

// Query and Projection Operators / Query Selectors / Comparison

/**
 * Matches values that are equal to a specified
 * value.
 *
 * [$eq](https://www.mongodb.com/docs/manual/reference/operator/query/eq/#mongodb-query-op.-eq)
 * @since 2.0.0
 */
const val `$eq` = "\$eq"

/**
 * Matches values that are greater than a
 * specified value.
 *
 * [$gt](https://www.mongodb.com/docs/manual/reference/operator/query/gt/#mongodb-query-op.-gt)
 * @since 2.0.0
 */
const val `$gt` = "\$gt"

/**
 * Matches values that are greater than or equal
 * to a specified value.
 *
 * [$gte](https://www.mongodb.com/docs/manual/reference/operator/query/gte/#mongodb-query-op.-gte)
 * @since 2.0.0
 */
const val `$gte` = "\$gte"

/**
 * Matches any of the values specified in an array.
 *
 * [$in](https://www.mongodb.com/docs/manual/reference/operator/query/in/#mongodb-query-op.-in)
 * @since 2.0.0
 */
const val `$in` = "\$in"

/**
 * Matches values that are less than a specified
 * value.
 *
 * [$lt](https://www.mongodb.com/docs/manual/reference/operator/query/lt/#mongodb-query-op.-lt)
 * @since 2.0.0
 */
const val `$lt` = "\$lt"

/**
 * Matches values that are less than or equal to a
 * specified value.
 *
 * [$lte](https://www.mongodb.com/docs/manual/reference/operator/query/lte/#mongodb-query-op.-lte)
 * @since 2.0.0
 */
const val `$lte` = "\$lte"

/**
 * Matches all values that are not equal to a
 * specified value.
 *
 * [$ne](https://www.mongodb.com/docs/manual/reference/operator/query/ne/#mongodb-query-op.-ne)
 * @since 2.0.0
 */
const val `$ne` = "\$ne"

/**
 * Matches none of the values specified in an
 * array.
 *
 * [$nin](https://www.mongodb.com/docs/manual/reference/operator/query/nin/#mongodb-query-op.-nin)
 * @since 2.0.0
 */
const val `$nin` = "\$nin"

// Query and Projection Operators / Query Selectors / Logical

/**
 * Joins query clauses with a logical AND returns
 * all documents that match the conditions of both
 * clauses.
 *
 * [$and](https://www.mongodb.com/docs/manual/reference/operator/query/and/#mongodb-query-op.-and)
 * @since 2.0.0
 */
const val `$and` = "\$and"

/**
 * Inverts the effect of a query expression and
 * returns documents that do not match the query
 * expression.
 *
 * [$not](https://www.mongodb.com/docs/manual/reference/operator/query/not/#mongodb-query-op.-not)
 * @since 2.0.0
 */
const val `$not` = "\$not"

/**
 * Joins query clauses with a logical NOR returns
 * all documents that fail to match both clauses.
 *
 * [$nor](https://www.mongodb.com/docs/manual/reference/operator/query/nor/#mongodb-query-op.-nor)
 * @since 2.0.0
 */
const val `$nor` = "\$nor"

/**
 * Joins query clauses with a logical OR returns
 * all documents that match the conditions of either clause.
 *
 * [$or](https://www.mongodb.com/docs/manual/reference/operator/query/or/#mongodb-query-op.-or)
 * @since 2.0.0
 */
const val `$or` = "\$or"

// Query and Projection Operators / Query Selectors / Element

/**
 * Matches documents that have the specified field.
 *
 * [$exists](https://www.mongodb.com/docs/manual/reference/operator/query/exists/#mongodb-query-op.-exists)
 * @since 2.0.0
 */
const val `$exists` = "\$exists"

/**
 * Selects documents if a field is of the
 * specified type.
 *
 * [$type](https://www.mongodb.com/docs/manual/reference/operator/query/type/#mongodb-query-op.-type)
 * @since 2.0.0
 */
const val `$type` = "\$type"

// Query and Projection Operators / Query Selectors / Evaluation

/**
 * Allows use of aggregation expressions within
 * the query language.
 *
 * [$expr](https://www.mongodb.com/docs/manual/reference/operator/query/expr/#mongodb-query-op.-expr)
 * @since 2.0.0
 */
const val `$expr` = "\$expr"

/**
 * Validate documents against the given JSON
 * Schema.
 *
 * [$jsonSchema](https://www.mongodb.com/docs/manual/reference/operator/query/jsonSchema/#mongodb-query-op.-jsonSchema)
 * @since 2.0.0
 */
const val `$jsonSchema` = "\$jsonSchema"

/**
 * Performs a modulo operation on the value of a
 * field and selects documents with a specified
 * result.
 *
 * [$mod](https://www.mongodb.com/docs/manual/reference/operator/query/mod/#mongodb-query-op.-mod)
 * @since 2.0.0
 */
const val `$mod` = "\$mod"

/**
 * Selects documents where values match a
 * specified regular expression.
 *
 * [$regex](https://www.mongodb.com/docs/manual/reference/operator/query/regex/#mongodb-query-op.-regex)
 * @since 2.0.0
 */
const val `$regex` = "\$regex"

/**
 * Performs text search.
 *
 * [$text](https://www.mongodb.com/docs/manual/reference/operator/query/text/#mongodb-query-op.-text)
 * @since 2.0.0
 */
const val `$text` = "\$text"

/**
 * Matches documents that satisfy a JavaScript
 * expression.
 *
 * [$where](https://www.mongodb.com/docs/manual/reference/operator/query/where/#mongodb-query-op.-where)
 * @since 2.0.0
 */
const val `$where` = "\$where"

// Query and Projection Operators / Query Selectors / Geospatial

/**
 * Selects geometries that intersect with a
 * GeoJSON geometry. The 2dsphere index supports
 * $geoIntersects.
 *
 * [$geoIntersects](https://www.mongodb.com/docs/manual/reference/operator/query/geoIntersects/#mongodb-query-op.-geoIntersects)
 * @since 2.0.0
 */
const val `$geoIntersects` = "\$geoIntersects"

/**
 * Selects geometries within a bounding GeoJSON
 * geometry. The 2dsphere and 2d indexes support
 * $geoWithin.
 *
 * [$geoWithin](https://www.mongodb.com/docs/manual/reference/operator/query/geoWithin/#mongodb-query-op.-geoWithin)
 * @since 2.0.0
 */
const val `$geoWithin` = "\$geoWithin"

/**
 * Returns geospatial objects in proximity to a
 * point. Requires a geospatial index. The
 * 2dsphere and 2d indexes support $near.
 *
 * [$near](https://www.mongodb.com/docs/manual/reference/operator/query/near/#mongodb-query-op.-near)
 * @since 2.0.0
 */
const val `$near` = "\$near"

/**
 * Returns geospatial objects in proximity to a
 * point on a sphere. Requires a geospatial index.
 * The 2dsphere and 2d indexes support $nearSphere.
 *
 * [$nearSphere](https://www.mongodb.com/docs/manual/reference/operator/query/nearSphere/#mongodb-query-op.-nearSphere)
 * @since 2.0.0
 */
const val `$nearSphere` = "\$nearSphere"

// Query and Projection Operators / Query Selectors / Array

/**
 * Matches arrays that contain all elements
 * specified in the query.
 *
 * [$all](https://www.mongodb.com/docs/manual/reference/operator/query/all/#mongodb-query-op.-all)
 * @since 2.0.0
 */
const val `$all` = "\$all"

/**
 * Selects documents if the array field is a
 * specified size.
 *
 * [$size](https://www.mongodb.com/docs/manual/reference/operator/query/size/#mongodb-query-op.-size)
 * @since 2.0.0
 */
const val `$size` = "\$size"

// Query and Projection Operators / Query Selectors / Bitwise

/**
 * Matches numeric or binary values in which a set
 * of bit positions all have a value of 0.
 *
 * [$bitsAllClear](https://www.mongodb.com/docs/manual/reference/operator/query/bitsAllClear/#mongodb-query-op.-bitsAllClear)
 * @since 2.0.0
 */
const val `$bitsAllClear` = "\$bitsAllClear"

/**
 * Matches numeric or binary values in which a set
 * of bit positions all have a value of 1.
 *
 * [$bitsAllSet](https://www.mongodb.com/docs/manual/reference/operator/query/bitsAllSet/#mongodb-query-op.-bitsAllSet)
 * @since 2.0.0
 */
const val `$bitsAllSet` = "\$bitsAllSet"

/**
 * Matches numeric or binary values in which any
 * bit from a set of bit positions has a value of 0.
 *
 * [$bitsAnyClear](https://www.mongodb.com/docs/manual/reference/operator/query/bitsAnyClear/#mongodb-query-op.-bitsAnyClear)
 * @since 2.0.0
 */
const val `$bitsAnyClear` = "\$bitsAnyClear"

/**
 * Matches numeric or binary values in which any
 * bit from a set of bit positions has a value of 1.
 *
 * [$bitsAnySet](https://www.mongodb.com/docs/manual/reference/operator/query/bitsAnySet/#mongodb-query-op.-bitsAnySet)
 * @since 2.0.0
 */
const val `$bitsAnySet` = "\$bitsAnySet"

// Query and Projection Operators / Projection Operators

/**
 * Projects the document's score assigned during $text operation.
 *
 * [$meta](https://www.mongodb.com/docs/manual/reference/operator/aggregation/meta/#mongodb-expression-exp.-meta)
 * @since 2.0.0
 */
const val `$meta` = "\$meta"

// Query and Projection Operators / Miscellaneous Operators

/**
 * Adds a comment to a query predicate.
 *
 * [$comment](https://www.mongodb.com/docs/manual/reference/operator/query/comment/#mongodb-query-op.-comment)
 * @since 2.0.0
 */
const val `$comment` = "\$comment"

/**
 * Generates a random float between 0 and 1.
 *
 * [$rand](https://www.mongodb.com/docs/manual/reference/operator/query/rand/#mongodb-query-op.-rand)
 * @since 2.0.0
 */
const val `$rand` = "\$rand"

// Update Operators / Fields

/**
 * Sets the value of a field to current date,
 * either as a Date or a Timestamp.
 *
 * [$currentDate](https://www.mongodb.com/docs/manual/reference/operator/update/currentDate/#mongodb-update-up.-currentDate)
 * @since 2.0.0
 */
const val `$currentDate` = "\$currentDate"

/**
 * Increments the value of the field by the
 * specified amount.
 *
 * [$inc](https://www.mongodb.com/docs/manual/reference/operator/update/inc/#mongodb-update-up.-inc)
 * @since 2.0.0
 */
const val `$inc` = "\$inc"

/**
 * Only updates the field if the specified value
 * is less than the existing field value.
 *
 * [$min](https://www.mongodb.com/docs/manual/reference/operator/update/min/#mongodb-update-up.-min)
 * @since 2.0.0
 */
const val `$min` = "\$min"

/**
 * Only updates the field if the specified value
 * is greater than the existing field value.
 *
 * [$max](https://www.mongodb.com/docs/manual/reference/operator/update/max/#mongodb-update-up.-max)
 * @since 2.0.0
 */
const val `$max` = "\$max"

/**
 * Multiplies the value of the field by the
 * specified amount.
 *
 * [$mul](https://www.mongodb.com/docs/manual/reference/operator/update/mul/#mongodb-update-up.-mul)
 * @since 2.0.0
 */
const val `$mul` = "\$mul"

/**
 * Renames a field.
 *
 * [$rename](https://www.mongodb.com/docs/manual/reference/operator/update/rename/#mongodb-update-up.-rename)
 * @since 2.0.0
 */
const val `$rename` = "\$rename"

/**
 * Sets the value of a field if an update results
 * in an insert of a document. Has no effect on
 * update operations that modify existing
 * documents.
 *
 * [$setOnInsert](https://www.mongodb.com/docs/manual/reference/operator/update/setOnInsert/#mongodb-update-up.-setOnInsert)
 * @since 2.0.0
 */
const val `$setOnInsert` = "\$setOnInsert"

// Update Operators / Array / Operators

/**
 * Acts as a placeholder to update all elements in
 * an array for the documents that match the query
 * condition.
 *
 * [$&#x5B;&#x5D;](https://www.mongodb.com/docs/manual/reference/operator/update/positional-all/#mongodb-update-up.---)
 * @since 2.0.0
 */
fun `$`() = "\$[]"

/**
 * Acts as a placeholder to update all elements
 * that match the arrayFilters condition for the
 * documents that match the query condition.
 *
 * [$&#x5B;&#x3C;identifier&#x3E;&#x5D;](https://www.mongodb.com/docs/manual/reference/operator/update/positional-filtered/#mongodb-update-up.---identifier--)
 * @since 2.0.0
 */
fun `$`(identifier: String) = "\$[$identifier]"

/**
 * Adds elements to an array only if they do not
 * already exist in the set.
 *
 * [$addToSet](https://www.mongodb.com/docs/manual/reference/operator/update/addToSet/#mongodb-update-up.-addToSet)
 * @since 2.0.0
 */
const val `$addToSet` = "\$addToSet"

/**
 * Removes the first or last item of an array.
 *
 * [$pop](https://www.mongodb.com/docs/manual/reference/operator/update/pop/#mongodb-update-up.-pop)
 * @since 2.0.0
 */
const val `$pop` = "\$pop"

/**
 * Removes all array elements that match a
 * specified query.
 *
 * [$pull](https://www.mongodb.com/docs/manual/reference/operator/update/pull/#mongodb-update-up.-pull)
 * @since 2.0.0
 */
const val `$pull` = "\$pull"

/**
 * Adds an item to an array.
 *
 * [$push](https://www.mongodb.com/docs/manual/reference/operator/update/push/#mongodb-update-up.-push)
 * @since 2.0.0
 */
const val `$push` = "\$push"

/**
 * Removes all matching values from an array.
 *
 * [$pullAll](https://www.mongodb.com/docs/manual/reference/operator/update/pullAll/#mongodb-update-up.-pullAll)
 * @since 2.0.0
 */
const val `$pullAll` = "\$pullAll"

// Update Operators / Array / Modifiers

/**
 * Modifies the $push and $addToSet operators to
 * append multiple items for array updates.
 *
 * [$each](https://www.mongodb.com/docs/manual/reference/operator/update/each/#mongodb-update-up.-each)
 * @since 2.0.0
 */
const val `$each` = "\$each"

/**
 * Modifies the $push operator to specify the
 * position in the array to add elements.
 *
 * [$position](https://www.mongodb.com/docs/manual/reference/operator/update/position/#mongodb-update-up.-position)
 * @since 2.0.0
 */
const val `$position` = "\$position"

// Update Operators / Bitwise

/**
 * Performs bitwise AND, OR, and XOR updates of
 * integer values.
 *
 * [$bit](https://www.mongodb.com/docs/manual/reference/operator/update/bit/#mongodb-update-up.-bit)
 * @since 2.0.0
 */
const val `$bit` = "\$bit"

// Aggregation Pipeline Stages / Stages

/**
 * Adds new fields to documents. Similar to
 * $project, $addFields reshapes each document in
 * the stream; specifically, by adding new fields
 * to output documents that contain both the
 * existing fields from the input documents and
 * the newly added fields.
 *
 * $set is an alias for $addFields.
 *
 * [$addFields](https://www.mongodb.com/docs/manual/reference/operator/aggregation/addFields/#mongodb-pipeline-pipe.-addFields)
 * @since 2.0.0
 */
const val `$addFields` = "\$addFields"

/**
 * Categorizes incoming documents into groups,
 * called buckets, based on a specified expression
 * and bucket boundaries.
 *
 * [$bucket](https://www.mongodb.com/docs/manual/reference/operator/aggregation/bucket/#mongodb-pipeline-pipe.-bucket)
 * @since 2.0.0
 */
const val `$bucket` = "\$bucket"

/**
 * Categorizes incoming documents into a specific
 * number of groups, called buckets, based on a
 * specified expression. Bucket boundaries are
 * automatically determined in an attempt to
 * evenly distribute the documents into the
 * specified number of buckets.
 *
 * [$bucketAuto](https://www.mongodb.com/docs/manual/reference/operator/aggregation/bucketAuto/#mongodb-pipeline-pipe.-bucketAuto)
 * @since 2.0.0
 */
const val `$bucketAuto` = "\$bucketAuto"

/**
 * Returns statistics regarding a collection or
 * view.
 *
 * [$collStats](https://www.mongodb.com/docs/manual/reference/operator/aggregation/collStats/#mongodb-pipeline-pipe.-collStats)
 * @since 2.0.0
 */
const val `$collStats` = "\$collStats"

/**
 * Returns a count of the number of documents at
 * this stage of the aggregation pipeline.
 *
 * Distinct from the $count aggregation
 * accumulator.
 *
 * [$count](https://www.mongodb.com/docs/manual/reference/operator/aggregation/count/#mongodb-pipeline-pipe.-count)
 * @since 2.0.0
 */
const val `$count` = "\$count"

/**
 * Processes multiple aggregation pipelines within
 * a single stage on the same set of input
 * documents. Enables the creation of
 * multi-faceted aggregations capable of
 * characterizing data across multiple dimensions,
 * or facets, in a single stage.
 *
 * [$facet](https://www.mongodb.com/docs/manual/reference/operator/aggregation/facet/#mongodb-pipeline-pipe.-facet)
 * @since 2.0.0
 */
const val `$facet` = "\$facet"

/**
 * Returns an ordered stream of documents based on
 * the proximity to a geospatial point.
 * Incorporates the functionality of $match,
 * $sort, and $limit for geospatial data. The
 * output documents include an additional distance
 * field and can include a location identifier
 * field.
 *
 * [$geoNear](https://www.mongodb.com/docs/manual/reference/operator/aggregation/geoNear/#mongodb-pipeline-pipe.-geoNear)
 * @since 2.0.0
 */
const val `$geoNear` = "\$geoNear"

/**
 * Performs a recursive search on a collection. To
 * each output document, adds a new array field
 * that contains the traversal results of the
 * recursive search for that document.
 *
 * [$graphLookup](https://www.mongodb.com/docs/manual/reference/operator/aggregation/graphLookup/#mongodb-pipeline-pipe.-graphLookup)
 * @since 2.0.0
 */
const val `$graphLookup` = "\$graphLookup"

/**
 * Groups input documents by a specified
 * identifier expression and applies the
 * accumulator expression(s), if specified, to
 * each group. Consumes all input documents and
 * outputs one document per each distinct group.
 * The output documents only contain the
 * identifier field and, if specified, accumulated
 * fields.
 *
 * [$group](https://www.mongodb.com/docs/manual/reference/operator/aggregation/group/#mongodb-pipeline-pipe.-group)
 * @since 2.0.0
 */
const val `$group` = "\$group"

/**
 * Returns statistics regarding the use of each
 * index for the collection.
 *
 * [$indexStats](https://www.mongodb.com/docs/manual/reference/operator/aggregation/indexStats/#mongodb-pipeline-pipe.-indexStats)
 * @since 2.0.0
 */
const val `$indexStats` = "\$indexStats"

/**
 * Passes the first n documents unmodified to the
 * pipeline where n is the specified limit. For
 * each input document, outputs either one
 * document (for the first n documents) or zero
 * documents (after the first n documents).
 *
 * [$limit](https://www.mongodb.com/docs/manual/reference/operator/aggregation/limit/#mongodb-pipeline-pipe.-limit)
 * @since 2.0.0
 */
const val `$limit` = "\$limit"

/**
 * Lists all sessions that have been active long
 * enough to propagate to the system.sessions
 * collection.
 *
 * [$listSessions](https://www.mongodb.com/docs/manual/reference/operator/aggregation/listSessions/#mongodb-pipeline-pipe.-listSessions)
 * @since 2.0.0
 */
const val `$listSessions` = "\$listSessions"

/**
 * Performs a left outer join to another
 * collection in the same database to filter in
 * documents from the "joined" collection for
 * processing.
 *
 * [$lookup](https://www.mongodb.com/docs/manual/reference/operator/aggregation/lookup/#mongodb-pipeline-pipe.-lookup)
 * @since 2.0.0
 */
const val `$lookup` = "\$lookup"

/**
 * Filters the document stream to allow only
 * matching documents to pass unmodified into the
 * next pipeline stage. $match uses standard
 * MongoDB queries. For each input document,
 * outputs either one document (a match) or zero
 * documents (no match).
 *
 * [$match](https://www.mongodb.com/docs/manual/reference/operator/aggregation/match/#mongodb-pipeline-pipe.-match)
 * @since 2.0.0
 */
const val `$match` = "\$match"

/**
 * Writes the resulting documents of the
 * aggregation pipeline to a collection. The stage
 * can incorporate (insert new documents, merge
 * documents, replace documents, keep existing
 * documents, fail the operation, process
 * documents with a custom update pipeline) the
 * results into an output collection. To use the
 * $merge stage, it must be the last stage in the
 * pipeline.
 *
 * New in version 4.2.
 *
 * [$merge](https://www.mongodb.com/docs/manual/reference/operator/aggregation/merge/#mongodb-pipeline-pipe.-merge)
 * @since 2.0.0
 */
const val `$merge` = "\$merge"

/**
 * Writes the resulting documents of the
 * aggregation pipeline to a collection. To use
 * the $out stage, it must be the last stage in
 * the pipeline.
 *
 * [$out](https://www.mongodb.com/docs/manual/reference/operator/aggregation/out/#mongodb-pipeline-pipe.-out)
 * @since 2.0.0
 */
const val `$out` = "\$out"

/**
 * Returns plan cache information for a collection.
 *
 * [$planCacheStats](https://www.mongodb.com/docs/manual/reference/operator/aggregation/planCacheStats/#mongodb-pipeline-pipe.-planCacheStats)
 * @since 2.0.0
 */
const val `$planCacheStats` = "\$planCacheStats"

/**
 * Reshapes each document in the stream, such as
 * by adding new fields or removing existing
 * fields. For each input document, outputs one
 * document.
 *
 * See also $unset for removing existing fields.
 *
 * [$project](https://www.mongodb.com/docs/manual/reference/operator/aggregation/project/#mongodb-pipeline-pipe.-project)
 * @since 2.0.0
 */
const val `$project` = "\$project"

/**
 * Reshapes each document in the stream by
 * restricting the content for each document based
 * on information stored in the documents
 * themselves. Incorporates the functionality of
 * $project and $match. Can be used to implement
 * field level redaction. For each input document,
 * outputs either one or zero documents.
 *
 * [$redact](https://www.mongodb.com/docs/manual/reference/operator/aggregation/redact/#mongodb-pipeline-pipe.-redact)
 * @since 2.0.0
 */
const val `$redact` = "\$redact"

/**
 * Replaces a document with the specified embedded
 * document. The operation replaces all existing
 * fields in the input document, including the _id
 * field. Specify a document embedded in the input
 * document to promote the embedded document to
 * the top level.
 *
 * $replaceWith is an alias for $replaceRoot stage.
 *
 * [$replaceRoot](https://www.mongodb.com/docs/manual/reference/operator/aggregation/replaceRoot/#mongodb-pipeline-pipe.-replaceRoot)
 * @since 2.0.0
 */
const val `$replaceRoot` = "\$replaceRoot"

/**
 * Replaces a document with the specified embedded
 * document. The operation replaces all existing
 * fields in the input document, including the _id
 * field. Specify a document embedded in the input
 * document to promote the embedded document to
 * the top level.
 *
 * $replaceWith is an alias for $replaceRoot stage.
 *
 * [$replaceWith](https://www.mongodb.com/docs/manual/reference/operator/aggregation/replaceWith/#mongodb-pipeline-pipe.-replaceWith)
 * @since 2.0.0
 */
const val `$replaceWith` = "\$replaceWith"

/**
 * Randomly selects the specified number of
 * documents from its input.
 *
 * [$sample](https://www.mongodb.com/docs/manual/reference/operator/aggregation/sample/#mongodb-pipeline-pipe.-sample)
 * @since 2.0.0
 */
const val `$sample` = "\$sample"

/**
 * Performs a full-text search of the field or
 * fields in an Atlas collection.
 *
 * > $search is only available for MongoDB Atlas
 * > clusters, and is not available for
 * > self-managed deployments.
 *
 * [$search](https://www.mongodb.com/docs/atlas/atlas-search/query-syntax/#mongodb-pipeline-pipe.-search)
 * @since 2.0.0
 */
const val `$search` = "\$search"

/**
 * Groups documents into windows and applies one
 * or more operators to the documents in each
 * window.
 *
 * New in version 5.0.
 *
 * [$setWindowFields](https://www.mongodb.com/docs/manual/reference/operator/aggregation/setWindowFields/#mongodb-pipeline-pipe.-setWindowFields)
 * @since 2.0.0
 */
const val `$setWindowFields` = "\$setWindowFields"

/**
 * Skips the first n documents where n is the
 * specified skip number and passes the remaining
 * documents unmodified to the pipeline. For each
 * input document, outputs either zero documents
 * (for the first n documents) or one document (if
 * after the first n documents).
 *
 * [$skip](https://www.mongodb.com/docs/manual/reference/operator/aggregation/skip/#mongodb-pipeline-pipe.-skip)
 * @since 2.0.0
 */
const val `$skip` = "\$skip"

/**
 * Groups incoming documents based on the value of
 * a specified expression, then computes the count
 * of documents in each distinct group.
 *
 * [$sortByCount](https://www.mongodb.com/docs/manual/reference/operator/aggregation/sortByCount/#mongodb-pipeline-pipe.-sortByCount)
 * @since 2.0.0
 */
const val `$sortByCount` = "\$sortByCount"

/**
 * Performs a union of two collections; i.e.
 * combines pipeline results from two collections
 * into a single result set.
 *
 * New in version 4.4.
 *
 * [$unionWith](https://www.mongodb.com/docs/manual/reference/operator/aggregation/unionWith/#mongodb-pipeline-pipe.-unionWith)
 * @since 2.0.0
 */
const val `$unionWith` = "\$unionWith"

/**
 * Deconstructs an array field from the input
 * documents to output a document for each element.
 * Each output document replaces the array with an
 * element value. For each input document, outputs
 * n documents where n is the number of array
 * elements and can be zero for an empty array.
 *
 * [$unwind](https://www.mongodb.com/docs/manual/reference/operator/aggregation/unwind/#mongodb-pipeline-pipe.-unwind)
 * @since 2.0.0
 */
const val `$unwind` = "\$unwind"

//

/**
 * Returns information on active and/or dormant
 * operations for the MongoDB deployment.
 *
 * [$currentOp](https://www.mongodb.com/docs/manual/reference/operator/aggregation/currentOp/#mongodb-pipeline-pipe.-currentOp)
 * @since 2.0.0
 */
const val `$currentOp` = "\$currentOp"

/**
 * Lists all active sessions recently in use on
 * the currently connected mongos or mongod
 * instance. These sessions may have not yet
 * propagated to the system.sessions collection.
 *
 * [$listLocalSessions](https://www.mongodb.com/docs/manual/reference/operator/aggregation/listLocalSessions/#mongodb-pipeline-pipe.-listLocalSessions)
 * @since 2.0.0
 */
const val `$listLocalSessions` = "\$listLocalSessions"

// TODO Aggregation Pipeline Operators
//  https://www.mongodb.com/docs/manual/reference/operator/aggregation/

// TODO Query Modifiers
//  https://www.mongodb.com/docs/manual/reference/operator/query-modifier/
