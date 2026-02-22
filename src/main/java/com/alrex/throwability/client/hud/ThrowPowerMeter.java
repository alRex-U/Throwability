package com.alrex.throwability.client.hud;

import com.alrex.throwability.common.ability.AbstractThrowingAbility;
import com.alrex.throwability.common.ability.IThrowabilityProvider;
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
        if (!(player instanceof IThrowabilityProvider)) return;
        AbstractThrowingAbility throwingAbility = ((IThrowabilityProvider) player).getThrowAbility();
        if (!throwingAbility.isCharging()) return;

		event.setCanceled(true);

		RenderSystem.disableBlend();

        renderMeter(event.getMatrixStack(), throwingAbility);

		RenderSystem.enableBlend();
	}

    private void renderMeter(MatrixStack stack, AbstractThrowingAbility throwingAbility) {
		Minecraft mc = Minecraft.getInstance();
		mc.getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);

		int max = 182;
        int size = (int) ((float) throwingAbility.getChargingTick() * max / throwingAbility.getMaxChargingTick());
		MainWindow window = mc.getWindow();
		int height = window.getGuiScaledHeight() - 29;
		int x = window.getGuiScaledWidth() / 2 - 91;
		AbstractGui.blit(stack, x, height, 0, 0, 84, 182, 5, 256, 256);
		AbstractGui.blit(stack, x, height, 0, 0, 89, size, 5, 256, 256);
	}
}
