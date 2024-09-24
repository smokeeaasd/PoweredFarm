package dev.lucas.poweredFarm.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object Crops : IntIdTable() {
    val type = varchar("type" ,50)
}