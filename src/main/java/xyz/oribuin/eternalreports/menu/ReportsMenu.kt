package xyz.oribuin.eternalreports.menu

import dev.rosewood.guiframework.GuiFactory
import dev.rosewood.guiframework.gui.ClickAction
import org.apache.commons.lang.WordUtils.capitalizeFully
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.obj.Report
import xyz.oribuin.eternalreports.hook.PlaceholderAPIHook
import xyz.oribuin.eternalreports.manager.MessageManager
import xyz.oribuin.eternalreports.manager.ReportManager
import xyz.oribuin.eternalreports.util.HexUtils.colorify
import xyz.oribuin.eternalreports.util.PluginUtils
import xyz.oribuin.orilibrary.util.StringPlaceholders

class ReportsMenu(plugin: EternalReports, var sortedType: SortType = SortType.ALL) : Menu(plugin, "report-menu") {

    override fun buildGui() {

        val reports = mutableListOf<Report>()
        this.plugin.getManager(ReportManager::class.java).reports.forEach { reports.add(it) }

        val borderBlock = this.createButton("border-item") { _, _ -> ClickAction.NOTHING }.getItemStack(true) ?: ItemStack(Material.AIR)
        this.setBorder(screen, BorderType.valueOf(menuConfig.getString("border-type") ?: "BORDER"), borderBlock)

        val sortIterator = SortType.values().asList().listIterator()

        this.createButton("forward-page") { _, _ -> ClickAction.PAGE_FORWARDS }
        this.createButton("back-page") { _, _ -> ClickAction.PAGE_BACKWARDS }
        when (sortedType) {
            SortType.RESOLVED -> reports.removeIf { it.isResolved }
            SortType.UNRESOLED -> reports.removeIf { !it.isResolved }
            SortType.LATEST -> reports.sortBy { it.time }
            SortType.OLDEST -> reports.sortByDescending { it.time }
            else -> reports.sortBy { it.id }
        }

        this.screen.setPaginatedSection(GuiFactory.createScreenSection(this.reportSlots()), reports.size) { _, start, end ->
            val results = GuiFactory.createPageContentsResult()


            for (i in start until end.coerceAtMost(reports.size)) {
                val report = reports[i]
                val placeholders = StringPlaceholders.builder()
                    .addPlaceholder("report_id", report.id)
                    .addPlaceholder("sender", report.sender.name)
                    .addPlaceholder("reporter", report.sender.name)
                    .addPlaceholder("reported", report.reported.name)
                    .addPlaceholder("reason", report.reason)
                    .addPlaceholder("resolved", resolvedFormatted(report.isResolved))
                    .addPlaceholder("time", PluginUtils.formatTime(report.time))


                results.addPageContent(this.createButton("report-item", placeholders.build(), addButton = false) { event, _ ->
                    val player = event.whoClicked as Player

                    when (event.click) {
                        ClickType.LEFT -> this.executeCommands("left", placeholders, player)
                        ClickType.RIGHT -> this.executeCommands("right", placeholders, player)
                        else -> {
                        } // Do Nothing
                        // Temporarily Removed.
//                        ClickType.SHIFT_LEFT -> this.executeCommands("shift-left", placeholders, player)
//                        ClickType.SHIFT_RIGHT -> this.executeCommands("shift-right", placeholders, player)
//                        else -> println(event.click.name)
                    }
                    ClickAction.REFRESH
                })
            }
            return@setPaginatedSection results
        }

        // Temporarily Removed.
        this.createButton(
            "switch-filter",
            StringPlaceholders.builder().addPlaceholder("filter", formatEnum(sortedType))
                .addPlaceholder("next", if (sortIterator.hasNext()) formatEnum(sortIterator.next()) else "None")
                .addPlaceholder("previous", if (sortIterator.hasPrevious()) formatEnum(sortIterator.previous()) else "None")
                .build(),
            addButton = false
        )
        { event, _ ->
            val types = SortType.values().toList().listIterator()

            if (event.isLeftClick && types.hasNext()) sortedType = types.next()
            if (event.isRightClick && types.hasPrevious()) sortedType = types.previous()
            ReportsMenu(plugin, sortedType).openGui(listOf(event.whoClicked as Player))
            ClickAction.REFRESH
        }

        super.buildGui()
    }

    private fun reportSlots(): List<Int> {
        val slots = mutableListOf<Int>()
        for (i in 10..16) slots.add(i)
        for (i in 19..25) slots.add(i)
        for (i in 28..34) slots.add(i)
        for (i in 37..43) slots.add(i)
        return slots
    }

    enum class SortType {
        ALL, RESOLVED, UNRESOLED, LATEST, OLDEST
    }

    private fun resolvedFormatted(resolved: Boolean): String? {
        val msg = plugin.getManager(MessageManager::class.java)

        return if (resolved) {
            msg.messageConfig.getString("resolve-formatting.is-resolved")?.let { colorify(it) }
        } else {
            msg.messageConfig.getString("resolve-formatting.isnt-resolved")?.let { colorify(it) }
        }
    }

    private fun executeCommands(path: String, placeholders: StringPlaceholders.Builder, pplayer: Player) {
        menuConfig.getStringList("report-item.player-commands.$path-click-commands").forEach { c: String ->
            pplayer.performCommand(this.format(pplayer, c, placeholders.addPlaceholder("player", pplayer.name).build()))
        }

        menuConfig.getStringList("report-item.console-commands.$path-click-commands").forEach { c: String ->
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.format(pplayer, c, placeholders.addPlaceholder("player", pplayer.name).build()))
        }
    }

    private fun format(player: Player, text: String, placeholders: StringPlaceholders): String {
        return colorify(PlaceholderAPIHook.apply(player, placeholders.apply(text)))
    }

    private fun formatEnum(enum: Enum<*>): String {
        return capitalizeFully(enum.name.toLowerCase().replace("_", " "))
    }
}