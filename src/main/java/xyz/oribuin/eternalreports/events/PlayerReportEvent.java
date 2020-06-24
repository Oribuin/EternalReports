package xyz.oribuin.eternalreports.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import xyz.oribuin.eternalreports.EternalReports;

public class PlayerReportEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private final Player sender;
    private final Player reported;
    private final String reason;

    private boolean resolved;

    public PlayerReportEvent(Player sender, Player reported, String reason, boolean resolved) {
        super(reported);

        this.sender = sender;
        this.reported = reported;
        this.reason = reason;
        this.resolved = resolved;

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

    /**
     * This returns the player who reported the other player
     *
     * @return Command Sender
     */

    public Player getSender() {
        return sender;
    }


    /**
     * The reason on why the player was reported
     *
     * @return Report Reason
     */

    public String getReason() {
        return reason;
    }

    /**
     * Check if report was resolved.
     *
     * @return true if reported is resolved
     */

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
        EternalReports.getInstance().getDataManager().updateReports(this.getSender(), this.getPlayer(), this.getReason(), resolved);
    }
}
