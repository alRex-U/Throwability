package com.alrex.throwability.client.hud;

import com.alrex.throwability.Throwability;
import com.alrex.throwability.ThrowabilityConfig;
import com.alrex.throwability.common.ability.AbstractThrowingAbility;
import com.alrex.throwability.common.ability.IThrowabilityProvider;
import com.alrex.throwability.common.ability.LocalThrowingAbility;
import com.alrex.throwability.common.ability.ThrowType;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class ThrowPowerMeter extends AbstractGui {
	private static final ResourceLocation ICON_LOCATION = new ResourceLocation(Throwability.MOD_ID, "textures/gui/gui_icon.png");

	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent.Pre event) {
		if (ThrowabilityConfig.Client.HIDE_HUD.get()) return;
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

	public static void blitWithColor(MatrixStack matrixStack, int x, int y, int xTex, int yTex, int width, int height, int texWidth, int texHeight, int r, int g, int b, int a) {
		BufferBuilder lvt_10_1_ = Tessellator.getInstance().getBuilder();
		float sX = x, sY = y, eX = x + width, eY = y + height;
		float sXTex = xTex / (float) texWidth, sYTex = yTex / (float) texHeight, eXTex = (xTex + width) / (float) texWidth, eYTex = (yTex + height) / (float) texHeight;
		Matrix4f pose = matrixStack.last().pose();
		lvt_10_1_.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
		lvt_10_1_.vertex(pose, sX, eY, 0f).color(r, g, b, a).uv(sXTex, eYTex).endVertex();
		lvt_10_1_.vertex(pose, eX, eY, 0f).color(r, g, b, a).uv(eXTex, eYTex).endVertex();
		lvt_10_1_.vertex(pose, eX, sY, 0f).color(r, g, b, a).uv(eXTex, sYTex).endVertex();
		lvt_10_1_.vertex(pose, sX, sY, 0f).color(r, g, b, a).uv(sXTex, sYTex).endVertex();
		lvt_10_1_.end();
		RenderSystem.enableAlphaTest();
		WorldVertexBufferUploader.end(lvt_10_1_);
	}

	private void renderMeter(MatrixStack stack, AbstractThrowingAbility throwingAbility, float partialTick) {
		Minecraft mc = Minecraft.getInstance();

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
		float fadeInScale = ThrowabilityConfig.Client.HUD_FADE_IN.get() ? (1f - MathHelper.square(1f - chargePhase)) : 1f;
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

		RenderSystem.enableTexture();

		mc.getTextureManager().bind(ICON_LOCATION);
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
