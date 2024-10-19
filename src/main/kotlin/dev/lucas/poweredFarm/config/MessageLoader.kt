package dev.lucas.poweredFarm.config

import dev.lucas.poweredFarm.config.messages.*
import org.bukkit.Bukkit

class MessageLoader(private val config: Configuration) {
    private val commandMessageKeyMap = CommandMessageKey.entries.associateBy { it.key }

    fun loadMessages() {
        val cropMessages =
            config.getMessageConfig(Configuration.locale).getList("crops")?.mapNotNull { cropMessageData ->
                (cropMessageData as? Map<*, *>)?.let {
                    val type = it["type"] as? String ?: return@mapNotNull null
                    val displayName = it["display-name"] as? String ?: return@mapNotNull null
                    val title = it["title"] as? String ?: return@mapNotNull null
                    val loreLines = it["lore"] as? List<*> ?: return@mapNotNull null

                    CropMessage(
                        type,
                        displayName,
                        config.parseText(title),
                        loreLines.map { line -> line as String },
                    )
                }
            }?.toMutableList() ?: mutableListOf()

        val storageMessages =
            config.getMessageConfig(Configuration.locale).getConfigurationSection("storage")?.let { storageSection ->
                val title = storageSection.getString("title") ?: return@let null
                val iconSection = storageSection.getConfigurationSection("icon") ?: return@let null
                val iconTitle = iconSection.getString("title") ?: return@let null
                val iconLore = iconSection.getStringList("lore")
                val collectSection = storageSection.getConfigurationSection("collect") ?: return@let null
                val collectTitle = collectSection.getString("title") ?: return@let null
                val collectLore = collectSection.getStringList("lore")

                StorageMessage(
                    config.parseText(title),
                    StorageIcon(config.parseText(iconTitle), iconLore.map { config.parseText(it) }),
                    StorageCollectIcon(collectTitle, collectLore.map { config.parseText(it) })
                )
            }!!

        val commandMessages =
            config.getMessageConfig(Configuration.locale).getConfigurationSection("messages")?.let { commandSection ->
                commandSection.getKeys(false).mapNotNull { commandKey ->
                    val message = commandSection.getString(commandKey) ?: return@mapNotNull null
                    commandMessageKeyMap[commandKey]!!.key to config.parseText(message)
                }.toMap()
            }!!

        Configuration.messages = Messages(cropMessages, storageMessages, commandMessages)
    }
}