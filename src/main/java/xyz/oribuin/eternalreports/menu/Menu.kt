package xyz.oribuin.eternalreports.menu

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.orilibrary.FileUtils.createMenuFile
import java.io.File

abstract class Menu(val plugin: EternalReports, private val guiName: String) {
    val menuConfig: FileConfiguration

    init {
        createMenuFile(plugin, guiName)
        menuConfig = YamlConfiguration.loadConfiguration(menuFile)
    }

    fun reload() {
        createMenuFile(plugin, guiName)
        YamlConfiguration.loadConfiguration(menuFile)
    }

    private val menuFile: File
        get() = File("${plugin.dataFolder}${File.separator}menus", "$guiName.yml")
}

