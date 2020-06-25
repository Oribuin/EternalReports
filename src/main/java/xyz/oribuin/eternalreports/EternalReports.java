package xyz.oribuin.eternalreports;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.oribuin.eternalreports.commands.CmdReport;
import xyz.oribuin.eternalreports.database.DatabaseConnector;
import xyz.oribuin.eternalreports.database.SQLiteConnector;
import xyz.oribuin.eternalreports.managers.ConfigManager;
import xyz.oribuin.eternalreports.managers.DataManager;
import xyz.oribuin.eternalreports.managers.MessageManager;
import xyz.oribuin.eternalreports.utils.OriCommand;

public class EternalReports extends JavaPlugin {

    private static EternalReports instance;
    private DatabaseConnector connector;
    private ConfigManager configManager;
    private DataManager dataManager;
    private MessageManager messageManager;

    public static EternalReports getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.connector = new SQLiteConnector(this);

        // Register all the commands
        this.registerCommands(new CmdReport(this));

        // Register Managers
        this.configManager = new ConfigManager(this);
        this.dataManager = new DataManager(this);
        this.messageManager = new MessageManager(this);

        this.saveDefaultConfig();
        this.reload();
    }

    public void reload() {
        this.configManager.reload();
        this.messageManager.reload();
        this.dataManager.reload();
    }

    private void registerCommands(OriCommand... commands) {
        for (OriCommand cmd : commands) {
            cmd.registerCommand();
        }
    }

    public DatabaseConnector getConnector() {
        return connector;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }


}
