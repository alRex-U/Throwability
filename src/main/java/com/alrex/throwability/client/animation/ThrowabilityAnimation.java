package com.alrex.throwability.client.animation;

import com.alrex.throwability.common.capability.IThrow;
import com.alrex.throwability.utils.MathUtil;
import com.alrex.throwability.utils.VectorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.HandSide;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ThrowabilityAnimation {
	public static void animatePost(
			PlayerEntity player,
			IThrow iThrow,
			PlayerModelTransformer transformer
	) {
		if (!iThrow.isCharging()) return;
		float phase = MathUtil.lerp(iThrow.getOldPower(), iThrow.getChargingPower(), transformer.getPartialTick()) / iThrow.getMaxPower();
		float factor = 1 - MathUtil.squaring(1 - phase);
		if (player.getMainArm() == HandSide.RIGHT) {
			transformer
					.rotateLeftArm(
							(float) Math.toRadians(-75),
							0,
							0,
							factor
					)
					.rotateRightArm(
							(float) Math.toRadians(-140),
							(float) Math.toRadians(20),
							0,
							factor
					);
		} else {
			transformer
					.rotateRightArm(
							(float) Math.toRadians(-75),
							0,
							0,
							factor
					)
					.rotateLeftArm(
							(float) Math.toRadians(-135),
							(float) Math.toRadians(-20),
							0,
							factor
					);
		}
	}

	@SubscribeEvent
	public static void onRenderTick(TickEvent.RenderTickEvent event) {
		if (event.phase == TickEvent.Phase.END) return;
		ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
		if (clientPlayer == null) return;
		for (PlayerEntity player : clientPlayer.level.players()) {
			IThrow iThrow = IThrow.get(player);
			if (iThrow == null) continue;
			if (iThrow.isCharging()) {
				float phase = MathUtil.lerp(iThrow.getOldPower(), iThrow.getChargingPower(), event.renderTickTime) / iThrow.getMaxPower();
				float factor = 1 - MathUtil.squaring(1 - phase);
				player.yBodyRot = (player.getMainArm() == HandSide.RIGHT ? 40 : -40) * factor + (float) VectorUtil.toYawDegree(player.getLookAngle().multiply(1, 0, 1));
			}
		}
	}
}
