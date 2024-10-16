package dev.lucas.poweredFarm

import dev.lucas.InventoryUIListener
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
            server.pluginManager.registerEvents(InventoryUIListener(), this)
            registerExpansion()
        }
    }

    override fun onDisable() {

    }

    private fun registerExpansion() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            val expansion = PoweredFarmExpansion()
            expansion.persist()
            expansion.register()
            logger.info("registered PlaceholderAPI expansion.")
            return
        }
        logger.warning("PlaceholderAPI not found, placeholders will not work.")
    }
}
