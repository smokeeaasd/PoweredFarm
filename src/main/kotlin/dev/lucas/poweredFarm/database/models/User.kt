package dev.lucas.poweredFarm.database.models

import dev.lucas.poweredFarm.database.tables.Bags
import dev.lucas.poweredFarm.database.tables.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

data class User(val id: Int?, val uuid: String, val experience: Int) : IModel<User> {
    companion object {
        private fun ResultRow.toUser(): User = User(
            id = this[Users.id].value,
            uuid = this[Users.uuid],
            experience = this[Users.experience]
        )

        fun find(id: Int): User? = transaction {
            Users.selectAll().where { Users.id eq id }
                .mapNotNull { it.toUser() }
                .singleOrNull()
        }

        fun findByUUID(uuid: String): User? = transaction {
            Users.selectAll().where { Users.uuid eq uuid }
                .mapNotNull { it.toUser() }
                .singleOrNull()
        }

        fun create(uuid: String, experience: Int): User = transaction {
            val insertedId = Users.insertAndGetId {
                it[Users.uuid] = uuid
                it[Users.experience] = experience
            }
            User(insertedId.value, uuid, experience)
        }
    }

    override fun save() {
        Users.update({ Users.id eq id }) {
            it[experience] = experience
        }
    }

    override fun delete(): Boolean {
        return transaction {
            Users.deleteWhere { id eq this@User.id } > 0
        }
    }

    fun bags(): List<Bag> = transaction {
        Bags.selectAll()
            .where{ Bags.user eq this@User.id }
            .mapNotNull { row ->
                Crop.findById(row[Bags.crop].value)?.let { crop ->
                    Bag(
                        id = row[Bags.id].value,
                        user = this@User,
                        crop = crop,
                        amount = row[Bags.amount]
                    )
                }
            }
    }
}