package kireiko.dev.anticheat.api.player;

import kireiko.dev.anticheat.MX;
import kireiko.dev.anticheat.api.CheckPacketRegister;
import kireiko.dev.anticheat.api.PacketCheckHandler;
import kireiko.dev.anticheat.api.events.MXFlagEvent;
import kireiko.dev.anticheat.managers.CheckManager;
import kireiko.dev.anticheat.services.AnimatedPunishService;
import kireiko.dev.anticheat.utils.ConfigCache;
import kireiko.dev.anticheat.utils.MessageUtils;
import kireiko.dev.anticheat.utils.protocol.ProtocolLib;
import kireiko.dev.millennium.math.Statistics;
import kireiko.dev.millennium.types.EvictingList;
import kireiko.dev.millennium.vectors.Pair;
import lombok.Data;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

@Data
public final class PlayerProfile {

    private final Player player;
    private final Set<PacketCheckHandler> checks = new HashSet<>();
    private final List<Location> pastLoc = new EvictingList<>(20);
    private final List<Long> ping = new EvictingList<>(10);
    private final List<Integer> sensitivity = new EvictingList<>(14);
    private final SensitivityProcessor sensitivityProcessor = new SensitivityProcessor(this);
    private final CinematicComponent cinematicComponent = new CinematicComponent(this);
    private final List<String> logs = new ArrayList<>();
    public boolean transactionSentKeep;
    public boolean transactionBoot = true;
    public long transactionTime, transactionLastTime, transactionPing;
    public short transactionId;
    public int airTicks, flagCount, punishAnimation, teleportTicks;
    public boolean sneaking = false, sprinting = false, ground = false;
    private boolean cinematic = false;
    private Location to;
    private Location from;
    private float vl;
    private long attackBlockToTime, lastTeleport = 0;
    private boolean alerts, debug, ignoreExitBan, ignoreFirstTick = true;
    private Pair<String, String> banAnimInfo;
    private Pair<Location, Location> banAnimPositions;
    private final Object instance;

    public PlayerProfile(Player player) {
        this.player = player;
        this.to = this.from = player.getLocation();
        this.instance = this;
    }

    public void punish(final String check, final String component, final String info, final float m) {
        if (!ConfigCache.BYPASS.equalsIgnoreCase("none")
                && this.player.hasPermission(ConfigCache.BYPASS)) {
            return;
        }
        // this.vl += 10.0f * m;
        final float tempVl = this.vl + 10.0f * m;
        final double vlLimit = ConfigCache.VL_LIMIT;
        MXFlagEvent event = new MXFlagEvent(this.player, check, component, info, tempVl, vlLimit);
        Bukkit.getPluginManager().callEvent(event); // forget to call event?
        if (event.isCancelled()) {
            return;
        }
        this.vl = tempVl;
        this.flagCount += (m == 0.0) ? 0 : 1;
        String builder = this.wrapString(ConfigCache.ALERT_MSG
                .replace("%check%", check)
                .replace("%component%", component)
                .replace("%info%", info));
        MessageUtils.sendMessagesToPlayers(MX.permission, builder);
        if (ConfigCache.LOG_IN_FILES) {
            logs.add("[" + MessageUtils.getDate() + "] "
                    + this.getPlayer().getName()
                    + " >> " + check + " (" + component + ") " + info + " ["
                    + ((int) this.vl) + "/"
                    + ConfigCache.VL_LIMIT
                    + "]");
        }
        if (this.vl >= vlLimit) {
            if (ConfigCache.PUNISH_EFFECT) {
                AnimatedPunishService.punish(this, new Pair<>(check, info));
            } else {
                forcePunish(check, info);
            }
        } else if (this.vl >= vlLimit / 1.8) {
            if (flagCount > 2 && !ConfigCache.SUSPECTED.isEmpty()) {
                MessageUtils.sendMessagesToPlayersNative(
                        MX.permissionHead + "personal",
                        MX.permission,
                        this.wrapString(ConfigCache.SUSPECTED
                                .replace("%check%", check)
                                .replace("%info%", info))
                );
                this.flagCount = 0;
            }
        } else if (flagCount == 2 && !ConfigCache.UNUSUAL.isEmpty()) {
            MessageUtils.sendMessagesToPlayersNative(
                    MX.permissionHead + "personal",
                    MX.permission,
                    this.wrapString(ConfigCache.UNUSUAL
                            .replace("%check%", check)
                            .replace("%info%", info))
            );
        }
    }

    public void fade(float vl) {
        this.vl -= vl;
        if (this.vl < 0) this.vl = 0;
    }

    @SneakyThrows
    public void initChecks(Object dependency) {
        for (Class<? extends PacketCheckHandler> checkHandler : CheckManager.getChecks()) {
            this.checks.add(checkHandler.getConstructor(dependency.getClass()).newInstance(this));
        }
    }

    public void run(Object handler) {
        CheckPacketRegister.runCustom(handler, checks);
    }

    private String wrapString(String v) {
        return MessageUtils.wrapColors(v.replace("%player%", this.getPlayer().getName())
                .replace("%vl%", String.valueOf(this.vl))
                .replace("%vlLimit%", String.valueOf(ConfigCache.VL_LIMIT))
        );
    }

    public boolean ignoreCinematic() {
        return cinematic && ConfigCache.IGNORE_CINEMATIC;
    }

    public boolean toggleAlerts() {
        this.alerts = !this.alerts;
        return this.alerts;
    }

    public boolean toggleDebug() {
        this.debug = !this.debug;
        return this.debug;
    }

    public void forcePunish(String check, String info) {
        MX.bannedPerMinuteCount++;
        this.ignoreExitBan = true;
        this.vl = 0;
        Bukkit.getScheduler().runTask(MX.getInstance(), () -> {
            String banMsg = this.wrapString(ConfigCache.BAN_COMMAND
                    .replace("%check%", check)
                    .replace("%info%", info));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), banMsg);
            this.setBanAnimInfo(null);
        });
    }

    public void debug(String msg) {
        if (debug)
            this.player.sendMessage(wrapString("&9&l[Debug] &f" + msg));
    }

    public void setAttackBlockToTime(long time) {
        if (time < System.currentTimeMillis() + 10) return;
        if (!ConfigCache.BYPASS.equalsIgnoreCase("none")
                && this.player.hasPermission(ConfigCache.BYPASS)) {
            return;
        }
        this.attackBlockToTime = time;
    }

    public int getEntityId() {
        return ProtocolLib.isTemporary(this.getPlayer())
                ? new Random().nextInt()
                : this.getPlayer().getEntityId();
    }

    public int calculateSensitivity() {
        if (Statistics.getDistinct(getSensitivity()) != getSensitivity().size()) {
            final Set<Integer> prev = new HashSet<>();
            for (int i : getSensitivity()) {
                if (prev.contains(i / 5)) {
                    return i;
                } else {
                    prev.add(i / 5);
                }
            }
        }
        return -1;
    }
    @Override public int hashCode() {
        return System.identityHashCode(this);
    }
    @Override public boolean equals(Object o) {
        return this == o;
    }
}
