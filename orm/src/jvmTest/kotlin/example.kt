package org.cufy.monkt.test

import kotlinx.coroutines.runBlocking
import org.cufy.bson.bson
import org.cufy.mongodb.drop
import org.cufy.mongodb.find
import org.cufy.mongodb.findOne
import org.cufy.mongodb.insertMany
import org.cufy.monkt.*
import org.cufy.monkt.schema.*
import org.cufy.monkt.schema.extension.count
import org.cufy.monkt.schema.extension.find
import org.cufy.monkt.schema.extension.init
import org.cufy.monkt.schema.extension.unique
import java.util.UUID.randomUUID
import kotlin.test.*

class ExampleTest {
    lateinit var monkt: Monkt

    @BeforeTest
    fun setup() {
        runBlocking {
            val uri = "mongodb://localhost"
            val name = "monkt-example-test-${randomUUID()}"
            monkt = Monkt()
            monkt.connect(uri, name)
        }
    }

    @AfterTest
    fun cleanup() {
        runBlocking {
            monkt.database().drop()
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

            val source = Model1.collection().find().single()

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

            val source0 = Model1.collection().findOne({ "_id" by instance._id })!!

            assertEquals("Alpha".bson, source0["value"])

            instance.value = "Beta"

            instance.save()

            val source1 = Model1.collection().findOne({ "_id" by instance._id })!!

            assertEquals("Alpha".bson, source1["value"])
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

                    assertFailsWith<IllegalStateException> { println(count) }
                    assertFailsWith<IllegalStateException> { println(instances) }

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

            Model1.collection().insertMany(
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

//
//    @Test
//    fun `bulkAggregation offset and limits`() {
//        // @formatter:off
//        data class Document1(var value1: String = "") : Document
//        data class Document2(var value2: String = "") : Document
//        data class Document3(var value3: String = "") : Document
//        data class Document4(var value4: String = "") : Document
//        data class Document5(var value5: String = "") : Document
//
//        val Model1 = Model("Document1", ::Document1) { field(Document1::value1, StringSchema) }
//        val Model2 = Model("Document2", ::Document2) { field(Document2::value2, StringSchema) }
//        val Model3 = Model("Document3", ::Document3) { field(Document3::value3, StringSchema) }
//        val Model4 = Model("Document4", ::Document4) { field(Document4::value4, StringSchema) }
//        val Model5 = Model("Document5", ::Document5) { field(Document5::value5, StringSchema) }
//        // @formatter:on
//
//        val models = listOf(Model1, Model2, Model3, Model4, Model5)
//
//        runBlocking {
//            models.forEach { monkt += it }
//            monkt.init()
//
//            for (i in 10..50) {
//                models.forEachIndexed { index, model ->
//                    model.create({ "value${index + 1}" by "$i" })
//                }
//            }
//
//            val documents = aggregate(models.mapIndexed { index, model ->
//                model.collection() to {
//                    by {
//                        `$match` by {
//                            "value${index + 1}" by {
//                                `$regex` by BsonRegExp("$index$", "i")
//                            }
//                        }
//                    }
//                }
//            }, {
//                by { `$sort` by { "_id" by 1.b } }
//                by { `$skip` by 5.b }
//                by { `$limit` by 15.b }
//            }).map { (i, d) -> models[i](d) }
//
//            assertEquals(listOf(
//                "20" to Model1, "21" to Model2, "22" to Model3, "23" to Model4, "24" to Model5,
//                "30" to Model1, "31" to Model2, "32" to Model3, "33" to Model4, "34" to Model5,
//                "40" to Model1, "41" to Model2, "42" to Model3, "43" to Model4, "44" to Model5
//            ), documents.map { document ->
//                when (document) {
//                    is Document1 -> document.value1 to Model1
//                    is Document2 -> document.value2 to Model2
//                    is Document3 -> document.value3 to Model3
//                    is Document4 -> document.value4 to Model4
//                    is Document5 -> document.value5 to Model5
//                    else -> error("")
//                }
//            })
//        }
//    }
//
//    @Test
//    fun `bulkAggregation for counting`() {
//        // @formatter:off
//        data class Document1(var value1: String = "") : Document
//        data class Document2(var value2: String = "") : Document
//
//        val Model1 = Model("Document1", ::Document1) { field(Document1::value1, StringSchema) }
//        val Model2 = Model("Document2", ::Document2) { field(Document2::value2, StringSchema) }
//        // @formatter:on
//
//
//        runBlocking {
//            monkt += Model1
//            monkt += Model2
//            monkt.init()
//
//            Model1.create(List(10) { BsonDocument { "value1" by "$it" } })
//            Model2.create(List(15) { BsonDocument { "value2" by "$it" } })
//
//            val count = aggregate(
//                Model1.collection() to {
//                    by { `$match` by { "value1" by { `$regex` by BsonRegExp("^[1-7]+$") } } }
//                },
//                Model2.collection() to {
//                    by { `$match` by { "value2" by { `$regex` by BsonRegExp("^[1-3]+$") } } }
//                },
//                pipeline = {
//                    by { `$count` by "count" }
//                }
//            ).single().second["count"] as? BsonNumber
//
//            // 1..7 (7)
//            // 1..3, 11..13 (6)
//            assertEquals(13L, count?.toLong())
//        }
//    }
}
