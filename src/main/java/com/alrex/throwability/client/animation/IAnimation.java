package com.alrex.throwability.client.animation;

import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

public interface IAnimation {
    boolean isActive(PlayerEntity player);

    @Nullable
    ModelRotation getModelRotation(PlayerEntity player, @Nullable ModelRotation parentRotation, float partialTick);

    void animateModel(PlayerModelAnimator animator);

    default void tick(PlayerEntity player) {
    }

    default void onStartAnimation(PlayerEntity player) {
    }

    default void onFinishAnimation(PlayerEntity player) {
    }
}
