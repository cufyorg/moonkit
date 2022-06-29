### Introduction

Node.js is the best when it comes to community support and
simplicity.
But, when it comes to the language itself and the builtin
features, Yuk!
I was used to a lot of libraries in Node.js and that what
held me from
going all in server-side Kotlin.
After the introduction of ktor and kgraphql, it was mongoose
the only
library left to have an alternative in kotlin!

So, I made mongoose in Kotlin 🤤

### Install

The main way of installing this library is
using `jitpack.io`

```kts
repositories {
    // ...
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // Replace TAG with the desired version
    implementation("org.cufy:mangaka:TAG")
}
```

### Starting the connection

The following is how to do the mangaka connection on the
default mangaka instance.

```kotlin
suspend fun foo() {
    Mangaka.connect("mongodb://localhost:27017", "Mangaka")
}
```

### Schema Definition

Mangaka was made to mimic the style of mongoose as possible.
Obviously a real mongoose in kotlin is impossible to be maid
since type-unions are not a supported thing in kotlin. But,
the thing that kotlin has instead is extension functions.
So, mangaka took a good advantage of that.

For example, the following with mongoose:

```typescript
export interface Entity extends Document {
    value?: string;
    friendId: ObjectId;
    list: string[];
}

export const EntitySchema = new Schema<Entity>({
    value: {
        type: SchemaTypes.String,
        default: () => "Initialized",
        validate: value => value !== "Invalid",
        immutable: value => value === "Immutable"
    },
    friendId: {
        type: SchemaTypes.ObjectId,
        ref: () => "Entity",
        exists: true // using module 'mongoose-extra-validators'
    },
    list: {
        type: [SchemaTypes.String],
        default: () => ["FirstElement"]
    }
});

export const EntityModel = model("Entity", EntitySchema, "entities");
```

Has the following equivalent with mangaka:

```kotlin
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
```

### Model Usage

The model is a tricky one, since in mongoose the model has
mongoose internal stuff with javascript specific wizardry
which is not even needed in mangaka. But, still the
developer experience the first priority here in mangaka.
So, the code appears the same but internal it is not.

For example, the following code with mangaka:

```typescript
async function useEntityModel() {
    const entity = new EntityModel()
    await EntityModel.create({
        value: "SomeValue"
    })
    await EntityModel.findOne({
        value: "SomeValue"
    })
}
```

Has the following equivalent with mangaka:

```kotlin
suspend fun useEntityModel() {
    val entity = EntityModel()
    EntityModel.create(document(
        Entity::value by "SomeValue"
    ))
    EntityModel.findOne(document(
        Entity::value by "SomeValue"
    ))
}
```

### Document Usage

One of the best things about mongoose is the interface
`Document` that has shortcuts for saving, deleting and
validating values without the need to know the client,
database or collection they came from.
This is one of the easiest to mimic features that was
implemented in mangaka.

For example, the following with mongoose:

```typescript
async function useEntityDocument(entity: Entity) {
    await entity.validate()
    await entity.save()
    await entity.remove()
}
```

Has the following equivalent with mangaka:

```kotlin
suspend fun useEntityDocument(entity: Entity) {
    entity.validate()
    entity.save()
    entity.remove()
}
```

### Static and Member functions

This feature doesn't event need to be implemented in
mangaka.
The best thing about kotlin extension functions is that
anyone can write their own extension function.

The following is an example for extension functions:

```kotlin
// example virtual value
val Entity.firstElement: String?
    get() = list.firstOrNull()

// example member function
suspend fun Entity.findFriend(): Entity? {
    return model.findOne(document(
        "_id" by friendId
    ))
}

// example static function
suspend fun Model<Entity>.findByValue(value: String): Entity? {
    return findOne(document(
        Entity::value by value
    ))
}
```
