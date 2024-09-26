package dev.lucas.poweredFarm.database.models

import dev.lucas.poweredFarm.database.tables.Crops
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

data class Crop(val id: Int?, val type: String, val limit: Int) : IModel<Crop> {
    companion object {
        private fun ResultRow.toCrop(): Crop = Crop(
            id = this[Crops.id].value,
            type = this[Crops.type],
            limit = this[Crops.limit]
        )

        fun findById(id: Int): Crop? = transaction {
            Crops.selectAll().where { Crops.id eq id }
                .mapNotNull { it.toCrop() }
                .singleOrNull()
        }

        fun findByType(type: String): Crop? = transaction {
            Crops.selectAll().where { Crops.type eq type }
                .mapNotNull { it.toCrop() }
                .singleOrNull()
        }

        fun all(): List<Crop> = transaction {
            Crops.selectAll()
                .mapNotNull { it.toCrop() }
        }

        fun create(type: String, limit: Int): Crop = transaction {
            val insertedId = Crops.insertAndGetId {
                it[Crops.type] = type
                it[Crops.limit] = limit
            }
            Crop(insertedId.value, type, limit)
        }

        fun clear() = transaction {
            Crops.deleteAll()
        }
    }

    override fun save() {
        Crops.update({ Crops.id eq id }) {
            it[Crops.type] = type
            it[Crops.limit] = limit
        }
    }

    override fun delete(): Boolean = transaction {
        Crops.deleteWhere { Crops.id eq this@Crop.id } > 0
    }
}