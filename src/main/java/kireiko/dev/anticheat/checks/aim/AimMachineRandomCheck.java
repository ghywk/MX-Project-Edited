package kireiko.dev.anticheat.checks.aim;

import kireiko.dev.anticheat.api.PacketCheckHandler;
import kireiko.dev.anticheat.api.data.ConfigLabel;
import kireiko.dev.anticheat.api.events.UseEntityEvent;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.managers.CheckManager;
import kireiko.dev.anticheat.utils.RandomnessCheckerUtil;
import kireiko.dev.anticheat.utils.container.CircularBuffer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 自定义 Aim 检查：对比“理论攻击角”与实际角度差，判断是否存在机械随机噪声。
 * 使用可扩展的公式计算器，便于后续新增公式。
 */
public final class AimMachineRandomCheck implements PacketCheckHandler {

    private static final int SAMPLE_SIZE = 20;

    private final PlayerProfile profile;
    private Map<String, Object> localCfg = new TreeMap<>();
    private final RandomnessCheckerUtil randomnessChecker = new RandomnessCheckerUtil();
    private final List<BaseAimCalculator> calculators = new ArrayList<>();
    private final Map<String, DiffBuffer> buffers = new LinkedHashMap<>();

    public AimMachineRandomCheck(PlayerProfile profile) {
        this.profile = profile;
        registerCalculators();
        if (CheckManager.classCheck(this.getClass())) {
            this.localCfg = CheckManager.getConfig(this.getClass());
        }
    }

    @Override
    public ConfigLabel config() {
        localCfg.put("enabled", true);
        localCfg.put("maxRange", 6.0);
        localCfg.put("requireBothAxes", true);
        localCfg.put("vl", 8);
        return new ConfigLabel("aim_machinery", localCfg);
    }

    @Override
    public void applyConfig(Map<String, Object> params) {
        localCfg = params;
    }

    @Override
    public Map<String, Object> getConfig() {
        return localCfg;
    }

    @Override
    public void event(Object o) {
        if (o instanceof UseEntityEvent) {
            UseEntityEvent event = (UseEntityEvent) o;
            if (event.isAttack()) {
                onAttack(event.getTarget());
            }
        }
    }

    /**
     * 在攻击时记录差值样本，样本满后判定随机性。
     */
    private void onAttack(Entity target) {
        if (!isEnabled()) return;
        if (target == null) return;
        if (profile.isIgnoreFirstTick()) return;

        Player player = profile.getPlayer();
        Location playerLoc = profile.getTo() != null ? profile.getTo() : player.getLocation();
        float playerYaw = playerLoc.getYaw();
        float playerPitch = playerLoc.getPitch();

        double maxRange = getDoubleCfg("maxRange", 6.0);
        if (maxRange > 0) {
            if (playerLoc.getWorld() == null || target.getWorld() == null) return;
            if (!playerLoc.getWorld().equals(target.getWorld())) return;
            if (playerLoc.distanceSquared(target.getLocation()) > maxRange * maxRange) return;
        }

        for (BaseAimCalculator calculator : calculators) {
            BaseAim base = calculator.compute(player, target);
            if (base == null) continue;

            float yawDiff = wrapAngle(playerYaw - base.yaw);
            float pitchDiff = playerPitch - base.pitch;

            DiffBuffer buffer = buffers.get(calculator.id());
            if (buffer == null) continue;
            buffer.yaw.add(yawDiff);
            buffer.pitch.add(pitchDiff);

            if (!buffer.isReady()) continue;

            boolean yawNoise = randomnessChecker.isCenteredNoise(buffer.yaw.getAllLatest());
            boolean pitchNoise = randomnessChecker.isCenteredNoise(buffer.pitch.getAllLatest());
            boolean requireBoth = getBooleanCfg("requireBothAxes", true);
            boolean flagged = requireBoth ? (yawNoise && pitchNoise) : (yawNoise || pitchNoise);

            if (flagged) {
                float vl = getNumCfg("vl", 8);
                profile.punish("Aim", "MachineRandom", "Mechanized Randomization (" + calculator.id() + ")", vl / 10f);
                buffer.clear();
            }
        }
    }

    private boolean isEnabled() {
        Object v = localCfg.get("enabled");
        return !(v instanceof Boolean) || (Boolean) v;
    }

    private float getNumCfg(String key, float def) {
        Object v = localCfg.get(key);
        return v instanceof Number ? ((Number) v).floatValue() : def;
    }

    private double getDoubleCfg(String key, double def) {
        Object v = localCfg.get(key);
        return v instanceof Number ? ((Number) v).doubleValue() : def;
    }

    private boolean getBooleanCfg(String key, boolean def) {
        Object v = localCfg.get(key);
        return v instanceof Boolean ? (Boolean) v : def;
    }

    private float wrapAngle(float angle) {
        float wrapped = angle % 360f;
        if (wrapped >= 180f) wrapped -= 360f;
        if (wrapped < -180f) wrapped += 360f;
        return wrapped;
    }

    /**
     * 注册角度公式，后续新增只需添加实现并注册。
     */
    private void registerCalculators() {
        calculators.clear();
        calculators.add(new CenterOfEntityCalculator());
        for (BaseAimCalculator calculator : calculators) {
            buffers.put(calculator.id(), new DiffBuffer(SAMPLE_SIZE));
        }
    }

    /**
     * 理论角度计算接口，便于扩展。
     */
    private interface BaseAimCalculator {
        String id();
        BaseAim compute(Player player, Entity target);
    }

    /**
     * 基于实体中心的角度公式（你提供的版本）。
     */
    private static final class CenterOfEntityCalculator implements BaseAimCalculator {
        @Override
        public String id() {
            return "center_v1";
        }

        @Override
        public BaseAim compute(Player player, Entity target) {
            Location targetLoc = target.getLocation();
            Location eye = player.getEyeLocation();

            double targetCenterX = targetLoc.getX();
            double targetCenterY = targetLoc.getY() + target.getHeight() / 2.0;
            double targetCenterZ = targetLoc.getZ();

            double xDiff = targetCenterX - eye.getX();
            double yDiff = targetCenterY - eye.getY();
            double zDiff = targetCenterZ - eye.getZ();

            double distance = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
            if (distance == 0) return null;

            float targetYaw = (float) ((Math.atan2(zDiff, xDiff) * (180.0 / Math.PI)) - 90.0);
            float targetPitch = (float) (-(Math.atan2(yDiff, distance) * (180.0 / Math.PI)));
            return new BaseAim(targetYaw, targetPitch);
        }
    }

    /**
     * 保存一组 yaw/pitch 差值样本。
     */
    private static final class DiffBuffer {
        private final CircularBuffer yaw;
        private final CircularBuffer pitch;

        DiffBuffer(int size) {
            this.yaw = new CircularBuffer(size);
            this.pitch = new CircularBuffer(size);
        }

        boolean isReady() {
            return yaw.isFull() && pitch.isFull();
        }

        void clear() {
            yaw.clear();
            pitch.clear();
        }
    }

    /**
     * 理论攻击角。
     */
    private static final class BaseAim {
        private final float yaw;
        private final float pitch;

        BaseAim(float yaw, float pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
        }
    }
}
