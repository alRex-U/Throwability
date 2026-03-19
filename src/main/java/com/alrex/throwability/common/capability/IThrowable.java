package com.alrex.throwability.common.capability;

import com.alrex.throwability.common.ability.ThrowType;
import com.alrex.throwability.common.sound.SoundEvents;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IThrowable {
    Entity throwAsEntity(Player thrower, ItemStack stack, int chargedTick);

    default Entity throwAsItem(Player thrower, ItemStack stack, int chargedTick) {
        var itemEntity = new ItemEntity(
                thrower.level(), thrower.getX(), thrower.getEyeY() - 0.3, thrower.getZ(), stack
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

    default void onThrownOnClient(Player thrower, ItemStack stack, ThrowType type, int chargedTick) {
        thrower.playSound(SoundEvents.THROW.get(), Mth.clamp(chargedTick / (float) getMaxChargeTick(stack), 0f, 1f), 1f);
    }

    default boolean canThrowableNow(Player thrower, ItemStack stack) {
        Pose pose = thrower.getPose();
        return pose == Pose.STANDING || pose == Pose.CROUCHING;
    }

    default int getMaxChargeTick(ItemStack stack) {
        return 20;
    }
}
