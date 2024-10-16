package dev.lucas.poweredFarm.commands.farm

import dev.lucas.InventoryUI
import dev.lucas.InventoryUIButton
import dev.lucas.InventoryUIComponent
import dev.lucas.poweredFarm.config.Configuration
import dev.lucas.poweredFarm.util.item.ItemUtil.Companion.removeAllAttributes
import dev.lucas.poweredFarm.util.player.PlayerUtil
import io.papermc.paper.command.brigadier.CommandSourceStack
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object StorageSubCommand {
    private const val INVENTORY_SIZE = 27
    private const val START_INDEX = 11

    fun execute(stack: CommandSourceStack, args: Array<out String>) {
        val sender = stack.sender as? Player ?: return

        val inventoryUI = createInventoryUI(sender)
        populateInventoryUI(inventoryUI, sender)

        inventoryUI.open(sender)
    }

    private fun createInventoryUI(player: Player): InventoryUI {
        val title = PlaceholderAPI.setPlaceholders(player, Configuration.storageMessage.title)
        return InventoryUI(INVENTORY_SIZE, Component.text(title))
    }

    private fun populateInventoryUI(inventoryUI: InventoryUI, player: Player) {
        var index = START_INDEX
        for (crop in Configuration.cropMessages) {
            val item = createCropItem(crop.type)
            val lore = createLore(crop.lore, player)
            val title = LegacyComponentSerializer.legacySection()
                .deserialize(PlaceholderAPI.setPlaceholders(player, crop.title))

            inventoryUI.addComponent(
                InventoryUIButton(
                    item,
                    title,
                    lore.toMutableList(),
                    onLeftClick = {
                        player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                        player.closeInventory()
                        player.performCommand("farm store ${crop.type}")
                    },
                ),
                index
            )
            index++
        }

        val playerSkull = PlayerUtil.getPlayerSkull(player.name)
        val iconTitle = LegacyComponentSerializer.legacySection()
            .deserialize(PlaceholderAPI.setPlaceholders(player, Configuration.storageMessage.infoIcon.title))
        val iconLore = createLore(Configuration.storageMessage.infoIcon.lore, player)

        inventoryUI.addComponent(
            InventoryUIComponent(
                playerSkull,
                iconTitle,
                iconLore.toMutableList()
            ),
            0
        )

        val storeButton = InventoryUIButton(
            ItemStack(Material.CHEST),
            LegacyComponentSerializer.legacySection()
                .deserialize(PlaceholderAPI.setPlaceholders(player, Configuration.storageMessage.storeIcon.title)),
            createLore(Configuration.storageMessage.storeIcon.lore, player).toMutableList(),
            onLeftClick = {
                player.apply {
                    playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                    performCommand("farm store all")
                    performCommand("farm storage")
                }
            },
            onRightClick = {
                player.apply {
                    playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                    performCommand("farm collect")
                    performCommand("farm storage")
                }
            }
        )

        inventoryUI.addComponent(storeButton, 26)
    }

    private fun createCropItem(type: String): ItemStack {
        val material = Material.getMaterial(type.uppercase()) ?: Material.AIR
        return ItemStack(material).apply { removeAllAttributes() }
    }

    private fun createLore(loreLines: List<String>, player: Player): List<Component> {
        return loreLines.map { line ->
            Component.text(PlaceholderAPI.setPlaceholders(player, line))
                .decorations(setOf(TextDecoration.ITALIC), false)
        }
    }
}