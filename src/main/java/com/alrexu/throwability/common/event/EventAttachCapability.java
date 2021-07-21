package com.alrexu.throwability.common.event;

import com.alrexu.throwability.common.capability.capabilities.ThrowProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


public class EventAttachCapability {
	@SubscribeEvent
	public void attach(AttachCapabilitiesEvent<Entity> event) {
		if (!(event.getObject() instanceof PlayerEntity)) return;
		event.addCapability(ThrowProvider.CAPABILITY_LOCATION, new ThrowProvider());
	}
}
