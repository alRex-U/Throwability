package com.alrexu.throwability.common.logic.processor;

import com.alrexu.throwability.client.input.KeyBindings;
import com.alrexu.throwability.client.input.KeyRecorder;
import com.alrexu.throwability.common.capability.IThrow;
import com.alrexu.throwability.common.capability.capabilities.ThrowProvider;
import com.alrexu.throwability.common.network.ItemThrowMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class ThrowLogic {
	private int currentItem = 0;

	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event) {
		if (!event.player.isUser()) return;
		if (event.side != LogicalSide.CLIENT) return;
		PlayerEntity player = event.player;

		if (event.phase != TickEvent.Phase.START) return;
		IThrow iThrow = ThrowProvider.get(player);
		if (iThrow == null) return;

		if (currentItem != player.inventory.currentItem) {
			iThrow.cancel();
			currentItem = player.inventory.currentItem;
			return;
		}

		if (KeyRecorder.getStateThrow().isPressed() && player.collidedVertically) {
			iThrow.chargeThrowPower();
		} else if (KeyBindings.getKeyThrow().isKeyDown() && iThrow.isCharging()) {
			iThrow.chargeThrowPower();
		}

		if (iThrow.isCharging() && !KeyBindings.getKeyThrow().isKeyDown()) {
			if (iThrow.getStrength() > 1) {
				ItemThrowMessage.send(player, iThrow.getStrength(), player.isSneaking());
				iThrow.throwItem(player, player.isSneaking());
			} else {
				iThrow.cancel();
			}
		}
	}
}
