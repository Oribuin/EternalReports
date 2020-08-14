package xyz.oribuin.eternalreports.menus

import dev.rosewood.guiframework.GuiFactory
import dev.rosewood.guiframework.GuiFramework
import dev.rosewood.guiframework.gui.ClickAction
import dev.rosewood.guiframework.gui.GuiSize
import dev.rosewood.guiframework.gui.screen.GuiScreen
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.hooks.PlaceholderAPIHook.apply
import xyz.oribuin.eternalreports.managers.ReportManager
import xyz.oribuin.eternalreports.utils.HexUtils.colorify
import xyz.oribuin.eternalreports.utils.StringPlaceholders
import xyz.oribuin.eternalreports.utils.StringPlaceholders.Companion.empty
import java.util.*
import java.util.function.Function

class ReportsMenu(plugin: EternalReports, private val player: Player) : Menu(plugin, "report-menu") {
    private val guiFramework = GuiFramework.instantiate(plugin)
    private val container = GuiFactory.createContainer()

    companion object {
        var instance: ReportsMenu? = null
            private set
    }

    private fun buildGui() {
        container.addScreen(mainMenu())
        guiFramework.guiManager.registerGui(container)
    }

    private fun mainMenu(): GuiScreen {
        val screen = GuiFactory.createScreen(container, GuiSize.ROWS_SIX)
                .setTitle(colorify(this.getValue("menu-name")))

        this.borderSlots().forEach { slot: Int -> getItem("border-item")?.let { screen.addItemStackAt(slot, it) } }
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
                    .setClickAction(Function { event: InventoryClickEvent ->
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
                    .setClickAction(Function { event: InventoryClickEvent ->
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
                        .setClickAction(Function { event: InventoryClickEvent ->
                            val pplayer = event.whoClicked as Player
                            if (menuConfig.getBoolean("use-sound")) {
                                menuConfig.getString("click-sound")?.let { Sound.valueOf(it) }?.let { pplayer.playSound(pplayer.location, it, 100f, 1f) }
                            }

                            when (event.click) {
                                ClickType.LEFT -> {
                                    this.executeCommands("left-click", event, placeholders, pplayer)
                                }
                                ClickType.RIGHT -> {
                                    this.executeCommands("right-click", event, placeholders, pplayer)
                                }
                                ClickType.SHIFT_LEFT -> {
                                    this.executeCommands("shift-left-click", event, placeholders, pplayer)
                                }
                                ClickType.SHIFT_RIGHT -> {
                                    this.executeCommands("shift-right-click", event, placeholders, pplayer)
                                }
                                ClickType.MIDDLE -> {
                                    this.executeCommands("middle-click", event, placeholders, pplayer)
                                }
                                else -> {
                                    // Unused
                                }
                            }

                            ClickAction.CLOSE
                        })

                results.addPageContent(guiButton)
            }

            return@setPaginatedSection results
        }

        return screen
    }

    private fun borderSlots(): List<Int> {
        val slots: MutableList<Int> = ArrayList()
        for (i in 0..8) slots.add(i)
        run {
            var i = 9
            while (i <= 36) {
                slots.add(i)
                i += 9
            }
        }
        run {
            var i = 17
            while (i <= 44) {
                slots.add(i)
                i += 9
            }
        }
        for (i in 45..53) slots.add(i)
        slots.addAll(listOf(45, 53))
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
        if (isInvalid) buildGui()
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

    private fun executeCommands(path: String, event: InventoryClickEvent, placeholders: StringPlaceholders.Builder, pplayer: Player) {
        menuConfig.getStringList("report-item.player-commands.$path-commands").forEach { c: String ->
            pplayer.performCommand(this.format(c, placeholders.addPlaceholder("player", event.whoClicked.name).build()))
        }

        menuConfig.getStringList("report-item.console-commands.$path-commands").forEach { c: String ->
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.format(c, placeholders.addPlaceholder("player", event.whoClicked.name).build()))
        }
    }

    private fun resolvedFormatted(resolved: Boolean): String {
        return if (resolved) {
            this.getValue("resolved-formatting.is-resolved")
        } else {
            this.getValue("resolved-formatting.isnt-resolved")
        }
    }

}