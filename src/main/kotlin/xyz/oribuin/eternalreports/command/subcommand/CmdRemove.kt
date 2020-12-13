package xyz.oribuin.eternalreports.command.subcommand

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.data.Report
import xyz.oribuin.eternalreports.event.ReportDeleteEvent
import xyz.oribuin.eternalreports.manager.ConfigManager
import xyz.oribuin.eternalreports.manager.DataManager
import xyz.oribuin.eternalreports.manager.MessageManager
import xyz.oribuin.eternalreports.manager.ReportManager
import xyz.oribuin.orilibrary.OriCommand
import xyz.oribuin.orilibrary.StringPlaceholders
import xyz.oribuin.orilibrary.SubCommand

class CmdRemove(val plugin: EternalReports, command: OriCommand) : SubCommand(command, "remove", "delete") {
    private val messageManager = plugin.getManager(MessageManager::class.java)
    private val cooldowns = mutableMapOf<CommandSender, Long>()

    override fun executeArgument(sender: CommandSender, args: Array<String>) {
        // Check permission
        if (!sender.hasPermission("eternalreports.delete")) {
            messageManager.sendMessage(sender, "invalid-permission")
            return
        }

        // Check args
        if (args.size == 1) {
            messageManager.sendMessage(sender, "invalid-arguments")
            return
        }

        // Get reports matching ID
        val reports = plugin.getManager(ReportManager::class.java).reports.filter { report -> report.id == args[1].toInt() }

        // Check if there aren't any matchingg
        if (reports.isEmpty()) {
            messageManager.sendMessage(sender, "invalid-report")
            return
        }

        // Get report if not null
        val report = reports[0]

        // Placeholders
        val placeholders = StringPlaceholders.builder()
            .addPlaceholder("sender", sender.name)
            .addPlaceholder("reported", report.reported.name)
            .addPlaceholder("reason", report.reason)
            .addPlaceholder("report_id", report.id).build()


        // Confirmation
        if (cooldowns.containsKey(sender)) {
            val secondsLeft = (cooldowns[sender] ?: return).div(1000).plus(30L).minus(System.currentTimeMillis().div(1000))
            if (secondsLeft > 0) {
                this.runReportDeletion(messageManager, sender, placeholders, report)
                return
            }
        }

        cooldowns[sender] = System.currentTimeMillis()
        messageManager.sendMessage(sender, "command.removed-confirm", placeholders)
    }

    private fun runReportDeletion(messageManager: MessageManager, sender: CommandSender, placeholders: StringPlaceholders, report: Report) {
        val event = ReportDeleteEvent(report)
        Bukkit.getPluginManager().callEvent(event)
        if (event.isCancelled) {
            return
        }

        // Send message
        messageManager.sendMessage(sender, "commands.removed-report", placeholders)

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
                messageManager.sendMessage(staffMember, "alerts.report-deleted", placeholders)
            }


        // Delete report
        plugin.getManager(DataManager::class.java).deleteReport(report)
    }

}