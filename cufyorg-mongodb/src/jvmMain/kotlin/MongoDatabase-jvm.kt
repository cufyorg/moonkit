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
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.cufy.mongodb

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.cufy.bson.BsonDocument
import org.cufy.bson.java
import org.cufy.bson.kt

/* ============= ------------------ ============= */

actual data class MongoDatabase(val java: JavaMongoDatabase) {
    override fun toString() = "MongoDatabase($name)"
}

/* ============= ------------------ ============= */

/**
 * Create a new [MongoDatabase] instance wrapping
 * this database instance.
 *
 * @since 2.0.0
 */
val JavaMongoDatabase.kt: MongoDatabase
    get() = MongoDatabase(this)

/* ============= ------------------ ============= */

actual val MongoDatabase.name: String
    get() = java.name

actual val MongoDatabase.readPreference: ReadPreference
    get() = java.readPreference

actual val MongoDatabase.writeConcern: WriteConcern
    get() = java.writeConcern.kt

actual val MongoDatabase.readConcern: ReadConcern
    get() = java.readConcern.kt

actual operator fun MongoDatabase.get(name: String): MongoCollection {
    return java.getCollection(name, org.bson.BsonDocument::class.java).kt
}

/* ============= ------------------ ============= */

actual suspend fun MongoDatabase.aggregate(
    pipeline: List<BsonDocument>,
    options: AggregateOptions,
    session: ClientSession?,
): List<BsonDocument> {
    val publisher = when (session) {
        null -> java.aggregate(pipeline.map { it.java }, org.bson.BsonDocument::class.java)
        else -> java.aggregate(session.java, pipeline.map { it.java }, org.bson.BsonDocument::class.java)
    }
    return publisher
        .apply(options)
        .asFlow()
        .toList()
        .map { it.kt }
}

/* TODO MongoDatabase.watch(pipeline, options, session) */

actual suspend fun MongoDatabase.listCollectionNames(
    session: ClientSession?,
): List<String> {
    val publisher = when (session) {
        null -> java.listCollectionNames()
        else -> java.listCollectionNames(session.java)
    }
    return publisher
        .asFlow()
        .toList()
}

actual suspend fun MongoDatabase.listCollections(
    filter: BsonDocument,
    options: ListCollectionsOptions,
    session: ClientSession?,
): List<BsonDocument> {
    val publisher = when (session) {
        null -> java.listCollections(org.bson.BsonDocument::class.java)
        else -> java.listCollections(session.java, org.bson.BsonDocument::class.java)
    }
    return publisher
        .filter(filter.java)
        .apply(options)
        .asFlow()
        .toList()
        .map { it.kt }
}

actual suspend fun MongoDatabase.runCommand(
    command: BsonDocument,
    preference: ReadPreference,
    session: ClientSession?,
): BsonDocument? {
    val publisher = when (session) {
        null -> java.runCommand(command.java, preference, org.bson.BsonDocument::class.java)
        else -> java.runCommand(session.java, command.java, preference, org.bson.BsonDocument::class.java)
    }
    return publisher.awaitFirstOrNull()?.kt
}

actual suspend fun MongoDatabase.drop(
    session: ClientSession?,
) {
    val publisher = when (session) {
        null -> java.drop()
        else -> java.drop(session.java)
    }
    publisher.awaitFirstOrNull()
}

actual suspend fun MongoDatabase.createCollection(
    name: String,
    options: CreateCollectionOptions,
    session: ClientSession?,
) {
    val publisher = when (session) {
        null -> java.createCollection(name, options.java)
        else -> java.createCollection(session.java, name, options.java)
    }
    publisher.awaitFirstOrNull()
}

actual suspend fun MongoDatabase.createView(
    name: String,
    on: String,
    pipeline: List<BsonDocument>,
    options: CreateViewOptions,
    session: ClientSession?,
) {
    val publisher = when (session) {
        null -> java.createView(name, on, pipeline.map { it.java }, options.java)
        else -> java.createView(session.java, name, on, pipeline.map { it.java }, options.java)
    }
    publisher.awaitFirstOrNull()
}

/* ============= ------------------ ============= */
