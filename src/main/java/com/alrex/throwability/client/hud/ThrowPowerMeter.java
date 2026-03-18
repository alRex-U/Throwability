package com.alrex.throwability.client.hud;

import com.alrex.throwability.Throwability;
import com.alrex.throwability.ThrowabilityConfig;
import com.alrex.throwability.common.ability.AbstractThrowingAbility;
import com.alrex.throwability.common.ability.IThrowabilityProvider;
import com.alrex.throwability.common.ability.LocalThrowingAbility;
import com.alrex.throwability.common.ability.ThrowType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

@OnlyIn(Dist.CLIENT)
public class ThrowPowerMeter extends GuiComponent implements IIngameOverlay {
	private static final ResourceLocation ICON_LOCATION = new ResourceLocation(Throwability.MOD_ID, "textures/gui/gui_icon.png");

	public static void blitWithColor(PoseStack matrixStack, int x, int y, int xTex, int yTex, int width, int height, int texWidth, int texHeight, int r, int g, int b, int a) {
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
		float sX = x, sY = y, eX = x + width, eY = y + height;
		float sXTex = xTex / (float) texWidth, sYTex = yTex / (float) texHeight, eXTex = (xTex + width) / (float) texWidth, eYTex = (yTex + height) / (float) texHeight;
		Matrix4f pose = matrixStack.last().pose();
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
		bufferBuilder.vertex(pose, sX, eY, 0f).color(r, g, b, a).uv(sXTex, eYTex).endVertex();
		bufferBuilder.vertex(pose, eX, eY, 0f).color(r, g, b, a).uv(eXTex, eYTex).endVertex();
		bufferBuilder.vertex(pose, eX, sY, 0f).color(r, g, b, a).uv(eXTex, sYTex).endVertex();
		bufferBuilder.vertex(pose, sX, sY, 0f).color(r, g, b, a).uv(sXTex, sYTex).endVertex();
		bufferBuilder.end();
		BufferUploader.end(bufferBuilder);
	}

	@Override
	public void render(ForgeIngameGui forgeIngameGui, PoseStack poseStack, float partialTick, int width, int height) {
		var mc = Minecraft.getInstance();
		Player player = mc.player;
		if (mc.options.getCameraType() != CameraType.FIRST_PERSON) return;
        if (!(player instanceof IThrowabilityProvider)) return;
        AbstractThrowingAbility throwingAbility = ((IThrowabilityProvider) player).getThrowAbility();
        if (!throwingAbility.isCharging()) return;

		RenderSystem.disableBlend();

		renderMeter(poseStack, throwingAbility, partialTick, height, width);

		RenderSystem.enableBlend();
	}

	private void renderMeter(PoseStack stack, AbstractThrowingAbility throwingAbility, float partialTick, int screenHeight, int screenWidth) {
		Minecraft mc = Minecraft.getInstance();

		float chargePhase = Mth.clamp((throwingAbility.getChargingTick() + partialTick) / throwingAbility.getMaxChargingTick(), 0, 1);
		var window = mc.getWindow();
		float guiScale = (float) window.getGuiScale();
		float screenCenterY = screenHeight / 2f;
		float screenCenterX = screenWidth / 2f;

		final int division = 16;
		float innerRadius = 4f * guiScale;
		float outerRadius = 6f * guiScale;
		Matrix4f pose = stack.last().pose();

		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		var tesselator = Tesselator.getInstance();
		BufferBuilder builder = tesselator.getBuilder();
		float fadeInScale = ThrowabilityConfig.Client.HUD_FADE_IN.get() ? (1f - Mth.square(1f - chargePhase)) : 1f;
		int a = (int) (200f * fadeInScale);
		int r, g, b;
		ThrowType currentType = LocalThrowingAbility.getCurrentThrowType();
		switch (currentType) {
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
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		builder.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
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
					screenCenterX - innerRadius * Mth.sin(angle),
					screenCenterY - innerRadius * Mth.cos(angle),
					0
			).color(r, g, b, a).endVertex();
			builder.vertex(pose,
					screenCenterX - outerRadius * Mth.sin(angle),
					screenCenterY - outerRadius * Mth.cos(angle),
					0
			).color(r, g, b, a).endVertex();

			if (shouldBreak) {
				break;
			}
		}
		tesselator.end();

		RenderSystem.enableTexture();

		RenderSystem.setShaderTexture(0, ICON_LOCATION);
		for (int i = 0; i < ThrowType.values().length; i++) {
			int yOffset;
			if (i == currentType.ordinal()) {
				yOffset = -2;
				r = 255;
				g = 255;
				b = 255;
				a = (int) (200f * fadeInScale);
			} else {
				yOffset = 0;
				r = 128;
				g = 128;
				b = 128;
				a = (int) Math.min(200f * fadeInScale, 128f);
			}

			blitWithColor(stack,
					(int) (screenCenterX - 15 + 11 * i + 1),
					(int) (screenCenterY + outerRadius + 4 + yOffset),
					8 * i, 0, 8, 8, 32, 32,
					r, g, b, a
			);
		}
	}

}
