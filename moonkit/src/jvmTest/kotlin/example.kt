package org.cufy.monktop

import kotlinx.coroutines.runBlocking
import org.cufy.bson.BsonDocument
import org.cufy.bson.Id
import org.cufy.kped.*
import org.cufy.mongodb.`$set`
import org.cufy.mongodb.drop
import org.cufy.moonkit.*
import java.math.BigDecimal
import java.util.UUID.randomUUID
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ExampleTest {
    lateinit var client: OpClient

    @BeforeTest
    fun before() {
        runBlocking {
            val connectionString = "mongodb://localhost"
            val name = "monop-example-test-${randomUUID()}"
            client = createOpClient(connectionString, name)
        }
    }

    @AfterTest
    fun after() {
        runBlocking {
            client.defaultDatabase()!!.drop()
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

            Transaction.insertOne(inputDocument)(client)

            val outputDocument = Transaction.find()(client).single()

            val output = outputDocument decode TransactionFragmentCodec

            assertEquals(input, output)
        }
    }

    @Test
    fun `use of from and into infix`() {
        runBlocking {
            val id = Id<Transaction>()

            val document1 = BsonDocument {
                Transaction.Id by id
                Transaction.Value by BigDecimal.ONE
            }

            Transaction.insertOne(document1)(client)

            val projection1 = TransactionProjection(document1)

            assertEquals(BigDecimal.ONE, projection1.value)

            Transaction.updateOneById(id, {
                `$set` by { Transaction.Value by BigDecimal.TEN }
            })(client)

            val document2 = Transaction.findOneById(id)(client)!!

            val projection2 = TransactionProjection(document2)

            assertEquals(BigDecimal.TEN, projection2.value)
        }
    }
}

object Transaction : OpCollection {
    override val name = "Transaction"

    val Id = "_id" be Bson.Id<Transaction>()
    val From = "from" be Bson.String.Nullable
    val To = "to" be Bson.String.Nullable
    val Value = "value" be Bson.BigDecimal
}

class TransactionProjection(val element: BsonDocument) {
    companion object : CodecClass<TransactionProjection, BsonDocument>({
        encodeCatching { it: TransactionProjection -> it.element }
        decodeCatching { it: BsonDocument -> TransactionProjection(it) }
    })

    val value by lazy { element[Transaction.Value] }
}

data class TransactionFragment(
    val id: Id<Transaction>,
    val from: String?,
    val to: String?,
    val value: BigDecimal,
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
