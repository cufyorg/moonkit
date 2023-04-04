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
package org.cufy.bson.internal

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.cufy.bson.Id

/**
 * The serializer for [Id].
 *
 * @author LSafer
 * @since 2.0.0
 */
internal object IdSerializer : KSerializer<Id<*>> {
    override val descriptor = String.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Id<*>) {
        encoder.encodeInline(descriptor).encodeString(value.value)
    }

    override fun deserialize(decoder: Decoder): Id<*> {
        return Id<Any?>(decoder.decodeInline(descriptor).decodeString())
    }
}
