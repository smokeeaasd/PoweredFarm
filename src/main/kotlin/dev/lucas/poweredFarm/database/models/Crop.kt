package dev.lucas.poweredFarm.database.models

import dev.lucas.poweredFarm.database.tables.Crops
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

data class Crop(val id: Int?, val type: String) : IModel<Crop> {
    companion object {
        fun find(id: Int): Crop? {
            return transaction {
                Crops.select(Crops.columns)
                    .where { Crops.id eq id }
                    .mapNotNull {
                        Crop(
                            id = it[Crops.id].value,
                            type = it[Crops.type]
                        )
                    }
                    .singleOrNull()
            }
        }

        fun create(type: String): Crop {
            return transaction {
                val insertedId = Crops.insertAndGetId {
                    it[Crops.type] = type
                }

                Crop(insertedId.value, type)
            }
        }
    }

    override fun save() {
        Crops.update({ Crops.id eq id }) {
            it[type] = type
        }
    }

    override fun delete(): Boolean {
        return transaction {
            Crops.deleteWhere { id eq this@Crop.id } > 0
        }
    }
}
