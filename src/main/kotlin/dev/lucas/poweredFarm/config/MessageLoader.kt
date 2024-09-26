package dev.lucas.poweredFarm.config

class MessageLoader(private val config: Configuration) {

    fun loadMessages() {
        Configuration.cropMessages = config.getMessageConfig(Configuration.locale).getList("crops")?.mapNotNull { cropMessageData ->
            (cropMessageData as? Map<*, *>)?.let {
                val type = it["type"] as? String ?: return@mapNotNull null
                val title = it["title"] as? String ?: return@mapNotNull null
                val loreLines = it["lore"] as? List<*> ?: return@mapNotNull null
                val fullText = it["full"] as? String ?: return@mapNotNull null

                CropMessage(
                    type,
                    config.parseText(title),
                    config.buildLore(loreLines),
                    config.parseText(fullText)
                )
            }
        }?.toMutableList() ?: mutableListOf()
    }
}