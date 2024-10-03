package dev.lucas.poweredFarm.commands

import dev.lucas.poweredFarm.Main
import dev.lucas.poweredFarm.config.Configuration
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.entity.Player

object ReloadCommand : BasicCommand {
    override fun suggest(commandSourceStack: CommandSourceStack, args: Array<out String>?): MutableCollection<String> {
        return mutableListOf("reload")
    }
    override fun execute(stack: CommandSourceStack, args: Array<out String>) {
        val sender = stack.sender as? Player ?: return
        val plugin = sender.server.pluginManager.getPlugin("PoweredFarm") ?: return
        val config = Configuration(plugin.dataFolder, plugin.logger, plugin as Main)

        if (config.initialize()) {
            sender.sendMessage("Configuration reloaded successfully!")
        } else {
            sender.sendMessage("Failed to reload configuration.")
        }
    }
}