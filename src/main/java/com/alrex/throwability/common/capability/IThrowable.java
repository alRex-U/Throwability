package com.alrex.throwability.common.capability;

import com.alrex.throwability.common.sound.SoundEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public interface IThrowable {
    Entity throwAsEntity(PlayerEntity thrower, ItemStack stack, int chargedTick);

    default Entity throwAsItem(PlayerEntity thrower, ItemStack stack, int chargedTick) {
        ItemEntity itemEntity = new ItemEntity(
                thrower.level, thrower.getX(), thrower.getEyeY() - 0.3, thrower.getZ(), stack
        );
        itemEntity.setPickUpDelay(20);
        float pitchSin = MathHelper.sin(thrower.xRot * ((float) Math.PI / 180F));
        float pitchCos = MathHelper.cos(thrower.xRot * ((float) Math.PI / 180F));
        float yawSin = MathHelper.sin(thrower.yRot * ((float) Math.PI / 180F));
        float yawCos = MathHelper.cos(thrower.yRot * ((float) Math.PI / 180F));
        float random = thrower.getRandom().nextFloat() * ((float) Math.PI * 2F);
        float random2 = 0.02F * thrower.getRandom().nextFloat();
        float scale = (chargedTick / 20f);
        if (scale > 1f) scale = 1f;
        scale *= 3f;
        itemEntity.setDeltaMovement(
                ((-yawSin * pitchCos * 0.3F) + 0.5f * Math.cos(random) * (double) random2) * scale,
                ((-pitchSin * 0.3F + 0.05 + (thrower.getRandom().nextFloat() - thrower.getRandom().nextFloat()) * 0.05F)) * scale,
                ((yawCos * pitchCos * 0.3F) + 0.5f * Math.sin(random) * (double) random2) * scale
        );
        return itemEntity;
    }

    default void onThrownOnClient(PlayerEntity thrower, ItemStack stack) {
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
