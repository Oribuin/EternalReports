package xyz.oribuin.eternalreports.menu

import dev.rosewood.guiframework.GuiFactory
import dev.rosewood.guiframework.GuiFramework
import dev.rosewood.guiframework.gui.ClickAction
import dev.rosewood.guiframework.gui.ClickActionType
import dev.rosewood.guiframework.gui.GuiSize
import dev.rosewood.guiframework.gui.screen.GuiScreen
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.hook.PlaceholderAPIHook.apply
import xyz.oribuin.eternalreports.manager.MessageManager
import xyz.oribuin.eternalreports.manager.ReportManager
import xyz.oribuin.eternalreports.util.FileUtils
import xyz.oribuin.eternalreports.util.HexUtils.colorify
import xyz.oribuin.eternalreports.util.PluginUtils
import xyz.oribuin.eternalreports.util.StringPlaceholders
import xyz.oribuin.eternalreports.util.StringPlaceholders.Companion.empty
import java.util.*

class ReportsMenu(plugin: EternalReports, private val player: Player) : Menu(plugin, "report-menu") {

    private val guiFramework = GuiFramework.instantiate(plugin)
    private val container = GuiFactory.createContainer().setTickRate(menuConfig.getInt("tick-update-rate"))

    companion object {
        var instance: ReportsMenu? = null
            private set
    }

    private fun buildGui() {
        FileUtils.createMenuFile(plugin, "report-menu")

        container.addScreen(mainMenu())
        guiFramework.guiManager.registerGui(container)
    }

    private fun mainMenu(): GuiScreen {
        val screen = GuiFactory.createScreen(container, GuiSize.ROWS_SIX)
                .setTitle(colorify(this.getValue("menu-name")))

        this.borderSlots().forEach { slot -> getItem("border-item")?.let { screen.addItemStackAt(slot, it) } }

        val reports = plugin.getManager(ReportManager::class).reports

        if (reports.size == 0) {
            getItem("no-reports")?.let { screen.addItemStackAt(menuConfig.getInt("no-reports.slot"), it) }
        }

        // Add forward page
        if (menuConfig.getString("forward-page") != null) {
            val lore = mutableListOf<String>()
            for (string in menuConfig.getStringList("forward-page.lore"))
                lore.add(this.format(string, empty()))

            screen.addButtonAt(menuConfig.getInt("forward-page.slot"), GuiFactory.createButton()
                    .setName(this.getValue("forward-page.name", empty()))
                    .setLore(lore)
                    .setIcon(Material.valueOf(this.getValue("forward-page.material")))
                    .setGlowing(menuConfig.getBoolean("forward-page.glowing"))
                    .setClickAction({ event: InventoryClickEvent ->
                        val pplayer = event.whoClicked as Player
                        if (menuConfig.getBoolean("use-sound")) {
                            menuConfig.getString("click-sound")?.let { Sound.valueOf(it) }?.let { pplayer.playSound(pplayer.location, it, 100f, 1f) }
                        }

                        ClickAction.PAGE_FORWARDS
                    }))
        }

        // Add back page
        if (menuConfig.getString("back-page") != null) {
            val lore = mutableListOf<String>()
            for (string in menuConfig.getStringList("back-page.lore"))
                lore.add(this.format(string, empty()))

            screen.addButtonAt(menuConfig.getInt("back-page.slot"), GuiFactory.createButton()
                    .setName(this.getValue("back-page.name", empty()))
                    .setLore(lore)
                    .setIcon(Material.valueOf(this.getValue("back-page.material")))
                    .setGlowing(menuConfig.getBoolean("back-page.glowing"))
                    .setClickAction({ event: InventoryClickEvent ->
                        val pplayer = event.whoClicked as Player
                        if (menuConfig.getBoolean("use-sound")) {
                            menuConfig.getString("click-sound")?.let { Sound.valueOf(it) }?.let { pplayer.playSound(pplayer.location, it, 100f, 1f) }
                        }

                        ClickAction.PAGE_BACKWARDS
                    }))
        }

        screen.setPaginatedSection(GuiFactory.createScreenSection(reportSlots()), reports.size) { _: Int, startIndex: Int, endIndex: Int ->
            val results = GuiFactory.createPageContentsResult()
            for (i in startIndex until endIndex.coerceAtMost(reports.size)) {
                val report = reports[i]


                val placeholders = StringPlaceholders.builder()
                        .addPlaceholder("report_id", report.id)
                        .addPlaceholder("sender", report.sender.name)
                        .addPlaceholder("reported", report.reported.name)
                        .addPlaceholder("reason", report.reason)
                        .addPlaceholder("resolved", resolvedFormatted(report.isResolved))
                        .addPlaceholder("time", PluginUtils.formatTime(report.time))

                val lore = mutableListOf<String>()
                for (string in menuConfig.getStringList("report-item.lore"))
                    lore.add(this.format(string, placeholders.build()))

                val guiButton = GuiFactory.createButton()
                        .setName(this.getValue("report-item.name", placeholders.build()))
                        .setLore(lore)
                        .setIcon(Material.PLAYER_HEAD) { itemMeta: ItemMeta ->
                            val meta = itemMeta as SkullMeta
                            meta.owningPlayer = report.reported
                        }
                        .setGlowing(menuConfig.getBoolean("report-item.glowing"))

                        .setClickAction({ event: InventoryClickEvent ->
                            val pplayer = event.whoClicked as Player
                            if (menuConfig.getBoolean("use-sound")) {
                                menuConfig.getString("click-sound")?.let { Sound.valueOf(it) }?.let { pplayer.playSound(pplayer.location, it, 100f, 1f) }
                            }

                            this.executeCommands("left-click", placeholders, pplayer)

                            ClickAction.CLOSE

                        }, ClickActionType.LEFT_CLICK)
                        .setClickAction({ event: InventoryClickEvent ->
                            val pplayer = event.whoClicked as Player
                            if (menuConfig.getBoolean("use-sound")) {
                                menuConfig.getString("click-sound")?.let { Sound.valueOf(it) }?.let { pplayer.playSound(pplayer.location, it, 100f, 1f) }
                            }

                            this.executeCommands("right-click", placeholders, pplayer)

                            ClickAction.CLOSE

                        }, ClickActionType.RIGHT_CLICK)
                        .setClickAction({ event: InventoryClickEvent ->
                            val pplayer = event.whoClicked as Player
                            if (menuConfig.getBoolean("use-sound")) {
                                menuConfig.getString("click-sound")?.let { Sound.valueOf(it) }?.let { pplayer.playSound(pplayer.location, it, 100f, 1f) }
                            }

                            this.executeCommands("shift-left-click", placeholders, pplayer)

                            ClickAction.CLOSE

                        }, ClickActionType.SHIFT_LEFT_CLICK)
                        .setClickAction({ event: InventoryClickEvent ->
                            val pplayer = event.whoClicked as Player
                            if (menuConfig.getBoolean("use-sound")) {
                                menuConfig.getString("click-sound")?.let { Sound.valueOf(it) }?.let { pplayer.playSound(pplayer.location, it, 100f, 1f) }
                            }

                            this.executeCommands("shift-right-click", placeholders, pplayer)

                            ClickAction.CLOSE

                        }, ClickActionType.SHIFT_RIGHT_CLICK)

                results.addPageContent(guiButton)
            }

            return@setPaginatedSection results
        }
        return screen
    }

    private fun borderSlots(): List<Int> {
        val slots: MutableList<Int> = ArrayList()
        for (i in 0..8) slots.add(i)
        slots.add(9)
        slots.add(17)
        slots.add(18)
        slots.add(26)
        slots.add(27)
        slots.add(35)
        slots.add(36)
        slots.add(44)
        for (i in 45..53) slots.add(i)
        return slots
    }

    private fun reportSlots(): List<Int> {
        val slots: MutableList<Int> = ArrayList()
        for (i in 10..16) slots.add(i)
        for (i in 19..25) slots.add(i)
        for (i in 28..34) slots.add(i)
        for (i in 37..43) slots.add(i)
        return slots
    }

    fun openMenu() {
        if (isInvalid)
            buildGui()

        container.openFor(player)
    }

    private val isInvalid: Boolean
        get() = !guiFramework.guiManager.activeGuis.contains(container)

    private fun getValue(path: String): String {
        val value = menuConfig.getString(path) ?: "Invalid path $path"

        return colorify(apply(player, value))
    }

    private fun getValue(path: String, placeholders: StringPlaceholders): String {
        val value = menuConfig.getString(path) ?: "Invalid path $path"

        return colorify(placeholders.apply(apply(player, value)))
    }

    private fun getItem(path: String): ItemStack? {
        val itemStack = menuConfig.getString("$path.material")?.let { Material.valueOf(it) }?.let { ItemStack(it) }

        val itemMeta = itemStack?.itemMeta ?: return ItemStack(Material.STRUCTURE_VOID)

        itemMeta.setDisplayName(this.getValue("$path.name"))

        val lore: MutableList<String> = ArrayList()
        for (line in menuConfig.getStringList("$path.lore")) lore.add(this.format(line, empty()))

        itemMeta.lore = lore
        if (menuConfig.getBoolean("$path.glowing")) {
            itemMeta.addEnchant(Enchantment.MENDING, 1, true)
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }

        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        itemStack.itemMeta = itemMeta
        return itemStack
    }

    private fun format(text: String, placeholders: StringPlaceholders): String {
        return colorify(apply(player, placeholders.apply(text)))
    }

    private fun executeCommands(path: String, placeholders: StringPlaceholders.Builder, pplayer: Player) {
        menuConfig.getStringList("report-item.player-commands.$path-commands").forEach { c: String ->
            pplayer.performCommand(this.format(c, placeholders.addPlaceholder("player", pplayer.name).build()))
        }

        menuConfig.getStringList("report-item.console-commands.$path-commands").forEach { c: String ->
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.format(c, placeholders.addPlaceholder("player", pplayer.name).build()))
        }
    }

    private fun resolvedFormatted(resolved: Boolean): String? {
        val msg = plugin.getManager(MessageManager::class)

        return if (resolved) {
            msg.messageConfig.getString("resolve-formatting.is-resolved")?.let { apply(player, it) }?.let { colorify(it) }
        } else {
            msg.messageConfig.getString("resolve-formatting.isnt-resolved")?.let { apply(player, it) }?.let { colorify(it) }
        }
    }

}