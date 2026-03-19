package com.alrex.throwability.mixin.client;

import com.alrex.throwability.client.animation.firstperson.FirstPersonAnimationHandler;
import com.alrex.throwability.common.ability.AbstractThrowingAbility;
import com.alrex.throwability.common.ability.IThrowabilityProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    private void onRenderItem(AbstractClientPlayer player, float partialTick, float p_228405_3_, InteractionHand hand, float p_228405_5_, ItemStack itemStack, float p_228405_7_, PoseStack poseStack, MultiBufferSource multiBufferSource, int p_228405_10_, CallbackInfo ci) {
        if (player instanceof IThrowabilityProvider) {
            AbstractThrowingAbility throwingAbility = ((IThrowabilityProvider) player).getThrowAbility();
            var handSide = (hand == InteractionHand.MAIN_HAND) ? player.getMainArm() : player.getMainArm().getOpposite();
            FirstPersonAnimationHandler.applyItemArmRotation(player, throwingAbility, poseStack, handSide, partialTick);
        }
    }

    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;applyItemArmTransform(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V"))
    private void onApplyItemTransform(AbstractClientPlayer player, float partialTick, float p_228405_3_, InteractionHand hand, float p_228405_5_, ItemStack itemStack, float p_228405_7_, PoseStack poseStack, MultiBufferSource multiBufferSource, int p_228405_10_, CallbackInfo ci) {
        if (player instanceof IThrowabilityProvider) {
            AbstractThrowingAbility throwingAbility = ((IThrowabilityProvider) player).getThrowAbility();
            var handSide = (hand == InteractionHand.MAIN_HAND) ? player.getMainArm() : player.getMainArm().getOpposite();
            FirstPersonAnimationHandler.applyItemArmTranslation(player, throwingAbility, poseStack, handSide, partialTick);
        }
    }
}
