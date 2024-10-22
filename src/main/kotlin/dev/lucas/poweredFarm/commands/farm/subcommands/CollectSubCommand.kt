package dev.lucas.poweredFarm.commands.farm.subcommands

import dev.lucas.poweredFarm.config.Configuration
import dev.lucas.poweredFarm.config.messages.CommandMessageKey
import dev.lucas.poweredFarm.database.models.Bag
import dev.lucas.poweredFarm.database.models.User
import dev.lucas.poweredFarm.util.CropUtil
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object CollectSubCommand {
    fun execute(sender: CommandSender, args: Array<out String>) {
        val player = sender as? Player ?: return
        val user = User.findByUUID(player.uniqueId.toString()) ?: return
        val cropType = args.getOrNull(1) ?: run {
            val message = Configuration.messages.commandMessages[CommandMessageKey.FARM_COLLECT_COMMAND_USAGE.key]!!
            player.sendMessage(message)
            return@execute
        }

        val bags = if (cropType.equals("all", true)) {
            user.bags().filter { it.amount > 0 }
        } else {
            user.bags().filter { it.crop.type.equals(cropType, true) && it.amount > 0 }
        }

        if (bags.isEmpty()) {
            val message = Configuration.messages.commandMessages[CommandMessageKey.FARM_COLLECT_NO_CROPS.key]!!
            player.sendMessage(message)
            return
        }

        bags.forEach { collectCrop(player, it) }
    }

    private fun collectCrop(player: Player, bag: Bag) {
        val item = ItemStack(Material.valueOf(bag.crop.type.uppercase()))
        var amount = bag.amount

        if (!hasInventorySpace(player, item, amount)) {
            val message = Configuration.messages.commandMessages[CommandMessageKey.FARM_COLLECT_INVENTORY_FULL.key]!!
            player.sendMessage(message)
            return
        }

        amount = addItemToInventory(player, item, amount)
        if (amount > 0)
            sendCollectedMessage(player, item, bag.amount - amount)

        bag.amount = amount
        bag.save()
    }

    private fun hasInventorySpace(player: Player, item: ItemStack, amount: Int): Boolean {
        var remainingAmount = amount

        // Check partial stacks
        for (slot in player.inventory) {
            if (slot != null && slot.type == item.type && slot.amount < slot.maxStackSize) {
                val space = slot.maxStackSize - slot.amount
                if (remainingAmount <= space) {
                    return true
                } else {
                    remainingAmount -= space
                }
            }
        }

        // Check empty slots
        val emptySlots = player.inventory.contents.count { it == null }
        val fullStacksNeeded = (remainingAmount + item.maxStackSize - 1) / item.maxStackSize
        return emptySlots >= fullStacksNeeded
    }

    private fun addItemToInventory(player: Player, item: ItemStack, amount: Int): Int {
        var remainingAmount = amount

        // Fill partial stacks first
        for (slot in player.inventory) {
            if (slot != null && slot.type == item.type && slot.amount < slot.maxStackSize) {
                val space = slot.maxStackSize - slot.amount
                if (remainingAmount <= space) {
                    slot.amount += remainingAmount
                    return 0
                } else {
                    slot.amount = slot.maxStackSize
                    remainingAmount -= space
                }
            }
        }

        // Add new stacks if there is still remaining amount
        for (i in 0 until player.inventory.size) {
            if (remainingAmount <= 0) break
            val emptySlot = player.inventory.firstEmpty()
            if (emptySlot == -1) {
                break
            }
            val newItem = item.clone().apply { this.amount = minOf(remainingAmount, item.maxStackSize) }
            player.inventory.setItem(emptySlot, newItem)
            remainingAmount -= newItem.amount
        }

        return remainingAmount
    }

    private fun sendCollectedMessage(player: Player, item: ItemStack, amount: Int) {
        val cropName = CropUtil.getCropDisplayName(item.type.name)
        val message = Configuration.messages.commandMessages[CommandMessageKey.FARM_COLLECT_COLLECTED.key]!!
            .replace("{amount}", amount.toString())
            .replace("{crop}", cropName)

        player.sendMessage(message)
    }
}