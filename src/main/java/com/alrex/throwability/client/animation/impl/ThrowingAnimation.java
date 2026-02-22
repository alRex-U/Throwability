package com.alrex.throwability.client.animation.impl;

import com.alrex.throwability.client.animation.IAnimation;
import com.alrex.throwability.client.animation.ModelRotation;
import com.alrex.throwability.client.animation.PlayerModelAnimator;
import com.alrex.throwability.common.ability.AbstractThrowingAbility;
import com.alrex.throwability.common.ability.IThrowabilityProvider;
import com.alrex.throwability.utils.MathUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;

public class ThrowingAnimation implements IAnimation {
    @Override
    public boolean isActive(PlayerEntity player) {
        return player instanceof IThrowabilityProvider
                && ((IThrowabilityProvider) player).getThrowAbility().isCharging();
    }

    private float getRotationAngle(PlayerEntity player, AbstractThrowingAbility throwingAbility, float partialTick) {
        return (float) (Math.toRadians(player.getMainArm() == HandSide.RIGHT ? 40f : -40f) * getFactor(throwingAbility, partialTick));
    }

    private float getFactor(AbstractThrowingAbility throwingAbility, float partialTick) {
        float phase = (throwingAbility.getChargingTick() + partialTick) / throwingAbility.getMaxChargingTick();
        phase = MathHelper.clamp(phase, 0, 1);
        return 1 - MathUtil.squaring(1 - phase);
    }

    @Override
    public void onStartAnimation(PlayerEntity player) {
        player.yBodyRot = player.yRot;
    }

    @Nullable
    @Override
    public ModelRotation getModelRotation(PlayerEntity player, @Nullable ModelRotation parentRotation, float partialTick) {
        if (!(player instanceof IThrowabilityProvider)) return null;
        float rotAngle = getRotationAngle(player, ((IThrowabilityProvider) player).getThrowAbility(), partialTick);
        return new ModelRotation(Vector3f.YP.rotation(rotAngle));
    }

    @Override
    public void animateModel(PlayerModelAnimator animator) {
        PlayerEntity player = animator.getPlayer();
        if (!(player instanceof IThrowabilityProvider)) return;
        AbstractThrowingAbility throwingAbility = ((IThrowabilityProvider) player).getThrowAbility();
        float rotAngle = getRotationAngle(player, throwingAbility, animator.getPartialTick());
        float factor = getFactor(throwingAbility, animator.getPartialTick());

        animator.addRotation(
                animator.getModel().head,
                0,
                rotAngle,
                0,
                factor
        );
        if (player.getMainArm() == HandSide.RIGHT) {
            animator.setRotation(
                    animator.getModel().leftArm,
                    (float) Math.toRadians(-75),
                    0,
                    0,
                    factor
            );
            animator.setRotation(
                    animator.getModel().rightArm,
                    (float) Math.toRadians(-140),
                    (float) Math.toRadians(20),
                    0,
                    factor
            );
        } else {
            animator.setRotation(
                    animator.getModel().rightArm,
                    (float) Math.toRadians(-75),
                    0,
                    0,
                    factor
            );
            animator.setRotation(
                    animator.getModel().leftArm,
                    (float) Math.toRadians(-135),
                    (float) Math.toRadians(-20),
                    0,
                    factor
            );
        }
    }
}
