package dev.lucas.poweredFarm.config

data class StorageIcon (
    val title: String,
    val lore: List<String>
)

data class CropMessage(
    val type: String,
    val title: String,
    val lore: List<String>
)

data class StorageCollectIcon (
    val title: String,
    val lore: List<String>
)

data class StorageMessage(
    val title: String,
    val infoIcon: StorageIcon,
    val storeIcon: StorageCollectIcon
)