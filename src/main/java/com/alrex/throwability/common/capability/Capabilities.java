package com.alrex.throwability.common.capability;

import com.alrex.throwability.Throwability;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Capabilities {
	public static final ResourceLocation THROW_LOCATION = new ResourceLocation(Throwability.MOD_ID, "throw");
	public static final Capability<IThrow> THROW_CAPABILITY = CapabilityManager.get(new CapabilityToken<IThrow>() {
	});

	@SubscribeEvent
	public static void register(RegisterCapabilitiesEvent event) {
		event.register(IThrow.class);
	}
}
