package com.alrex.throwability.mixin.client;

import com.alrex.throwability.client.animation.AnimationHost;
import com.alrex.throwability.client.animation.IAnimationHostProvider;
import com.alrex.throwability.client.animation.PlayerRotation;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.util.math.vector.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
    public PlayerRendererMixin(EntityRendererManager manager, PlayerModel<AbstractClientPlayerEntity> model, float p_i50965_3_) {
        super(manager, model, p_i50965_3_);
    }

    @Inject(
            method = "render(Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
            at = @At("HEAD")
    )
    private void onRenderHead(AbstractClientPlayerEntity player, float p_225623_2_, float partialTick, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, CallbackInfo ci) {
        if (player instanceof IAnimationHostProvider) {
            AnimationHost animationHost = ((IAnimationHostProvider) player).getAnimationHost();
            animationHost.startAnimationSection(player);
        }
    }

    @Inject(
            method = "render(Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
            at = @At("RETURN")
    )
    private void onRenderTail(AbstractClientPlayerEntity player, float p_225623_2_, float partialTick, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, CallbackInfo ci) {
        if (player instanceof IAnimationHostProvider) {
            AnimationHost animationHost = ((IAnimationHostProvider) player).getAnimationHost();
            animationHost.finishAnimationSection(player);
        }
    }

    @Inject(
            method = "setupRotations(Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;Lcom/mojang/blaze3d/matrix/MatrixStack;FFF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onSetupRotationsHead(AbstractClientPlayerEntity player, MatrixStack matrixStack, float p_225621_3_, float yRotDegree, float partialTick, CallbackInfo ci) {
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
            method = "setupRotations(Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;Lcom/mojang/blaze3d/matrix/MatrixStack;FFF)V",
            at = @At("RETURN")
    )
    private void onSetupRotationsTail(AbstractClientPlayerEntity player, MatrixStack matrixStack, float p_225621_3_, float yRotDegree, float partialTick, CallbackInfo ci) {
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
