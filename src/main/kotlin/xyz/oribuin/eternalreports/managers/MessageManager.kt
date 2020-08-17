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
    lateinit var messageConfig: FileConfiguration

    override fun reload() {
        createFile(plugin, MESSAGE_CONFIG)
        messageConfig = YamlConfiguration.loadConfiguration(File(plugin.dataFolder, MESSAGE_CONFIG))

        for (value in MsgSettings.values()) {
            if (messageConfig.get(value.key) == null) {
                messageConfig.set(value.key, value.defaultValue)
            }
            value.load(messageConfig)
        }

        messageConfig.save(File(plugin.dataFolder, MESSAGE_CONFIG))
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

    enum class MsgSettings(val key: String, val defaultValue: Any) {
        // Misc Stuff
        PREFIX("prefix", "<rainbow:0.7>Reports &f» "),
        COOLDOWN("cooldown", "&bYou cannot execute this command for another &f%cooldown% &bseconds!"),
        RELOAD("reload", "&bYou have reloaded EternalReports (&f%version&&b)"),

        // Command success messages
        CMD_REPORT_USER("commands.reported-user", "&bYou have reported &f%player% &bfor &f&n%reason%&b!"),
        CMD_RESOLVED_REPORT("commands.resolved-report", "&bYou have resolved &f%reporter%&b''s&b report! (&f%report_id%&b)"),
        CMD_UNRESOLVED_REPORT("commands.unresolved-report", "&bYou have unresolved &f%reporter%&b''s&b report! (&f%report_id%&b)"),
        CMD_REMOVED_REPORT("commands.removed-report", "&bYou have successfully removed &f%reporter%''s&b report! (&f%report_id%&b)"),

        // Alert Messages
        ALERTS_USER_REPORTED("alerts.user.reported", "&f%sender% &bhas reported &f%player%&b for &f&n%reason%&b!"),
        ALERTS_REPORT_RESOLVED("alerts.report-resolved", "&f%sender% &bhas set &f%player''s&b report as &b%resolved%&f!"),
        ALERTS_REPORT_DELETED("alerts.report-deleted", "&f%sender% &bhas deleted &f%player''s&b report (ID: &f%report_id%&b)"),

        // Error Messages
        INVALID_PERMISSION("invalid-permission", "&cYou do not have permission for this command."),
        INVALID_PLAYER("invalid-player", "&cThat is not a valid player."),
        INVALID_ARGUMENTS("invalid-arguments", "&cYou have provided invalid arguments."),
        INVALID_REPORT("invalid-report", "&cPlease include a valid report id."),
        REPORT_EXISTS("report-exists", "&cThis report already exists."),
        HAS_BYPASS("has-bypass", "&cYou cannot report this player."),
        PLAYER_ONLY("only-player", "&cONly a player can execute this command."),
        UKNOWN_COMMAND("unknown-command", "&cAn unknown command was entered."),

        // "Resolved% Text
        IS_RESOLVED("resolve-formatting.is-resolved", "&fResolved"),
        ISNT_RESOLVED("resolve-formatting.isnt-resolved", "&fUnresolved");

        private var value: Any? = null

        /**
         * Gets the setting as a boolean
         *
         * @return The setting as a boolean
         */
        val boolean: Boolean
            get() = value as Boolean

        /**
         * @return the setting as a String
         */
        val string: String
            get() = value as String

        /**
         * @return the setting as a string list
         */
        val stringList: List<*>
            get() = value as List<*>

        /**
         * Loads the value from the config and caches it
         */
        fun load(config: FileConfiguration) {
            value = config[key]
        }

    }


}