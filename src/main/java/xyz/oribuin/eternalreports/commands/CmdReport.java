package xyz.oribuin.eternalreports.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalreports.EternalReports;
import xyz.oribuin.eternalreports.data.StaffData;
import xyz.oribuin.eternalreports.events.PlayerReportEvent;
import xyz.oribuin.eternalreports.managers.MessageManager;
import xyz.oribuin.eternalreports.utils.OriCommand;
import xyz.oribuin.eternalreports.utils.StringPlaceholders;

import java.util.List;

public class CmdReport extends OriCommand {
    private final EternalReports plugin;

    public CmdReport(EternalReports plugin) {
        super("report");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        MessageManager msg = plugin.getMessageManager();

        // Check if sender is player
        if (!(sender instanceof Player)) {
            msg.sendMessage(sender, "player-only");
            return true;
        }

        Player player = (Player) sender;

        // Check arguments
        if (args.length <= 1) {
            msg.sendMessage(player, "invalid-arguments");
            return true;
        }

        // Reported user
        Player reported = Bukkit.getPlayerExact(args[0]);

        // Check if reported user is null
        if (reported == null) {
            msg.sendMessage(player, "invalid-player");
            return true;
        }

        // Check if the player has permission to bypass report
        if (reported.hasPermission("eternalreports.bypass")) {
            msg.sendMessage(player, "has-bypass");
            return true;
        }


        // Report reason
        final String reason = String.join(" ", args).substring(args[0].length() + 1);

        // Create Placeholders
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("sender", player.getName())
                .addPlaceholder("player", reported.getName())
                .addPlaceholder("reason", reason)
                .build();


        // Send the command sender the report message
        msg.sendMessage(player, "commands.reported-user", placeholders);

        // Message staff members with alerts
        Bukkit.getOnlinePlayers().stream()
                .filter(staffMember -> staffMember.hasPermission("eternalreports.alerts") && new StaffData(staffMember).hasNotifications())
                .forEach(staffMember -> msg.sendMessage(staffMember, "alerts.user-reported", placeholders));


        // Call PlayerReportEvent
        Bukkit.getPluginManager().callEvent(new PlayerReportEvent(player, reported, reason, false));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
