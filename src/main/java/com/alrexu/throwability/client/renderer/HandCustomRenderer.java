package com.alrexu.throwability.client.renderer;

import com.alrexu.throwability.common.capability.IThrow;
import com.alrexu.throwability.common.capability.capabilities.ThrowProvider;
import com.alrexu.throwability.utilities.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class HandCustomRenderer {
	@SubscribeEvent
	public void onRenderPre(RenderPlayerEvent.Pre event) {
		AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) event.getPlayer();
		IThrow iThrow = ThrowProvider.get(player);
		if (iThrow == null) return;
		HandSide hand = player.getPrimaryHand();

		if (iThrow.isCharging()) {
			ClientPlayerEntity mainPlayer = Minecraft.getInstance().player;
			if (mainPlayer == null) return;
			Vector3d posOffset = RenderUtil.getPlayerOffset(mainPlayer, player, event.getPartialRenderTick());
			PlayerRenderer renderer = event.getRenderer();
			PlayerModel<AbstractClientPlayerEntity> model = renderer.getEntityModel();

			event.getMatrixStack().push();
			event.getMatrixStack().translate(posOffset.getX(), posOffset.getY(), posOffset.getZ());

			float rotate;
			if (iThrow.getChargingPower() == iThrow.getMaxPower()) {
				rotate = -20f;
			} else {
				rotate = MathHelper.lerp((iThrow.getChargingPower() + event.getPartialRenderTick()) / (float) iThrow.getMaxPower(), 130f, -20f);
			}
			if (hand == HandSide.RIGHT) {
				model.bipedRightArm.showModel = true;
				RenderUtil.rotateRightArm(player, model.bipedRightArm,
						(float) Math.toRadians(rotate),
						(float) -Math.toRadians(player.renderYawOffset),
						(float) Math.toRadians(0.0F)
				);
				model.bipedRightArmwear.showModel = true;
				RenderUtil.rotateRightArm(player, model.bipedRightArmwear,
						(float) Math.toRadians(rotate),
						(float) -Math.toRadians(player.renderYawOffset),
						(float) Math.toRadians(0.0F)
				);
			} else {
				model.bipedLeftArm.showModel = true;
				RenderUtil.rotateLeftArm(player, model.bipedLeftArm,
						(float) Math.toRadians(rotate),
						(float) -Math.toRadians(player.renderYawOffset),
						(float) Math.toRadians(0.0F)
				);
				model.bipedLeftArmwear.showModel = true;
				RenderUtil.rotateLeftArm(player, model.bipedLeftArmwear,
						(float) Math.toRadians(rotate),
						(float) -Math.toRadians(player.renderYawOffset),
						(float) Math.toRadians(0.0F)
				);
			}
			ResourceLocation location = player.getLocationSkin();
			int light = renderer.getPackedLight(player, event.getPartialRenderTick());
			renderer.getRenderManager().textureManager.bindTexture(location);

			if (hand == HandSide.RIGHT) {
				model.bipedRightArm.render(
						event.getMatrixStack(),
						event.getBuffers().getBuffer(RenderType.getEntitySolid(location)),
						light,
						OverlayTexture.NO_OVERLAY
				);
				model.bipedRightArmwear.render(
						event.getMatrixStack(),
						event.getBuffers().getBuffer(RenderType.getEntityTranslucent(location)),
						light,
						OverlayTexture.NO_OVERLAY
				);
			} else {
				model.bipedLeftArm.render(
						event.getMatrixStack(),
						event.getBuffers().getBuffer(RenderType.getEntitySolid(location)),
						light,
						OverlayTexture.NO_OVERLAY
				);
				model.bipedLeftArmwear.render(
						event.getMatrixStack(),
						event.getBuffers().getBuffer(RenderType.getEntityTranslucent(location)),
						light,
						OverlayTexture.NO_OVERLAY
				);
			}

			event.getMatrixStack().pop();
			if (hand == HandSide.RIGHT) {
				model.bipedRightArm.showModel = false;
				model.bipedRightArmwear.showModel = false;
			} else {
				model.bipedLeftArm.showModel = false;
				model.bipedLeftArmwear.showModel = false;
			}
		}
	}

	@SubscribeEvent
	public void onRenderPost(RenderPlayerEvent.Post event) {
		PlayerRenderer renderer = event.getRenderer();
		PlayerModel<AbstractClientPlayerEntity> model = renderer.getEntityModel();

		model.bipedRightArm.showModel = true;
		model.bipedLeftArm.showModel = true;
		model.bipedLeftArmwear.showModel = true;
		model.bipedRightArmwear.showModel = true;
	}
}
