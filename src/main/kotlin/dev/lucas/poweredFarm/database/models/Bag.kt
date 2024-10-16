package dev.lucas.poweredFarm.database.models

import dev.lucas.poweredFarm.database.tables.Bags
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

data class Bag(val id: Int?, val user: User, val crop: Crop, var amount: Int) : IModel<Bag> {
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

        fun clear() = transaction {
            Bags.deleteAll()
        }
    }

    override fun save() {
        return transaction {
            Bags.update({ Bags.id eq this@Bag.id}) {
                it[amount] = this@Bag.amount
            }
        }
    }

    override fun delete(): Boolean {
        return transaction {
            Bags.deleteWhere { user eq this@Bag.user.id } > 0
        }
    }

}
