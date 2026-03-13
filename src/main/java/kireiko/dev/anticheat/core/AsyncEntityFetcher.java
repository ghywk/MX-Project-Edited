package kireiko.dev.anticheat.core;

import com.comphenix.protocol.ProtocolLibrary;
import kireiko.dev.anticheat.MX;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.concurrent.CompletableFuture;

public final class AsyncEntityFetcher {
    public static CompletableFuture<Entity> getEntityFromIDAsync(final World world, final int entityId) {
        CompletableFuture<Entity> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTask(MX.getInstance(), () -> {
            try {
                Entity entity = ProtocolLibrary.getProtocolManager().getEntityFromID(world, entityId);
                future.complete(entity);
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        });

        return future;
    }
}
