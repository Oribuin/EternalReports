package xyz.oribuin.eternalreports

import org.bukkit.Bukkit
import xyz.oribuin.eternalreports.command.CmdReport
import xyz.oribuin.eternalreports.command.CmdReports
import xyz.oribuin.eternalreports.hook.PlaceholderAPIHook
import xyz.oribuin.eternalreports.hook.PlaceholderExp
import xyz.oribuin.eternalreports.listener.PlayerJoin
import xyz.oribuin.eternalreports.manager.*
import xyz.oribuin.orilibrary.OriPlugin
import xyz.oribuin.orilibrary.util.FileUtils
import java.util.*

/**
 * @author Oribuin
 */

class EternalReports : OriPlugin() {
    val toggleList = mutableSetOf<UUID>()


    override fun enablePlugin() {
        // Register all the managers
        Bukkit.getScheduler().runTaskAsynchronously(this, Runnable {
            this.getManager(ConfigManager::class.java)
            this.getManager(DataManager::class.java)
            this.getManager(GuiManager::class.java)
            this.getManager(MessageManager::class.java)
            this.getManager(ReportManager::class.java)
        })

        FileUtils.createMenuFile(this, "report-menu")

        CmdReport(this).register()
        CmdReports(this).register()

        // Register all the listeners
        server.pluginManager.registerEvents(PlayerJoin(this), this)

        if (PlaceholderAPIHook.enabled()) {
            PlaceholderExp(this).register()
        }
    }

    override fun disablePlugin() {

    }
}