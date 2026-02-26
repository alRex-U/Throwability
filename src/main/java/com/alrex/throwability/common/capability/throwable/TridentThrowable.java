package com.alrex.throwability.common.capability.throwable;

import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class TridentThrowable implements IThrowable {
    @Override
    public Entity throwAsEntity(PlayerEntity thrower, ItemStack stack, int chargedTick) {
        Item item = stack.getItem();
        if (item instanceof TridentItem) {
            stack.hurtAndBreak(1, thrower, (player) -> player.broadcastBreakEvent(thrower.getUsedItemHand()));
            TridentEntity entity = new TridentEntity(
                    thrower.level, thrower, stack
            );
            Vector3d throwVec = ThrowUtil.getBasicThrowingVector(thrower);
            double speedScale = 4. * MathHelper.clamp(chargedTick / (double) getMaxChargeTick(), 0, 1);

            entity.setDeltaMovement(throwVec.scale(speedScale));

            return entity;
        }
        return throwAsItem(thrower, stack, chargedTick);
    }

    @Override
    public void onThrownOnClient(PlayerEntity thrower, ItemStack stack) {
        thrower.playSound(SoundEvents.TRIDENT_THROW, 1, 1);
    }
}
