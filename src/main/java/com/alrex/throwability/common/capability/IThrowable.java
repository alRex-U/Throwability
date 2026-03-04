package com.alrex.throwability.common.capability;

import com.alrex.throwability.common.ability.ThrowType;
import com.alrex.throwability.common.sound.SoundEvents;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface IThrowable {
    Entity throwAsEntity(PlayerEntity thrower, ItemStack stack, int chargedTick);

    default Entity throwAsItem(PlayerEntity thrower, ItemStack stack, int chargedTick) {
        ItemEntity itemEntity = new ItemEntity(
                thrower.level, thrower.getX(), thrower.getEyeY() - 0.3, thrower.getZ(), stack
        );
        itemEntity.setPickUpDelay(20);
        double scale = (chargedTick / 20.);
        if (scale > 1.) scale = 1.;
        scale *= 3. * ThrowUtil.getSpeedScale(thrower);
        itemEntity.setDeltaMovement(
                ThrowUtil.getBasicThrowingVector(thrower).scale(scale)
        );
        return itemEntity;
    }

    default void onThrownOnClient(PlayerEntity thrower, ItemStack stack, ThrowType type) {
        thrower.playSound(SoundEvents.THROW.get(), 1f, 1f);
    }

    default boolean canThrowableNow(PlayerEntity thrower, ItemStack stack) {
        Pose pose = thrower.getPose();
        return pose == Pose.STANDING || pose == Pose.CROUCHING;
    }

    default int getMaxChargeTick(ItemStack stack) {
        return 20;
    }
}
