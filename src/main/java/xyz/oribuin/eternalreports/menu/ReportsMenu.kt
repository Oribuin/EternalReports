package xyz.oribuin.eternalreports.menu

import org.bukkit.entity.Player
import xyz.oribuin.eternalreports.EternalReports

class ReportsMenu(plugin: EternalReports, private val player: Player) : Menu(plugin, "report-menu") {
    override fun buildGui() {

    }
//
//    private val guiFramework = GuiFramework.instantiate(plugin)
//    override val container = GuiFactory.createContainer().setTickRate(menuConfig.getInt("tick-update-rate"))
//
//    companion object {
//        var instance: ReportsMenu? = null
//            private set
//    }
//
//    override fun buildGui() {
//        container.addScreen(mainMenu())
//        guiFramework.guiManager.registerGui(container)
//    }
//
//    private fun mainMenu(): GuiScreen {
//        val screen = GuiFactory.createScreen(container, GuiSize.ROWS_SIX)
//            .setTitle(colorify(this.getValue("menu-name")))
//
//        this.borderSlots().forEach { slot -> getItem("border-item").let { screen.addItemStackAt(slot, it) } }
//
//        val reports = plugin.getManager(ReportManager::class.java).reports
//
//        if (reports.size == 0) {
//            getItem("no-reports").let { screen.addItemStackAt(menuConfig.getInt("no-reports.slot"), it) }
//        }
//
//        // Add forward page
//        if (menuConfig.getString("forward-page") != null) {
//            val lore = mutableListOf<String>()
//            for (string in menuConfig.getStringList("forward-page.lore"))
//                lore.add(this.format(string, empty()))
//
//            screen.addButtonAt(
//                menuConfig.getInt("forward-page.slot"), GuiFactory.createButton(this.getItem("forward-page"))
//                    .setClickAction({ event ->
//                        val pplayer = event.whoClicked as Player
//                        if (menuConfig.getBoolean("use-sound")) {
//                            menuConfig.getString("click-sound")?.let { Sound.valueOf(it) }?.let { pplayer.playSound(pplayer.location, it, 100f, 1f) }
//                        }
//
//                        ClickAction.PAGE_FORWARDS
//                    })
//            )
//        }
//
//        // Add back page
//        if (menuConfig.getString("back-page") != null) {
//            val lore = mutableListOf<String>()
//            for (string in menuConfig.getStringList("back-page.lore"))
//                lore.add(this.format(string, empty()))
//
//            screen.addButtonAt(
//                menuConfig.getInt("back-page.slot"), GuiFactory.createButton()
//                    .setName(this.getValue("back-page.name", empty()))
//                    .setLore(lore)
//                    .setIcon(Material.valueOf(this.getValue("back-page.material")))
//                    .setGlowing(menuConfig.getBoolean("back-page.glowing"))
//                    .setClickAction({ event ->
//                        val pplayer = event.whoClicked as Player
//                        if (menuConfig.getBoolean("use-sound")) {
//                            menuConfig.getString("click-sound")?.let { Sound.valueOf(it) }?.let { pplayer.playSound(pplayer.location, it, 100f, 1f) }
//                        }
//
//                        ClickAction.PAGE_BACKWARDS
//                    })
//            )
//        }
//
//        screen.setPaginatedSection(GuiFactory.createScreenSection(reportSlots()), reports.size) { _: Int, startIndex: Int, endIndex: Int ->
//            val results = GuiFactory.createPageContentsResult()
//            for (i in startIndex until endIndex.coerceAtMost(reports.size)) {
//                val report = reports[i]
//
//
//                val placeholders = StringPlaceholders.builder()
//                    .addPlaceholder("report_id", report.id)
//                    .addPlaceholder("sender", report.sender.name)
//                    .addPlaceholder("reporter", report.sender.name)
//                    .addPlaceholder("reported", report.reported.name)
//                    .addPlaceholder("reason", report.reason)
//                    .addPlaceholder("resolved", resolvedFormatted(report.isResolved))
//                    .addPlaceholder("time", PluginUtils.formatTime(report.time))
//
//                val lore = mutableListOf<String>()
//                for (string in menuConfig.getStringList("report-item.lore"))
//                    lore.add(this.format(string, placeholders.build()))
//
//                val guiButton = GuiFactory.createButton()
//                    .setName(this.getValue("report-item.name", placeholders.build()))
//                    .setLore(lore)
//                    .setIcon(Material.PLAYER_HEAD) { itemMeta: ItemMeta ->
//                        val meta = itemMeta as SkullMeta
//                        meta.owningPlayer = report.reported
//                    }
//                    .setGlowing(menuConfig.getBoolean("report-item.glowing"))
//                    .setClickAction({
//                        val pplayer = it.whoClicked as Player
//                        if (menuConfig.getBoolean("use-sound")) {
//                            menuConfig.getString("click-sound")?.let { Sound.valueOf(it) }?.let { pplayer.playSound(pplayer.location, it, 100f, 1f) }
//                        }
//                        when (it.click) {
//                            ClickType.LEFT -> {
//                                this.executeCommands("left-click", placeholders, pplayer)
//                            }
//                            ClickType.RIGHT -> {
//                                this.executeCommands("right-click", placeholders, pplayer)
//                            }
//
//                            ClickType.SHIFT_LEFT -> {
//                                this.executeCommands("shift-left-click", placeholders, pplayer)
//                            }
//
//                            ClickType.SHIFT_RIGHT -> {
//                                this.executeCommands("shift-right-click", placeholders, pplayer)
//                            }
//                            else -> {
//                                // Unused
//                            }
//                        }
//
//                        ClickAction.CLOSE
//                    })
//
//                results.addPageContent(guiButton)
//            }
//
//            return@setPaginatedSection results
//        }
//        return screen
//    }
//
//    private fun borderSlots(): List<Int> {
//        val slots = mutableListOf<Int>()
//        for (i in 0..8) slots.add(i)
//        slots.add(9)
//        slots.add(17)
//        slots.add(18)
//        slots.add(26)
//        slots.add(27)
//        slots.add(35)
//        slots.add(36)
//        slots.add(44)
//        for (i in 45..53) slots.add(i)
//        return slots
//    }
//
//    private fun reportSlots(): List<Int> {
//        val slots = mutableListOf<Int>()
//        for (i in 10..16) slots.add(i)
//        for (i in 19..25) slots.add(i)
//        for (i in 28..34) slots.add(i)
//        for (i in 37..43) slots.add(i)
//        return slots
//    }
//
//    fun openMenu() {
//        if (isInvalid)
//            buildGui()
//
//        container.openFor(player)
//    }
//
//    private val isInvalid: Boolean
//        get() = !guiFramework.guiManager.activeGuis.contains(container)
//
//    private fun getValue(path: String): String {
//        val value = menuConfig.getString(path) ?: "Invalid path $path"
//
//        return colorify(apply(player, value))
//    }
//
//    private fun getValue(path: String, placeholders: StringPlaceholders): String {
//        val value = menuConfig.getString(path) ?: "Invalid path $path"
//
//        return colorify(placeholders.apply(apply(player, value)))
//    }
//
//    private fun getItem(path: String): ItemStack {
//        val itemStack = menuConfig.getString("$path.material")?.let { Material.valueOf(it) }?.let { ItemStack(it) }
//
//        val itemMeta = itemStack?.itemMeta ?: return ItemStack(Material.STRUCTURE_VOID)
//
//        itemMeta.setDisplayName(this.getValue("$path.name"))
//
//        val lore: MutableList<String> = ArrayList()
//        for (line in menuConfig.getStringList("$path.lore")) lore.add(this.format(line, empty()))
//
//        itemMeta.lore = lore
//        if (menuConfig.getBoolean("$path.glowing")) {
//            itemMeta.addEnchant(Enchantment.MENDING, 1, true)
//            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
//        }
//
//        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
//        itemStack.itemMeta = itemMeta
//        return itemStack
//    }
//
//    private fun format(text: String, placeholders: StringPlaceholders): String {
//        return colorify(apply(player, placeholders.apply(text)))
//    }
//
//    private fun executeCommands(path: String, placeholders: StringPlaceholders.Builder, pplayer: Player) {
//        menuConfig.getStringList("report-item.player-commands.$path-commands").forEach { c: String ->
//            pplayer.performCommand(this.format(c, placeholders.addPlaceholder("player", pplayer.name).build()))
//        }
//
//        menuConfig.getStringList("report-item.console-commands.$path-commands").forEach { c: String ->
//            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.format(c, placeholders.addPlaceholder("player", pplayer.name).build()))
//        }
//    }
//
//    private fun resolvedFormatted(resolved: Boolean): String? {
//        val msg = plugin.getManager(MessageManager::class.java)
//
//        return if (resolved) {
//            msg.messageConfig.getString("resolve-formatting.is-resolved")?.let { apply(player, it) }?.let { colorify(it) }
//        } else {
//            msg.messageConfig.getString("resolve-formatting.isnt-resolved")?.let { apply(player, it) }?.let { colorify(it) }
//        }
//    }

}