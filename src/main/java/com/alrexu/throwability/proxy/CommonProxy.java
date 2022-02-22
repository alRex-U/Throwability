package com.alrexu.throwability.proxy;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.simple.SimpleChannel;

abstract public class CommonProxy {
	public abstract void registerMessages(SimpleChannel instance);

	public abstract void registerHandlers(IEventBus eventBus);

	public abstract void onCreated();
}
