package com.alrexu.throwability.client.hud;

import com.alrexu.throwability.common.capability.IThrow;
import com.alrexu.throwability.common.capability.capabilities.ThrowProvider;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class ThrowPowerMeter {
	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent.Pre event) {
		Minecraft mc = Minecraft.getInstance();
		PlayerEntity player = mc.player;
		IThrow iThrow = ThrowProvider.get(player);
		if (iThrow == null || !iThrow.isCharging() || event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE)
			return;
		event.setCanceled(true);

		RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
		RenderSystem.disableBlend();

		renderMeter(event.getMatrixStack(), iThrow);

		RenderSystem.enableBlend();
		RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	private void renderMeter(MatrixStack stack, IThrow iThrow) {
		Minecraft mc = Minecraft.getInstance();
		mc.getTextureManager().bindTexture(AbstractGui.field_230665_h_);

		int max = 182;
		int k = (int) ((float) iThrow.getChargingPower() * max / iThrow.getMaxPower());
		int height = mc.getMainWindow().getScaledHeight() - 29;
		int x = mc.getMainWindow().getScaledWidth() / 2 - 91;
		AbstractGui.func_238464_a_(stack, x, height, 0, 0, 84, 182, 5, 256, 256);
		AbstractGui.func_238464_a_(stack, x, height, 0, 0, 89, k, 5, 256, 256);
	}
}
