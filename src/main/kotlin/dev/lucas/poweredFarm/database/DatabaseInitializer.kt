package dev.lucas.poweredFarm.database

import dev.lucas.poweredFarm.config.Configuration
import dev.lucas.poweredFarm.database.models.Crop

class DatabaseInitializer(private val config: Configuration) {
    fun initializeDatabase() {
        DatabaseFactory.init()
        syncCrops()
        config.saveLocale()
    }

    private fun syncCrops() {
        val configCrops = config.getConfig().getList("crops")?.mapNotNull { cropData ->
            (cropData as? Map<*, *>)?.let {
                val type = it["type"] as? String ?: return@mapNotNull null
                val limit = it["limit"] as? Int ?: 0
                Crop(id = null, type = type, limit = limit)
            }
        } ?: emptyList()

        val dbCrops = Crop.all()

        val cropsToAdd = configCrops.filter { configCrop ->
            dbCrops.none { it.type == configCrop.type }
        }

        val cropsToUpdate = configCrops.filter { configCrop ->
            dbCrops.any { it.type == configCrop.type && it.limit != configCrop.limit }
        }

        val cropsToRemove = dbCrops.filter { dbCrop ->
            configCrops.none { it.type == dbCrop.type }
        }

        // Add new crops
        cropsToAdd.forEach { crop ->
            Crop.create(type = crop.type, limit = crop.limit)
        }

        // Update existing crops
        cropsToUpdate.forEach { configCrop ->
            dbCrops.find { it.type == configCrop.type }?.apply {
                limit = configCrop.limit
                save()
            }
        }

        // Remove crops that are no longer in the config
        cropsToRemove.forEach { crop ->
            crop.delete()
        }

        Configuration.crops = Crop.all().toMutableList()
    }
}