package dev.lucas.poweredFarm.util

import com.google.common.collect.MultimapBuilder
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class ItemUtil {
    companion object {
        fun ItemStack.removeAllAttributes() {
            editMeta {
                it.attributeModifiers = MultimapBuilder
                    .hashKeys()
                    .hashSetValues()
                    .build()

                it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            }
        }
    }
}