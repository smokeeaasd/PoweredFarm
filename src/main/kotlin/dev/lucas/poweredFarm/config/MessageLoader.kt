package dev.lucas.poweredFarm.config

class MessageLoader(private val config: Configuration) {

    fun loadMessages() {
        Configuration.cropMessages = config.getMessageConfig(Configuration.locale).getList("crops")?.mapNotNull { cropMessageData ->
            (cropMessageData as? Map<*, *>)?.let { it ->
                val type = it["type"] as? String ?: return@mapNotNull null
                val title = it["title"] as? String ?: return@mapNotNull null
                val loreLines = it["lore"] as? List<*> ?: return@mapNotNull null

                CropMessage(
                    type,
                    config.parseText(title),
                    loreLines.map { line -> line as String },
                )
            }
        }?.toMutableList() ?: mutableListOf()

        Configuration.storageMessage =
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
                    StorageIcon(
                        config.parseText(iconTitle),
                        iconLore.map { config.parseText(it) }
                    ),
                    StorageCollectIcon(
                        collectTitle,
                        collectLore.map { config.parseText(it) }
                    )
                )
            }!!
    }
}