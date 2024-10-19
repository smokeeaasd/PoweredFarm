package dev.lucas.poweredFarm.commands.farm

import dev.lucas.poweredFarm.config.Configuration
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

object FarmCommandTabCompleter : TabCompleter {
    private val mainCommands = listOf("storage", "collect", "store")

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): MutableList<String> {
        return when {
            args.isNullOrEmpty() -> mainCommands.toMutableList()
            args.size == 1 -> filterCommands(args[0])
            args.size == 2 && args[0] in arrayOf("collect", "store") -> filterCropTypes(args[1])
            else -> mutableListOf()
        }
    }

    private fun filterCommands(input: String): MutableList<String> {
        return mainCommands.filter { it.startsWith(input) }.toMutableList()
    }

    private fun filterCropTypes(input: String): MutableList<String> {
        val cropTypes = listOf("all") + Configuration.crops.map { it.type.lowercase() }
        return cropTypes.filter { it.startsWith(input) }.toMutableList()
    }
}