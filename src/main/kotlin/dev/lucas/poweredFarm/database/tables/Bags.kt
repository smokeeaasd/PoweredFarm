package dev.lucas.poweredFarm.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Bags : IntIdTable() {
    val user = reference("user_id", Users, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    val crop = reference("crop_id", Crops, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    val amount = integer("amount")
}