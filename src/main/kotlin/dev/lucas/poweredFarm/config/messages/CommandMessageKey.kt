package dev.lucas.poweredFarm.config.messages

enum class CommandMessageKey(val key: String) {
    CONFIG_RELOAD_SUCCESS("config-reload-success"),
    CONFIG_RELOAD_FAIL("config-reload-fail"),
    ONLY_PLAYER("only-player"),
    NO_PERMISSION("no-permission"),
    FARM_COMMAND_USAGE("farm-command-usage"),
    FARM_COLLECT_COMMAND_USAGE("farm-collect-command-usage"),
    FARM_COLLECT_NO_CROPS("farm-collect-no-crops"),
    FARM_COLLECT_INVENTORY_FULL("farm-collect-inventory-full"),
    FARM_COLLECT_COLLECTED("farm-collect-collected"),
    FARM_STORE_COMMAND_USAGE("farm-store-command-usage"),
    FARM_STORE_NO_CROPS("farm-store-no-crops"),
    FARM_STORE_INVALID_CROP("farm-store-invalid-crop"),
    FARM_STORE_PLAYER_DONT_HAVE_CROP("farm-store-player-dont-have-crop"),
    FARM_STORE_STORED("farm-store-stored"),
    FARM_STORE_NO_SPACE_LEFT("farm-store-no-space-left")
}