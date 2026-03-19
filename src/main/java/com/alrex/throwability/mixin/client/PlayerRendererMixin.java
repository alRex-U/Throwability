package com.alrex.throwability.mixin.client;

import com.alrex.throwability.client.animation.AnimationHost;
import com.alrex.throwability.client.animation.IAnimationHostProvider;
import com.alrex.throwability.client.animation.PlayerRotation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel<AbstractClientPlayer> model, float p_174291_) {
        super(context, model, p_174291_);
    }

    @Inject(
            method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD")
    )
    private void onRenderHead(AbstractClientPlayer player, float p_117789_, float p_117790_, PoseStack poseStack, MultiBufferSource multiBufferSource, int p_117793_, CallbackInfo ci) {
        if (player instanceof IAnimationHostProvider) {
            AnimationHost animationHost = ((IAnimationHostProvider) player).getAnimationHost();
            animationHost.startAnimationSection(player);
        }
    }

    @Inject(
            method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("RETURN")
    )
    private void onRenderTail(AbstractClientPlayer player, float p_117789_, float p_117790_, PoseStack poseStack, MultiBufferSource multiBufferSource, int p_117793_, CallbackInfo ci) {
        if (player instanceof IAnimationHostProvider) {
            AnimationHost animationHost = ((IAnimationHostProvider) player).getAnimationHost();
            animationHost.finishAnimationSection(player);
        }
    }

    @Inject(
            method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onSetupRotationsHead(AbstractClientPlayer player, PoseStack matrixStack, float p_225621_3_, float yRotDegree, float partialTick, CallbackInfo ci) {
        if (player instanceof IAnimationHostProvider) {
            AnimationHost animationHost = ((IAnimationHostProvider) player).getAnimationHost();
            if (animationHost.shouldStopVanillaRotation(player)) {
                ci.cancel();

                PlayerRotation rotation = animationHost.getRotation(player, partialTick);
                if (rotation == null) return;

                Vector3f center = rotation.getCenter();
                Vector3f translation = rotation.getTranslation();
                matrixStack.translate(translation.x(), translation.y(), translation.z());
                matrixStack.translate(center.x(), center.y(), center.z());
                matrixStack.mulPose(rotation.getRotation());
                matrixStack.translate(-center.x(), -center.y(), -center.z());
            }
        }
    }

    @Inject(
            method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V",
            at = @At("RETURN")
    )
    private void onSetupRotationsTail(AbstractClientPlayer player, PoseStack matrixStack, float p_225621_3_, float yRotDegree, float partialTick, CallbackInfo ci) {
        if (player instanceof IAnimationHostProvider) {
            AnimationHost animationHost = ((IAnimationHostProvider) player).getAnimationHost();
            PlayerRotation rotation = animationHost.getRotation(player, partialTick);
            if (rotation == null) return;

            Vector3f center = rotation.getCenter();
            Vector3f translation = rotation.getTranslation();
            matrixStack.translate(translation.x(), translation.y(), translation.z());
            matrixStack.translate(center.x(), center.y(), center.z());
            matrixStack.mulPose(rotation.getRotation());
            matrixStack.translate(-center.x(), -center.y(), -center.z());
        }
    }
}
