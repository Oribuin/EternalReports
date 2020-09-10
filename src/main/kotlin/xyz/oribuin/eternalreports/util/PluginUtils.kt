package xyz.oribuin.eternalreports.util

import xyz.oribuin.eternalreports.manager.ConfigManager
import java.text.SimpleDateFormat
import java.util.*

object PluginUtils {

    @JvmStatic
    fun formatTime(long: Long): String {
        return SimpleDateFormat(ConfigManager.Setting.TIME.string).format(Date(long))
    }
}