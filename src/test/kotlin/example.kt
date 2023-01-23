package org.cufy.monkt.test

import kotlinx.coroutines.runBlocking
import org.cufy.bson.bstring
import org.cufy.monkt.*
import org.cufy.monkt.schema.*
import org.cufy.monkt.schema.extension.*
import org.junit.jupiter.api.*
import java.util.*
import java.util.UUID.randomUUID
import kotlin.test.assertEquals
import kotlin.test.assertFails

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
            monkt.database().dropSuspend()
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

            val source = Model1.collection().findSuspend().single()

            assertEquals(setOf("_id"), source.keys)
        }
    }

//    @Test
//    fun `map - mapping to different nullable types with coercers`() {
//        class Document1 : Document {
//            lateinit var value: String
//        }
//
//        val Model1: Model<Document1> = Model("Document1", ::Document1) {
//            field(Document1::value) {
//                map {
//                    schema { NullableSchema(Int32Schema) }
//                    coercer(StandardInt32Decoder)
//                    decodeMapper { it?.toString() ?: "" }
//                    encodeMapper { it.toIntOrNull() }
//                }
//                requireDecode()
//            }
//        }
//
//        monkt += Model1
//
//        runBlocking {
//            monkt.init()
//
//            Model1.create({ Document1::value by "33" })
//
//            val instance = Model1.find().single()
//
//            assertEquals("33", instance.value)
//
//            instance.value = "Something"
//
//            instance.save()
//
//            val source = Model1.collection().findSuspend().single()
//
//            assertEquals(setOf("_id", "value"), source.keys)
//            assertEquals(source["value"], bnull)
//        }
//    }

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

            val source0 = Model1.collection().findOneSuspend({ "_id" by instance._id })!!

            assertEquals(bstring("Alpha"), source0["value"])

            instance.value = "Beta"

            instance.save()

            val source1 = Model1.collection().findOneSuspend({ "_id" by instance._id })!!

            assertEquals(bstring("Alpha"), source1["value"])
        }
    }

    @Test
    fun `options - multiple levels of dependency and mapping`() {
        class Document1 : Document {
            val value: String by Document.fantom()
        }

        class Document2 : Document {
            val name: String by Document.fantom()
            val alt: String? by Document.fantom()
            var dependencies: List<Document1> by Document.fantom()
        }

        val Schema1 = ObjectSchema(::Document1) {
            fantom("name", StringSchema)
            fantom("value", StringSchema)
        }

        val Model1 = Model("Document1", Schema1)

        val Model2 = Model("Document2", ::Document2) {
            fantom("name", StringSchema)
            fantom("alt", NullableSchema(StringSchema)) {
                default { null }
            }
            fantom("dependencies", ArraySchema(Schema1)) {
                init {
                    val count by count(Model1) { "name" by instance.name }
                    val instances by find(Model1) { "name" by instance.name }

                    assertThrows<IllegalStateException> { println(count) }
                    assertThrows<IllegalStateException> { println(instances) }

                    wait()

                    assertEquals(count, instances.size.toLong())

                    instance.dependencies = instances

                    if (instance.alt != null) {
                        val instances2 by find(Model1) { "name" by instance.alt }

                        wait()

                        instance.dependencies = instances2
                    }
                }
            }
        }

        monkt += Model1
        monkt += Model2

        runBlocking {
            monkt.init()

            Model1.collection().insertManySuspend(
                { "name" by "A"; "value" by "0" },
                { "name" by "A"; "value" by "1" },
                { "name" by "B"; "value" by "2" },
                { "name" by "B"; "value" by "3" },
                { "name" by "B"; "value" by "4" },
                { "name" by "C"; "value" by "5" },
                { "name" by "C"; "value" by "6" },
                { "name" by "C"; "value" by "7" },
                { "name" by "C"; "value" by "8" },
            )

            val instances = Model2.create(
                { "name" by "A"; "alt" by "C" },
                { "name" by "B" }
            )

            assertEquals(listOf("5", "6", "7", "8"), instances[0].dependencies.map { it.value })
            assertEquals(listOf("2", "3", "4"), instances[1].dependencies.map { it.value })
        }
    }

    @Test
    fun `unique index actually works`() {
        class Document1 : Document {
            val value: String by Document.fantom()
        }

        val Schema1 = ObjectSchema(::Document1) {
            fantom("value", StringSchema) {
                unique()
            }
        }

        val Model1 = Model("Document1", Schema1)

        monkt += Model1

        runBlocking {
            monkt.init()

            assertFails {
                Model1.create(
                    { Document1::value by "a" },
                    { Document1::value by "a" },
                )
            }
        }
    }

    @Test
    fun `convenient lateinit property integration`() {
        class Document1 : Document {
            lateinit var value: String
        }

        val Schema1 = ObjectSchema(::Document1) {
            field(Document1::value) {
                schema { StringSchema }
                requireEncode()
            }
        }

        val Model1 = Model("Document1", Schema1)

        monkt += Model1

        runBlocking {
            monkt.init()

            val document1 = Model1()
            document1.value = "Hello World"
            document1.save()

            val document2 = Model1()
            assertFails { document2.save() }
        }
    }
}
