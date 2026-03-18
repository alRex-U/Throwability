package com.alrex.throwability.client.animation.impl;

import com.alrex.throwability.client.animation.IAnimation;
import com.alrex.throwability.client.animation.PlayerModelAnimator;
import com.alrex.throwability.client.animation.PlayerRotation;
import com.alrex.throwability.client.animation.Rotation;
import com.alrex.throwability.common.ability.AbstractThrowingAbility;
import com.alrex.throwability.common.ability.IThrowabilityProvider;
import com.alrex.throwability.utils.MathUtil;
import com.mojang.math.Vector3f;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class ThrowingAnimation implements IAnimation {
    public static float getFactor(AbstractThrowingAbility throwingAbility, float partialTick) {
        float phase = (throwingAbility.getChargingTick() + partialTick) / throwingAbility.getMaxChargingTick();
        phase = Mth.clamp(phase, 0, 1);
        return 1 - MathUtil.squaring(1 - phase);
    }

    @Override
    public boolean isActive(Player player) {
        return player instanceof IThrowabilityProvider
                && ((IThrowabilityProvider) player).getThrowAbility().isCharging();
    }

    private float getRotationAngle(Player player, AbstractThrowingAbility throwingAbility, float partialTick) {
        return (float) (Math.toRadians(player.getMainArm() == HumanoidArm.RIGHT ? -40f : 40f) * getFactor(throwingAbility, partialTick));
    }

    private float getOffHandArmFactor(AbstractThrowingAbility throwingAbility, float partialTick) {
        float phase = (throwingAbility.getChargingTick() + partialTick) / throwingAbility.getMaxChargingTick();
        phase = Mth.clamp(phase, 0, 1);
        return 1 - (1 - phase) * (1 - phase) * (1 - phase);
    }

    @Override
    public void onStartAnimation(Player player) {
        player.yBodyRot = player.getYRot();
    }

    @Nullable
    @Override
    public PlayerRotation getModelRotation(Player player, @Nullable PlayerRotation parentRotation, float partialTick) {
        if (!(player instanceof IThrowabilityProvider)) return null;
        float rotAngle = getRotationAngle(player, ((IThrowabilityProvider) player).getThrowAbility(), partialTick);
        return new PlayerRotation(Vector3f.YP.rotation(rotAngle));
    }

    @Override
    public void animateModel(PlayerModelAnimator animator) {
        Player player = animator.getPlayer();
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
        if (player.getMainArm() == HumanoidArm.RIGHT) {
            animator.setRotation(
                    animator.getModel().leftArm,
                    Rotation.Builder.create()
                            .apply(
                                    new Vector3f(-1f / Mth.SQRT_OF_TWO, 0f, -1f / Mth.SQRT_OF_TWO),
                                    (float) Math.toRadians(90 - animator.getHeadPitch() / 2)
                            ).build(),
                    getOffHandArmFactor(throwingAbility, animator.getPartialTick())
            );
            animator.bob(
                    animator.getModel().leftArm,
                    -factor
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
                    Rotation.Builder.create()
                            .apply(
                                    new Vector3f(-1f / Mth.SQRT_OF_TWO, 0f, 1f / Mth.SQRT_OF_TWO),
                                    (float) Math.toRadians(90 - animator.getHeadPitch() / 2)
                            ).build(),
                    getOffHandArmFactor(throwingAbility, animator.getPartialTick())
            );
            animator.bob(
                    animator.getModel().rightArm,
                    factor
            );
            animator.setRotation(
                    animator.getModel().leftArm,
                    (float) Math.toRadians(-140),
                    (float) Math.toRadians(-20),
                    0,
                    factor
            );
        }
        animator.addRotation(
                animator.getModel().rightLeg,
                0, rotAngle / 2f, 0,
                factor
        );
        animator.addRotation(
                animator.getModel().leftLeg,
                0, rotAngle / 2f, 0,
                factor
        );
    }
}
