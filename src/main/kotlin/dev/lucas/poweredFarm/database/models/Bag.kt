package dev.lucas.poweredFarm.database.models

import dev.lucas.poweredFarm.database.tables.Bags
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

data class Bag(val id: Int?, val user: User, val crop: Crop, val amount: Int) : IModel<Bag> {
    companion object {
        fun find(id: Int) {
            return transaction {
                Bags.selectAll()
                    .where { Bags.id eq id }
                    .mapNotNull {
                        val user = User.find(it[Bags.user].value)
                        val crop = Crop.findById(it[Bags.crop].value)
                        user?.let { u ->
                            crop?.let { crop ->
                                Bag(
                                    id = it[Bags.id].value,
                                    user = u,
                                    crop = crop,
                                    amount = it[Bags.amount],
                                )
                            }
                        }
                    }
                    .singleOrNull()
            }
        }

        fun create(user: User, crop: Crop, amount: Int): Bag {
            return transaction {
                val insertedId = Bags.insertAndGetId {
                    it[Bags.user] = user.id ?: error("User ID cannot be null")
                    it[Bags.crop] = crop.id ?: error("Crop ID cannot be null")
                    it[Bags.amount] = amount
                }

                Bag(insertedId.value, user, crop, amount)
            }
        }
    }

    override fun save() {
        Bags.update({ Bags.id eq id }) {
            it[amount] = this@Bag.amount
        }
    }

    override fun delete(): Boolean {
        return transaction {
            Bags.deleteWhere { user eq this@Bag.user.id } > 0
        }
    }

}
