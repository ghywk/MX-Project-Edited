package kireiko.dev.anticheat.api.player.fun;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class PhantomWorld {


    // Will create a "phantom" block
    public static void setBlock(Player player, Location location, Material blockType) {
        try {
            PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_CHANGE);
            packet.getBlockPositionModifier().write(0, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
            packet.getBlockData().write(0, WrappedBlockData.createData(blockType));
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setLocalTime(Player player, long time) {
        try {
            PacketContainer timePacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.UPDATE_TIME);
            timePacket.getLongs().write(0, 0L);
            timePacket.getLongs().write(1, time);
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, timePacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setLocalHealthAndHunger(Player player, float hp, int hunger) {
        try {
            PacketContainer healthPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.UPDATE_HEALTH);
            healthPacket.getFloat().write(0, hp); // Health (1.0f = half heart)
            healthPacket.getIntegers().write(0, hunger); // Hunger (1 = 1 food point)
            healthPacket.getFloat().write(1, 0.0f); // Saturation (0.0f = no saturation)
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, healthPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
