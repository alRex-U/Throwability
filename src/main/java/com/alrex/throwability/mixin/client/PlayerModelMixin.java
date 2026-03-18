package com.alrex.throwability.mixin.client;

import com.alrex.throwability.client.animation.AnimationHost;
import com.alrex.throwability.client.animation.IAnimationHostProvider;
import com.alrex.throwability.client.animation.PlayerModelAnimator;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin<T extends LivingEntity> extends BipedModel<T> {
	@Shadow
	@Final
	private boolean slim;

	public PlayerModelMixin(float p_i1148_1_) {
		super(p_i1148_1_);
	}

    @Inject(
            method = "Lnet/minecraft/client/renderer/entity/model/PlayerModel;setupAnim(Lnet/minecraft/entity/LivingEntity;FFFFF)V",
            at = @At("HEAD"), cancellable = true
    )
	protected void onSetupAnimHead(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
        if (!(entity instanceof PlayerEntity)) return;
		PlayerEntity player = (PlayerEntity) entity;
        if (!(player instanceof IAnimationHostProvider)) return;
        AnimationHost animationHost = ((IAnimationHostProvider) player).getAnimationHost();

        if (animationHost.shouldStopVanillaModelAnimation(player)) {
            PlayerModel model = (PlayerModel) (Object) this;
            PlayerModelAnimator animator = new PlayerModelAnimator(
                    player, model, slim, ageInTicks, limbSwing, limbSwingAmount, netHeadYaw, headPitch
            );
            animationHost.animateModel(animator);
            animator.copyFromBodyToWear();
            info.cancel();
        }
	}

	@Inject(method = "Lnet/minecraft/client/renderer/entity/model/PlayerModel;setupAnim(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
	protected void onSetupAnimTail(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
        if (!(entity instanceof PlayerEntity)) return;
		PlayerEntity player = (PlayerEntity) entity;
        if (!(player instanceof IAnimationHostProvider)) return;
        AnimationHost animationHost = ((IAnimationHostProvider) player).getAnimationHost();

        PlayerModel model = (PlayerModel) (Object) this;
        PlayerModelAnimator animator = new PlayerModelAnimator(
                player, model, slim, ageInTicks, limbSwing, limbSwingAmount, netHeadYaw, headPitch
        );
        animationHost.animateModel(animator);
        animator.copyFromBodyToWear();
	}
}
