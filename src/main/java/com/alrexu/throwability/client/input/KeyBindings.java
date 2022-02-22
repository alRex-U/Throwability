package com.alrexu.throwability.client.input;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
	public static final KeyMapping keyBindThrow = new KeyMapping("key.throwability.throw", GLFW.GLFW_KEY_U, "key.categories.inventory");

	public static KeyMapping getKeyThrow() {
		return keyBindThrow;
	}

	@SubscribeEvent
	public static void register(FMLClientSetupEvent event) {
		ClientRegistry.registerKeyBinding(keyBindThrow);
	}
}
