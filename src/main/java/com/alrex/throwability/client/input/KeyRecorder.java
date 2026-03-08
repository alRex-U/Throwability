package com.alrex.throwability.client.input;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class KeyRecorder {
	private static final KeyState keyThrow = new KeyState();
	private static final KeyState keyDrop = new KeyState();

	public static KeyState getStateThrow() {
		return keyThrow;
	}

	public static KeyState getStateDrop() {
		return keyDrop;
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.START) return;
		record(KeyBindings.getKeyThrow(), keyThrow);
		record(KeyBindings.getKeyDropItem(), keyDrop);
	}

	private static void record(KeyBinding keyBinding, KeyState state) {
		state.pressed = (keyBinding.isDown() && state.tickKeyDown == 0);
		state.doubleTapped = (keyBinding.isDown() && 0 < state.tickNotKeyDown && state.tickNotKeyDown <= 2);
		if (keyBinding.isDown()) {
			state.tickKeyDown++;
			state.tickNotKeyDown = 0;
			state.justReleased = false;
		} else {
			state.justReleased = (state.tickKeyDown > 0);
			state.tickKeyDown = 0;
			state.tickNotKeyDown++;
		}
	}

	public static class KeyState {
		private boolean pressed = false;
		private boolean doubleTapped = false;
		private boolean justReleased = false;
		private int tickKeyDown = 0;
		private int tickNotKeyDown = 0;

		public boolean isPressed() {
			return pressed;
		}

		public boolean isDoubleTapped() {
			return doubleTapped;
		}

		public boolean isJustReleased() {
			return justReleased;
		}

		public int getTickKeyDown() {
			return tickKeyDown;
		}

		public int getTickNotKeyDown() {
			return tickNotKeyDown;
		}
	}
}
