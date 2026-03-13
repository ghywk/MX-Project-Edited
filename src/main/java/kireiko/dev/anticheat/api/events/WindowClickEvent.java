package kireiko.dev.anticheat.api.events;

import com.comphenix.protocol.events.PacketEvent;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class WindowClickEvent {
    private PacketEvent packetEvent;
}
