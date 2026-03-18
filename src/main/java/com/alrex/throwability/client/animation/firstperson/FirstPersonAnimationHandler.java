package com.alrex.throwability.client.animation.firstperson;

import com.alrex.throwability.client.animation.impl.ThrowingAnimation;
import com.alrex.throwability.common.ability.AbstractThrowingAbility;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FirstPersonAnimationHandler {
    public static void applyItemArmRotation(Player player, AbstractThrowingAbility throwingAbility, PoseStack matrixStack, HumanoidArm handSide, float partial) {
        if (!throwingAbility.isCharging()) return;
        if (handSide != player.getMainArm()) return;
        float factor = ThrowingAnimation.getFactor(throwingAbility, partial);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(factor * 20));
    }

    public static void applyItemArmTranslation(Player player, AbstractThrowingAbility throwingAbility, PoseStack matrixStack, HumanoidArm handSide, float partial) {
        if (!throwingAbility.isCharging()) return;
        if (handSide != player.getMainArm()) return;
        float sign = handSide == HumanoidArm.RIGHT ? 1 : -1;
        float factor = ThrowingAnimation.getFactor(throwingAbility, partial);
        matrixStack.translate(factor * sign * 0.1f, factor * 0.1f, factor * 0.2f);
    }
}
