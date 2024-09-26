package dev.lucas.poweredFarm.database

import dev.lucas.poweredFarm.config.Configuration
import dev.lucas.poweredFarm.database.models.Crop

class DatabaseInitializer(private val config: Configuration) {
    fun initializeDatabase() {
        DatabaseFactory.init()
        loadCrops()
        config.saveLocale()
    }

    private fun loadCrops() {
        Crop.clear()
        Configuration.crops = config.getConfig().getList("crops")?.mapNotNull { cropData ->
            (cropData as? Map<*, *>)?.let {
                val type = it["type"] as? String ?: return@mapNotNull null
                val limit = it["limit"] as? Int ?: 0
                Crop.create(type, limit)
            }
        }?.toMutableList() ?: mutableListOf()
    }
}