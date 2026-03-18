package com.alrex.throwability.mixin.client;

import com.alrex.throwability.client.animation.AnimationHost;
import com.alrex.throwability.client.animation.IAnimationHostProvider;
import com.alrex.throwability.client.animation.PlayerModelAnimator;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin<T extends LivingEntity> extends HumanoidModel<T> {
	@Shadow
	@Final
	private boolean slim;

    public PlayerModelMixin(ModelPart p_170677_) {
        super(p_170677_);
    }

    @Inject(
            method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
            at = @At("HEAD"), cancellable = true
    )
	protected void onSetupAnimHead(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
        if (!(entity instanceof Player player)) return;
        if (!(player instanceof IAnimationHostProvider)) return;
        var animationHost = ((IAnimationHostProvider) player).getAnimationHost();

        if (animationHost.shouldStopVanillaModelAnimation(player)) {
            var model = (PlayerModel<?>) (Object) this;
            PlayerModelAnimator animator = new PlayerModelAnimator(
                    player, model, slim, ageInTicks, limbSwing, limbSwingAmount, netHeadYaw, headPitch
            );
            animationHost.animateModel(animator);
            animator.copyFromBodyToWear();
            info.cancel();
        }
	}

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
	protected void onSetupAnimTail(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
        if (!(entity instanceof Player player)) return;
        if (!(player instanceof IAnimationHostProvider)) return;
        AnimationHost animationHost = ((IAnimationHostProvider) player).getAnimationHost();

        var model = (PlayerModel<?>) (Object) this;
        PlayerModelAnimator animator = new PlayerModelAnimator(
                player, model, slim, ageInTicks, limbSwing, limbSwingAmount, netHeadYaw, headPitch
        );
        animationHost.animateModel(animator);
        animator.copyFromBodyToWear();
	}
}
