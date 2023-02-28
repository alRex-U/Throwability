package com.alrex.throwability.client.input;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class KeyRecorder {
	private static final KeyState keyThrow = new KeyState();

	public static KeyState getStateThrow() {
		return keyThrow;
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.START) return;
		record(KeyBindings.getKeyThrow(), keyThrow);
	}

	private static void record(KeyMapping keyBinding, KeyState state) {
		state.pressed = (keyBinding.isDown() && state.tickKeyDown == 0);
		state.doubleTapped = (keyBinding.isDown() && 0 < state.tickNotKeyDown && state.tickNotKeyDown <= 2);
		if (keyBinding.isDown()) {
			state.tickKeyDown++;
			state.tickNotKeyDown = 0;
		} else {
			state.tickKeyDown = 0;
			state.tickNotKeyDown++;
		}
	}

	public static class KeyState {
		private boolean pressed = false;
		private boolean doubleTapped = false;
		private int tickKeyDown = 0;
		private int tickNotKeyDown = 0;

		public boolean isPressed() {
			return pressed;
		}

		public boolean isDoubleTapped() {
			return doubleTapped;
		}

		public int getTickKeyDown() {
			return tickKeyDown;
		}

		public int getTickNotKeyDown() {
			return tickNotKeyDown;
		}
	}
}
