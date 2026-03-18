package com.alrex.throwability.client.animation;


import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public interface IAnimation {
    boolean isActive(Player player);

    @Nullable
    PlayerRotation getModelRotation(Player player, @Nullable PlayerRotation parentRotation, float partialTick);

    void animateModel(PlayerModelAnimator animator);

    default void tick(Player player) {
    }

    default void onStartAnimation(Player player) {
    }

    default void onFinishAnimation(Player player) {
    }
}
