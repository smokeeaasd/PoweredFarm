package dev.lucas.poweredFarm.commands.farm

import dev.lucas.poweredFarm.commands.farm.subcommands.CollectSubCommand
import dev.lucas.poweredFarm.commands.farm.subcommands.StorageSubCommand
import dev.lucas.poweredFarm.commands.farm.subcommands.StoreSubCommand
import dev.lucas.poweredFarm.config.Configuration
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.entity.Player

object FarmCommand : BasicCommand {
    override fun suggest(commandSourceStack: CommandSourceStack, args: Array<out String>?): MutableCollection<String> {
        return when {
            args == null || args.isEmpty() -> mutableListOf("storage", "collect", "store")
            args.size == 1 -> listOf("storage", "collect", "store").filter { it.startsWith(args[0]) }.toMutableList()
            args.size == 2 && args[0] == "store" -> mutableListOf("all", *Configuration.crops.map { it.type.lowercase() }.filter { it.startsWith(args[1]) }.toTypedArray())
            else -> mutableListOf()
        }
    }

    override fun execute(stack: CommandSourceStack, args: Array<out String>) {
        if (stack.sender !is Player) {
            stack.sender.sendMessage("Only players can execute this command.")
            return
        }

        when (args[0]) {
            "storage" -> StorageSubCommand.execute(stack, args)
            "collect" -> CollectSubCommand.execute(stack, args)
            "store" -> StoreSubCommand.execute(stack, args)
            else -> stack.sender.sendMessage("§cUsage: /farm <storage|collect|store>")
        }
    }
}