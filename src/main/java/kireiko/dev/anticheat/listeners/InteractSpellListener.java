package kireiko.dev.anticheat.listeners;

import kireiko.dev.anticheat.MX;
import kireiko.dev.anticheat.api.data.PlayerContainer;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.api.player.fun.Hook;
import kireiko.dev.anticheat.api.player.fun.Rocket;
import kireiko.dev.anticheat.api.player.fun.Spell;
import kireiko.dev.anticheat.core.AsyncScheduler;
import kireiko.dev.anticheat.services.FunThingsService;
import kireiko.dev.anticheat.utils.ConfigCache;
import kireiko.dev.anticheat.utils.MessageUtils;
import kireiko.dev.anticheat.utils.TitleUtils;
import kireiko.dev.anticheat.utils.enums.ParticleTypes;
import kireiko.dev.millennium.math.AxisAlignedBB;
import kireiko.dev.millennium.math.BuildSpeed;
import kireiko.dev.millennium.math.RayTrace;
import kireiko.dev.millennium.vectors.Vec2;
import kireiko.dev.millennium.vectors.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.ThreadLocalRandom;

public class InteractSpellListener implements Listener {
    @EventHandler
    public void interact(PlayerInteractEvent event) {
        if (!ConfigCache.INTERACT_SPELL) {
            return;
        }
        final Player player = event.getPlayer();
        final PlayerProfile profile = PlayerContainer.getProfile(player);
        if (profile == null) {
            return;
        }
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) && event.hasItem()) {
            final ItemStack item = event.getItem();
            if (item == null || !item.hasItemMeta()) return;
            final String name = item.getItemMeta().getDisplayName();
            if (name == null) return; // why nulled?? (@NotNull String getDisplayName())
            switch (name) {
                case "§9Hook":
                    FunThingsService.add(new Hook(profile, profile.getTo().clone().add(0, 1.63, 0)));
                    event.setCancelled(true);
                    break;
                case "§9Посох Винтербелла":
                    FunThingsService.add(new Spell(profile, profile.getTo().clone().add(0, 1.63, 0), 0.7, 8,
                            ParticleTypes.SNOWBALL, ParticleTypes.SNOWBALL, new PotionEffect(PotionEffectType.SLOW, 60, 2)));
                    event.setCancelled(true);
                    break;
                case "§9Посох Пламени":
                    FunThingsService.add(new Spell(profile, profile.getTo().clone().add(0, 1.63, 0), 0.6, 10,
                            ParticleTypes.FLAME, ParticleTypes.LAVA, new PotionEffect(PotionEffectType.WITHER, 45, 1)));
                    event.setCancelled(true);
                    break;
                case "§9Проклятое заклинание": {
                    { // fire
                        final float f1 = -10f;
                        final float f2 = 0f;
                        final Location location = profile.getTo().clone().add(0, 1.63, 0);
                        location.setYaw(location.getYaw() + f1);
                        location.setPitch(location.getPitch() + f2);
                        FunThingsService.add(new Spell(profile, location, 0.8, 6,
                                ParticleTypes.FLAME, ParticleTypes.LAVA,
                                new PotionEffect(PotionEffectType.WITHER, 70, 1)));
                    }
                    { // ice
                        final float f1 = 10f;
                        final float f2 = 0f;
                        final Location location = profile.getTo().clone().add(0, 1.63, 0);
                        location.setYaw(location.getYaw() + f1);
                        location.setPitch(location.getPitch() + f2);
                        FunThingsService.add(new Spell(profile, location, 0.8, 4,
                                ParticleTypes.SNOWBALL, ParticleTypes.SNOWBALL,
                                new PotionEffect(PotionEffectType.SLOW, 70, 1)));
                    }
                    { // smoke
                        final float f1 = 0f;
                        final float f2 = 10f;
                        final Location location = profile.getTo().clone().add(0, 1.63, 0);
                        location.setYaw(location.getYaw() + f1);
                        location.setPitch(location.getPitch() + f2);
                        FunThingsService.add(new Spell(profile, location, 0.8, 8,
                                ParticleTypes.SMOKE_NORMAL, ParticleTypes.SMOKE_NORMAL,
                                new PotionEffect(PotionEffectType.BLINDNESS, 70, 1)));
                    }
                    { // cloud
                        final float f1 = 0f;
                        final float f2 = -10f;
                        final Location location = profile.getTo().clone().add(0, 1.63, 0);
                        location.setYaw(location.getYaw() + f1);
                        location.setPitch(location.getPitch() + f2);
                        FunThingsService.add(new Spell(profile, location, 0.8, 2,
                                ParticleTypes.CLOUD, ParticleTypes.CLOUD,
                                new PotionEffect(PotionEffectType.LEVITATION, 70, 2)));
                    }
                    { // explosion
                        final float f1 = 0f;
                        final float f2 = 0f;
                        final Location location = profile.getTo().clone().add(0, 1.63, 0);
                        location.setYaw(location.getYaw() + f1);
                        location.setPitch(location.getPitch() + f2);
                        FunThingsService.add(new Spell(profile, location, 0.8, 15,
                                ParticleTypes.EXPLOSION_NORMAL, ParticleTypes.EXPLOSION_LARGE, null));
                    }
                    event.setCancelled(true);
                    break;
                }
                case "§9AK-47": {
                    final float f1 = (ThreadLocalRandom.current().nextFloat() * 6f) - 3f;
                    final float f2 = (ThreadLocalRandom.current().nextFloat() * 6f) - 3f;
                    final Location location = profile.getTo().clone().add(0, 1.63, 0);
                    location.setYaw(location.getYaw() + f1);
                    location.setPitch(location.getPitch() + f2);
                    FunThingsService.add(new Spell(profile, location, 1.5, 4,
                            ParticleTypes.CRIT_MAGIC, ParticleTypes.CRIT_MAGIC, null));
                    event.setCancelled(true);
                    break;
                }
                case "§9Rocket Launcher":
                    AsyncScheduler.run(() -> {
                        for (PlayerProfile target : PlayerContainer.getUuidPlayerProfileMap().values()) {
                            if (target.getPlayer().getUniqueId().equals(player.getUniqueId())) continue;
                            if (player.getWorld().equals(target.getPlayer().getWorld())
                                    && player.getLocation().distance(target.getPlayer().getLocation()) < 125) {
                                final Location t = target.getPlayer().getLocation();
                                final double x = t.getX(), y = t.getY(), z = t.getZ();
                                final double hitbox = 1.8;
                                final AxisAlignedBB axisAlignedBB = new AxisAlignedBB(
                                        x - hitbox, y - hitbox, z - hitbox,
                                        x + hitbox, y + hitbox, z + hitbox
                                );
                                if (RayTrace.doRayTrace(BuildSpeed.FAST,
                                        new Vec2(profile.getTo().getPitch(), profile.getTo().getYaw()),
                                        new Vec3(profile.getTo().toVector()), axisAlignedBB, 125)) {
                                    FunThingsService.add(new Rocket(profile, target, profile.getTo().clone().add(0, 1.63, 0)));
                                    TitleUtils.sendTitle(player,
                                            MessageUtils.wrapColors("&a[   +   ]"),
                                            "", 0, 20, 20);
                                    Bukkit.getScheduler().runTaskLater(MX.getInstance(), () -> {
                                        TitleUtils.sendTitle(player,
                                                MessageUtils.wrapColors("&e[  +  ]"),
                                                "", 0, 20, 20);
                                    }, 1L);
                                    Bukkit.getScheduler().runTaskLater(MX.getInstance(), () -> {
                                        TitleUtils.sendTitle(player,
                                                MessageUtils.wrapColors("&c[ + ]"),
                                                "", 0, 20, 20);
                                    }, 2L);
                                    Bukkit.getScheduler().runTaskLater(MX.getInstance(), () -> {
                                        TitleUtils.sendTitle(player,
                                                MessageUtils.wrapColors("&4[+]"),
                                                "", 0, 20, 20);
                                        player.sendMessage(MessageUtils.wrapColors("&4//TARGET SPOTTED: " + target.getPlayer().getName().toUpperCase() + "//"));
                                    }, 3L);
                                    break;
                                }
                            }
                            TitleUtils.sendTitle(player,
                                    MessageUtils.wrapColors("&f[     +     ]"),
                                    "", 0, 5, 5);
                        }
                    });
                    event.setCancelled(true);
                    break;
            }
        }
    }
}
