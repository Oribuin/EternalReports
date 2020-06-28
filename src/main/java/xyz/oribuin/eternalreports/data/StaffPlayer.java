package xyz.oribuin.eternalreports.data;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StaffPlayer {
    private final Player player;
    private final Map<UUID, Boolean> toggleMap;

    public StaffPlayer(Player player) {
        this.player = player;
        this.toggleMap = new HashMap<>();
    }

    public Player getPlayer() {
        return player;
    }

    public Map<UUID, Boolean> getToggleMap() {
        return toggleMap;
    }

    public boolean hasNotifications() {
        return  toggleMap.get(player.getUniqueId());
    }
}
