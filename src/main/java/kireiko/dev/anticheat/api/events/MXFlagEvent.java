package kireiko.dev.anticheat.api.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public final class MXFlagEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final String check;
    private final String component;
    private final String info;
    private final float vl;
    private final double vlLimit;
    @Setter
    private boolean cancelled;

    public MXFlagEvent(Player player, String check, String component, String info, float vl, double vlLimit) {
        super(true);
        this.player = player;
        this.check = check;
        this.component = component;
        this.info = info;
        this.vl = vl;
        this.vlLimit = vlLimit;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
