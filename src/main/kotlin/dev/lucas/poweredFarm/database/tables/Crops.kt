package dev.lucas.poweredFarm.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object Crops : IntIdTable() {
    val type = varchar("type" ,50)
    val limit: Column<Int> = integer("limit")
}
