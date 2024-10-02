package dev.lucas.poweredFarm

import dev.lucas.poweredFarm.commands.StorageCommand
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents

class PoweredFarmBootstrapper : PluginBootstrap {
    override fun bootstrap(context: BootstrapContext) {
        val manager = context.lifecycleManager
        manager.registerEventHandler(
            LifecycleEvents.COMMANDS
        ) { event: ReloadableRegistrarEvent<Commands?> ->
            val commands = event.registrar()
            commands.register("storage", StorageCommand)
        }
    }
}
