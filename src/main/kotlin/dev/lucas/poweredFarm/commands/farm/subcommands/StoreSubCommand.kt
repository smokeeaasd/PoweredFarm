package dev.lucas.poweredFarm.commands.farm.subcommands

import dev.lucas.poweredFarm.config.Configuration
import dev.lucas.poweredFarm.database.models.User
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.PlayerInventory

object StoreSubCommand {
    fun execute(stack: CommandSourceStack, args: Array<out String>) {
        if (stack.sender !is Player) {
            stack.sender.sendMessage("§cOnly players can execute this command.")
            return
        }

        if (args.size < 2) {
            stack.sender.sendMessage("§cUsage: /farm store <cropType|all>")
            return
        }

        val player = stack.sender as Player
        val user = User.findByUUID(player.uniqueId.toString())

        if (user == null) {
            stack.sender.sendMessage("§cUser not found.")
            return
        }

        if (args[1] == "all") {
            storeAllCrops(player, user)
        } else {
            storeSingleCrop(player, user, args[1])
        }
    }

    private fun storeAllCrops(player: Player, user: User) {
        val inventory = player.inventory
        val crops = Configuration.crops

        crops.forEach { crop ->
            val items = inventory.filter { it != null && it.type == Material.getMaterial(crop.type.uppercase()) }
            val totalAmount = items.sumOf { it.amount }
            val bag = user.bags().find { it.crop.type == crop.type }

            if (bag != null && totalAmount > 0) {
                val amountToStore = calculateAmountToStore(totalAmount, crop.limit, bag.amount)
                if (amountToStore > 0) {
                    removeItemsFromInventory(inventory, crop.type, amountToStore)
                    bag.amount += amountToStore
                    bag.save()
                    player.sendMessage("§aStored $amountToStore of ${crop.type}.")
                }
            }
        }
    }

    private fun storeSingleCrop(player: Player, user: User, cropType: String) {
        val crop = Configuration.crops.find { it.type == cropType }

        if (crop == null) {
            player.sendMessage("§cInvalid crop type. Available types: ${Configuration.crops.joinToString { it.type }}")
            return
        }

        val inventory = player.inventory
        val items = inventory.filter { it != null && it.type == Material.getMaterial(crop.type.uppercase()) }
        val totalAmount = items.sumOf { it.amount }
        val bag = user.bags().find { it.crop.type == crop.type }

        if (bag == null) {
            player.sendMessage("§cYou don't have any crop to store.")
            return
        }

        val amountToStore = calculateAmountToStore(totalAmount, crop.limit, bag.amount)

        if (amountToStore > 0) {
            removeItemsFromInventory(inventory, crop.type, amountToStore)
            bag.amount += amountToStore
            bag.save()
            player.sendMessage("§aStored $amountToStore of ${crop.type}.")
        } else {
            player.sendMessage("§cNo space left to store the crop.")
        }
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
                } else {
                    remaining -= currentItem.amount
                    inventory.clear(i)
                }
            }
        }
    }
}