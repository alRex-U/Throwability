package com.alrex.throwability.common.ability;

import com.alrex.throwability.common.capability.Capabilities;
import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.common.capability.throwable.StandardThrowable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class RemoteThrowingAbility extends AbstractThrowingAbility {
    private final Player player;

    public RemoteThrowingAbility(Player player) {
        this.player = player;
    }

    @Override
    public void tick() {
        super.tick();

        var selected = player.getInventory().getSelected();
        if (selected.isEmpty()) {
            stopCharging();
            chargingTick = Mth.clamp(chargingTick, 0, StandardThrowable.getInstance().getMaxChargeTick(selected));
            return;
        }
        IThrowable throwable = selected.getCapability(Capabilities.THROWABLE_CAPABILITY).orElseGet(StandardThrowable::getInstance);
        chargingTick = Mth.clamp(chargingTick, 0, throwable.getMaxChargeTick(selected));
    }

    public static class SyncedData {
        private final boolean charging;
        private final int chargingTick;

        public SyncedData(boolean charging, int chargingTick) {
            this.charging = charging;
            this.chargingTick = chargingTick;
        }

        public static SyncedData read(FriendlyByteBuf buffer) {
            return new SyncedData(
                    buffer.readBoolean(),
                    buffer.readInt()
            );
        }

        public void apply(RemoteThrowingAbility remote) {
            remote.setMaxChargingTick(chargingTick);
            remote.charging = charging;
        }

        public void write(FriendlyByteBuf buffer) {
            buffer.writeBoolean(this.charging);
            buffer.writeInt(this.chargingTick);
            buffer.writeInt(this.chargingTick);
        }
    }
}
