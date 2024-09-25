package dev.lucas.poweredFarm.database.models

import dev.lucas.poweredFarm.database.tables.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

data class User(val id: Int?, val uuid: String, val experience: Int) : IModel<User> {
    companion object {
        fun find(id: Int): User? {
            return transaction {
                Users.select(Users.columns)
                    .where { Users.id eq id }
                    .mapNotNull {
                        User(
                            id = it[Users.id].value,
                            uuid = it[Users.uuid],
                            experience = it[Users.experience]
                        )
                    }
                    .singleOrNull()
            }
        }

        fun find(uuid: String): User? {
            return transaction {
                Users.select(Users.columns)
                    .where { Users.uuid eq uuid }
                    .mapNotNull {
                        User(
                            id = it[Users.id].value,
                            uuid = it[Users.uuid],
                            experience = it[Users.experience]
                        )
                    }
                    .singleOrNull()
            }
        }

        fun create(uuid: String, experience: Int): User {
            return transaction {
                val insertedId = Users.insertAndGetId {
                    it[Users.uuid] = uuid
                    it[Users.experience] = experience
                }

                User(insertedId.value, uuid, experience)
            }
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
}