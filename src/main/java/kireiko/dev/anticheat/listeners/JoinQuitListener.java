package kireiko.dev.anticheat.listeners;

import kireiko.dev.anticheat.api.data.PlayerContainer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class JoinQuitListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerContainer.init(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerContainer.unload(event.getPlayer());
    }
}
