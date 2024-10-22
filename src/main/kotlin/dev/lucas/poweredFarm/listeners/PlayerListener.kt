package dev.lucas.poweredFarm.listeners

import dev.lucas.poweredFarm.Main
import dev.lucas.poweredFarm.config.Configuration
import dev.lucas.poweredFarm.config.messages.CommandMessageKey
import dev.lucas.poweredFarm.database.models.Bag
import dev.lucas.poweredFarm.database.models.Crop
import dev.lucas.poweredFarm.database.models.User
import dev.lucas.poweredFarm.util.CropUtil
import org.bukkit.Particle
import org.bukkit.block.data.Ageable
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
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

    @EventHandler
        fun onPlayerBreakBlock(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block

        if (player.isSneaking) return
        if (block.blockData !is Ageable) return

        val ageable = block.blockData as Ageable
        val user = User.findByUUID(getPlayerUUID(player)) ?: return
        val bag = user.bags().find { it.crop.type.equals(block.type.name, true) && it.amount > 0 } ?: return

        if (ageable.age < ageable.maximumAge) return

        event.isCancelled = true
        val drops = block.getDrops(player.inventory.itemInMainHand, player)
        ageable.age = 0
        event.block.blockData = ageable

        bag.amount--
        bag.save()

        for (drop in drops) {
            block.world.dropItemNaturally(block.location.add(0.5, 0.5, 0.5), drop)
        }

        player.spawnParticle(Particle.HAPPY_VILLAGER, block.location.add(0.5, 0.5, 0.5), 10, 0.5, 0.5, 0.5)

        if (bag.amount == 0) {
            val cropName = CropUtil.getCropDisplayName(bag.crop.type)
            val message = Configuration.messages.commandMessages[CommandMessageKey.EMPTY_BAG.key]!!
                .replace("{crop}", cropName)

            player.sendMessage(message)
        }
    }
}
