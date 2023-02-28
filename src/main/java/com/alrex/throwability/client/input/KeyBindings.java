package com.alrex.throwability.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
	private static final KeyMapping keyBindThrow = new KeyMapping("key.throwability.throw", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_MIDDLE, "key.categories.throwability");
	private static final KeyMapping keyBindAllModifier = new KeyMapping("key.throwability.modifier.all", GLFW.GLFW_KEY_LEFT_SHIFT, "key.categories.throwability");
	private static final KeyMapping keyBindSpecialModifier = new KeyMapping("key.throwability.modifier.special", GLFW.GLFW_KEY_LEFT_CONTROL, "key.categories.throwability");

	public static KeyMapping getKeyThrow() {
		return keyBindThrow;
	}

	public static KeyMapping getKeyAllModifier() {
		return keyBindAllModifier;
	}

	public static KeyMapping getKeySpecialModifier() {
		return keyBindSpecialModifier;
	}

	@SubscribeEvent
	public static void register(FMLClientSetupEvent event) {
		ClientRegistry.registerKeyBinding(keyBindThrow);
		ClientRegistry.registerKeyBinding(keyBindAllModifier);
		ClientRegistry.registerKeyBinding(keyBindSpecialModifier);
	}
}
