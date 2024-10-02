package dev.lucas.poweredFarm.placeholders

import dev.lucas.poweredFarm.database.models.Bag
import dev.lucas.poweredFarm.database.models.User
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
        if (player == null) {
            return ""
        }

        val user = User.findByUUID(player.uniqueId.toString()) ?: return null
        val bags = user.bags()

        return when {
            identifier.endsWith("_limit") -> getLimit(bags, identifier)
            identifier.endsWith("_amount") -> getAmount(bags, identifier)
            else -> identifier
        }
    }

    private fun getLimit(bags: List<Bag>, identifier: String): String? {
        val type = identifier.removeSuffix("_limit")
        return bags.find { it.crop.type == type }?.crop?.limit?.toString()
    }

    private fun getAmount(bags: List<Bag>, identifier: String): String? {
        val type = identifier.removeSuffix("_amount")
        return bags.find { it.crop.type == type }?.amount?.toString()
    }
}