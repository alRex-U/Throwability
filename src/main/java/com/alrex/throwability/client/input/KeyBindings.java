package com.alrex.throwability.client.input;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
	private static final KeyBinding keyBindThrow = new KeyBinding("key.throwability.throw", InputMappings.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_MIDDLE, "key.categories.throwability");
	private static final KeyBinding keyBindAllModifier = new KeyBinding("key.throwability.modifier.all", GLFW.GLFW_KEY_LEFT_SHIFT, "key.categories.throwability");
	private static final KeyBinding keyBindSpecialModifier = new KeyBinding("key.throwability.modifier.special", GLFW.GLFW_KEY_LEFT_CONTROL, "key.categories.throwability");

	public static KeyBinding getKeyThrow() {
		return keyBindThrow;
	}

	public static KeyBinding getKeyAllModifier() {
		return keyBindAllModifier;
	}

	public static KeyBinding getKeySpecialModifier() {
		return keyBindSpecialModifier;
	}

	@SubscribeEvent
	public static void register(FMLClientSetupEvent event) {
		ClientRegistry.registerKeyBinding(keyBindThrow);
		ClientRegistry.registerKeyBinding(keyBindAllModifier);
		ClientRegistry.registerKeyBinding(keyBindSpecialModifier);
	}
}
