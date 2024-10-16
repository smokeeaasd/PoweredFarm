package dev.lucas.poweredFarm.commands.farm.subcommands

import dev.lucas.poweredFarm.database.models.Bag
import dev.lucas.poweredFarm.database.models.User
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object CollectSubCommand {
    fun execute(stack: CommandSourceStack, args: Array<out String>) {
        val player = stack.sender as? Player ?: return
        val user = User.findByUUID(player.uniqueId.toString()) ?: return

        user.bags().forEach { bag ->
            collectCrop(player, bag)
        }

        player.sendMessage("§aColheita realizada com sucesso.")
    }

    private fun collectCrop(player: Player, bag: Bag) {
        val item = ItemStack(Material.valueOf(bag.crop.type.uppercase()))
        var amount = bag.amount

        while (amount > 0) {
            if (player.inventory.firstEmpty() == -1) {
                player.sendMessage("§cSeu inventário está cheio.")
                return
            }

            val slot = player.inventory.firstOrNull { it != null && it.type == item.type && it.amount < it.maxStackSize }
            if (slot != null) {
                val remainingSpace = slot.maxStackSize - slot.amount
                if (remainingSpace >= amount) {
                    slot.amount += amount
                    amount = 0
                } else {
                    slot.amount = slot.maxStackSize
                    amount -= remainingSpace
                }
            } else {
                val newItem = item.clone().apply { this.amount = minOf(amount, item.maxStackSize) }
                player.inventory.addItem(newItem)
                amount -= newItem.amount
            }
        }

        bag.amount = amount
        bag.save()
    }
}