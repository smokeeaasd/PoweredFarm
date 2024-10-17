package dev.lucas.poweredFarm.commands.farm

import dev.lucas.poweredFarm.config.Configuration
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

object FarmCommandTabCompleter : TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): MutableList<String> {
        return when {
            args == null || args.isEmpty() -> mutableListOf("storage", "collect", "store")
            args.size == 1 -> listOf("storage", "collect", "store").filter { it.startsWith(args[0]) }.toMutableList()
            args.size == 2 && args[0] == "store" -> mutableListOf("all", *Configuration.crops.map { it.type.lowercase() }.filter { it.startsWith(args[1]) }.toTypedArray())
            else -> mutableListOf()
        }
    }
}
