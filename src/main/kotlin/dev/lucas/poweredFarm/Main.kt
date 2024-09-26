package dev.lucas.poweredFarm

import dev.lucas.poweredFarm.config.Configuration
import dev.lucas.poweredFarm.listeners.PlayerListener
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    private val configuration = Configuration(dataFolder, logger, this)
    override fun onEnable() {
        configuration.initialize()

        Bukkit.getPluginManager().registerEvents(PlayerListener(this), this)
    }

    override fun onDisable() {

    }
}
