package com.alrex.throwability.common.capability;

import com.alrex.throwability.Throwability;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;

public class Capabilities {
	public static final ResourceLocation THROWABLE_LOCATION = new ResourceLocation(Throwability.MOD_ID, "throwable");
	public static final Capability<IThrowable> THROWABLE_CAPABILITY = CapabilityManager.get(new CapabilityToken<IThrowable>() {
	});

	@SubscribeEvent
	public static void register(RegisterCapabilitiesEvent event) {
		event.register(IThrowable.class);
	}
}
