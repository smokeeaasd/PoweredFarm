package dev.lucas.poweredFarm.commands

import dev.lucas.poweredFarm.Main
import dev.lucas.poweredFarm.config.Configuration
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object PoweredFarmCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only players can execute this command.")
            return true
        }

        if (args.isEmpty() || args[0] != "reload") {
            sender.sendMessage("Usage: /poweredfarm reload")
            return true
        }

        val plugin = sender.server.pluginManager.getPlugin("PoweredFarm") as? Main ?: return true
        val config = Configuration(plugin.dataFolder, plugin.logger, plugin)

        if (config.initialize()) {
            sender.sendMessage("Configuration reloaded successfully!")
        } else {
            sender.sendMessage("Failed to reload configuration.")
        }

        return true
    }
}