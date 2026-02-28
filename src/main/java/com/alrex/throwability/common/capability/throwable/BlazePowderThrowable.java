package com.alrex.throwability.common.capability.throwable;

import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.common.entity.ThrownBlazePowderEntity;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class BlazePowderThrowable implements IThrowable {
    @Override
    public Entity throwAsEntity(PlayerEntity thrower, ItemStack stack, int chargedTick) {
        if (stack.getItem() == Items.BLAZE_POWDER) {
            ThrownBlazePowderEntity entity = new ThrownBlazePowderEntity(thrower.level, thrower);
            Vector3d throwVec = ThrowUtil.getBasicThrowingVector(thrower);
            double speedScale = 3. * MathHelper.clamp(chargedTick / (double) getMaxChargeTick(stack), 0, 1);

            entity.setDeltaMovement(throwVec.scale(speedScale));

            return entity;
        }
        return throwAsItem(thrower, stack, chargedTick);
    }
}
