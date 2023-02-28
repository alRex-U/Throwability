package com.alrex.throwability.common.capability;

import com.alrex.throwability.Throwability;
import com.alrex.throwability.common.capability.impl.Throw;
import com.alrex.throwability.common.capability.storage.ThrowStorage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class Capabilities {
	public static final ResourceLocation THROW_LOCATION = new ResourceLocation(Throwability.MOD_ID, "throw");
	@CapabilityInject(IThrow.class)
	public static final Capability<IThrow> THROW_CAPABILITY = null;

	@SubscribeEvent
	public static void register(FMLCommonSetupEvent event) {
		CapabilityManager.INSTANCE.register(IThrow.class, new ThrowStorage(), Throw::new);
	}
}
