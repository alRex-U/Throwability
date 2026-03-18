package com.alrex.throwability.proxy;

import com.alrex.throwability.common.eventhandle.InteractionHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.network.simple.SimpleChannel;

abstract public class CommonProxy {
	public abstract void registerMessages(SimpleChannel instance);

	public void registerHandlers(IEventBus eventBus) {
		eventBus.register(InteractionHandler.class);
	}

	public abstract void onCreated();
}
