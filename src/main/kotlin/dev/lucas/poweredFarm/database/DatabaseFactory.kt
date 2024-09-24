package dev.lucas.poweredFarm.database

import dev.lucas.poweredFarm.database.tables.Bags
import dev.lucas.poweredFarm.database.tables.Crops
import dev.lucas.poweredFarm.database.tables.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object DatabaseFactory {
    fun init() {
        val dbFile = File("plugins/PoweredFarm/data.db")
        Database.connect("jdbc:sqlite:${dbFile.absolutePath}", driver = "org.sqlite.JDBC")

        transaction {
            SchemaUtils.create(
                Users,
                Crops,
                Bags
            )
        }
    }
}