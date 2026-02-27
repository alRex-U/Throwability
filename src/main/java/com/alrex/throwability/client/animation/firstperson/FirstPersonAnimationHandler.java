package com.alrex.throwability.client.animation.firstperson;

import com.alrex.throwability.client.animation.impl.ThrowingAnimation;
import com.alrex.throwability.common.ability.AbstractThrowingAbility;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FirstPersonAnimationHandler {
    public static void applyItemArmRotation(PlayerEntity player, AbstractThrowingAbility throwingAbility, MatrixStack matrixStack, HandSide handSide, float partial) {
        if (!throwingAbility.isCharging()) return;
        if (handSide != player.getMainArm()) return;
        float sign = handSide == HandSide.RIGHT ? 1 : -1;
        float factor = ThrowingAnimation.getFactor(throwingAbility, partial);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(factor * 20));
    }

    public static void applyItemArmTranslation(PlayerEntity player, AbstractThrowingAbility throwingAbility, MatrixStack matrixStack, HandSide handSide, float partial) {
        if (!throwingAbility.isCharging()) return;
        if (handSide != player.getMainArm()) return;
        float sign = handSide == HandSide.RIGHT ? 1 : -1;
        float factor = ThrowingAnimation.getFactor(throwingAbility, partial);
        matrixStack.translate(factor * sign * 0.1f, factor * 0.1f, factor * 0.2f);
    }
}
