package xyz.oribuin.eternalreports.managers

import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.hooks.PlaceholderAPIHook
import xyz.oribuin.eternalreports.utils.FileUtils.createFile
import xyz.oribuin.eternalreports.utils.HexUtils.colorify
import xyz.oribuin.eternalreports.utils.StringPlaceholders
import xyz.oribuin.eternalreports.utils.StringPlaceholders.Companion.empty
import java.io.File

class MessageManager(plugin: EternalReports) : Manager(plugin) {
    private lateinit var messageConfig: FileConfiguration

    override fun reload() {
        createFile(plugin, MESSAGE_CONFIG)
        messageConfig = YamlConfiguration.loadConfiguration(File(plugin.dataFolder, MESSAGE_CONFIG))
        StringPlaceholders.apply { }
    }


    @JvmOverloads
    fun sendMessage(sender: CommandSender, messageId: String, placeholders: StringPlaceholders = empty()) {
        if (messageConfig.getString(messageId) == null) {
            sender.spigot().sendMessage(*TextComponent.fromLegacyText(colorify("#ff4072$messageId is null in messages.yml")))
            return
        }

        if (messageConfig.getString(messageId)!!.isNotEmpty()) {
            val msg = messageConfig.getString("prefix") + placeholders.apply(messageConfig.getString(messageId)!!)
            sender.spigot().sendMessage(*TextComponent.fromLegacyText(colorify(parsePlaceholders(sender, msg))))
        }
    }

    private fun parsePlaceholders(sender: CommandSender, message: String): String {
        return if (sender is Player)
            PlaceholderAPIHook.apply(sender, message)
        else
            message
    }

    companion object {
        private const val MESSAGE_CONFIG = "messages.yml"
    }

    override fun disable() {
        // Unused
    }
}