package kireiko.dev.anticheat.api.events;

import kireiko.dev.anticheat.api.player.PlayerProfile;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class NoRotationEvent {
    private PlayerProfile profile;
}
