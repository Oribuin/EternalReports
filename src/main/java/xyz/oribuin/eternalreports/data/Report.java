package xyz.oribuin.eternalreports.data;

import org.bukkit.entity.Player;

public class Report {
    private final Player sender;
    private final Player player;
    private String reason;
    private boolean resolved;

    private Report(Player sender, Player player, String reason, boolean resolved) {
        this.player = player;
        this.sender = sender;
        this.reason = reason;
    }

    public Player getPlayer() {
        return player;
    }

    public String getReason() {
        return reason;
    }

    public Player getSender() {
        return sender;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }
}
