/*
 *	Copyright 2022-2023 cufy.org and meemer.com
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

typealias JavaClientSession = com.mongodb.reactivestreams.client.ClientSession
typealias JavaMongoClient = com.mongodb.reactivestreams.client.MongoClient
typealias JavaMongoCollection = com.mongodb.reactivestreams.client.MongoCollection<org.bson.BsonDocument>
typealias JavaMongoDatabase = com.mongodb.reactivestreams.client.MongoDatabase

typealias JavaCursorType = com.mongodb.CursorType
typealias JavaReturnDocument = com.mongodb.client.model.ReturnDocument
typealias JavaFullDocument = com.mongodb.client.model.changestream.FullDocument
typealias JavaCommitQuorum = com.mongodb.CreateIndexCommitQuorum
typealias JavaMongoNamespace = com.mongodb.MongoNamespace
typealias JavaCreateIndexModel = com.mongodb.client.model.IndexModel
typealias JavaIndexOptionDefaults = com.mongodb.client.model.IndexOptionDefaults
typealias JavaClusteredIndexOptions = com.mongodb.client.model.ClusteredIndexOptions
typealias JavaChangeStreamPreAndPostImagesOptions = com.mongodb.client.model.ChangeStreamPreAndPostImagesOptions
typealias JavaCollationCaseFirst = com.mongodb.client.model.CollationCaseFirst
typealias JavaCollationStrength = com.mongodb.client.model.CollationStrength
typealias JavaCollationAlternate = com.mongodb.client.model.CollationAlternate
typealias JavaCollationMaxVariable = com.mongodb.client.model.CollationMaxVariable
typealias JavaCollation = com.mongodb.client.model.Collation
typealias JavaReadConcern = com.mongodb.ReadConcern
typealias JavaWriteConcern = com.mongodb.WriteConcern
typealias JavaTransactionOptions = com.mongodb.TransactionOptions
typealias JavaValidationLevel = com.mongodb.client.model.ValidationLevel
typealias JavaValidationAction = com.mongodb.client.model.ValidationAction
typealias JavaValidationOptions = com.mongodb.client.model.ValidationOptions
typealias JavaTimeSeriesGranularity = com.mongodb.client.model.TimeSeriesGranularity
typealias JavaTimeSeriesOptions = com.mongodb.client.model.TimeSeriesOptions
typealias JavaWriteModel = com.mongodb.client.model.WriteModel<org.bson.BsonDocument>
typealias JavaDeleteOneModel = com.mongodb.client.model.DeleteOneModel<org.bson.BsonDocument>
typealias JavaDeleteManyModel = com.mongodb.client.model.DeleteManyModel<org.bson.BsonDocument>
typealias JavaInsertOneModel = com.mongodb.client.model.InsertOneModel<org.bson.BsonDocument>
typealias JavaReplaceOneModel = com.mongodb.client.model.ReplaceOneModel<org.bson.BsonDocument>
typealias JavaUpdateOneModel = com.mongodb.client.model.UpdateOneModel<org.bson.BsonDocument>
typealias JavaUpdateManyModel = com.mongodb.client.model.UpdateManyModel<org.bson.BsonDocument>
typealias JavaDeleteResult = com.mongodb.client.result.DeleteResult
typealias JavaInsertOneResult = com.mongodb.client.result.InsertOneResult
typealias JavaInsertManyResult = com.mongodb.client.result.InsertManyResult
typealias JavaUpdateResult = com.mongodb.client.result.UpdateResult
typealias JavaBulkWriteResult = com.mongodb.bulk.BulkWriteResult

typealias JavaDeleteOptions = com.mongodb.client.model.DeleteOptions
typealias JavaInsertOneOptions = com.mongodb.client.model.InsertOneOptions
typealias JavaInsertManyOptions = com.mongodb.client.model.InsertManyOptions
typealias JavaUpdateOptions = com.mongodb.client.model.UpdateOptions
typealias JavaReplaceOptions = com.mongodb.client.model.ReplaceOptions
typealias JavaBulkWriteOptions = com.mongodb.client.model.BulkWriteOptions
typealias JavaCountOptions = com.mongodb.client.model.CountOptions
typealias JavaEstimatedCountOptions = com.mongodb.client.model.EstimatedDocumentCountOptions
typealias JavaFindOneAndDeleteOptions = com.mongodb.client.model.FindOneAndDeleteOptions
typealias JavaFindOneAndReplaceOptions = com.mongodb.client.model.FindOneAndReplaceOptions
typealias JavaFindOneAndUpdateOptions = com.mongodb.client.model.FindOneAndUpdateOptions
typealias JavaFindPublisher = com.mongodb.reactivestreams.client.FindPublisher<org.bson.BsonDocument>
typealias JavaAggregatePublisher = com.mongodb.reactivestreams.client.AggregatePublisher<org.bson.BsonDocument>
typealias JavaDistinctPublisher = com.mongodb.reactivestreams.client.DistinctPublisher<org.bson.BsonDocument>
typealias JavaWatchPublisher = com.mongodb.reactivestreams.client.ChangeStreamPublisher<org.bson.BsonDocument>
typealias JavaListDatabasesPublisher = com.mongodb.reactivestreams.client.ListDatabasesPublisher<org.bson.BsonDocument>
typealias JavaListCollectionsPublisher = com.mongodb.reactivestreams.client.ListCollectionsPublisher<org.bson.BsonDocument>
typealias JavaListIndexesPublisher = com.mongodb.reactivestreams.client.ListIndexesPublisher<org.bson.BsonDocument>
typealias JavaCreateIndexOptions = com.mongodb.client.model.IndexOptions
typealias JavaCreateIndexesOptions = com.mongodb.client.model.CreateIndexOptions
typealias JavaDropIndexOptions = com.mongodb.client.model.DropIndexOptions
typealias JavaCreateCollectionOptions = com.mongodb.client.model.CreateCollectionOptions
typealias JavaRenameCollectionOptions = com.mongodb.client.model.RenameCollectionOptions
typealias JavaCreateViewOptions = com.mongodb.client.model.CreateViewOptions
typealias JavaClientSessionOptions = com.mongodb.ClientSessionOptions
typealias JavaTextSearchOptions = com.mongodb.client.model.TextSearchOptions
typealias JavaPushOptions = com.mongodb.client.model.PushOptions
