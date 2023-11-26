# Moonkit [![](https://jitpack.io/v/org.cufy/moonkit.svg)](https://jitpack.io/#org.cufy/moonkit)

DSL Based MongoDB driver wrapper for kotlin with an optional schema system.

> Note: this README is demonstrating how to use the ORM module.
> But, the ORM module is DEPRECATED and a demonstration of using
> MOONKIT module is coming in a day called "Someday"

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
    const val moonkit_version = "TAG"
    implementation("org.cufy.kped:bson:$moonkit_version")
    implementation("org.cufy.moonkit:coroutines:$moonkit_version")
    implementation("org.cufy.moonkit:orm:$moonkit_version")
}
```

### Starting the connection

The following is how to do the monkt connection on the
default monkt instance.

```kotlin
suspend fun foo() {
    Monkt.connect("mongodb://localhost:27017", "Monkt")
    // Initialization can be done later
    thread {
        runBlocking {
            Monkt += Model1
            Monkt += Model2
            Monkt.init()
        }
    }
}
```

### Schema Definition

Monkt was made to mimic the style of mongoose as possible.
Obviously a real mongoose in kotlin is impossible to be maid
since type-unions are not a supported thing in kotlin. But,
the thing that kotlin has instead is extension functions.
So, monkt took a good advantage of that.

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

export const EntityModel = model("Entity", EntitySchema, "Entity");
```

Has the following equivalent with monkt:

```kotlin
class Entity : Document {
    var value: String? = null
    lateinit var friendId: Id<Entity>
    lateinit var list: List<String>
}

val EntitySchema = ObjectSchema(::Entity) {
    field(Entity::value) {
        schema { NullableSchema(StringSchema) }
        default { "Initialized" }
        validate { value != "Invalid" }
        immutable { value == "Immutable" }
    }
    field(Entity::friendId) {
        schema { IdSchema() }
        exists { EntityModel }
    }
    field(Entity::list) {
        schema { ArraySchema(StringSchema) }
        default { mutableListOf("FirstElement") }
    }
}

val EntityModel: Model<Entity> = Model("Entity", EntitySchema)
```

### Model Usage

The model is a tricky one, since in mongoose the model has
mongoose internal stuff with javascript specific wizardry
which is not even needed in monkt. But, still the
developer experience is the first priority here in monkt.
So, the code appears the same, but internally it is not.

For example, the following code with mongoose:

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

Has the following equivalent with monkt:

```kotlin
suspend fun useEntityModel() {
    val entity = EntityModel()
    EntityModel.create({
        Entity::value by "SomeValue"
        // equilvilant:
        "value" by "SomeValue"
    })
    EntityModel.findOne({
        Entity::value by "SomeValue"
        // equilvilant:
        "value" by "SomeValue"
    })
}
```

### Document Usage

One of the best things about mongoose is the interface
`Document` that has shortcuts for saving, deleting and
validating values without the need to know the client,
database or collection they came from.
This is one of the easiest to mimic features that was
implemented in monkt.

For example, the following with mongoose:

```typescript
async function useEntityDocument(entity: Entity) {
    await entity.validate()
    await entity.save()
    await entity.remove()
}
```

Has the following equivalent with monkt:

```kotlin
suspend fun useEntityDocument(entity: Entity) {
    entity.validate()
    entity.save()
    entity.delete()
}
```

### Static and Member functions

This feature doesn't event need to be implemented in
monkt.
The best thing about kotlin extension functions is that
anyone can write their own extension function.

The following is an example for extension functions:

```kotlin
// example virtual value
val Entity.firstElement: String?
    get() = list.firstOrNull()

// example of fantom value
val Entity.fantomValue by Document.fantom()

// example member function
suspend fun Entity.findFriend(): Entity? {
    return model.findOne({
        "_id" by friendId
    })
}

// example static function
suspend fun Model<Entity>.findByValue(value: String): Entity? {
    return findOne({
        Entity::value by value
    })
}
```
