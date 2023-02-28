package com.alrex.throwability.mixin.client;

import com.alrex.throwability.client.animation.PlayerModelTransformer;
import com.alrex.throwability.client.animation.ThrowabilityAnimation;
import com.alrex.throwability.common.capability.IThrow;
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
	private PlayerModelTransformer transformer = null;

	public PlayerModelMixin(ModelPart p_170677_) {
		super(p_170677_);
	}

	@Inject(method = "Lnet/minecraft/client/model/PlayerModel;setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("HEAD"))
	protected void onSetupAnimHead(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
		if (!(entity instanceof Player)) return;
		PlayerModel model = (PlayerModel) (Object) this;
		Player player = (Player) entity;

		transformer = new PlayerModelTransformer(
				player,
				model,
				slim,
				ageInTicks,
				limbSwing,
				limbSwingAmount,
				netHeadYaw,
				headPitch
		);
		transformer.reset();
		transformer.copyFromBodyToWear();
	}

	@Inject(method = "Lnet/minecraft/client/model/PlayerModel;setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
	protected void onSetupAnimTail(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
		if (!(entity instanceof Player)) return;
		Player player = (Player) entity;
		IThrow iThrow = IThrow.get(player);
		if (iThrow == null) {
			transformer = null;
			return;
		}

		if (transformer != null) {
			ThrowabilityAnimation.animatePost(player, iThrow, transformer);
			transformer.copyFromBodyToWear();
			transformer = null;
		}
	}
}
