
```kotlin
class Something : Document {
    // these fields are all optional and just for demonstration
    var _id: Id<Something> by Document._id()

    var fantomField: String by Document.fantom()

    lateinit var lateinitField: String

    var normalField: String? = null
}

val SomethingSchema = ObjectSchema(::Something) {
    fantom("fantomField") {
        schema { StringSchema }
    }
    field(Something::lateinitField) {
        schema { StringSchema }
        mustDecode()
    }
    field(Something::normalField) {
        schema { NullableSchema(StringSchema) }
    }
}

val SomethingModel = Model("Something", SomethingSchema)

suspend fun main() {
    Monkt.connect("localhost", "MyDatabase")
    Monkt.init()
}
```
