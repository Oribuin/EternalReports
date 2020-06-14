package xyz.oribuin.eternalreports.menus;

import dev.rosewood.guiframework.GuiFactory;
import dev.rosewood.guiframework.GuiFramework;
import dev.rosewood.guiframework.gui.GuiContainer;
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

        List<Integer> BORDER_SLOTS = new ArrayList<>();
        List<Integer> EMOJI_SLOTS = new ArrayList<>();

        for (int i = 0; i <= 8; i++) BORDER_SLOTS.add(i);
        for (int i = 9; i <= 36; i += 9) BORDER_SLOTS.add(i);
        for (int i = 17; i <= 44; i += 9) BORDER_SLOTS.add(i);
        for (int i = 45; i <= 53; i++) BORDER_SLOTS.add(i);

        for (int i = 10; i <= 16; i++) EMOJI_SLOTS.add(i);
        for (int i = 19; i <= 25; i++) EMOJI_SLOTS.add(i);
        for (int i = 28; i <= 34; i++) EMOJI_SLOTS.add(i);
        for (int i = 37; i <= 43; i++) EMOJI_SLOTS.add(i);


        BORDER_SLOTS.addAll(Arrays.asList(45, 53));

        ItemStack borderItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta borderMeta = borderItem.getItemMeta();
        if (borderMeta == null)
            return;

        borderMeta.setDisplayName(" ");
        borderMeta.addItemFlags(ItemFlag.values());
        borderItem.setItemMeta(borderMeta);

        List<String> reportList = new ArrayList<>();
        // TODO: make stuff show in the GUI
    }

    private boolean isInvalid() {
        return this.guiContainer == null || !this.guiFramework.getGuiManager().getActiveGuis().contains(this.guiContainer);
    }


}
