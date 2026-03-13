package kireiko.dev.anticheat.api.events;

import kireiko.dev.anticheat.api.player.PlayerProfile;

public final class CTransactionEvent {

    public final PlayerProfile protocol;
    public final long time, delay;

    public CTransactionEvent(PlayerProfile protocol) {
        this.time = System.currentTimeMillis();
        this.protocol = protocol;
        this.delay = protocol.transactionPing;
    }
}
