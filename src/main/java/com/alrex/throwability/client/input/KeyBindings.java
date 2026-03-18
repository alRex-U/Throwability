package com.alrex.throwability.client.input;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
	private static final KeyMapping keyBindThrow = new KeyMapping("key.throwability.throw", GLFW.GLFW_KEY_Q, "key.categories.throwability");
	private static final KeyMapping keyBindSpecialModifier = new KeyMapping("key.throwability.modifier.special", GLFW.GLFW_KEY_LEFT_SHIFT, "key.categories.throwability");

	public static KeyMapping getKeyThrow() {
		return keyBindThrow;
	}

	public static KeyMapping getKeySpecialModifier() {
		return keyBindSpecialModifier;
	}

	public static KeyMapping getKeyDropItem() {
		return Minecraft.getInstance().options.keyDrop;
	}

	public static boolean throwAndDropAreConflicting() {
		return keyBindThrow.same(getKeyDropItem());
	}

	@SubscribeEvent
	public static void register(RegisterKeyMappingsEvent event) {
		event.register(keyBindThrow);
		event.register(keyBindSpecialModifier);
	}
}
