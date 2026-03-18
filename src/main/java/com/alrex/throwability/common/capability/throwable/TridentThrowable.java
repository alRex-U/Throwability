package com.alrex.throwability.common.capability.throwable;

import com.alrex.throwability.common.ability.ThrowType;
import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.phys.Vec3;

public class TridentThrowable implements IThrowable {
    @Override
    public Entity throwAsEntity(Player thrower, ItemStack stack, int chargedTick) {
        Item item = stack.getItem();
        if (item instanceof TridentItem) {
            stack.hurtAndBreak(1, thrower, (player) -> player.broadcastBreakEvent(thrower.getUsedItemHand()));
            var entity = new ThrownTrident(
                    thrower.level, thrower, stack
            );
            Vec3 throwVec = ThrowUtil.getBasicThrowingVector(thrower);
            double speedScale = 4.
                    * ThrowUtil.getSpeedScale(thrower)
                    * Mth.clamp(chargedTick / (double) getMaxChargeTick(stack), 0, 1);

            entity.setDeltaMovement(throwVec.scale(speedScale));

            return entity;
        }
        return throwAsItem(thrower, stack, chargedTick);
    }

    @Override
    public void onThrownOnClient(Player thrower, ItemStack stack, ThrowType type, int chargedTick) {
        thrower.playSound(SoundEvents.TRIDENT_THROW, Mth.clamp(chargedTick / (float) getMaxChargeTick(stack), 0, 1f), 1);
    }
}
