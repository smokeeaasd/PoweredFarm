package dev.lucas.poweredFarm

import dev.lucas.poweredFarm.config.Configuration
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    private val configuration = Configuration(dataFolder, logger)
    override fun onEnable() {
        configuration.initialize()
    }

    override fun onDisable() {

    }
}
