package com.alrexu.throwability.proxy;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.network.simple.SimpleChannel;

abstract public class CommonProxy {
	public abstract void registerMessages(SimpleChannel instance);

	public abstract void registerHandlers(IEventBus eventBus);
}
