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
                    loreLines.map { line -> line as String }
                )
            }
        }?.toMutableList() ?: mutableListOf()

        Configuration.storageMessage =
            config.getMessageConfig(Configuration.locale).getConfigurationSection("storage")?.let { storageSection ->
                val title = storageSection.getString("title") ?: return@let null
                StorageMessage(config.parseText(title))
            } ?: StorageMessage("%player_name%'s Storage")
    }
}