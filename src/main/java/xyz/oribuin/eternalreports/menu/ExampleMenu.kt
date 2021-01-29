package xyz.oribuin.eternalreports.menu

import com.google.common.collect.MultimapBuilder
import dev.rosewood.guiframework.gui.ClickAction
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.data.Report
import xyz.oribuin.eternalreports.manager.ReportManager

class ExampleMenu(plugin: EternalReports) : Menu(plugin, "report-menu") {

    override fun buildGui() {
        val sortedType: SortType

        val reports = mutableListOf<Report>()
        this.plugin.getManager(ReportManager::class.java).reports.forEach { reports.add(it) }

        val borderBlock = this.createButton("border-item") { _, _ -> ClickAction.NOTHING }.getItemStack(true) ?: ItemStack(Material.AIR)
        this.setBorder(screen, BorderType.valueOf(menuConfig.getString("border-type") ?: "BORDER"), borderBlock)

        this.addButton(this.createButton("forward-page") { _, _ -> ClickAction.PAGE_FORWARDS }, "forward-page")
        this.addButton(this.createButton("back-page") { _, _ -> ClickAction.PAGE_BACKWARDS }, "back-page")
        

        super.buildGui()
    }

    enum class SortType {
        ALL, RESOLVED, UNRESOLED, LATEST, OLDEST
    }
}