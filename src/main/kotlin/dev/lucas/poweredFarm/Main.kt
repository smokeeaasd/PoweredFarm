package dev.lucas.poweredFarm

import dev.lucas.poweredFarm.config.Configuration
import dev.lucas.poweredFarm.listeners.PlayerListener
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Main : JavaPlugin() {
    private val configuration = Configuration(dataFolder, logger)
    override fun onEnable() {
        configuration.initialize()

        Bukkit.getPluginManager().registerEvents(PlayerListener(this), this)
    }

    override fun onDisable() {

    }
}
