package xyz.oribuin.eternalreports.command.subcommand

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.event.ReportResolveEvent
import xyz.oribuin.eternalreports.manager.ConfigManager
import xyz.oribuin.eternalreports.manager.DataManager
import xyz.oribuin.eternalreports.manager.MessageManager
import xyz.oribuin.eternalreports.manager.ReportManager
import xyz.oribuin.eternalreports.util.HexUtils
import xyz.oribuin.orilibrary.OriCommand
import xyz.oribuin.orilibrary.StringPlaceholders
import xyz.oribuin.orilibrary.SubCommand

class CmdResolve(private val plugin: EternalReports, command: OriCommand) : SubCommand(command, "resolve") {
    override fun executeArgument(sender: CommandSender, args: Array<String>) {
        val messageManager = plugin.getManager(MessageManager::class.java)

        // Check Permission
        if (!sender.hasPermission("eternalreports.resolve")) {
            messageManager.sendMessage(sender, "invalid-permission")
            return
        }

        // Check Argument
        if (args.size == 1) {
            messageManager.sendMessage(sender, "invalid-arguments")
            return
        }

        // Get the reports matching ID
        val reports = plugin.getManager(ReportManager::class.java).reports.filter { report -> report.id == args[1].toInt() }

        // Check if the reports matching id is empty
        if (reports.isEmpty()) {
            messageManager.sendMessage(sender, "invalid-report")
            return
        }

        // Define report if not null
        val report = reports[0]

        // Define placeholders
        val placeholders = StringPlaceholders.builder()
            .addPlaceholder("sender", sender.name)
            .addPlaceholder("reported", report.reported.name)
            .addPlaceholder("reason", report.reason)
            .addPlaceholder("report_id", report.id)
            .addPlaceholder("resolved", resolvedFormatted(report.isResolved)).build()


        val event = ReportResolveEvent(report)

        Bukkit.getPluginManager().callEvent(event)
        if (event.isCancelled) {
            return
        }
        // If the report is resolved
        if (report.isResolved) {
            // Send Message
            messageManager.sendMessage(sender, "commands.unresolved-report", placeholders)

            // Set report as unresolved.
            plugin.getManager(DataManager::class.java).resolveReport(report, false)
            report.isResolved = false

            // Log change in console.
            this.plugin.logger.info(sender.name + " has resolved ${report.sender.name}'s report on ${report.reported.name} for ${report.reason}")

        } else {

            // Send Message
            messageManager.sendMessage(sender, "commands.resolved-report", placeholders)

            // Set report as resolved
            plugin.getManager(DataManager::class.java).resolveReport(report, true)
            report.isResolved = true

            // Log change in console.
            this.plugin.logger.info(sender.name + " has unresolved ${report.sender.name}'s report on ${report.reported.name} for ${report.reason}")
        }

        // Message staff members with alerts
        Bukkit.getOnlinePlayers().stream()
            // Check if the players online has permission for alerts and reports are toggled on.
            .filter { staffMember -> staffMember.hasPermission("eternalreports.alerts") && plugin.toggleList.contains(staffMember.uniqueId) }

            // Send alert messages to all 'staff members'
            .forEach { staffMember ->
                if (ConfigManager.Setting.ALERT_SETTINGS_SOUND_ENABLED.boolean) {
                    staffMember.playSound(staffMember.location, Sound.valueOf(ConfigManager.Setting.ALERT_SETTINGS_SOUND.string), ConfigManager.Setting.ALERT_SETTINGS_SOUND_VOLUME.float, 0f)
                }

                // Actually send the message
                messageManager.sendMessage(staffMember, "alerts.report-resolved", placeholders)
            }
    }

    private fun resolvedFormatted(resolved: Boolean): String? {
        return if (resolved) {
            plugin.getManager(MessageManager::class.java).messageConfig.getString("resolve-formatting.is-resolved")?.let { HexUtils.colorify(it) }
        } else {
            plugin.getManager(MessageManager::class.java).messageConfig.getString("resolve-formatting.isnt-resolved")?.let { HexUtils.colorify(it) }
        }
    }
}