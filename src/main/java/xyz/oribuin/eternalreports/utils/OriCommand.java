package xyz.oribuin.eternalreports.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.*;

import java.util.List;

public abstract class OriCommand implements TabExecutor {

    private final String commandName;

    public OriCommand(String commandName) {
        this.commandName = commandName;
    }

    // In Theory: Register the command.
    public void registerCommand() {
        PluginCommand cmd = Bukkit.getPluginCommand(this.getName());

        if (cmd != null) {
            cmd.setExecutor(this);
            cmd.setTabCompleter(this);
        }
    }

    // Register Command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }


    // Register command tab complete
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    // Get Command Name
    public String getName() {
        return commandName;
    }
}
