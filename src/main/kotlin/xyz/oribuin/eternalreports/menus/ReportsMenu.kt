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
import xyz.oribuin.eternalreports.hooks.PlaceholderAPIHook
import xyz.oribuin.eternalreports.utils.HexUtils
import xyz.oribuin.eternalreports.utils.StringPlaceholders
import java.util.function.Function

class ReportsMenu(private val player: Player) : Menu("report-menu") {
    private val guiFramework: GuiFramework = GuiFramework.instantiate(plugin)
    private val guiContainer = GuiFactory.createContainer()

    companion object {
        var instance: ReportsMenu? = null
            private set
    }


    fun openGui() {
        if (isInvalid) buildGui()
        guiContainer.openFor(player)
    }

    private fun buildGui() {
        instance = this
        guiContainer.addScreen(globalReports())
        guiFramework.guiManager.registerGui(guiContainer)
    }

    private fun globalReports(): GuiScreen {
        val guiScreen = GuiFactory.createScreen(guiContainer, GuiSize.ROWS_SIX)
                .setTitle(HexUtils.colorify(this.getValue("menu-name")))

        this.borderSlots().forEach { slot: Int -> guiScreen.addItemStackAt(slot, getItem("border-item")) }

        if (plugin.reportManager.globalReportCount == 0) {
            guiScreen.addItemStackAt(menuConfig.getInt("no-reports.slot"), getItem("no-reports"))
        }

        val reports = plugin.reportManager.reports

        // TODO: Add Filter Button (Requires other GuiScreens)
        /*
        if (menuConfig.getString("filter-button") != null && menuConfig.getBoolean("filter-button.enabled")) {

            val placeholders = StringPlaceholders.builder()
                    .addPlaceholder("filter", resolveFilter(guiScreen))
                    .addPlaceholder("next_filter", resolveNextFilter(guiScreen))
                    .addPlaceholder("last_filter", resolveLastFilter(guiScreen))
                    .build()

            val lore = mutableListOf<String>()
            for (string in menuConfig.getStringList("filter-button.lore"))
                lore.add(this.format(string, placeholders))

            guiScreen.addButtonAt(menuConfig.getInt("filter-button.slot"), GuiFactory.createButton()
                    .setName(this.getValue("filter-button.name", placeholders))
                    .setLore(lore)
                    .setIcon(Material.valueOf(this.getValue("filter-button.material")))
                    .setGlowing(menuConfig.getBoolean("filter-button.glowing"))
                    .setClickAction(Function { event: InventoryClickEvent ->
                        val pplayer = event.whoClicked as Player
                        if (menuConfig.getBoolean("use-sound")) {
                            menuConfig.getString("click-sound")?.let { Sound.valueOf(it) }?.let { pplayer.playSound(pplayer.location, it, 100f, 1f) }
                        }

                        if (event.isLeftClick) {
                            ClickAction.TRANSITION_FORWARDS
                        } else if (event.isRightClick) {
                            ClickAction.TRANSITION_BACKWARDS
                        }

                        ClickAction.REFRESH
                    }))
        }

         */

        // Add forward page
        if (menuConfig.getString("forward-page") != null) {
            val lore = mutableListOf<String>()
            for (string in menuConfig.getStringList("forward-page.lore"))
                lore.add(this.format(string, StringPlaceholders.empty()))

            guiScreen.addButtonAt(menuConfig.getInt("forward-page.slot"), GuiFactory.createButton()
                    .setName(this.getValue("forward-page.name", StringPlaceholders.empty()))
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
                lore.add(this.format(string, StringPlaceholders.empty()))

            guiScreen.addButtonAt(menuConfig.getInt("back-page.slot"), GuiFactory.createButton()
                    .setName(this.getValue("back-page.name", StringPlaceholders.empty()))
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

        guiScreen.setPaginatedSection(GuiFactory.createScreenSection(reportSlots()), reports.size) { _: Int, startIndex: Int, endIndex: Int ->
            val results = GuiFactory.createPageContentsResult()
            for (i in startIndex until endIndex.coerceAtMost(reports.size)) {
                val report = reports[i]


                val placeholders = StringPlaceholders.builder()
                        .addPlaceholder("id", report.id)
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

        return guiScreen
    }

    private val isInvalid: Boolean get() = !guiFramework.guiManager.activeGuis.contains(guiContainer)

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
        val reportSlots: MutableList<Int> = ArrayList()
        for (i in 10..16) reportSlots.add(i)
        for (i in 19..25) reportSlots.add(i)
        for (i in 28..34) reportSlots.add(i)
        for (i in 37..43) reportSlots.add(i)
        return reportSlots
    }

    // Get value config path formatted
    private fun getValue(configPath: String): String {
        return menuConfig.getString(configPath)?.let { HexUtils.colorify(it) }?.let { PlaceholderAPIHook.apply(player, it) }!!
    }

    private fun getValue(configPath: String, placeholders: StringPlaceholders): String {
        return menuConfig.getString(configPath)?.let { HexUtils.colorify(placeholders.apply(it)) }?.let { PlaceholderAPIHook.apply(player, it) }!!
    }

    private fun format(string: String, placeholders: StringPlaceholders): String {
        return HexUtils.colorify(PlaceholderAPIHook.apply(player, placeholders.apply(string)))
    }

    private fun getItem(configPath: String): ItemStack {
        val itemStack = menuConfig.getString("$configPath.material")?.let { Material.valueOf(it) }?.let { ItemStack(it) }
        val itemMeta = itemStack?.itemMeta ?: return ItemStack(Material.AIR)

        itemMeta.setDisplayName(this.getValue("$configPath.name"))

        val lore = mutableListOf<String>()
        for (string in menuConfig.getStringList("$configPath.lore"))
            lore.add(this.format(string, StringPlaceholders.empty()))
        itemMeta.lore = lore

        if (menuConfig.getBoolean("$configPath.glowing")) {
            itemMeta.addEnchant(Enchantment.MENDING, 1, true)
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }

        for (itemFlag in menuConfig.getStringList("$configPath.item-flags"))
            itemMeta.addItemFlags(ItemFlag.valueOf(itemFlag))

        itemStack.itemMeta = itemMeta
        return itemStack
    }

    private fun resolvedFormatted(resolved: Boolean): String {
        if (resolved) {
            return this.getValue("resolved-formatting.is-resolved")
        } else {
            return this.getValue("resolved-formatting.isnt-resolved")
        }
    }

    private fun executeCommands(path: String, event: InventoryClickEvent, placeholders: StringPlaceholders.Builder, pplayer: Player) {
        menuConfig.getStringList("report-item.player-commands.$path-commands").forEach { c: String ->
            pplayer.performCommand(this.format(c, placeholders.addPlaceholder("player", event.whoClicked.name).build()))
        }

        menuConfig.getStringList("report-item.console-commands.$path-commands").forEach { c: String ->
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.format(c, placeholders.addPlaceholder("player", event.whoClicked.name).build()))
        }
    }
}
