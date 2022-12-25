package org.cufy.monkt.test

import kotlinx.coroutines.runBlocking
import org.cufy.bson.bnull
import org.cufy.bson.bstring
import org.cufy.monkt.*
import org.cufy.monkt.schema.*
import org.cufy.monkt.schema.extension.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*
import java.util.UUID.randomUUID
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExampleTest {
    lateinit var monkt: Monkt

    @BeforeEach
    fun setup() {
        runBlocking {
            val uri = "mongodb://localhost"
            val name = "monkt-example-test-${randomUUID()}"
            monkt = Monkt()
            monkt.connect(uri, name)
        }
    }

    @AfterEach
    fun cleanup() {
        runBlocking {
            monkt.database.dropSuspend()
            monkt.shutdown()
        }
    }

    @Test
    fun `unsetIfNull - do unset nulls and only nulls`() {
        class Document1 : Document {
            var value: String? = null
        }

        val Model1: Model<Document1> = Model("Document1", ::Document1) {
            field(Document1::value) {
                schema { NullableSchema(StringSchema) }
                unsetIfNull()
            }
        }

        monkt += Model1

        runBlocking {
            monkt.init()

            Model1.create({ Document1::value by "Something" })

            val instance = Model1.find().single()

            assertEquals("Something", instance.value)

            instance.value = null

            instance.save()

            val source = Model1.collection.findSuspend().single()

            assertEquals(setOf("_id"), source.keys)
        }
    }

    @Test
    fun `map - mapping to different nullable types with coercers`() {
        class Document1 : Document {
            lateinit var value: String
        }

        val Model1: Model<Document1> = Model("Document1", ::Document1) {
            field(Document1::value) {
                map {
                    schema { NullableSchema(Int32Schema) }
                    coercer(StandardInt32Coercer)
                    decodeMapper { it?.toString() ?: "" }
                    encodeMapper { it.toIntOrNull() }
                }
                requireDecode()
            }
        }

        monkt += Model1

        runBlocking {
            monkt.init()

            Model1.create({ Document1::value by "33" })

            val instance = Model1.find().single()

            assertEquals("33", instance.value)

            instance.value = "Something"

            instance.save()

            val source = Model1.collection.findSuspend().single()

            assertEquals(setOf("_id", "value"), source.keys)
            assertEquals(source["value"], bnull)
        }
    }

    @Test
    fun `immutable - should ignore updates when not new`() {
        class Document1 : Document {
            var value: String by Document.fantom()
        }

        val Model1: Model<Document1> = Model("Document1", ::Document1) {
            fantom("value") {
                schema { StringSchema }
                immutable()
            }
        }

        monkt += Model1

        runBlocking {
            monkt.init()

            val instance = Model1()

            instance.value = "Alpha"

            instance.save()

            val source0 = Model1.collection.findOneSuspend({ "_id" by instance._id })!!

            assertEquals(bstring("Alpha"), source0["value"])

            instance.value = "Beta"

            instance.save()

            val source1 = Model1.collection.findOneSuspend({ "_id" by instance._id })!!

            assertEquals(bstring("Alpha"), source1["value"])
        }
    }
}
