package xyz.oribuin.eternalreports.utils

import xyz.oribuin.eternalreports.managers.ConfigManager
import java.text.SimpleDateFormat
import java.util.*

object PluginUtils {

    @JvmStatic
    fun formatTime(long: Long): String {
        return SimpleDateFormat(ConfigManager.Setting.TIME.string).format(Date(long))
    }
}