package kireiko.dev.anticheat.api.data;

import kireiko.dev.millennium.types.EvictingList;
import kireiko.dev.millennium.vectors.Vec2f;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class RotationsContainer {
    private static final Map<UUID, List<Vec2f>> uuidPlayerRotationsMap = new ConcurrentHashMap<>();
    public static void register(final UUID uuid, final Vec2f vec2f) {
        uuidPlayerRotationsMap.computeIfAbsent(uuid, k -> new EvictingList<>(600)).add(vec2f);
    }
    public static String getJson(final UUID uuid) {
        List<Vec2f> rotations = uuidPlayerRotationsMap.get(uuid);
        if (rotations == null) return "[]";
        final StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < rotations.size(); i++) {
            Vec2f vec = rotations.get(i);
            sb.append("[")
                            .append(vec.getX()).append(",")
                            .append(vec.getY())
                            .append("]");
            if (i < rotations.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
