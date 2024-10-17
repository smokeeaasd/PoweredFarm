package dev.lucas.poweredFarm.commands.farm.subcommands

import dev.lucas.poweredFarm.config.Configuration
import dev.lucas.poweredFarm.database.models.User
import dev.lucas.poweredFarm.util.CropUtil
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.PlayerInventory

object StoreSubCommand {
    fun execute(stack: CommandSourceStack, args: Array<out String>) {
        val player = stack.sender as? Player ?: return
        val user = User.findByUUID(player.uniqueId.toString()) ?: run {
            player.sendMessage("§cUser not found.")
            return
        }

        if (args.size < 2) {
            player.sendMessage("§cUsage: /farm store <cropType|all>")
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
            player.sendMessage("§cInvalid crop type. Available types: ${Configuration.crops.joinToString { it.type }}§c.")
            return
        }

        val totalAmount = getTotalAmount(player.inventory, crop.type)
        if (totalAmount > 0) {
            storeCrop(player, user, crop.type, totalAmount, crop.limit)
            return
        }

        player.sendMessage("§cYou don't have any ${CropUtil.getCropDisplayName(crop.type)}.")
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
            player.sendMessage("§aStored ${CropUtil.getCropDisplayName(cropType)}. §7${amountToStore}x")
            return
        }

        player.sendMessage("§cNo space left to store the crop.")
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