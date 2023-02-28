package com.alrex.throwability.client.hud;

import com.alrex.throwability.common.capability.IThrow;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class ThrowPowerMeter extends AbstractGui {
	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent.Pre event) {
		Minecraft mc = Minecraft.getInstance();
		PlayerEntity player = mc.player;
		if (player == null || event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) return;
		IThrow iThrow = IThrow.get(player);
		if (iThrow == null || !iThrow.isCharging())
			return;
		event.setCanceled(true);

		RenderSystem.disableBlend();

		renderMeter(event.getMatrixStack(), iThrow);

		RenderSystem.enableBlend();
	}

	private void renderMeter(MatrixStack stack, IThrow iThrow) {
		Minecraft mc = Minecraft.getInstance();
		mc.getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);

		int max = 182;
		int size = (int) ((float) iThrow.getChargingPower() * max / iThrow.getMaxPower());
		MainWindow window = mc.getWindow();
		int height = window.getGuiScaledHeight() - 29;
		int x = window.getGuiScaledWidth() / 2 - 91;
		AbstractGui.blit(stack, x, height, 0, 0, 84, 182, 5, 256, 256);
		AbstractGui.blit(stack, x, height, 0, 0, 89, size, 5, 256, 256);
	}
}
