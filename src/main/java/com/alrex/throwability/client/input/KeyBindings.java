package com.alrex.throwability.client.input;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
	private static final KeyBinding keyBindThrow = new KeyBinding("key.throwability.throw", GLFW.GLFW_KEY_Q, "key.categories.throwability");
	private static final KeyBinding keyBindSpecialModifier = new KeyBinding("key.throwability.modifier.special", GLFW.GLFW_KEY_LEFT_SHIFT, "key.categories.throwability");

	public static KeyBinding getKeyThrow() {
		return keyBindThrow;
	}

	public static KeyBinding getKeySpecialModifier() {
		return keyBindSpecialModifier;
	}

	public static KeyBinding getKeyDropItem() {
		return Minecraft.getInstance().options.keyDrop;
	}

	public static boolean throwAndDropAreConflicting() {
		return keyBindThrow.same(getKeyDropItem());
	}

	@SubscribeEvent
	public static void register(FMLClientSetupEvent event) {
		ClientRegistry.registerKeyBinding(keyBindThrow);
		ClientRegistry.registerKeyBinding(keyBindSpecialModifier);
	}
}
