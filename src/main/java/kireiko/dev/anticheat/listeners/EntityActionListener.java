package kireiko.dev.anticheat.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import kireiko.dev.anticheat.MX;
import kireiko.dev.anticheat.api.data.PlayerContainer;
import kireiko.dev.anticheat.api.events.EntityActionEvent;
import kireiko.dev.anticheat.api.player.PlayerProfile;

import java.util.Collections;

public final class EntityActionListener extends PacketAdapter {

    public EntityActionListener() {
        super(
                MX.getInstance(),
                ListenerPriority.HIGHEST,
                Collections.singletonList(PacketType.Play.Client.ENTITY_ACTION),
                ListenerOptions.ASYNC
        );
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PlayerProfile protocol = PlayerContainer.getProfile(event.getPlayer());
        if (protocol == null) {
            return;
        }

        if (event.getPacket().getModifier().getValues().size() > 1) {
            String typeString = event.getPacket().getModifier().getValues().get(1).toString();
            AbilitiesEnum type = getEnum(typeString);

            if (typeString != null) {
                if (type == AbilitiesEnum.PRESS_SHIFT_KEY) {
                    protocol.sneaking = true;
                } else if (type == AbilitiesEnum.RELEASE_SHIFT_KEY) {
                    protocol.sneaking = false;
                } else if (type == AbilitiesEnum.START_SPRINTING) {
                    protocol.sprinting = true;
                } else if (type == AbilitiesEnum.STOP_SPRINTING) {
                    protocol.sprinting = false;
                }
            }
            EntityActionEvent e = new EntityActionEvent(type);
            protocol.run(e);
        }
    }

    private AbilitiesEnum getEnum(String s) {
        for (AbilitiesEnum type : AbilitiesEnum.values()) {
            if (type.toString().equals(s)) {
                return type;
            }
        }
        return null;
    }

    public enum AbilitiesEnum {
        START_SPRINTING,
        STOP_SPRINTING,
        PRESS_SHIFT_KEY,
        RELEASE_SHIFT_KEY
    }

}