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

import com.mongodb.client.model.*
import com.mongodb.reactivestreams.client.ClientSession
import org.cufy.bson.*
import org.cufy.monkt.internal.*
import org.cufy.monkt.schema.extension.*

/*==================================================
================= Monkt Operations =================
==================================================*/

/* ============= ------ init ------ ============= */

/**
 * Perform monkt initialization.
 *
 * @param tweak init operation tweaks.
 * @throws IllegalStateException if already initialized.
 * @since 2.0.0
 */
@OptIn(InternalMonktApi::class)
@AdvancedMonktApi("Tweaks should be created and handled internally")
suspend fun Monkt.initImpl(
    session: ClientSession? = null,
    tweak: InitTweak
) {
    require(!this.isInitialized) { "Monkt instance already initialized" }

    val initializationTweak = tweak.initializationTweak
    val indexesTweak = tweak.indexesTweak
    val indexOptions = tweak.indexOptions

    models.forEach { it.deferredMonkt.complete(this) }

    performStaticInitialization(models, initializationTweak)

    val modelIndexesTable = performIndexes(models, indexesTweak)

    modelIndexesTable.forEach { (model, indexes) ->
        model.collection().createIndexesSuspend(indexes, session, indexOptions)
    }
}

/**
 * Perform monkt initialization.
 *
 * @param block the tweak block.
 * @throws IllegalStateException if already initialized.
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
suspend fun Monkt.init(
    session: ClientSession? = null,
    block: InitTweak.() -> Unit = {}
) {
    val tweak = InitTweak()
    tweak.apply(block)
    initImpl(session, tweak)
}

/*==================================================
================= Codec Operations =================
==================================================*/

/* ============= ----- Decode ----- ============= */

/**
 * Decode the given [documents] into instances of
 * type [T].
 *
 * This will also perform the following options:
 * - [InitializationConfiguration]
 * - [MigrationConfiguration]
 *
 * @param documents the documents to be decoded.
 * @param tweak decoding operation tweaks.
 * @return the decoded instances.
 * @since 2.0.0
 */
@AdvancedMonktApi("Tweaks should be created and handled internally")
suspend fun <T : Any> Model<T>.decodeImpl(
    documents: List<BsonDocument>,
    tweak: DecodeTweak
): List<T> {
    val isNew = tweak.isNew
    val isDeleted = tweak.isDeleted
    val initializationTweak = tweak.initializationTweak
    val migrationTweak = tweak.migrationTweak

    val instances = documents.map {
        Document.performDecoding(this, it)
    }

    instances.forEach {
        Document.setNew(it, isNew)
        Document.setDeleted(it, isDeleted)
    }

    monkt().performInitialization(instances, initializationTweak)
    monkt().performMigration(instances, migrationTweak)

    return instances
}

/**
 * Decode the given [documents] into instances of
 * type [T].
 *
 * This will also perform the following options:
 * - [InitializationConfiguration]
 * - [MigrationConfiguration]
 *
 * @param documents the documents to be decoded.
 * @param block the tweak block.
 * @return the decoded instances.
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
suspend operator fun <T : Any> Model<T>.invoke(
    documents: List<BsonDocument>,
    block: DecodeTweak.() -> Unit = {}
): List<T> {
    val tweak = DecodeTweak()
    tweak.apply(block)
    return decodeImpl(documents, tweak)
}

/**
 * Decode the given [documents] into instances of
 * type [T].
 *
 * This will also perform the following options:
 * - [InitializationConfiguration]
 * - [MigrationConfiguration]
 *
 * @param documents the documents to be decoded.
 * @param block the tweak block.
 * @return the decoded instances.
 * @since 2.0.0
 */
suspend operator fun <T : Any> Model<T>.invoke(
    vararg documents: BsonDocumentBlock,
    block: DecodeTweak.() -> Unit = {}
): List<T> {
    return this(documents.map { document(it) }, block)
}

/**
 * Decode the given [document] into an instance of
 * type [T].
 *
 * This will also perform the following options:
 * - [InitializationConfiguration]
 * - [MigrationConfiguration]
 *
 * @param document the document to be decoded.
 * @param block the tweak block.
 * @return the decoded instance.
 * @since 2.0.0
 */
suspend operator fun <T : Any> Model<T>.invoke(
    document: BsonDocument,
    block: DecodeTweak.() -> Unit = {}
): T {
    return this(listOf(document), block).single()
}

/**
 * Decode the given [document] into an instance of
 * type [T].
 *
 * This will also perform the following options:
 * - [InitializationConfiguration]
 * - [MigrationConfiguration]
 *
 * @param document the document to be decoded.
 * @param block the tweak block.
 * @return the decoded instance.
 * @since 2.0.0
 */
suspend operator fun <T : Any> Model<T>.invoke(
    document: BsonDocumentBlock = {},
    block: DecodeTweak.() -> Unit = {}
): T {
    return this(document(document), block)
}

/* ============= ----- Encode ----- ============= */

/**
 * Encode the given [instances] into documents.
 *
 * This will also perform the following options:
 * - [NormalizationConfiguration]
 * - [ValidationConfiguration]
 *
 * @param instances the instances to be encoded.
 * @param tweak encoding operation tweaks.
 * @return the encoded documents.
 * @since 2.0.0
 */
@AdvancedMonktApi("Tweaks should be created and handled internally")
suspend fun <T : Any> Monkt.encodeImpl(
    instances: List<T>,
    tweak: EncodeTweak
): List<BsonDocument> {
    val normalizationTweak = tweak.normalizationTweak
    val validationTweak = tweak.validationTweak

    normalizeImpl(instances, normalizationTweak)
    validateImpl(instances, validationTweak)

    val documents = instances.map {
        Document.performEncoding(it)
    }

    return documents
}

/**
 * Encode the given [instances] into documents.
 *
 * This will also perform the following options:
 * - [NormalizationConfiguration]
 * - [ValidationConfiguration]
 *
 * @param instances the instances to be encoded.
 * @param block the tweak block.
 * @return the encoded documents.
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
suspend fun <T : Any> Monkt.encode(
    instances: List<T>,
    block: EncodeTweak.() -> Unit = {}
): List<BsonDocument> {
    val tweak = EncodeTweak()
    tweak.apply(block)
    return encodeImpl(instances, tweak)
}

/**
 * Encode the given [instances] into documents.
 *
 * This will also perform the following options:
 * - [NormalizationConfiguration]
 * - [ValidationConfiguration]
 *
 * @param instances the instances to be encoded.
 * @param block the tweak block.
 * @return the encoded documents.
 * @since 2.0.0
 */
suspend fun <T : Any> Monkt.encode(
    vararg instances: T,
    block: EncodeTweak.() -> Unit = {}
): List<BsonDocument> {
    return encode(instances.asList(), block)
}

/**
 * Encode the given [instances] into documents.
 *
 * This will also perform the following options:
 * - [NormalizationConfiguration]
 * - [ValidationConfiguration]
 *
 * @param instances the instances to be encoded.
 * @param block the tweak block.
 * @return the encoded documents.
 * @since 2.0.0
 */
suspend fun <T : Any> Model<T>.encode(
    instances: List<T>,
    block: EncodeTweak.() -> Unit = {}
): List<BsonDocument> {
    return monkt().encode(instances, block)
}

/**
 * Encode the given [instances] into documents.
 *
 * This will also perform the following options:
 * - [NormalizationConfiguration]
 * - [ValidationConfiguration]
 *
 * @param instances the instances to be encoded.
 * @param block the tweak block.
 * @return the encoded documents.
 * @since 2.0.0
 */
suspend fun <T : Any> Model<T>.encode(
    vararg instances: T,
    block: EncodeTweak.() -> Unit = {}
): List<BsonDocument> {
    return monkt().encode(instances.asList(), block)
}

/**
 * Encode the given [instance] into a document.
 *
 * This will also perform the following options:
 * - [NormalizationConfiguration]
 * - [ValidationConfiguration]
 *
 * @param instance the instance to be encoded.
 * @param block the tweak block.
 * @return the encoded document.
 * @since 2.0.0
 */
suspend fun <T : Any> Document.Companion.encode(
    instance: T,
    block: EncodeTweak.() -> Unit = {}
): BsonDocument {
    val model = Document.getModel(instance)
    return model.encode(listOf(instance), block).single()
}

/**
 * Encode [this] instance into a document.
 *
 * This will also perform the following options:
 * - [NormalizationConfiguration]
 * - [ValidationConfiguration]
 *
 * @param block the tweak block.
 * @return the encoded document.
 * @since 2.0.0
 */
suspend fun <T : Document> T.encode(
    block: EncodeTweak.() -> Unit = {}
): BsonDocument {
    return Document.encode(this, block)
}

/*==================================================
============= Managed Collection Query =============
==================================================*/

/* ============= ---- distinct ---- ============= */

// TODO distinct

/* ============= ------ find ------ ============= */

/**
 * Finds all documents in the collection. Then,
 * decode the result into instances of type [T]
 *
 * @param filter the query filter.
 * @param session the client session with which to associate this operation.
 * @param tweak find operation tweaks.
 * @return a list of instances of [T] from the found documents.
 * @since 2.0.0
 * @see MonktCollection.findSuspend
 */
@AdvancedMonktApi("Tweaks should be created and handled internally")
suspend fun <T : Any> Model<T>.findImpl(
    filter: Bson = bdocument,
    session: ClientSession? = null,
    tweak: FindTweak
): List<T> {
    val publisherBlock = tweak.publisherBlock
    val decodeTweak = tweak.decodeTweak

    val documents = collection().findSuspend(filter, session, publisherBlock)

    return decodeImpl(documents, decodeTweak)
}

/**
 * Finds all documents in the collection. Then,
 * decode the result into instances of type [T]
 *
 * @param filter the query filter.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @return a list of instances of [T] from the found documents.
 * @since 2.0.0
 * @see MonktCollection.findSuspend
 */
@OptIn(AdvancedMonktApi::class)
suspend fun <T : Any> Model<T>.find(
    filter: Bson = bdocument,
    session: ClientSession? = null,
    block: FindTweak.() -> Unit = {}
): List<T> {
    val tweak = FindTweak()
    tweak.apply(block)
    return findImpl(filter, session, tweak)
}

/**
 * Finds all documents in the collection. Then,
 * decode the result into instances of type [T]
 *
 * @param filter the query filter.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @return a list of instances of [T] from the found documents.
 * @since 2.0.0
 * @see MonktCollection.findSuspend
 */
suspend fun <T : Any> Model<T>.find(
    filter: BsonDocumentBlock,
    session: ClientSession? = null,
    block: FindTweak.() -> Unit = {}
): List<T> {
    return find(document(filter), session, block)
}

/**
 * Finds all documents in the collection that has
 * any of the given [ids]. Then, decode the result
 * into instances of type [T]
 *
 * Note: the result will not be sorted according
 * to the given [ids] list.
 *
 * @param ids the ids to filter.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @return a list of instances of [T] from the found documents.
 * @since 2.0.0
 * @see MonktCollection.findSuspend
 */
suspend fun <T : Any> Model<T>.findById(
    ids: List<Id<T>>,
    session: ClientSession? = null,
    block: FindTweak.() -> Unit = {}
): List<T> {
    return find({ "_id" by { `$in` by ids } }, session, block)
}

/**
 * Finds all documents in the collection that has
 * any of the given [ids]. Then, decode the result
 * into instances of type [T]
 *
 * Note: the result will not be sorted according
 * to the given [ids] list.
 *
 * @param ids the ids to filter.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @return a list of instances of [T] from the found documents.
 * @since 2.0.0
 * @see MonktCollection.findSuspend
 */
suspend fun <T : Any> Model<T>.findById(
    vararg ids: Id<T>,
    session: ClientSession? = null,
    block: FindTweak.() -> Unit = {}
): List<T> {
    return findById(ids.asList(), session, block)
}

/* ============= ---- findOne  ---- ============= */

/**
 * Finds the first document in the collection. Then,
 * decode the result into an instance of type [T]
 *
 * @param filter the query filter.
 * @param session the client session with which to associate this operation.
 * @param tweak find operation tweaks.
 * @return an instance of [T] from the found document.
 *         Or `null` if no document was found.
 * @since 2.0.0
 * @see MonktCollection.findOneSuspend
 */
@AdvancedMonktApi("Tweaks should be created and handled internally")
suspend fun <T : Any> Model<T>.findOneImpl(
    filter: Bson = bdocument,
    session: ClientSession? = null,
    tweak: FindTweak
): T? {
    val publisherBlock = tweak.publisherBlock
    val decodeTweak = tweak.decodeTweak

    val document = collection().findOneSuspend(filter, session, publisherBlock)

    document ?: return null

    return decodeImpl(listOf(document), decodeTweak).single()
}

/**
 * Finds the first document in the collection. Then,
 * decode the result into an instance of type [T]
 *
 * @param filter the query filter.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @return an instance of [T] from the found document.
 *         Or `null` if no document was found.
 * @since 2.0.0
 * @see MonktCollection.findOneSuspend
 */
@OptIn(AdvancedMonktApi::class)
suspend fun <T : Any> Model<T>.findOne(
    filter: Bson = bdocument,
    session: ClientSession? = null,
    block: FindTweak.() -> Unit = {}
): T? {
    val tweak = FindTweak()
    tweak.apply(block)
    return findOneImpl(filter, session, tweak)
}

/**
 * Finds the first document in the collection. Then,
 * decode the result into an instance of type [T]
 *
 * @param filter the query filter.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @return an instance of [T] from the found document.
 *         Or `null` if no document was found.
 * @since 2.0.0
 * @see MonktCollection.findOneSuspend
 */
suspend fun <T : Any> Model<T>.findOne(
    filter: BsonDocumentBlock,
    session: ClientSession? = null,
    block: FindTweak.() -> Unit = {}
): T? {
    return findOne(document(filter), session, block)
}

/**
 * Finds the first document in the collection that
 * has any of the given [ids]. Then, decode the
 * result into an instance of type [T]
 *
 * @param ids the ids to filter.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @return an instance of [T] from the found document.
 *         Or `null` if no document was found.
 * @since 2.0.0
 * @see MonktCollection.findOneSuspend
 */
suspend fun <T : Any> Model<T>.findOneById(
    ids: List<Id<T>>,
    session: ClientSession? = null,
    block: FindTweak.() -> Unit = {}
): T? {
    return findOne({ "_id" by { `$in` by ids } }, session, block)
}

/**
 * Finds the first document in the collection that
 * has any of the given [ids]. Then, decode the
 * result into an instance of type [T]
 *
 * @param ids the ids to filter.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @return an instance of [T] from the found document.
 *         Or `null` if no document was found.
 * @since 2.0.0
 * @see MonktCollection.findOneSuspend
 */
suspend fun <T : Any> Model<T>.findOneById(
    vararg ids: Id<T>,
    session: ClientSession? = null,
    block: FindTweak.() -> Unit = {}
): T? {
    return findOneById(ids.asList(), session, block)
}

/*==================================================
=========== Managed Collection Mutation  ===========
==================================================*/

/* ============= ------ save ------ ============= */

/**
 * Insert the given [instances] to the database. If
 * any of the instances are already in the database,
 * apply the updates instead.
 *
 * This will also perform the following options:
 * - [NormalizationConfiguration]
 * - [ValidationConfiguration]
 *
 * This will also set the instances to be
 * **`not new`** and **`not deleted`**
 *
 * @param instances the instances to be saved.
 * @param session the client session with which to associate this operation.
 * @param tweak the save operation tweaks.
 * @since 2.0.0
 */
@AdvancedMonktApi("Tweaks should be created and handled internally")
suspend fun <T : Any> Monkt.saveImpl(
    instances: List<T>,
    session: ClientSession? = null,
    tweak: SaveTweak
) {
    val encodeTweak = tweak.encodeTweak
    val writesTweak = tweak.writesTweak
    val updateOptions = tweak.updateOptions
    val bulkWriteOptions = tweak.bulkWriteOptions

    val documents = encodeImpl(instances, encodeTweak)

    val modelDocumentTable = (instances zip documents)
        .groupBy { (instance, _) -> Document.getModel(instance) }
        .mapValues { (_, value) ->
            value.map { (_, document) -> document }
        }

    val modelWritesTable = performWrites(instances, writesTweak)

    modelDocumentTable.forEach { (model, documents) ->
        val writes = modelWritesTable[model] ?: emptyList()

        val updateWrites = documents.map { document ->
            val filter = document { "_id" by document["_id"] }
            val update = document { `$set` by document }

            UpdateOneModel<BsonDocument>(filter, update, updateOptions)
        }

        model.collection().bulkWriteSuspend(
            updateWrites + writes,
            session,
            bulkWriteOptions
        )
    }

    instances.forEach {
        Document.setNew(it, false)
        Document.setDeleted(it, false)
    }
}

/**
 * Insert the given [instances] to the database. If
 * any of the instances are already in the database,
 * apply the updates instead.
 *
 * This will also perform the following options:
 * - [NormalizationConfiguration]
 * - [ValidationConfiguration]
 *
 * This will also set the instances to be
 * **`not new`** and **`not deleted`**
 *
 * @param instances the instances to be saved.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
suspend fun <T : Any> Monkt.save(
    instances: List<T>,
    session: ClientSession? = null,
    block: SaveTweak.() -> Unit = {}
) {
    val tweak = SaveTweak()
    tweak.apply(block)
    saveImpl(instances, session, tweak)
}

/**
 * Insert the given [instances] to the database. If
 * any of the instances are already in the database,
 * apply the updates instead.
 *
 * This will also perform the following options:
 * - [NormalizationConfiguration]
 * - [ValidationConfiguration]
 *
 * This will also set the instances to be
 * **`not new`** and **`not deleted`**
 *
 * @param instances the instances to be saved.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @since 2.0.0
 */
suspend fun <T : Any> Monkt.save(
    vararg instances: T,
    session: ClientSession? = null,
    block: SaveTweak.() -> Unit = {}
) {
    save(instances.asList(), session, block)
}

/**
 * Insert the given [instances] to the database. If
 * any of the instances are already in the database,
 * apply the updates instead.
 *
 * This will also perform the following options:
 * - [NormalizationConfiguration]
 * - [ValidationConfiguration]
 *
 * This will also set the instances to be
 * **`not new`** and **`not deleted`**
 *
 * @param instances the instances to be saved.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @since 2.0.0
 */
suspend fun <T : Any> Model<T>.save(
    instances: List<T>,
    session: ClientSession? = null,
    block: SaveTweak.() -> Unit = {}
) {
    monkt().save(instances, session, block)
}

/**
 * Insert the given [instances] to the database. If
 * any of the instances are already in the database,
 * apply the updates instead.
 *
 * This will also perform the following options:
 * - [NormalizationConfiguration]
 * - [ValidationConfiguration]
 *
 * This will also set the instances to be
 * **`not new`** and **`not deleted`**
 *
 * @param instances the instances to be saved.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @since 2.0.0
 */
suspend fun <T : Any> Model<T>.save(
    vararg instances: T,
    session: ClientSession? = null,
    block: SaveTweak.() -> Unit = {}
) {
    monkt().save(instances.asList(), session, block)
}

/**
 * Insert the given [instance] to the database. If
 * any of the instances are already in the database,
 * apply the updates instead.
 *
 * This will also perform the following options:
 * - [NormalizationConfiguration]
 * - [ValidationConfiguration]
 *
 * This will also set the instance to be
 * **`not new`** and **`not deleted`**
 *
 * @param instance the instance to be saved.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @since 2.0.0
 */
suspend fun <T : Any> Document.Companion.save(
    instance: T,
    session: ClientSession? = null,
    block: SaveTweak.() -> Unit = {}
) {
    val model = Document.getModel(instance)
    model.save(listOf(instance), session, block)
}

/**
 * Insert the [this] instance to the database. If
 * any of the instances are already in the database,
 * apply the updates instead.
 *
 * This will also perform the following options:
 * - [NormalizationConfiguration]
 * - [ValidationConfiguration]
 *
 * This will also set the instance to be
 * **`not new`** and **`not deleted`**
 *
 * @param session the client session with which to associate this operation
 * @param block the tweak block.
 * @since 2.0.0
 */
suspend fun <T : Document> T.save(
    session: ClientSession? = null,
    block: SaveTweak.() -> Unit = {}
) {
    Document.save(this, session, block)
}

/* ============= ----- create ----- ============= */

/**
 * Create new instances of type [T]. Insert them
 * to the database. Then, return the instances.
 *
 * This is equivalent to:
 * ```kotlin
 * Model(documents).also { save(it) }
 * ```
 *
 * @param documents the documents to be decoded.
 * @param session the client session with which to associate this operation.
 * @param tweak create operation tweak.
 * @return the decoded instances.
 * @since 2.0.0
 */
@AdvancedMonktApi("Tweaks should be created and handled internally")
suspend fun <T : Any> Model<T>.createImpl(
    documents: List<BsonDocument>,
    session: ClientSession? = null,
    tweak: CreateTweak
): List<T> {
    val decodeTweak = tweak.decodeTweak
    val saveTweak = tweak.saveTweak

    val instances = decodeImpl(documents, decodeTweak)
    monkt().saveImpl(instances, session, saveTweak)
    return instances
}

/**
 * Create new instances of type [T]. Insert them
 * to the database. Then, return the instances.
 *
 * This is equivalent to:
 * ```kotlin
 * Model(documents).also { save(it) }
 * ```
 *
 * @param documents the documents to be decoded.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @return the decoded instances.
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
suspend fun <T : Any> Model<T>.create(
    documents: List<BsonDocument>,
    session: ClientSession? = null,
    block: CreateTweak.() -> Unit = {}
): List<T> {
    val tweak = CreateTweak()
    tweak.apply(block)
    return createImpl(documents, session, tweak)
}

/**
 * Create new instances of type [T]. Insert them
 * to the database. Then, return the instances.
 *
 * This is equivalent to:
 * ```kotlin
 * Model(documents).also { save(it) }
 * ```
 *
 * @param documents the documents to be decoded.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @return the decoded instances.
 * @since 2.0.0
 */
suspend fun <T : Any> Model<T>.create(
    vararg documents: BsonDocumentBlock,
    session: ClientSession? = null,
    block: CreateTweak.() -> Unit = {}
): List<T> {
    return create(documents.map { document(it) }, session, block)
}

/**
 * Create new instance of type [T]. Insert it
 * to the database. Then, return it.
 *
 * This is equivalent to:
 * ```kotlin
 * Model(document).also { save(it) }
 * ```
 *
 * @param document the document to be decoded.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @return the decoded instance.
 * @since 2.0.0
 */
suspend fun <T : Any> Model<T>.create(
    document: BsonDocument,
    session: ClientSession? = null,
    block: CreateTweak.() -> Unit = {}
): T {
    return create(listOf(document), session, block)
        .single()
}

/**
 * Create new instance of type [T]. Insert it
 * to the database. Then, return it.
 *
 * This is equivalent to:
 * ```kotlin
 * Model(document).also { save(it) }
 * ```
 *
 * @param document the document to be decoded.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @return the decoded instance.
 * @since 2.0.0
 */
suspend fun <T : Any> Model<T>.create(
    document: BsonDocumentBlock,
    session: ClientSession? = null,
    block: CreateTweak.() -> Unit = {}
): T {
    return create(document(document), session, block)
}

/* ============= ----- delete ----- ============= */

/**
 * Delete the given [instances] from the database.
 * The actual documents deleted will be the
 * documents with the same `_id` as the instances.
 *
 * This will also perform the following options:
 * - [DeletionConfiguration]
 *
 * This will also set the instances to be
 * **`not new`** and **`deleted`**
 *
 * @param instances the instances to be deleted.
 * @param session the client session with which to associate this operation.
 * @param tweak the delete operation tweaks.
 * @since 2.0.0
 */
@AdvancedMonktApi("Tweaks should be created and handled internally")
suspend fun <T : Any> Monkt.deleteImpl(
    instances: List<T>,
    session: ClientSession? = null,
    tweak: DeleteTweak
) {
    val deletionTweak = tweak.deletionTweak
    val deleteOptions = tweak.deleteOptions

    val toDelete = performDeletion(instances, deletionTweak)

    val modelIdMap = toDelete
        .groupBy { Document.getModel(it) }
        .mapValues { (_, value) ->
            value.map { instance ->
                Document.getId(instance)
            }
        }

    modelIdMap.forEach { (model, ids) ->
        val filter = document { "_id" by { `$in` by ids } }

        model.collection().deleteManySuspend(filter, session, deleteOptions)
    }

    toDelete.forEach {
        Document.setNew(it, false)
        Document.setDeleted(it, true)
    }
}

/**
 * Delete the given [instances] from the database.
 * The actual documents deleted will be the
 * documents with the same `_id` as the instances.
 *
 * This will also perform the following options:
 * - [DeletionConfiguration]
 *
 * This will also set the instances to be
 * **`not new`** and **`deleted`**
 *
 * @param instances the instances to be deleted.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
suspend fun <T : Any> Monkt.delete(
    instances: List<T>,
    session: ClientSession? = null,
    block: DeleteTweak.() -> Unit = {}
) {
    val tweak = DeleteTweak()
    tweak.apply(block)
    deleteImpl(instances, session, tweak)
}

/**
 * Delete the given [instances] from the database.
 * The actual documents deleted will be the
 * documents with the same `_id` as the instances.
 *
 * This will also perform the following options:
 * - [DeletionConfiguration]
 *
 * This will also set the instances to be
 * **`not new`** and **`deleted`**
 *
 * @param instances the instances to be deleted.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block
 * @since 2.0.0
 */
suspend fun <T : Any> Monkt.delete(
    vararg instances: T,
    session: ClientSession? = null,
    block: DeleteTweak.() -> Unit = {}
) {
    delete(instances.asList(), session, block)
}

/**
 * Delete the given [instances] from the database.
 * The actual documents deleted will be the
 * documents with the same `_id` as the instances.
 *
 * This will also perform the following options:
 * - [DeletionConfiguration]
 *
 * This will also set the instances to be
 * **`not new`** and **`deleted`**
 *
 * @param instances the instances to be deleted.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block
 * @since 2.0.0
 */
suspend fun <T : Any> Model<T>.delete(
    instances: List<T>,
    session: ClientSession? = null,
    block: DeleteTweak.() -> Unit = {}
) {
    monkt().delete(instances, session, block)
}

/**
 * Delete the given [instances] from the database.
 * The actual documents deleted will be the
 * documents with the same `_id` as the instances.
 *
 * This will also perform the following options:
 * - [DeletionConfiguration]
 *
 * This will also set the instances to be
 * **`not new`** and **`deleted`**
 *
 * @param instances the instances to be deleted.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block
 * @since 2.0.0
 */
suspend fun <T : Any> Model<T>.delete(
    vararg instances: T,
    session: ClientSession? = null,
    block: DeleteTweak.() -> Unit = {}
) {
    monkt().delete(instances.asList(), session, block)
}

/**
 * Delete the given [instance] from the database.
 * The actual document deleted will be the
 * document with the same `_id` as the instance.
 *
 * This will also perform the following options:
 * - [DeletionConfiguration]
 *
 * This will also set the instance to be
 * **`not new`** and **`deleted`**
 *
 * @param instance the instance to be deleted.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @since 2.0.0
 */
suspend fun <T : Any> Document.Companion.delete(
    instance: T,
    session: ClientSession? = null,
    block: DeleteTweak.() -> Unit = {}
) {
    val model = Document.getModel(instance)
    model.delete(listOf(instance), session, block)
}

/**
 * Delete the [this] instance from the database.
 * The actual document deleted will be the
 * document with the same `_id` as the instance.
 *
 * This will also perform the following options:
 * - [DeletionConfiguration]
 *
 * This will also set the instance to be
 * **`not new`** and **`deleted`**
 *
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @since 2.0.0
 */
suspend fun <T : Document> T.delete(
    session: ClientSession? = null,
    block: DeleteTweak.() -> Unit = {}
) {
    Document.delete(this, session, block)
}

/* ============= --- deleteOne  --- ============= */

/**
 * Delete the first document matching the given
 * [filter].
 *
 * If no matching document was find, the deletion
 * is skipped.
 *
 * This will first get the document, decode it
 * into an instance of [T]. Then, delete it.
 * This way, all the deletion options will be
 * triggered, too.
 *
 * This is equivalent to doing:
 * ```kotlin
 * Model.findOne(filter)?.also { delete(it) }
 * ```
 *
 * @param filter the deletion filter.
 * @param session the client session with which to associate this operation.
 * @param tweak the find-and-delete operation tweak.
 * @return the deleted instance.
 *         Or `null` if no document was found.
 * @since 2.0.0
 */
@AdvancedMonktApi("Tweaks should be created and handled internally")
suspend fun <T : Any> Model<T>.deleteOneImpl(
    filter: Bson,
    session: ClientSession? = null,
    tweak: FindAndDeleteTweak
): T? {
    val findTweak = tweak.findTweak
    val deleteTweak = tweak.deleteTweak

    val instance = findOneImpl(filter, session, findTweak)

    instance ?: return null

    monkt().deleteImpl(listOf(instance), session, deleteTweak)

    return instance
}

/**
 * Delete the first document matching the given
 * [filter].
 *
 * If no matching document was find, the deletion
 * is skipped.
 *
 * This will first get the document, decode it
 * into an instance of [T]. Then, delete it.
 * This way, all the deletion options will be
 * triggered, too.
 *
 * This is equivalent to doing:
 * ```kotlin
 * Model.findOne(filter)?.also { delete(it) }
 * ```
 *
 * @param filter the deletion filter.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @return the deleted instance.
 *         Or `null` if no document was found.
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
suspend fun <T : Any> Model<T>.deleteOne(
    filter: Bson,
    session: ClientSession? = null,
    block: FindAndDeleteTweak.() -> Unit = {}
): T? {
    val tweak = FindAndDeleteTweak()
    tweak.apply(block)
    return deleteOneImpl(filter, session, tweak)
}

/**
 * Delete the first document matching the given
 * [filter].
 *
 * If no matching document was find, the deletion
 * is skipped.
 *
 * This will first get the document, decode it
 * into an instance of [T]. Then, delete it.
 * This way, all the deletion options will be
 * triggered, too.
 *
 * This is equivalent to doing:
 * ```kotlin
 * Model.findOne(filter)?.also { delete(it) }
 * ```
 *
 * @param filter the deletion filter.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @return the deleted instance.
 *         Or `null` if no document was found.
 * @since 2.0.0
 */
suspend fun <T : Any> Model<T>.deleteOne(
    filter: BsonDocumentBlock,
    session: ClientSession? = null,
    block: FindAndDeleteTweak.() -> Unit = {}
): T? {
    return deleteOne(document(filter), session, block)
}

/**
 * Delete the first document with any of the given
 * [ids].
 *
 * If no matching document was find, the deletion
 * is skipped.
 *
 * This will first get the document, decode it
 * into an instance of [T]. Then, delete it.
 * This way, all the deletion options will be
 * triggered, too.
 *
 * This is equivalent to doing:
 * ```kotlin
 * Model.findOne(filter)?.also { delete(it) }
 * ```
 *
 * @param ids the ids to filter.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @return the deleted instance.
 *         Or `null` if no document was found.
 * @since 2.0.0
 */
suspend fun <T : Any> Model<T>.deleteOneById(
    ids: List<Id<T>>,
    session: ClientSession? = null,
    block: FindAndDeleteTweak.() -> Unit = {}
): T? {
    return deleteOne({ "_id" by { `$in` by ids } }, session, block)
}

/**
 * Delete the first document with any of the given
 * [ids].
 *
 * If no matching document was find, the deletion
 * is skipped.
 *
 * This will first get the document, decode it
 * into an instance of [T]. Then, delete it.
 * This way, all the deletion options will be
 * triggered, too.
 *
 * This is equivalent to doing:
 * ```kotlin
 * Model.findOne(filter)?.also { delete(it) }
 * ```
 *
 * @param ids the ids to filter.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @return the deleted instance.
 *         Or `null` if no document was found.
 * @since 2.0.0
 */
suspend fun <T : Any> Model<T>.deleteOneById(
    vararg ids: Id<T>,
    session: ClientSession? = null,
    block: FindAndDeleteTweak.() -> Unit = {}
): T? {
    return deleteOneById(ids.asList(), session, block)
}

/* ============= --- deleteMany --- ============= */

/**
 * Delete all the documents matching the given
 * [filter].
 *
 * This will first get the documents, decode them
 * into instances of [T]. Then, delete then.
 * This way, all the deletion options will be
 * triggered, too.
 *
 * This is equivalent to doing:
 * ```kotlin
 * Model.find(filter).also { delete(it) }
 * ```
 *
 * @param filter the deletion filter.
 * @param session the client session with which to associate this operation.
 * @param tweak the find-and-delete operation tweak.
 * @return the deleted instances.
 * @since 2.0.0
 */
@AdvancedMonktApi("Tweaks should be created and handled internally")
suspend fun <T : Any> Model<T>.deleteManyImpl(
    filter: Bson,
    session: ClientSession? = null,
    tweak: FindAndDeleteTweak
): List<T> {
    val findTweak = tweak.findTweak
    val deleteTweak = tweak.deleteTweak

    val instances = findImpl(filter, session, findTweak)

    monkt().deleteImpl(instances, session, deleteTweak)

    return instances
}

/**
 * Delete all the documents matching the given
 * [filter].
 *
 * This will first get the documents, decode them
 * into instances of [T]. Then, delete then.
 * This way, all the deletion options will be
 * triggered, too.
 *
 * This is equivalent to doing:
 * ```kotlin
 * Model.find(filter).also { delete(it) }
 * ```
 *
 * @param filter the deletion filter.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @return the deleted instances.
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
suspend fun <T : Any> Model<T>.deleteMany(
    filter: Bson,
    session: ClientSession? = null,
    block: FindAndDeleteTweak.() -> Unit = {}
): List<T> {
    val tweak = FindAndDeleteTweak()
    tweak.apply(block)
    return deleteManyImpl(filter, session, tweak)
}

/**
 * Delete all the documents matching the given
 * [filter].
 *
 * This will first get the documents, decode them
 * into instances of [T]. Then, delete then.
 * This way, all the deletion options will be
 * triggered, too.
 *
 * This is equivalent to doing:
 * ```kotlin
 * Model.find(filter).also { delete(it) }
 * ```
 *
 * @param filter the deletion filter.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @return the deleted instances.
 * @since 2.0.0
 */
suspend fun <T : Any> Model<T>.deleteMany(
    filter: BsonDocumentBlock,
    session: ClientSession? = null,
    block: FindAndDeleteTweak.() -> Unit = {}
): List<T> {
    return deleteMany(document(filter), session, block)
}

/**
 * Delete all the documents that has any of the
 * given [ids].
 *
 * This will first get the documents, decode them
 * into instances of [T]. Then, delete then.
 * This way, all the deletion options will be
 * triggered, too.
 *
 * This is equivalent to doing:
 * ```kotlin
 * Model.find(filter).also { delete(it) }
 * ```
 *
 * @param ids the ids to filter.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @return the deleted instances.
 * @since 2.0.0
 */
suspend fun <T : Any> Model<T>.deleteManyById(
    ids: List<Id<T>>,
    session: ClientSession? = null,
    block: FindAndDeleteTweak.() -> Unit = {}
): List<T> {
    return deleteMany({ "_id" by { `$in` by ids } }, session, block)
}

/**
 * Delete all the documents that has any of the
 * given [ids].
 *
 * This will first get the documents, decode them
 * into instances of [T]. Then, delete then.
 * This way, all the deletion options will be
 * triggered, too.
 *
 * This is equivalent to doing:
 * ```kotlin
 * Model.find(filter).also { delete(it) }
 * ```
 *
 * @param ids the ids to filter.
 * @param session the client session with which to associate this operation.
 * @param block the tweak block.
 * @return the deleted instances.
 * @since 2.0.0
 */
suspend fun <T : Any> Model<T>.deleteManyById(
    vararg ids: Id<T>,
    session: ClientSession? = null,
    block: FindAndDeleteTweak.() -> Unit = {}
): List<T> {
    return deleteManyById(ids.asList(), session, block)
}

/*==================================================
=============== Options Performance  ===============
==================================================*/

/* ============= -- validateSafe -- ============= */

/**
 * Perform the validation options on the given
 * [instances] and return the errors.
 *
 * @param instances the instances to be validated.
 * @param tweak the validation performance tweaks.
 * @return the validation errors.
 * @since 2.0.0
 */
@AdvancedMonktApi("Tweaks should be created and handled internally")
suspend fun <T : Any> Monkt.validateSafeImpl(
    instances: List<T>,
    tweak: ValidationTweak
): List<Throwable> {
    return performValidation(instances, tweak)
}

/**
 * Perform the validation options on the given
 * [instances] and return the errors.
 *
 * @param instances the instances to be validated.
 * @param block the tweak block.
 * @return the validation errors.
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
suspend fun <T : Any> Monkt.validateSafe(
    instances: List<T>,
    block: ValidationTweak.() -> Unit = {}
): List<Throwable> {
    val tweak = ValidationTweak()
    tweak.apply(block)
    return validateSafeImpl(instances, tweak)
}

/**
 * Perform the validation options on the given
 * [instances] and return the errors.
 *
 * @param instances the instances to be validated.
 * @param block the tweak block.
 * @return the validation errors.
 * @since 2.0.0
 */
suspend fun <T : Any> Monkt.validateSafe(
    vararg instances: T,
    block: ValidationTweak.() -> Unit = {}
): List<Throwable> {
    return validateSafe(instances.asList(), block)
}

/**
 * Perform the validation options on the given
 * [instances] and return the errors.
 *
 * @param instances the instances to be validated.
 * @param block the tweak block.
 * @return the validation errors.
 * @since 2.0.0
 */
suspend fun <T : Any> Model<T>.validateSafe(
    instances: List<T>,
    block: ValidationTweak.() -> Unit = {}
): List<Throwable> {
    return monkt().validateSafe(instances, block)
}

/**
 * Perform the validation options on the given
 * [instances] and return the errors.
 *
 * @param instances the instances to be validated.
 * @param block the tweak block.
 * @return the validation errors.
 * @since 2.0.0
 */
suspend fun <T : Any> Model<T>.validateSafe(
    vararg instances: T,
    block: ValidationTweak.() -> Unit = {}
): List<Throwable> {
    return monkt().validateSafe(instances.asList(), block)
}

/**
 * Perform the validation options on the given
 * [instance] and return the errors.
 *
 * @param instance the instance to be validated.
 * @param block the tweak block.
 * @return the validation errors.
 * @since 2.0.0
 */
suspend fun <T : Any> Document.Companion.validateSafe(
    instance: T,
    block: ValidationTweak.() -> Unit = {}
): List<Throwable> {
    val model = Document.getModel(instance)
    return model.validateSafe(listOf(instance), block)
}

/**
 * Perform the validation options on [this]
 * instance and return the errors.
 *
 * @param block the tweak block.
 * @return the validation errors.
 * @since 2.0.0
 */
suspend fun <T : Document> T.validateSafe(
    block: ValidationTweak.() -> Unit = {}
): List<Throwable> {
    return Document.validateSafe(this, block)
}

/* ============= ---- validate ---- ============= */

/**
 * Perform the validation options on the given
 * [instances] and throw the errors if any.
 *
 * @param instances the instances to be validated.
 * @param tweak the validation performance tweaks.
 * @since 2.0.0
 */
@AdvancedMonktApi("Tweaks should be created and handled internally")
suspend fun <T : Any> Monkt.validateImpl(
    instances: List<T>,
    tweak: ValidationTweak
) {
    val errors = validateSafeImpl(instances, tweak)

    if (errors.isNotEmpty()) {
        val error = errors.first()
        errors.drop(1).forEach {
            error.addSuppressed(it)
        }
        throw error
    }
}

/**
 * Perform the validation options on the given
 * [instances] and throw the errors if any.
 *
 * @param instances the instances to be validated.
 * @param block the tweak block.
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
suspend fun <T : Any> Monkt.validate(
    instances: List<T>,
    block: ValidationTweak.() -> Unit = {}
) {
    val tweak = ValidationTweak()
    tweak.apply(block)
    validateImpl(instances, tweak)
}

/**
 * Perform the validation options on the given
 * [instances] and throw the errors if any.
 *
 * @param instances the instances to be validated.
 * @param block the tweak block.
 * @since 2.0.0
 */
suspend fun <T : Any> Monkt.validate(
    vararg instances: T,
    block: ValidationTweak.() -> Unit = {}
) {
    validate(instances.asList(), block)
}

/**
 * Perform the validation options on the given
 * [instances] and throw the errors if any.
 *
 * @param instances the instances to be validated.
 * @param block the tweak block.
 * @since 2.0.0
 */
suspend fun <T : Any> Model<T>.validate(
    instances: List<T>,
    block: ValidationTweak.() -> Unit = {}
) {
    monkt().validate(instances, block)
}

/**
 * Perform the validation options on the given
 * [instances] and throw the errors if any.
 *
 * @param instances the instances to be validated.
 * @param block the tweak block.
 * @since 2.0.0
 */
suspend fun <T : Any> Model<T>.validate(
    vararg instances: T,
    block: ValidationTweak.() -> Unit = {}
) {
    monkt().validate(instances.asList(), block)
}

/**
 * Perform the validation options on the given
 * [instance] and throw the errors if any.
 *
 * @param instance the instance to be validated.
 * @param block the tweak block.
 * @since 2.0.0
 */
suspend fun <T : Any> Document.Companion.validate(
    instance: T,
    block: ValidationTweak.() -> Unit = {}
) {
    val model = Document.getModel(instance)
    model.validate(listOf(instance), block)
}

/**
 * Perform the validation options on [this]
 * instance and throw the errors if any.
 *
 * @param block the tweak block.
 * @since 2.0.0
 */
suspend fun <T : Document> T.validate(
    block: ValidationTweak.() -> Unit = {}
) {
    Document.validate(this, block)
}

/* ============= - Normalization  - ============= */

/**
 * Perform the normalization options on the given
 * [instances].
 *
 * @param instances the instances to be normalized.
 * @param tweak the validation performance tweaks.
 * @since 2.0.0
 */
@AdvancedMonktApi("Tweaks should be created and handled internally")
suspend fun <T : Any> Monkt.normalizeImpl(
    instances: List<T>,
    tweak: NormalizationTweak
) {
    performNormalization(instances, tweak)
}

/**
 * Perform the normalization options on the given
 * [instances].
 *
 * @param instances the instances to be normalized.
 * @param block the tweak block.
 * @since 2.0.0
 */
@OptIn(AdvancedMonktApi::class)
suspend fun <T : Any> Monkt.normalize(
    instances: List<T>,
    block: NormalizationTweak.() -> Unit = {}
) {
    val tweak = NormalizationTweak()
    tweak.apply(block)
    normalizeImpl(instances, tweak)
}

/**
 * Perform the normalization options on the given
 * [instances].
 *
 * @param instances the instances to be normalized.
 * @param block the tweak block.
 * @since 2.0.0
 */
suspend fun <T : Any> Monkt.normalize(
    vararg instances: T,
    block: NormalizationTweak.() -> Unit = {}
) {
    normalize(instances.asList(), block)
}

/**
 * Perform the normalization options on the given
 * [instances].
 *
 * @param instances the instances to be normalized.
 * @param block the tweak block.
 * @since 2.0.0
 */
suspend fun <T : Any> Model<T>.normalize(
    instances: List<T>,
    block: NormalizationTweak.() -> Unit = {}
) {
    monkt().normalize(instances, block)
}

/**
 * Perform the normalization options on the given
 * [instances].
 *
 * @param instances the instances to be normalized.
 * @param block the tweak block.
 * @since 2.0.0
 */
suspend fun <T : Any> Model<T>.normalize(
    vararg instances: T,
    block: NormalizationTweak.() -> Unit = {}
) {
    monkt().normalize(instances.asList(), block)
}

/**
 * Perform the normalization options on the given
 * [instance].
 *
 * @param instance the instance to be normalized.
 * @param block the tweak block.
 * @since 2.0.0
 */
suspend fun <T : Any> Document.Companion.normalize(
    instance: T,
    block: NormalizationTweak.() -> Unit = {}
) {
    val model = Document.getModel(instance)
    model.normalize(listOf(instance), block)
}

/**
 * Perform the normalization options on [this]
 * instance.
 *
 * @param block the tweak block.
 * @since 2.0.0
 */
suspend fun <T : Document> T.normalize(
    block: NormalizationTweak.() -> Unit = {}
) {
    Document.normalize(this, block)
}

/*==================================================
=============== Collection Shortcuts ===============
==================================================*/

/* ============= - estimatedCount - ============= */

/**
 * Gets an estimate of the count of documents in a
 * collection using collection metadata.
 *
 * @param block the options block.
 * @return the estimated number of documents
 * @since 2.0.0
 * @see MonktCollection.estimatedDocumentCountSuspend
 */
suspend fun <T : Any> Model<T>.estimatedCount(
    block: EstimatedDocumentCountOptionsScope.() -> Unit = {}
): Long {
    return collection().estimatedDocumentCountSuspend(block)
}

/* ============= ----- count  ----- ============= */

/**
 * Counts the number of documents in the
 * collection according to the given options.
 *
 * Note: For a fast count of the total documents
 * in a collection see [estimatedCount].
 *
 * @param session the client session with which to associate this operation
 * @param filter the query filter
 * @param block the options block.
 * @return the number of documents
 * @since 2.0.0
 * @see MonktCollection.countDocumentsSuspend
 */
suspend fun <T : Any> Model<T>.count(
    filter: Bson = bdocument,
    session: ClientSession? = null,
    block: CountOptionsScope.() -> Unit = {}
): Long {
    return collection().countDocumentsSuspend(filter, session, block)
}

/**
 * Counts the number of documents in the
 * collection according to the given options.
 *
 * Note: For a fast count of the total documents
 * in a collection see [estimatedCount].
 *
 * @param session the client session with which to associate this operation
 * @param filter the query filter
 * @param block the options block.
 * @return the number of documents
 * @since 2.0.0
 * @see MonktCollection.countDocumentsSuspend
 */
suspend fun <T : Any> Model<T>.count(
    filter: BsonDocumentBlock,
    session: ClientSession? = null,
    block: CountOptionsScope.() -> Unit = {}
): Long {
    return collection().countDocumentsSuspend(filter, session, block)
}

/**
 * Check if any document in the collection matches
 * the given [filter].
 *
 * @param session the client session with which to associate this operation
 * @param filter the query filter
 * @param block the options block.
 * @return true, if any document matches the given [filter].
 * @since 2.0.0
 * @see MonktCollection.countDocumentsSuspend
 */
suspend fun <T : Any> Model<T>.exists(
    filter: Bson = bdocument,
    session: ClientSession? = null,
    block: CountOptionsScope.() -> Unit = {}
): Boolean {
    return count(filter, session, block) > 0L
}

/**
 * Check if any document in the collection matches
 * the given [filter].
 *
 * @param session the client session with which to associate this operation
 * @param filter the query filter
 * @param block the options block.
 * @return true, if any document matches the given [filter].
 * @since 2.0.0
 * @see MonktCollection.countDocumentsSuspend
 */
suspend fun <T : Any> Model<T>.exists(
    filter: BsonDocumentBlock,
    session: ClientSession? = null,
    block: CountOptionsScope.() -> Unit = {}
): Boolean {
    return exists(document(filter), session, block)
}

/* ============= --- aggregate  --- ============= */

/**
 * Aggregates documents according to the specified
 * aggregation pipeline.
 *
 * @param session the client session with which to associate this operation
 * @param pipeline the aggregate pipeline.
 * @param tweak aggregate operation tweaks.
 * @return a list of instances of [T] from the aggregation result documents.
 * @since 2.0.0
 * @see MonktCollection.aggregateSuspend
 */
@AdvancedMonktApi("Tweaks should be created and handled internally")
suspend fun <T : Any> Model<T>.aggregateImpl(
    pipeline: List<Bson>,
    session: ClientSession? = null,
    tweak: AggregateTweak
): List<T> {
    val publisherBlock = tweak.publisherBlock
    val decodeTweak = tweak.decodeTweak

    val documents = collection().aggregateSuspend(pipeline, session, publisherBlock)

    return decodeImpl(documents, decodeTweak)
}

/**
 * Aggregates documents according to the specified
 * aggregation pipeline.
 *
 * @param session the client session with which to associate this operation
 * @param pipeline the aggregate pipeline.
 * @param block the tweak block.
 * @return a list of instances of [T] from the aggregation result documents.
 * @since 2.0.0
 * @see MonktCollection.aggregateSuspend
 */
@OptIn(AdvancedMonktApi::class)
suspend fun <T : Any> Model<T>.aggregate(
    pipeline: List<Bson>,
    session: ClientSession? = null,
    block: AggregateTweak.() -> Unit = {}
): List<T> {
    val tweak = AggregateTweak()
    tweak.apply(block)
    return aggregateImpl(pipeline, session, tweak)
}

/**
 * Aggregates documents according to the specified
 * aggregation pipeline.
 *
 * @param session the client session with which to associate this operation
 * @param pipeline the aggregate pipeline.
 * @param block the tweak block.
 * @return a list of instances of [T] from the aggregation result documents.
 * @since 2.0.0
 * @see MonktCollection.aggregateSuspend
 */
suspend fun <T : Any> Model<T>.aggregate(
    vararg pipeline: BsonDocumentBlock,
    session: ClientSession? = null,
    block: AggregateTweak.() -> Unit = {}
): List<T> {
    return aggregate(pipeline.map { document(it) }, session, block)
}

/**
 * Perform a bulk aggregation in all the
 * collections in [this] list.
 *
 * The order of the given [pipelines] must match
 * the order of the collections in [this] list.
 *
 * @param pipelines the pipelines foreach collection.
 * @param pipeline the pipeline operations to be
 *                 performed on the combined
 *                 documents.
 * @param tweak aggregate operation tweaks.
 * @return a list of instances of [T] from the aggregation result documents.
 * @since 2.0.0
 * @see MonktCollection.aggregate
 */
@AdvancedMonktApi("Tweaks should be created and handled internally")
suspend fun <T : Any> List<Model<out T>>.aggregateImpl(
    pipelines: List<List<BsonDocument>>,
    pipeline: List<BsonDocument> = emptyList(),
    session: ClientSession? = null,
    tweak: AggregateTweak
): List<T> {
    val publisherBlock = tweak.publisherBlock
    val decodeTweak = tweak.decodeTweak

    val documents = map { it.collection() }
        .aggregateSuspend(pipelines, pipeline, session, publisherBlock)

    return documents
        .mapIndexed { i, (mi, d) -> Triple(mi, i, d) }
        .groupBy { (mi, _, _) -> get(mi) }
        .mapValues { it.value.map { (_, i, d) -> i to d } }
        .flatMap { (m, idl) ->
            val (il, dl) = idl.unzip()
            m.decodeImpl(dl, decodeTweak).zip(il)
        }
        .sortedBy { (_, i) -> i }
        .map { (d, _) -> d }
}

/**
 * Perform a bulk aggregation in all the
 * collections in [this] list.
 *
 * The order of the given [pipelines] must match
 * the order of the collections in [this] list.
 *
 * @param pipelines the pipelines foreach collection.
 * @param pipeline the pipeline operations to be
 *                 performed on the combined
 *                 documents.
 * @param block the tweak block.
 * @return a list of instances of [T] from the aggregation result documents.
 * @since 2.0.0
 * @see MonktCollection.aggregate
 */
@OptIn(AdvancedMonktApi::class)
suspend fun <T : Any> List<Model<out T>>.aggregate(
    pipelines: List<List<BsonDocument>>,
    pipeline: List<BsonDocument> = emptyList(),
    session: ClientSession? = null,
    block: AggregateTweak.() -> Unit = {}
): List<T> {
    val tweak = AggregateTweak()
    tweak.apply(block)
    return aggregateImpl(pipelines, pipeline, session, tweak)
}

/**
 * Perform a bulk aggregation in all the
 * collections in [this] list.
 *
 * The order of the given [pipelines] must match
 * the order of the collections in [this] list.
 *
 * @param pipelines the pipelines foreach collection.
 * @param pipeline the pipeline operations to be
 *                 performed on the combined
 *                 documents.
 * @param block the tweak block.
 * @return a list of instances of [T] from the aggregation result documents.
 * @since 2.0.0
 * @see MonktCollection.aggregate
 */
suspend fun <T : Any> List<Model<out T>>.aggregate(
    pipelines: List<BsonArrayBlock>,
    pipeline: BsonArrayBlock = {},
    session: ClientSession? = null,
    block: AggregateTweak.() -> Unit = {}
): List<T> {
    return aggregate(
        pipelines = pipelines.map { array(it).map { it as BsonDocument } },
        pipeline = array(pipeline).map { it as BsonDocument },
        session = session,
        block = block
    )
}

/**
 * Perform a bulk aggregation in all the
 * collections in [this] list.
 *
 * The order of the given [pipelines] must match
 * the order of the collections in [this] list.
 * It is allowed to add one additional item to be
 * the pipeline for operations to be performed on
 * the combined documents.
 *
 * @param pipelines the pipelines foreach collection
 *                 and an optional item: pipeline
 *                 operations to be performed on
 *                 the combined documents.
 * @param block the tweak block.
 * @return a list of instances of [T] from the aggregation result documents.
 * @since 2.0.0
 * @see MonktCollection.aggregate
 */
suspend fun <T : Any> List<Model<out T>>.aggregate(
    vararg pipelines: BsonArrayBlock,
    session: ClientSession? = null,
    block: AggregateTweak.() -> Unit = {}
): List<T> {
    val range = size..size + 1
    require(pipelines.size in range) {
        "List aggregation vararg pipelines size mismatch: " +
                "expected: $range ; " +
                "actual: ${pipelines.size}"
    }
    return aggregate(
        pipelines = pipelines.take(size),
        pipeline = pipelines.getOrElse(size) { {} },
        session = session,
        block = block
    )
}

/* ============= -- ensureIndex  -- ============= */

/**
 * Creates an index.
 * If the index already exists, remove the existing
 * index and recreate it.
 *
 * @param session the client session with which to associate this operation.
 * @param key an object describing the index key(s), which may not be null.
 * @param block the options block for the index.
 * @since 2.0.0
 * @see MonktCollection.dropIndex
 * @see MonktCollection.createIndex
 */
suspend fun <T : Any> Model<T>.ensureIndex(
    key: Bson,
    session: ClientSession? = null,
    block: IndexOptionsScope.() -> Unit = {}
): String {
    return collection().ensureIndexSuspend(key, session, block)
}

/**
 * Creates an index.
 * If the index already exists, remove the existing
 * index and recreate it.
 *
 * @param session the client session with which to associate this operation.
 * @param key an object describing the index key(s), which may not be null.
 * @param block the options block for the index.
 * @since 2.0.0
 * @see MonktCollection.dropIndex
 * @see MonktCollection.createIndex
 */
suspend fun <T : Any> Model<T>.ensureIndex(
    key: BsonDocumentBlock,
    session: ClientSession? = null,
    block: IndexOptionsScope.() -> Unit = {}
): String {
    return collection().ensureIndexSuspend(key, session, block)
}
