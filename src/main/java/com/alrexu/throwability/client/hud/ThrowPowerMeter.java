package com.alrexu.throwability.client.hud;

import com.alrexu.throwability.common.capability.IThrow;
import com.alrexu.throwability.common.capability.capabilities.ThrowProvider;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;

@OnlyIn(Dist.CLIENT)
public class ThrowPowerMeter {
	public static final IIngameOverlay THROW_METER = OverlayRegistry.registerOverlayTop(
			"Throw Meter", (gui, mStack, partialTicks, screenWidth, screenHeight) -> {
				Minecraft mc = Minecraft.getInstance();
				Player player = mc.player;
				if (player == null) return;
				IThrow iThrow = ThrowProvider.get(player);
				if (iThrow == null || !iThrow.isCharging()) return;

				renderMeter(mStack, gui, iThrow, screenHeight, screenWidth / 2 - 91);
			});

	private static void renderMeter(PoseStack stack, Gui gui, IThrow iThrow, int screenHeight, int x) {
		RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
		float scale = ((float) iThrow.getChargingPower() / iThrow.getMaxPower());
		int width = (int) (scale * 183.0F);
		int y = screenHeight - 32 + 3;
		gui.blit(stack, x, y, 0, 84, 182, 5);
		if (width > 0) {
			gui.blit(stack, x, y, 0, 89, width, 5);
		}
	}
}
