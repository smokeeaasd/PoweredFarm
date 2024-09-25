package dev.lucas.poweredFarm

import dev.lucas.poweredFarm.config.Configuration
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Main : JavaPlugin() {
    private val configuration = Configuration(dataFolder, logger)
    override fun onEnable() {
        configuration.initialize()

        iterate(File(dataFolder, "config.yml"))
    }

    override fun onDisable() {

    }

    fun iterate(file: File) {
        val yaml = YamlConfiguration.loadConfiguration(file)

        if (yaml.getKeys(false).isEmpty()) {
            Bukkit.getConsoleSender().sendMessage("Config file is empty or not loaded correctly.")
            return
        }

        for (key in yaml.getKeys(true)) {
            try {
                val value = yaml.get(key)
                Bukkit.getConsoleSender().sendMessage("Key: $key, Value: $value")
            } catch (e: Exception) {
                Bukkit.getConsoleSender().sendMessage("Error reading key: $key, Message: ${e.message}")
            }
        }
    }
}
