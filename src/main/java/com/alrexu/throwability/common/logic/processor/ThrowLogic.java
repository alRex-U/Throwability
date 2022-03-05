package com.alrexu.throwability.common.logic.processor;

import com.alrexu.throwability.client.input.KeyBindings;
import com.alrexu.throwability.client.input.KeyRecorder;
import com.alrexu.throwability.common.capability.IThrow;
import com.alrexu.throwability.common.capability.capabilities.ThrowProvider;
import com.alrexu.throwability.common.network.ItemThrowMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class ThrowLogic {
	private int currentItem = 0;

	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event) {
		if (!event.player.isLocalPlayer()) return;
		if (event.side != LogicalSide.CLIENT) return;
		Player player = event.player;

		if (event.phase != TickEvent.Phase.START) return;
		IThrow iThrow = ThrowProvider.get(player);
		if (iThrow == null) return;

		if (currentItem != player.getInventory().selected || Minecraft.getInstance().screen != null) {
			iThrow.cancel();
			currentItem = player.getInventory().selected;
			return;
		}

		if (KeyRecorder.getStateThrow().isPressed() && player.isOnGround()) {
			iThrow.chargeThrowPower();
		} else if (KeyBindings.getKeyThrow().isDown() && iThrow.isCharging()) {
			iThrow.chargeThrowPower();
		}

		if (iThrow.isCharging() && !KeyBindings.getKeyThrow().isDown()) {
			if (iThrow.getStrength() > 1) {
				ItemThrowMessage.send(player, iThrow.getStrength(), player.isShiftKeyDown());
				iThrow.throwItem(player, player.isShiftKeyDown());
			} else {
				iThrow.cancel();
			}
		}
	}
}
