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
package org.cufy.monkt.internal

import org.cufy.bson.BsonDocument
import org.cufy.bson.Pathname
import org.cufy.monkt.*
import org.cufy.monkt.schema.*

@InternalMonktApi
interface Mappers<M, N> {
    val encode: Mapper<M, N>
    val decode: Mapper<N, M>
    val encodeBson: BsonMapper<M, N>
    val decodeBson: BsonMapper<N, M>
    val opposite: Mappers<N, M>
}

@InternalMonktApi
fun <M, N> Mappers(
    encodeMapper: Mapper<M, N>,
    decodeMapper: Mapper<N, M>,
    bsonEncodeMapper: BsonMapper<M, N>,
    bsonDecodeMapper: BsonMapper<N, M>
): Mappers<M, N> {
    return object : Mappers<M, N> {
        val original = this
        override val encode: Mapper<M, N> = encodeMapper
        override val decode: Mapper<N, M> = decodeMapper
        override val encodeBson: BsonMapper<M, N> = bsonEncodeMapper
        override val decodeBson: BsonMapper<N, M> = bsonDecodeMapper
        override val opposite: Mappers<N, M> by lazy {
            object : Mappers<N, M> {
                override val encode: Mapper<N, M> = decodeMapper
                override val decode: Mapper<M, N> = encodeMapper
                override val encodeBson: BsonMapper<N, M> = bsonDecodeMapper
                override val decodeBson: BsonMapper<M, N> = bsonEncodeMapper
                override val opposite: Mappers<M, N> = original
            }
        }
    }
}

@OptIn(AdvancedMonktApi::class)
@InternalMonktApi
fun <T : Any, M, N> mapFieldDefinition(
    definition: FieldDefinition<T, N>,
    schema: Lazy<Schema<M>>,
    mappers: Mappers<M, N>
): FieldDefinition<T, M> {
    return object : FieldDefinition<T, M> {
        override val name = definition.name
        override val schema by schema

        override val getter: FieldDefinitionGetter<T, M> =
            mapFieldDefinitionGetter(definition.getter, mappers)

        override val setter: FieldDefinitionSetter<T, M> =
            mapFieldDefinitionSetter(definition.setter, mappers)

        override fun obtainStaticOptions(model: Model<*>, pathname: Pathname, dejaVu: Set<Schema<*>>): List<OptionData<Unit, Unit, *>> {
            return definition.obtainStaticOptions(model, pathname, dejaVu)
        }

        override fun obtainOptions(model: Model<*>, root: Any, pathname: Pathname, instance: T): List<OptionData<*, *, *>> {
            return definition.obtainOptions(model, root, pathname, instance)
        }

        override fun encode(instance: T, document: BsonDocument) {
            return definition.encode(instance, document)
        }

        override fun decode(instance: T, document: BsonDocument) {
            return definition.decode(instance, document)
        }
    }
}

@OptIn(AdvancedMonktApi::class)
@InternalMonktApi
fun <T : Any, M, N> mapOptionScope(
    scope: OptionScope<T, N, *>,
    schema: Lazy<Schema<M>>,
    mappers: Mappers<M, N>
): OptionScope<T, M, *> {
    return object : OptionScope<T, M, Any?> {
        override val model: Model<*> = scope.model
        override val root: Any = scope.root
        override val schema: Schema<M> by schema
        override val instance: T = scope.instance
        override val value: M = mappers.decode.map(scope.value)
        override val pathname: Pathname = scope.pathname
        override val configuration: Any? = scope.configuration

        override fun <I : Signal<O>, O> enqueue(signal: I): SignalProperty<O> =
            scope.enqueue(signal)

        override suspend fun wait() =
            scope.wait()
    }
}

@InternalMonktApi
fun <T : Any, M, N> mapOptionBlock(
    block: OptionBlock<T, N, *>,
    schema: Lazy<Schema<N>>?,
    mappers: Mappers<M, N>
): OptionBlock<T, M, *> {
    return {
        val scope = mapOptionScope(
            scope = this,
            schema = schema ?: lazy { mapSchema(this.schema, mappers.opposite) },
            mappers = mappers.opposite
        )

        block(scope, scope.value)
    }
}

@InternalMonktApi
fun <T : Any, M, N> mapOption(
    option: Option<T, N, *>,
    schema: Lazy<Schema<N>>?,
    mappers: Mappers<M, N>
): Option<T, M, *> {
    @Suppress("UNCHECKED_CAST")
    val block = option.block as OptionBlock<T, N, *>
    return Option(
        configuration = option.configuration,
        block = mapOptionBlock(block, schema, mappers)
    )
}

@InternalMonktApi
fun <T : Any, M, N> mapFieldDefinitionGetter(
    getter: FieldDefinitionGetter<T, N>,
    mappers: Mappers<M, N>
): FieldDefinitionGetter<T, M> {
    return { i ->
        val n = getter(i)
        mappers.decode.map(n)
    }
}

@InternalMonktApi
fun <T : Any, M, N> mapFieldDefinitionSetter(
    setter: FieldDefinitionSetter<T, N>,
    mappers: Mappers<M, N>
): FieldDefinitionSetter<T, M> {
    return { i, m ->
        val n = mappers.encode.map(m)
        setter(i, n)
    }
}

@OptIn(AdvancedMonktApi::class)
@InternalMonktApi
fun <M, N> mapDecoder(
    decoder: Decoder<out N>,
    mappers: Mappers<M, N>
): Decoder<M> {
    return Decoder { bm ->
        val bn = mappers.encodeBson.map(bm)
        val n = decoder.decode(bn)
        mappers.decode.map(n)
    }
}

@InternalMonktApi
fun <T : Any, M, N> mapFieldDefinitionCodecScope(
    scope: FieldDefinitionCodecScope<T, N>,
    schema: Lazy<Schema<M>>,
    mappers: Mappers<M, N>
): FieldDefinitionCodecScope<T, M> {
    return FieldDefinitionCodecScope(
        definition = mapFieldDefinition(
            definition = scope.definition,
            schema = schema,
            mappers = mappers
        ),
        instance = scope.instance,
        document = scope.document,
        value = mappers.decode.map(scope.value),
        bsonValue = mappers.decodeBson.map(scope.bsonValue)
    )
}

@InternalMonktApi
fun <T : Any, M, N> mapFieldDefinitionCodecBlock(
    blocks: List<FieldDefinitionCodecBlock<T, N>>,
    schema: Lazy<Schema<N>>?,
    mappers: Mappers<M, N>
): List<FieldDefinitionCodecBlock<T, M>> {
    if (blocks.isEmpty()) return emptyList()

    return listOf {
        val scope = mapFieldDefinitionCodecScope(
            scope = this,
            schema = schema ?: lazy { mapSchema(this.definition.schema, mappers.opposite) },
            mappers.opposite
        )

        blocks.forEach { it(scope) }
    }
}

@InternalMonktApi
fun <M, N> mapSchema(
    schema: Schema<N>,
    mappers: Mappers<M, N>
): Schema<M> {
    return MapSchema(
        schema = schema,
        encodeMapper = mappers.encode,
        decodeMapper = mappers.decode,
        bsonEncodeMapper = mappers.encodeBson,
        bsonDecodeMapper = mappers.decodeBson
    )
}

@InternalMonktApi
fun <M, N> mapCoercer(
    coercer: Coercer<out N>,
    mappers: Mappers<M, N>
): Coercer<M> {
    return MapCoercer(
        coercer = coercer,
        decodeMapper = mappers.decode,
        bsonEncodeMapper = mappers.encodeBson
    )
}
