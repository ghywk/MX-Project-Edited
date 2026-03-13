package kireiko.dev.anticheat.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public final class GhostBlockTest implements Listener {

    @EventHandler
    public void block(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void breAk(BlockBreakEvent event) {
        event.setCancelled(true);
    }
}
