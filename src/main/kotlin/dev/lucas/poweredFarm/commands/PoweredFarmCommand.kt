package dev.lucas.poweredFarm.commands

import dev.lucas.poweredFarm.Main
import dev.lucas.poweredFarm.config.Configuration
import dev.lucas.poweredFarm.config.messages.CommandMessageKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object PoweredFarmCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            val message = Configuration.messages.commandMessages[CommandMessageKey.ONLY_PLAYER.key]!!
            sender.sendMessage(message)
            return true
        }

        if (args.isEmpty() || args[0] != "reload") {
            sender.apply {
                sendMessage(" ")
                sendMessage("§a§lPOWERED §f§lFARM")
                sendMessage(" ")
                sendMessage("   §7by Lucas")
                sendMessage(" ")
                sendMessage("   §fCommands:")
                sendMessage(" ")
                sendMessage("   §7- /poweredfarm reload")
                sendMessage("   §7- /farm storage")
                sendMessage("   §7- /farm <store|collect> [args]")
                sendMessage(" ")
            }
            return true
        }

        val plugin = sender.server.pluginManager.getPlugin("PoweredFarm") as? Main ?: return true
        val config = Configuration(plugin.dataFolder, plugin.logger, plugin)

        if (config.initialize()) {
            val message = Configuration.messages.commandMessages[CommandMessageKey.CONFIG_RELOAD_SUCCESS.key]!!
            sender.sendMessage(message)
        } else {
            val message = Configuration.messages.commandMessages[CommandMessageKey.CONFIG_RELOAD_FAIL.key]!!
            sender.sendMessage(message)
        }

        return true
    }
}