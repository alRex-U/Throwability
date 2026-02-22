package com.alrex.throwability.common.ability;

import com.alrex.throwability.client.input.KeyBindings;
import com.alrex.throwability.common.capability.Capabilities;
import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.common.capability.throwable.StandardThrowable;
import com.alrex.throwability.common.network.SyncThrowStateMessage;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

public class LocalThrowingAbility extends AbstractThrowingAbility {
    private final ClientPlayerEntity player;
    private int currentItemSlot = -1;

    public LocalThrowingAbility(ClientPlayerEntity player) {
        this.player = player;
    }

    @Override
    public void tick() {
        super.tick();

        if (currentItemSlot != player.inventory.selected || Minecraft.getInstance().screen != null) {
            stopCharging();
            currentItemSlot = player.inventory.selected;
            return;
        }
        ItemStack selected = player.inventory.getSelected();
        if (selected.isEmpty()) {
            stopCharging();
            return;
        }

        IThrowable throwable = selected.getCapability(Capabilities.THROWABLE_CAPABILITY).orElseGet(StandardThrowable::getInstance);
        maxChargingTick = throwable.getMaxChargeTick();
        if (KeyBindings.getKeyThrow().isDown()) {
            if (!charging) {
                startCharging();
            }
        } else {
            if (charging) {
                if (chargingTick > maxChargingTick / 5f) {
                    ThrowType type;
                    if (KeyBindings.getKeySpecialModifier().isDown()) {
                        type = ThrowType.ONE_AS_ENTITY;
                    } else if (KeyBindings.getKeyAllModifier().isDown()) {
                        type = ThrowType.ALL_AS_ITEM;
                    } else {
                        type = ThrowType.ONE_AS_ITEM;
                    }
                    ThrowUtil.throwItem(player, player.inventory.selected, selected, throwable, type, chargingTick);
                }
                stopCharging();
            }
        }


        if (oldCharging != charging) {
            SyncThrowStateMessage.send(
                    player, new RemoteThrowingAbility.SyncedData(charging, chargingTick)
            );
        }
    }

    @Override
    public void startCharging() {
        super.startCharging();
        SyncThrowStateMessage.send(
                player, new RemoteThrowingAbility.SyncedData(charging, chargingTick)
        );
    }

    @Override
    public void stopCharging() {
        super.stopCharging();
        SyncThrowStateMessage.send(
                player, new RemoteThrowingAbility.SyncedData(charging, chargingTick)
        );
    }
}
