package com.alrex.throwability.common.ability;

import com.alrex.throwability.common.capability.Capabilities;
import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.common.capability.throwable.StandardThrowable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;

public class RemoteThrowingAbility extends AbstractThrowingAbility {
    private final PlayerEntity player;

    public RemoteThrowingAbility(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void tick() {
        super.tick();

        ItemStack selected = player.inventory.getSelected();
        if (selected.isEmpty()) {
            stopCharging();
            chargingTick = MathHelper.clamp(chargingTick, 0, StandardThrowable.getInstance().getMaxChargeTick(selected));
            return;
        }
        IThrowable throwable = selected.getCapability(Capabilities.THROWABLE_CAPABILITY).orElseGet(StandardThrowable::getInstance);
        chargingTick = MathHelper.clamp(chargingTick, 0, throwable.getMaxChargeTick(selected));
    }

    public static class SyncedData {
        private final boolean charging;
        private final int chargingTick;

        public SyncedData(boolean charging, int chargingTick) {
            this.charging = charging;
            this.chargingTick = chargingTick;
        }

        public static SyncedData read(PacketBuffer buffer) {
            return new SyncedData(
                    buffer.readBoolean(),
                    buffer.readInt()
            );
        }

        public void apply(RemoteThrowingAbility remote) {
            remote.setMaxChargingTick(chargingTick);
            remote.charging = charging;
        }

        public void write(PacketBuffer buffer) {
            buffer.writeBoolean(this.charging);
            buffer.writeInt(this.chargingTick);
            buffer.writeInt(this.chargingTick);
        }
    }
}
