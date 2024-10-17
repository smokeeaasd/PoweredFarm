package dev.lucas.poweredFarm.commands.farm.subcommands

import dev.lucas.InventoryUI
import dev.lucas.InventoryUIButton
import dev.lucas.InventoryUIComponent
import dev.lucas.poweredFarm.config.Configuration
import dev.lucas.poweredFarm.util.ItemUtil.Companion.removeAllAttributes
import dev.lucas.poweredFarm.util.PlayerUtil
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
        val player = stack.sender as? Player ?: return

        val inventoryUI = createInventoryUI(player)
        populateInventoryUI(inventoryUI, player)

        inventoryUI.open(player)
    }

    private fun createInventoryUI(player: Player): InventoryUI {
        val title = PlaceholderAPI.setPlaceholders(player, Configuration.storageMessage.title)
        return InventoryUI(INVENTORY_SIZE, Component.text(title))
    }

    private fun populateInventoryUI(inventoryUI: InventoryUI, player: Player) {
        var index = START_INDEX
        Configuration.cropMessages.forEach { crop ->
            addCropButton(inventoryUI, player, crop.type, crop.title, crop.lore, index)
            index++
        }

        addPlayerInfoIcon(inventoryUI, player)
        addStoreButton(inventoryUI, player)
    }

    private fun addCropButton(inventoryUI: InventoryUI, player: Player, cropType: String, title: String, lore: List<String>, index: Int) {
        val item = createCropItem(cropType)
        val loreComponents = createLore(lore, player)
        val titleComponent = LegacyComponentSerializer.legacySection().deserialize(PlaceholderAPI.setPlaceholders(player, title))

        inventoryUI.addComponent(
            InventoryUIButton(
                item,
                titleComponent,
                loreComponents.toMutableList(),
                onLeftClick = {
                    player.apply {
                        playSound(location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                        performCommand("farm store $cropType")
                        performCommand("farm storage")
                    }
                },
            ),
            index
        )
    }

    private fun addPlayerInfoIcon(inventoryUI: InventoryUI, player: Player) {
        val playerSkull = PlayerUtil.getPlayerSkull(player.name)
        val iconTitle = LegacyComponentSerializer.legacySection().deserialize(PlaceholderAPI.setPlaceholders(player, Configuration.storageMessage.infoIcon.title))
        val iconLore = createLore(Configuration.storageMessage.infoIcon.lore, player)

        inventoryUI.addComponent(
            InventoryUIComponent(
                playerSkull,
                iconTitle,
                iconLore.toMutableList()
            ),
            0
        )
    }

    private fun addStoreButton(inventoryUI: InventoryUI, player: Player) {
        val storeButton = InventoryUIButton(
            ItemStack(Material.CHEST),
            LegacyComponentSerializer.legacySection().deserialize(PlaceholderAPI.setPlaceholders(player, Configuration.storageMessage.storeIcon.title)),
            createLore(Configuration.storageMessage.storeIcon.lore, player).toMutableList(),
            onLeftClick = {
                player.apply {
                    playSound(location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                    performCommand("farm store all")
                    performCommand("farm storage")
                }
            },
            onRightClick = {
                player.apply {
                    playSound(location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
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