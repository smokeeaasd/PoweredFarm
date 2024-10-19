package dev.lucas.poweredFarm.config

import java.io.File
import java.io.FileOutputStream
import java.util.logging.Logger

class ResourceManager(private val dataFolder: File, private val logger: Logger) {

    fun createConfigFile(configFile: File) {
        if (!configFile.exists()) {
            copyResource("config.yml", configFile)
            logger.info("Created config.yml")
        } else {
            logger.info("File 'config.yml' already exists: ${configFile.path}")
        }
    }

    fun createMessagesDirectory() {
        val messagesDir = File(dataFolder, "locales")
        if (!messagesDir.exists()) {
            messagesDir.mkdirs()
            logger.info("Created 'locales' folder: ${messagesDir.path}")
        } else {
            logger.info("Folder 'locales' already exists: ${messagesDir.path}")
        }
    }

    fun createMessageFiles() {
        listOf("pt_BR.yml", "en_US.yml").forEach { fileName ->
            val file = File(dataFolder, "locales/$fileName")
            if (!file.exists()) {
                copyResource("locales/$fileName", file)
                logger.info("Created '$fileName'.")
            } else {
                logger.info("File '$fileName' already exists: ${file.path}")
            }
        }
    }

    private fun copyResource(resourcePath: String, destination: File) {
        this.javaClass.classLoader.getResourceAsStream(resourcePath)?.use { input ->
            FileOutputStream(destination).use { output ->
                input.copyTo(output)
            }
        } ?: logger.warning("Resource not found: $resourcePath")
    }
}