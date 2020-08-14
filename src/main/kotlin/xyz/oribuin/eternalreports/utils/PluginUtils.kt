package xyz.oribuin.eternalreports.utils

import org.bukkit.Bukkit
import xyz.oribuin.eternalreports.managers.ConfigManager
import java.text.SimpleDateFormat
import java.util.*

object PluginUtils {
    @JvmStatic
    fun debug(string: String) {
        val plugin = Bukkit.getPluginManager().getPlugin("EternalReports") ?: return
        if (plugin.config.getBoolean("debug", true)) {
            plugin.logger.info("DEBUG: $string")
        }
    }

    @JvmStatic
    fun formatTime(long: Long) : String {
        return SimpleDateFormat(ConfigManager.Setting.TIME.string).format(Date(long))
    }
}