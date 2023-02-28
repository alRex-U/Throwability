package com.alrex.throwability.client.hud;

import com.alrex.throwability.common.capability.IThrow;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@OnlyIn(Dist.CLIENT)
public class ThrowPowerMeter implements IIngameOverlay {
	@SubscribeEvent
	public static void register(FMLCommonSetupEvent event) {
		OverlayRegistry.registerOverlayTop("Throwability Power HUD", new ThrowPowerMeter());
	}

	@Override
	public void render(ForgeIngameGui gui, PoseStack poseStack, float partialTick, int width, int height) {
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if (player == null) return;
		IThrow iThrow = IThrow.get(player);
		if (iThrow == null || !iThrow.isCharging()) return;
		RenderSystem.setShaderTexture(0, Gui.GUI_ICONS_LOCATION);
		int max = 182;
		int size = (int) (iThrow.getChargingPower() * max / iThrow.getMaxPower());
		int tHeight = height - 29;
		int x = width / 2 - 91;
		Gui.blit(poseStack, x, tHeight, 0, 0, 84, 182, 5, 256, 256);
		Gui.blit(poseStack, x, tHeight, 0, 0, 89, size, 5, 256, 256);
	}
}
