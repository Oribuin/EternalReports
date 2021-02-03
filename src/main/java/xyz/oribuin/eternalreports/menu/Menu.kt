package xyz.oribuin.eternalreports.menu

import dev.rosewood.guiframework.GuiFactory
import dev.rosewood.guiframework.GuiFramework
import dev.rosewood.guiframework.framework.util.GuiUtil
import dev.rosewood.guiframework.gui.*
import dev.rosewood.guiframework.gui.screen.GuiScreen
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.util.HexUtils.colorify
import xyz.oribuin.orilibrary.util.FileUtils.createMenuFile
import xyz.oribuin.orilibrary.util.StringPlaceholders
import java.io.File
import java.util.function.BiFunction
import kotlin.math.max
import kotlin.math.min

abstract class Menu(val plugin: EternalReports, private val guiName: String) {
    var buttons: MutableMap<Int, GuiButton> = mutableMapOf()

    private val framework = GuiFramework.instantiate(plugin)
    private var container: GuiContainer
    var screen: GuiScreen
    var menuConfig: FileConfiguration

    init {
        createMenuFile(plugin, guiName)
        menuConfig = YamlConfiguration.loadConfiguration(menuFile)

        container = GuiFactory.createContainer()
            .setTickRate(min(1, menuConfig.getInt("update-in-ticks")))

        screen = GuiFactory.createScreen(container, GuiSize.valueOf((menuConfig.getString("gui-size") ?: "ROWS_SIX").toUpperCase()))
            .setTitle(colorify(menuConfig.getString("gui-name") ?: guiName))
    }

    private val menuFile: File
        get() = File("${plugin.dataFolder}${File.separator}menus", "$guiName.yml")

    open fun buildGui() {
        this.buttons.forEach { screen.addButtonAt(it.key, it.value) }
        this.buttons.clear()
    }

    fun openGui(players: List<Player>, screen: Int = 0) {
        this.buildGui()

        container.addScreen(this.screen)
        framework.guiManager.registerGui(container)

        players.forEach { container.openFor(it, screen) }
    }

    fun createButton(path: String, placeholders: StringPlaceholders = StringPlaceholders.empty(), addButton: Boolean = true, function: BiFunction<InventoryClickEvent, GuiButton, ClickAction>): GuiButton {
        val icon = GuiFactory.createButton()
            .setIconSupplier { GuiFactory.createIcon(Material.matchMaterial(menuConfig.getString("$path.material") ?: "BARRIER") ?: Material.BARRIER) }
            .setNameSupplier { formatString(menuConfig.getString("$path.name") ?: "&cInvalid Name", placeholders) }
            .setLoreSupplier { menuConfig.getStringList("$path.lore").map { formatString(it, placeholders) } }
            .setAmountSupplier { max(1, menuConfig.getInt("$path.amount")) }

        icon.setClickAction({ function.apply(it, icon) }, *ClickActionType.values())

        if (menuConfig.getBoolean("$path.sound-enabled")) {
            icon.setClickSoundSupplier { Sound.valueOf(menuConfig.getString("$path.sound") ?: Sound.ENTITY_ARROW_HIT_PLAYER.name) }
        }

        if (addButton) screen.addButtonAt(menuConfig.getInt("$path.slot"), icon)
        return icon
    }

    fun addButton(guiButton: GuiButton, path: String) {
        this.buttons[menuConfig.getInt("$path.slot")] = guiButton
    }

    fun setBorder(screen: GuiScreen, borderType: BorderType, itemStack: ItemStack) {
        when (borderType) {
            BorderType.BORDER -> GuiUtil.fillBorders(screen, itemStack)

            BorderType.FILL -> GuiUtil.fillScreen(screen, itemStack)
            // TODO BorderType.CORNERS
            else -> {
                // Do nothing
            }
        }
    }

    private fun formatString(string: String, placeholders: StringPlaceholders): GuiString {
        return GuiFactory.createString(placeholders.apply(colorify(string)))
    }

    enum class BorderType {
        FILL, CORNERS, BORDER
    }

    private val isInvalid: Boolean
        get() = !framework.guiManager.activeGuis.contains(container)
}

