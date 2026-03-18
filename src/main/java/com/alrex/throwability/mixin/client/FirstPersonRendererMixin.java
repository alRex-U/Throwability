package com.alrex.throwability.mixin.client;

import com.alrex.throwability.client.animation.firstperson.FirstPersonAnimationHandler;
import com.alrex.throwability.common.ability.AbstractThrowingAbility;
import com.alrex.throwability.common.ability.IThrowabilityProvider;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonRenderer.class)
public class FirstPersonRendererMixin {
    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/FirstPersonRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/model/ItemCameraTransforms$TransformType;ZLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V"))
    private void onRenderItem(AbstractClientPlayerEntity player, float partialTick, float p_228405_3_, Hand hand, float p_228405_5_, ItemStack itemStack, float p_228405_7_, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_228405_10_, CallbackInfo ci) {
        if (player instanceof IThrowabilityProvider) {
            AbstractThrowingAbility throwingAbility = ((IThrowabilityProvider) player).getThrowAbility();
            HandSide handSide = (hand == Hand.MAIN_HAND) ? player.getMainArm() : player.getMainArm().getOpposite();
            FirstPersonAnimationHandler.applyItemArmRotation(player, throwingAbility, matrixStack, handSide, partialTick);
        }
    }

    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/FirstPersonRenderer;applyItemArmTransform(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/util/HandSide;F)V"))
    private void onApplyItemTransform(AbstractClientPlayerEntity player, float partialTick, float p_228405_3_, Hand hand, float p_228405_5_, ItemStack itemStack, float p_228405_7_, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_228405_10_, CallbackInfo ci) {
        if (player instanceof IThrowabilityProvider) {
            AbstractThrowingAbility throwingAbility = ((IThrowabilityProvider) player).getThrowAbility();
            HandSide handSide = (hand == Hand.MAIN_HAND) ? player.getMainArm() : player.getMainArm().getOpposite();
            FirstPersonAnimationHandler.applyItemArmTranslation(player, throwingAbility, matrixStack, handSide, partialTick);
        }
    }
}
