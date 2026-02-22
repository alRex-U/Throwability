package com.alrex.throwability.client.animation;

public class ThrowabilityAnimation {
	/*
	public static void animatePost(
			PlayerEntity player,
			LocalThrowingAbility throwingAbility,
			PlayerModelTransformer transformer
	) {
		if (!throwingAbility.isCharging()) return;
		float phase = MathHelper.clamp(throwingAbility.getChargingTick()+transformer.getPartialTick()/throwingAbility.getMaxChargeTick(),0,1);
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
	public static void rotate(){

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

	 */
}
