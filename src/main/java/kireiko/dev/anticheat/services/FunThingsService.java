package kireiko.dev.anticheat.services;

import kireiko.dev.anticheat.MX;
import kireiko.dev.anticheat.api.player.fun.FunThing;
import kireiko.dev.anticheat.api.player.fun.Hook;
import kireiko.dev.anticheat.api.player.fun.Rocket;
import kireiko.dev.anticheat.api.player.fun.Spell;
import kireiko.dev.anticheat.core.AsyncScheduler;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FunThingsService {

    private static final List<FunThing> things = new CopyOnWriteArrayList<>();

    public static void add(FunThing funThing) {
        AsyncScheduler.run(() -> things.add(funThing));
    }

    public static void init() {
        Bukkit.getScheduler().runTaskTimer(MX.getInstance(), () -> AsyncScheduler.run(() -> {
            things.removeIf(thing -> {
                thing.tick();
                return (thing instanceof Hook && ((Hook) thing).getHoverTicks() >= 20) ||
                        (thing instanceof Rocket && ((Rocket) thing).isDestroyed()) ||
                        (thing instanceof Spell && ((Spell) thing).isDestroyed());
            });
        }), 0L, 1L);
    }

}
