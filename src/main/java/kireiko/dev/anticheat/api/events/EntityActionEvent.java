package kireiko.dev.anticheat.api.events;

import kireiko.dev.anticheat.listeners.EntityActionListener;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class EntityActionEvent {
    private EntityActionListener.AbilitiesEnum abilitiesEnum;
}
