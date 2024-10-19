package dev.lucas.poweredFarm.commands.farm.subcommands

import dev.lucas.poweredFarm.config.Configuration
import dev.lucas.poweredFarm.config.messages.CommandMessageKey
import dev.lucas.poweredFarm.database.models.User
import dev.lucas.poweredFarm.util.CropUtil
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.PlayerInventory

object StoreSubCommand {
    fun execute(sender: CommandSender, args: Array<out String>) {
        val player = sender as? Player ?: return
        val user = User.findByUUID(player.uniqueId.toString()) ?: run {
            player.sendMessage("§cUser not found in database")
            return
        }

        if (args.size < 2) {
            val message = Configuration.messages.commandMessages[CommandMessageKey.FARM_STORE_COMMAND_USAGE.key]!!
            player.sendMessage(message)
            return
        }

        if (args[1].equals("all", true)) {
            storeAllCrops(player, user)
            return
        }

        storeSingleCrop(player, user, args[1].uppercase())
    }

    private fun storeAllCrops(player: Player, user: User) {
        val inventory = player.inventory
        val crops = Configuration.crops

        crops.forEach { crop ->
            val totalAmount = getTotalAmount(inventory, crop.type)
            if (totalAmount > 0) {
                storeCrop(player, user, crop.type, totalAmount, crop.limit)
            }
        }
    }

    private fun storeSingleCrop(player: Player, user: User, cropType: String) {
        val crop = Configuration.crops.find { it.type.equals(cropType, true) } ?: run {
            val message = Configuration.messages.commandMessages[CommandMessageKey.FARM_STORE_INVALID_CROP.key]!!
                .replace("{crops}", Configuration.crops.joinToString(", "))

            player.sendMessage(message)
            return
        }

        val totalAmount = getTotalAmount(player.inventory, crop.type)
        if (totalAmount > 0) {
            storeCrop(player, user, crop.type, totalAmount, crop.limit)
            return
        }

        val message = Configuration.messages.commandMessages[CommandMessageKey.FARM_STORE_NO_CROPS.key]!!
            .replace("{crop}", CropUtil.getCropDisplayName(crop.type))

        player.sendMessage(message)
    }

    private fun getTotalAmount(inventory: PlayerInventory, cropType: String): Int {
        return inventory.filter { it != null && it.type == Material.getMaterial(cropType.uppercase()) }
            .sumOf { it.amount }
    }

    private fun storeCrop(player: Player, user: User, cropType: String, totalAmount: Int, cropLimit: Int) {
        val bag = user.bags().find { it.crop.type.equals(cropType, ignoreCase = true) } ?: return
        val amountToStore = calculateAmountToStore(totalAmount, cropLimit, bag.amount)

        if (amountToStore > 0) {
            removeItemsFromInventory(player.inventory, cropType, amountToStore)
            bag.amount += amountToStore
            bag.save()
            val message = Configuration.messages.commandMessages[CommandMessageKey.FARM_STORE_STORED.key]!!
                .replace("{crop}", CropUtil.getCropDisplayName(cropType))
                .replace("{amount}", amountToStore.toString())

            player.sendMessage(message)
            return
        }

        val message = Configuration.messages.commandMessages[CommandMessageKey.FARM_STORE_NO_SPACE_LEFT.key]!!
        player.sendMessage(message)
    }

    private fun calculateAmountToStore(itemAmount: Int, cropLimit: Int, currentBagAmount: Int): Int {
        val spaceLeft = cropLimit - currentBagAmount
        return if (itemAmount > spaceLeft) spaceLeft else itemAmount
    }

    private fun removeItemsFromInventory(inventory: PlayerInventory, cropType: String, amount: Int) {
        var remaining = amount
        for (i in 0 until inventory.size) {
            val currentItem = inventory.getItem(i)
            if (currentItem != null && currentItem.type == Material.getMaterial(cropType.uppercase())) {
                if (currentItem.amount > remaining) {
                    currentItem.amount -= remaining
                    break
                }
                remaining -= currentItem.amount
                inventory.clear(i)
            }
        }
    }
}