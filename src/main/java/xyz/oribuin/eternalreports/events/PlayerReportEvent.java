package xyz.oribuin.eternalreports.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerReportEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private final Player sender;
    private final String title;
    private final String description;

    public PlayerReportEvent(Player sender, Player reported, String title, String description) {
        super(reported);

        this.sender = sender;
        this.title = title;
        this.description = description;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public final void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    public final boolean isCancelled() {
        return cancelled;
    }

    public Player getSender() {
        return sender;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
