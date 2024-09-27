package dev.lucas.poweredFarm

import dev.lucas.poweredFarm.config.Configuration
import dev.lucas.poweredFarm.listeners.PlayerListener
import dev.lucas.poweredFarm.placeholders.PoweredFarmExpansion
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    private val configuration = Configuration(dataFolder, logger, this)
    override fun onEnable() {
        val success = configuration.initialize()

        if (success) {
            server.pluginManager.registerEvents(PlayerListener(this), this)

            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                PoweredFarmExpansion().register()
                logger.info("registered PlaceholderAPI expansion.")
            } else {
                logger.warning("PlaceholderAPI not found, placeholders will not work.")
            }
        }
    }

    override fun onDisable() {

    }
}
