package org.cufy.monkt.codec

import org.cufy.bson.BsonDocument
import org.cufy.bson.Id
import org.cufy.bson.java.java
import org.cufy.codec.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ExampleTest {
    @Test
    fun `common example 1`() {
        val id = Id<Document1>()
        val name = "Sulaiman"
        val age = 69

        val doc = BsonDocument {
            Document1.Id by id
            Document1.Name by name
            Document1.Age by age
        }

        val actual = doc decode Document1f1Codec

        val expected = Document1f1(
            id = id,
            name = name,
            age = age
        )

        println(doc)
        println(doc.java)
        assertEquals(expected, actual)
    }
}

object Document1 {
    val Id = FieldCodec("_id") { Id<Document1>() }
    val Name = FieldCodec("name") { String }
    val Age = FieldCodec("age") { Int32 }
}

data class Document1f1(
    val id: Id<Document1>,
    val name: String,
    val age: Int
)

val Document1f1Codec = Codec {
    encodeCatching { it: Document1f1 ->
        BsonDocument {
            Document1.Id by it.id
            Document1.Name by it.name
            Document1.Age by it.age
        }
    }
    decodeCatching { it: BsonDocument ->
        Document1f1(
            id = it[Document1.Id],
            name = it[Document1.Name],
            age = it[Document1.Age]
        )
    }
}
