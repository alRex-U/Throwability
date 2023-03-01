package com.alrex.throwability.common.logic;

import com.alrex.throwability.client.input.KeyBindings;
import com.alrex.throwability.client.input.KeyRecorder;
import com.alrex.throwability.common.capability.IThrow;
import com.alrex.throwability.common.capability.ThrowType;
import com.alrex.throwability.common.network.SyncThrowStateMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class Processor {
	private int currentItem = 0;
	private boolean oldCharging = false;

	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event) {
		if (event.side != LogicalSide.CLIENT) return;
		Player player = event.player;

		if (event.phase != TickEvent.Phase.START) return;
		IThrow iThrow = IThrow.get(player);
		if (iThrow == null) return;
		iThrow.tick();
		oldCharging = iThrow.isCharging();
		if (!event.player.isLocalPlayer()) return;

		if (currentItem != player.getInventory().selected || Minecraft.getInstance().screen != null) {
			iThrow.cancel();
			currentItem = player.getInventory().selected;
			return;
		}

		if (KeyRecorder.getStateThrow().isPressed()) {
			iThrow.chargeThrowPower();
		} else if (KeyBindings.getKeyThrow().isDown() && iThrow.isCharging()) {
			iThrow.chargeThrowPower();
		}

		if (iThrow.isCharging() && !KeyBindings.getKeyThrow().isDown()) {
			if (iThrow.getChargingPower() > iThrow.getMaxPower() / 5f) {
				ThrowType type;
				if (KeyBindings.getKeySpecialModifier().isDown()) {
					type = ThrowType.One_As_Entity;
				} else if (KeyBindings.getKeyAllModifier().isDown()) {
					type = ThrowType.All_As_Item;
				} else {
					type = ThrowType.One_As_Item;
				}
				iThrow.throwItem(player.getInventory().selected, type, iThrow.getChargingPower());
			} else {
				iThrow.cancel();
			}
		}
		if (oldCharging != iThrow.isCharging() || iThrow.isCharging()) {
			SyncThrowStateMessage.send(player, iThrow);
		}
	}
}
