package dev.lucas.poweredFarm.commands.farm.subcommands

import dev.lucas.poweredFarm.config.Configuration
import dev.lucas.poweredFarm.database.models.Bag
import dev.lucas.poweredFarm.database.models.User
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object CollectSubCommand {
    fun execute(sender: CommandSender, args: Array<out String>) {
        val player = sender as? Player ?: return
        val user = User.findByUUID(player.uniqueId.toString()) ?: return

        val bags = user.bags().filter { it.amount > 0 }
        if (bags.isEmpty()) {
            player.sendMessage("§cYou don't have any crops to collect.")
            return
        }

        bags.forEach { collectCrop(player, it) }
    }

    private fun collectCrop(player: Player, bag: Bag) {
        val item = ItemStack(Material.valueOf(bag.crop.type.uppercase()))
        var amount = bag.amount

        if (player.inventory.firstEmpty() == -1) {
            player.sendMessage("§cSeu inventário está cheio.")
            return
        }

        amount = addItemToInventory(player, item, amount)
        sendCollectedMessage(player, item, amount)

        bag.amount = amount
        bag.save()
    }

    private fun addItemToInventory(player: Player, item: ItemStack, amount: Int): Int {
        val slot = player.inventory.firstOrNull { it != null && it.type == item.type && it.amount < it.maxStackSize }

        return if (slot != null) {
            updateSlot(slot, amount)
        } else {
            addNewItem(player, item, amount)
        }
    }

    private fun updateSlot(slot: ItemStack, amount: Int): Int {
        val remainingSpace = slot.maxStackSize - slot.amount
        return if (remainingSpace >= amount) {
            slot.amount += amount
            0
        } else {
            slot.amount = slot.maxStackSize
            amount - remainingSpace
        }
    }

    private fun addNewItem(player: Player, item: ItemStack, amount: Int): Int {
        val newItem = item.clone().apply { this.amount = minOf(amount, item.maxStackSize) }
        player.inventory.addItem(newItem)
        return amount - newItem.amount
    }

    private fun sendCollectedMessage(player: Player, item: ItemStack, amount: Int) {
        val itemName = Configuration.cropMessages.first {
            it.type.equals(item.type.name, ignoreCase = true)
        }
        player.sendMessage("§aCollected ${itemName.title} §7x${amount}.")
    }
}