package org.cufy.mangaka.test

import kotlinx.serialization.Serializable
import org.cufy.mangaka.*
import org.cufy.mangaka.Mangaka.Companion.model
import org.cufy.mangaka.bson.by
import org.cufy.mangaka.bson.document
import org.cufy.mangaka.schema.ObjectSchema
import org.cufy.mangaka.schema.extension.default
import org.cufy.mangaka.schema.extension.existsAt
import org.cufy.mangaka.schema.extension.immutable
import org.cufy.mangaka.schema.extension.validate
import org.cufy.mangaka.schema.field
import org.cufy.mangaka.schema.schema
import org.cufy.mangaka.schema.types.ArraySchema
import org.cufy.mangaka.schema.types.IdSchema
import org.cufy.mangaka.schema.types.StringSchema

@Serializable
class Entity : Document {
    var value: String? = null
    lateinit var friendId: Id<Entity>
    lateinit var list: List<String>
}

val EntitySchema = ObjectSchema(::Entity) {
    field(Entity::value) {
        schema { StringSchema }
        default { "Initialized" }
        validate { it != "Invalid" }
        immutable { it == "Immutable" }
    }
    field(Entity::friendId) {
        schema { IdSchema() }
        existsAt { "entities" }
    }
    field(Entity::list) {
        schema { ArraySchema(StringSchema) }
        default { mutableListOf("FirstElement") }
    }
}

val EntityModel = model("Entity", EntitySchema, "entities")

suspend fun useEntityModel() {
    val entity = EntityModel()
    EntityModel.create(document(
        Entity::value by "SomeValue"
    ))
    EntityModel.findOne(document(
        Entity::value by "SomeValue"
    ))
}

suspend fun useEntityDocument(entity: Entity) {
    entity.validate()
    entity.save()
    entity.remove()
}

val Entity.firstElement: String?
    get() = list.firstOrNull()

suspend fun Entity.findFriend(): Entity? {
    return model.findOne(document(
        "_id" by friendId
    ))
}

suspend fun Model<Entity>.findByValue(value: String): Entity? {
    return findOne(document(
        Entity::value by value
    ))
}
