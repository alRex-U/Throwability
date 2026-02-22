package com.alrex.throwability.common.capability;

import com.alrex.throwability.common.capability.storage.ThrowableStorage;
import com.alrex.throwability.common.capability.throwable.StandardThrowable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nonnull;

public class Capabilities {
	@CapabilityInject(IThrowable.class)
	@Nonnull
	public static final Capability<IThrowable> THROWABLE_CAPABILITY = null;

	@SubscribeEvent
	public static void register(FMLCommonSetupEvent event) {
		CapabilityManager.INSTANCE.register(IThrowable.class, new ThrowableStorage(), StandardThrowable::getInstance);
	}
}
