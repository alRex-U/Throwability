package com.alrex.throwability.client.hud;

import com.alrex.throwability.ThrowabilityConfig;
import com.alrex.throwability.common.ability.AbstractThrowingAbility;
import com.alrex.throwability.common.ability.IThrowabilityProvider;
import com.alrex.throwability.common.ability.LocalThrowingAbility;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class ThrowPowerMeter extends AbstractGui {
	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent.Pre event) {
		Minecraft mc = Minecraft.getInstance();
		PlayerEntity player = mc.player;
		if (player == null || event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) return;
		if (mc.options.getCameraType() != PointOfView.FIRST_PERSON) return;
        if (!(player instanceof IThrowabilityProvider)) return;
        AbstractThrowingAbility throwingAbility = ((IThrowabilityProvider) player).getThrowAbility();
        if (!throwingAbility.isCharging()) return;

		RenderSystem.disableBlend();

		renderMeter(event.getMatrixStack(), throwingAbility, event.getPartialTicks());

		RenderSystem.enableBlend();
	}

	private void renderMeter(MatrixStack stack, AbstractThrowingAbility throwingAbility, float partialTick) {
		Minecraft mc = Minecraft.getInstance();
		mc.getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);

		float chargePhase = MathHelper.clamp((throwingAbility.getChargingTick() + partialTick) / throwingAbility.getMaxChargingTick(), 0, 1);
		MainWindow window = mc.getWindow();
		int screenHeight = window.getGuiScaledHeight();
		int screenWidth = window.getGuiScaledWidth();
		float guiScale = (float) window.getGuiScale();
		float screenCenterY = screenHeight / 2f;
		float screenCenterX = screenWidth / 2f;

		final int division = 16;
		float innerRadius = 4f * guiScale;
		float outerRadius = 6f * guiScale;
		Matrix4f pose = stack.last().pose();

		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.disableAlphaTest();
		RenderSystem.defaultBlendFunc();

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder builder = tessellator.getBuilder();
		int a = ThrowabilityConfig.Client.HUD_FADE_IN.get() ? (int) (200f * (1 - MathHelper.square(1 - chargePhase))) : 200;
		int r, g, b;
		switch (LocalThrowingAbility.getCurrentThrowType()) {
			case ONE_AS_ENTITY:
				r = 10;
				g = 229;
				b = 255;
				break;
			case ONE_AS_ITEM:
				r = 20;
				g = 230;
				b = 118;
				break;
			case ALL_AS_ITEM:
				r = 198;
				g = 255;
				b = 10;
				break;
			default:
				r = 255;
				g = 0;
				b = 0;
		}
		builder.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
		builder.vertex(pose, screenCenterX, screenCenterY - innerRadius, 0).color(r, g, b, a).endVertex();
		builder.vertex(pose, screenCenterX, screenCenterY - outerRadius, 0).color(r, g, b, a).endVertex();
		for (int i = 0; i < division; i++) {
			boolean shouldBreak = false;
			float renderingPhase = (i + 1f) / division;
			if (renderingPhase > chargePhase) {
				renderingPhase = chargePhase;
				shouldBreak = true;
			}
			float angle = (float) ((2. * Math.PI) * renderingPhase);
			builder.vertex(pose,
					screenCenterX - innerRadius * MathHelper.sin(angle),
					screenCenterY - innerRadius * MathHelper.cos(angle),
					0
			).color(r, g, b, a).endVertex();
			builder.vertex(pose,
					screenCenterX - outerRadius * MathHelper.sin(angle),
					screenCenterY - outerRadius * MathHelper.cos(angle),
					0
			).color(r, g, b, a).endVertex();

			if (shouldBreak) {
				break;
			}
		}
		tessellator.end();

		RenderSystem.disableBlend();
		RenderSystem.enableAlphaTest();
		RenderSystem.enableTexture();
	}
}
