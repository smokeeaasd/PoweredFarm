package dev.lucas.poweredFarm.commands.farm

import dev.lucas.poweredFarm.commands.farm.subcommands.CollectSubCommand
import dev.lucas.poweredFarm.commands.farm.subcommands.StorageSubCommand
import dev.lucas.poweredFarm.commands.farm.subcommands.StoreSubCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object FarmCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only players can execute this command.")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("§cUsage: /farm <storage|collect|store>")
            return true
        }

        when (args[0]) {
            "storage" -> StorageSubCommand.execute(sender, args)
            "collect" -> CollectSubCommand.execute(sender, args)
            "store" -> StoreSubCommand.execute(sender, args)
            else -> sender.sendMessage("§cUsage: /farm <storage|collect|store>")
        }

        return true
    }
}