package org.cufy.monktop

import kotlinx.coroutines.runBlocking
import org.cufy.bson.BsonDocument
import org.cufy.bson.Id
import org.cufy.codec.*
import org.cufy.mongodb.drop
import org.cufy.monop.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal
import java.util.UUID.randomUUID
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExampleTest {
    lateinit var monop: Monop

    @BeforeEach
    fun before() {
        runBlocking {
            val connectionString = "mongodb://localhost"
            val name = "monop-example-test-${randomUUID()}"
            monop = Monop(connectionString, name)
        }
    }

    @AfterEach
    fun after() {
        runBlocking {
            monop.database.drop()
        }
    }

    @Test
    fun `use simple read and write with codec`() {
        runBlocking {
            val input = TransactionFragment(
                id = Id(),
                from = "here",
                to = "there",
                value = BigDecimal("12.42")
            )

            val inputDocument = input encode TransactionFragmentCodec

            Transaction.insertOne(inputDocument)(monop)

            val outputDocument = Transaction.find()(monop).single()

            val output = outputDocument decode TransactionFragmentCodec

            assertEquals(input, output)
        }
    }
}

object Transaction : MonopCollection {
    override val name = "Transaction"

    val Id = FieldCodec("_id") { Id<Transaction>() }
    val From = FieldCodec("from") { String.Nullable }
    val To = FieldCodec("to") { String.Nullable }
    val Value = FieldCodec("value") { BigDecimal }
}

data class TransactionFragment(
    val id: Id<Transaction>,
    val from: String?,
    val to: String?,
    val value: BigDecimal
)

val TransactionFragmentCodec = Codec {
    encodeCatching { it: TransactionFragment ->
        BsonDocument {
            Transaction.Id by it.id
            Transaction.From by it.from
            Transaction.To by it.to
            Transaction.Value by it.value
        }
    }
    decodeCatching { it: BsonDocument ->
        TransactionFragment(
            id = it[Transaction.Id],
            from = it[Transaction.From],
            to = it[Transaction.To],
            value = it[Transaction.Value]
        )
    }
}
