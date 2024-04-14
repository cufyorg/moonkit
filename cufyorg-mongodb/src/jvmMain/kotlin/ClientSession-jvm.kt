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

/* ============= ------------------ ============= */

actual data class ClientSession(val java: JavaClientSession) {
    override fun toString() = "ClientSession#${hashCode()}"
}

/* ============= ------------------ ============= */

/**
 * Create a new [ClientSession] instance wrapping
 * this session instance.
 *
 * @since 2.0.0
 */
val JavaClientSession.kt: ClientSession
    get() = ClientSession(this)

/* ============= ------------------ ============= */
