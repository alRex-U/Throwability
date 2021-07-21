package com.alrexu.throwability.client.input;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
	public static final KeyBinding keyBindThrow = new KeyBinding("key.throwability.throw", GLFW.GLFW_KEY_T, "key.categories.gameplay");

	public static KeyBinding getKeyThrow() {
		return keyBindThrow;
	}

	@SubscribeEvent
	public static void register(FMLClientSetupEvent event) {
		ClientRegistry.registerKeyBinding(keyBindThrow);
	}
}
