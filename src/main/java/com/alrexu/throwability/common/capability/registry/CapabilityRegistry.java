package com.alrexu.throwability.common.capability.registry;

import com.alrexu.throwability.common.capability.IThrow;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CapabilityRegistry {
	@SubscribeEvent
	public void register(RegisterCapabilitiesEvent event) {
		event.register(IThrow.class);
	}

}
