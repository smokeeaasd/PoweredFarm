package dev.lucas.poweredFarm.database

import dev.lucas.poweredFarm.database.tables.Bags
import dev.lucas.poweredFarm.database.tables.Crops
import dev.lucas.poweredFarm.database.tables.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.sql.Connection

object DatabaseFactory {
    fun init() {
        val dbFile = File("plugins/PoweredFarm/data.db")
        Database.connect("jdbc:sqlite:${dbFile.absolutePath}?foreign_keys=on", driver = "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        transaction {
            SchemaUtils.create(
                Users,
                Crops,
                Bags
            )
        }
    }
}