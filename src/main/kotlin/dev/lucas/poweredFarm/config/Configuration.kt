package dev.lucas.poweredFarm.config

import dev.lucas.poweredFarm.database.DatabaseFactory
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.FileOutputStream
import java.util.logging.Logger

class Configuration(private val dataFolder: File, private val logger: Logger) {

    init {
        if (!dataFolder.exists())
            dataFolder.mkdirs()
    }

    fun initialize() {
        createConfigFile()
        createMessagesDirectory()
        createMessageFiles()
        DatabaseFactory.init()
    }

    private fun createConfigFile() {
        val configFile = File(dataFolder, "config.yml")
        if (configFile.exists()) {
            logger.info("File 'config.yml' already exists: ${configFile.path}")
            return
        }

        copyResource("config.yml", configFile)
        logger.info("Created config.yml")
    }

    private fun createMessagesDirectory() {
        val messagesDir = File(dataFolder, "messages")
        if (messagesDir.exists()) {
            logger.info("Folder 'messages' already exists: ${messagesDir.path}")
            return
        }

        messagesDir.mkdirs()
        logger.info("Created 'messages' folder: ${messagesDir.path}")
    }

    private fun createMessageFiles() {
        val messageFiles = listOf("pt_BR.yml", "en_US.yml")

        for (fileName in messageFiles) {
            val file = File(dataFolder, "messages/$fileName")
            if (file.exists()) {
                logger.info("File '$fileName' already exists: ${file.path}")
                continue
            }

            copyResource("messages/$fileName", file)
            logger.info("Created '$fileName'.")
        }
    }

    private fun copyResource(resourcePath: String, destination: File) {
        this.javaClass.classLoader.getResourceAsStream(resourcePath)?.use { input ->
            FileOutputStream(destination).use { output ->
                input.copyTo(output)
            }
        } ?: logger.warning("Resource not found: $resourcePath")
    }

    fun getConfig(): YamlConfiguration {
        val configFile = File(dataFolder, "config.yml")
        return YamlConfiguration.loadConfiguration(configFile)
    }

    fun getMessageConfig(locale: String): YamlConfiguration {
        val messagesDir = File(dataFolder, "messages")
        val messageConfigFile = File(messagesDir, locale.plus(".yml"))
        return YamlConfiguration.loadConfiguration(messageConfigFile)
    }
}