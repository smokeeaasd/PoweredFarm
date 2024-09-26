package dev.lucas.poweredFarm.listeners

import dev.lucas.poweredFarm.Main
import dev.lucas.poweredFarm.config.Configuration
import dev.lucas.poweredFarm.database.models.Bag
import dev.lucas.poweredFarm.database.models.Crop
import dev.lucas.poweredFarm.database.models.User
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerListener(private val plugin: Main) : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val playerUUID = getPlayerUUID(player)
        val userData = User.findByUUID(playerUUID)

        if (userData != null) {
            removeUnsetBags(userData)
            addNewBags(userData)
            plugin.logger.info("Loaded bags for existing user: ${player.name}")
            return
        }
            createNewPlayer(player)
        plugin.logger.info("Created new player: ${player.name} with default bags.")
    }

    private fun getPlayerUUID(player: Player): String {
        return player.uniqueId.toString()
    }

    private fun createNewPlayer(player: Player) {
        val user = User.create(getPlayerUUID(player), 0)
        Configuration.crops.forEach { crop ->
            Bag.create(user, crop, 0)
        }
        plugin.logger.info("Initialized bags for new player: ${player.name}")
    }

    private fun removeUnsetBags(user: User) {
        val bagsToRemove = user.bags().filter { bag ->
            Configuration.crops.none { it.type == bag.crop.type }
        }

        bagsToRemove.forEach { bag ->
            bag.delete()
            plugin.logger.info("Removed unset bag for crop type: ${bag.crop.type} for user: ${user.uuid}")
        }
    }

    private fun addNewBags(user: User) {
        Configuration.crops.forEach { cropData ->
            val existingBag = user.bags().find { it.crop.type == cropData.type }
            if (existingBag == null) {
                val crop = Crop.findByType(cropData.type) ?: return@forEach
                Bag.create(user, crop, 0)
                plugin.logger.info("Added new bag for crop type: ${cropData.type} for user: ${user.uuid}")
            }
        }
    }
}
