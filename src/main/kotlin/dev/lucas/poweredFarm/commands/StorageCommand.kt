package dev.lucas.poweredFarm.commands

import dev.lucas.InventoryUI
import dev.lucas.InventoryUIComponent
import dev.lucas.poweredFarm.config.Configuration
import dev.lucas.poweredFarm.util.item.ItemUtil.Companion.removeAllAttributes
import dev.lucas.poweredFarm.util.player.PlayerUtil
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object StorageCommand : BasicCommand {
    private const val INVENTORY_SIZE = 27
    private const val START_INDEX = 11

    override fun execute(stack: CommandSourceStack, args: Array<out String>) {
        val sender = stack.sender as? Player ?: return

        val inventoryUI = createInventoryUI(sender)
        populateInventoryUI(inventoryUI, sender)

        inventoryUI.open(sender)
    }

    private fun createInventoryUI(player: Player): InventoryUI {
        val title = PlaceholderAPI.setPlaceholders(player, "%player_name%'s Storage")
        return InventoryUI(INVENTORY_SIZE, Component.text(title))
    }

    private fun populateInventoryUI(inventoryUI: InventoryUI, player: Player) {
        var index = START_INDEX
        for (crop in Configuration.cropMessages) {
            val item = createCropItem(crop.type)
            val lore = createLore(crop.lore, player)
            val title = LegacyComponentSerializer.legacySection().deserialize(PlaceholderAPI.setPlaceholders(player, crop.title))

            inventoryUI.addComponent(InventoryUIComponent(item, title, lore.toMutableList()), index)
            index++
        }

        val playerSkull = PlayerUtil.getPlayerSkull(player.name)
        inventoryUI.addComponent(
            InventoryUIComponent(
                playerSkull,
                Component.text("Your Head"),
                mutableListOf(Component.text("This is your head!"))
            ),
            0
        )
    }

    private fun createCropItem(type: String): ItemStack {
        val material = Material.getMaterial(type.uppercase()) ?: Material.AIR
        return ItemStack(material).apply { removeAllAttributes() }
    }

    private fun createLore(loreLines: List<String>, player: Player): List<Component> {
        return loreLines.map { line ->
            LegacyComponentSerializer
                .legacySection()
                .deserialize(PlaceholderAPI.setPlaceholders(player, line))
                .decorations(setOf(TextDecoration.ITALIC), false)
        }
    }
}