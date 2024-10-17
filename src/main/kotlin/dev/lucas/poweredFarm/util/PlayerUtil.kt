package dev.lucas.poweredFarm.util

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class PlayerUtil {
    companion object {
        fun getPlayerSkull(playerName: String): ItemStack {
            val skullItem = ItemStack(Material.PLAYER_HEAD)
            val skullMeta = skullItem.itemMeta as SkullMeta

            val player = Bukkit.getOfflinePlayer(playerName)
            skullMeta.setOwningPlayer(player)
            skullItem.setItemMeta(skullMeta)

            return skullItem
        }
    }
}