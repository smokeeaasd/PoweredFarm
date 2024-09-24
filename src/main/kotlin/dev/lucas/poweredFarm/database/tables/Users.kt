package dev.lucas.poweredFarm.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object Users : IntIdTable() {
    val uuid: Column<String> = varchar("uuid", 36).uniqueIndex()
    val experience: Column<Int> = integer("experience").default(0)
}