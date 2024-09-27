package dev.lucas.poweredFarm.placeholders

import dev.lucas.poweredFarm.database.models.Bag
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

class PoweredFarmExpansion : PlaceholderExpansion() {

    override fun getIdentifier(): String {
        return "poweredfarm"
    }

    override fun getAuthor(): String {
        return "smokeeaasd"
    }

    override fun getVersion(): String {
        return "1.0-SNAPSHOT"
    }

    override fun onPlaceholderRequest(player: Player?, identifier: String): String? {
        return null
    }
}