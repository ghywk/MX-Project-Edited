package kireiko.dev.anticheat.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import kireiko.dev.anticheat.MX;

public final class TestListener extends PacketAdapter {
    public TestListener() {
        super(MX.getInstance(), ListenerPriority.HIGHEST,
                PacketType.Play.Client.getInstance());
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        event.getPlayer().sendMessage("e: " + event.getPacket().getType().name()
                + " " + event.getPacket().getStructures().getValues());
    }
}
