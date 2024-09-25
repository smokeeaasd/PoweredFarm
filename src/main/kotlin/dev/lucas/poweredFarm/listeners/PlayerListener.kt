package dev.lucas.poweredFarm.listeners

import dev.lucas.poweredFarm.database.models.Crop
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val crops = Crop.all()

        crops.forEach {
            Bukkit.getConsoleSender().sendMessage(it.type)
        }
    }
}