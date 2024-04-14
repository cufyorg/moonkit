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
package org.cufy.mongodb.gridfs

typealias JavaMongoFile = com.mongodb.client.gridfs.model.GridFSFile
typealias JavaMongoBucket = com.mongodb.reactivestreams.client.gridfs.GridFSBucket

typealias JavaDownloadPublisher = com.mongodb.reactivestreams.client.gridfs.GridFSDownloadPublisher
typealias JavaBucketFindPublisher = com.mongodb.reactivestreams.client.gridfs.GridFSFindPublisher

typealias JavaUploadOptions = com.mongodb.client.gridfs.model.GridFSUploadOptions
