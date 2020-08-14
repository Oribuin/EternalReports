package xyz.oribuin.eternalreports.menus

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.utils.FileUtils.createMenuFile
import java.io.File

abstract class Menu(val plugin: EternalReports, private val guiName: String) {
    val menuConfig: FileConfiguration

    init {
        createMenuFile(plugin, menuFile)
        menuConfig = YamlConfiguration.loadConfiguration(menuFile)
    }

    fun reload() {
        YamlConfiguration.loadConfiguration(menuFile)
    }

    fun getGuiName(): String {
        return guiName.toLowerCase()
    }

    private val menuFile: File
        get() = File(plugin.dataFolder.toString() + File.separator + "menus", getGuiName() + ".yml")
}

