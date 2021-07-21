package com.alrexu.throwability.common.capability.registry;

import com.alrexu.throwability.common.capability.IThrow;
import com.alrexu.throwability.common.capability.impl.Throw;
import com.alrexu.throwability.common.capability.storage.StorageThrow;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CapabilityRegistry {
	@SubscribeEvent
	public void register(FMLCommonSetupEvent event) {
		CapabilityManager.INSTANCE.register(IThrow.class, new StorageThrow(), Throw::new);
	}

}
