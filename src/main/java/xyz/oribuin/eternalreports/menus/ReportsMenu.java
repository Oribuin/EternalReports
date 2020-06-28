package xyz.oribuin.eternalreports.menus;

import dev.rosewood.guiframework.GuiFactory;
import dev.rosewood.guiframework.GuiFramework;
import dev.rosewood.guiframework.gui.GuiContainer;
import dev.rosewood.guiframework.gui.GuiSize;
import dev.rosewood.guiframework.gui.screen.GuiScreen;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.oribuin.eternalreports.EternalReports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReportsMenu {

    private final EternalReports plugin;
    private final Player player;
    private GuiFramework guiFramework;
    private GuiContainer guiContainer;

    public ReportsMenu(EternalReports plugin, Player player) {
        this.plugin = plugin;
        this.guiContainer = null;
        this.player = player;
    }

    public void openFor() {
        if (this.isInvalid())
            this.buildGui();

        this.guiContainer.openFor(player);
    }

    private void buildGui() {
        this.guiContainer = GuiFactory.createContainer();


        // TODO: make stuff show in the GUI


        guiContainer.addScreen(mainScreen());
        guiContainer.addScreen(myReports());
    }

    // The main menu that shows all open reports.
    private GuiScreen mainScreen() {
        // Define the GUIScreen
        GuiScreen screen =  GuiFactory.createScreen(this.guiContainer, GuiSize.ROWS_SIX)
                .setTitle("User Reports: " + 0); // Todo create report size | this.plugin.getDataManager().getReportSize()

        // Add all the border items
        this.borderList().forEach(integer -> screen.addItemStackAt(integer, borderItem()));

        // TODO: Add all plugin reports
        return screen;
    }


    // The "MyReports" menu that shows all the player's reports
    private GuiScreen myReports() {
        // Define PlayerDAta


        // Define the GUIScreen
        GuiScreen screen =  GuiFactory.createScreen(this.guiContainer, GuiSize.ROWS_SIX)
                .setTitle("Report Size: " + 0); // TODO: Get report total

        this.borderList().forEach(integer -> screen.addItemStackAt(integer, borderItem()));

        // TODO: Add MyReports List
        return screen;
    }

    private boolean isInvalid() {
        return this.guiContainer == null || !this.guiFramework.getGuiManager().getActiveGuis().contains(this.guiContainer);
    }

    private ItemStack borderItem() {
        ItemStack itemStack = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta borderMeta = itemStack.getItemMeta();
        if (borderMeta == null)
            return new ItemStack(Material.AIR);

        borderMeta.setDisplayName(" ");
        borderMeta.addItemFlags(ItemFlag.values());
        itemStack.setItemMeta(borderMeta);

        return itemStack;
    }

    private List<Integer> borderList() {
        List<Integer> slots = new ArrayList<>();

        for (int i = 0; i <= 8; i++) slots.add(i);
        for (int i = 9; i <= 36; i += 9) slots.add(i);
        for (int i = 17; i <= 44; i += 9) slots.add(i);
        for (int i = 45; i <= 53; i++) slots.add(i);
        slots.addAll(Arrays.asList(45, 53));

        return slots;
    }

    private List<Integer> reportList() {
        List<Integer> slots = new ArrayList<>();

        for (int i = 10; i <= 16; i++) slots.add(i);
        for (int i = 19; i <= 25; i++) slots.add(i);
        for (int i = 28; i <= 34; i++) slots.add(i);
        for (int i = 37; i <= 43; i++) slots.add(i);

        return slots;
    }

}
