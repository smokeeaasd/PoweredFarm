package dev.lucas.poweredFarm.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object Bags : IntIdTable() {
    val user = reference("user", Users)
    val crop = reference("crop", Crops)
    val amount: Column<Int> = integer("amount")
    val limit: Column<Int> = integer("limit")
}