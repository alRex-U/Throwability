package com.alrex.throwability.common.capability.throwable;

import com.alrex.throwability.common.ability.ThrowType;
import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.extern.AdditionalMods;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class TNTThrowable implements IThrowable {
    @Override
    public Entity throwAsEntity(PlayerEntity thrower, ItemStack stack, int chargedTick) {
        Item item = stack.getItem();
        if (item == Items.TNT) {
            Vector3d pos = ThrowUtil.getBasicThrowingPosition(thrower);
            TNTEntity entity = new TNTEntity(
                    thrower.level, pos.x(), pos.y(), pos.z(), thrower
            );
            Vector3d throwVec = ThrowUtil.getBasicThrowingVector(thrower);
            double phase = MathHelper.clamp(chargedTick / (double) getMaxChargeTick(stack), 0, 1);
            double speedScale = 3.
                    * ThrowUtil.getSpeedScale(thrower)
                    * phase;
            Vector3d deltaMovement = throwVec.scale(speedScale);

            entity.setDeltaMovement(deltaMovement);
            entity.setFuse((int) (40 + phase * 40));

            AdditionalMods.Naturot().rotateEntity(entity, deltaMovement);

            return entity;
        }
        return throwAsItem(thrower, stack, chargedTick);
    }

    @Override
    public void onThrownOnClient(PlayerEntity thrower, ItemStack stack, ThrowType type, int chargedTick) {
        IThrowable.super.onThrownOnClient(thrower, stack, type, chargedTick);
        if (type == ThrowType.ONE_AS_ENTITY) {
            thrower.playSound(SoundEvents.FLINTANDSTEEL_USE, MathHelper.clamp(chargedTick / (float) getMaxChargeTick(stack), 0, 1f), 1);
        }
    }
}
