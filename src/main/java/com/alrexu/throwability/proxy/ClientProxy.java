package com.alrexu.throwability.proxy;

import com.alrexu.throwability.client.hud.ThrowPowerMeter;
import com.alrexu.throwability.client.renderer.HandCustomRenderer;
import com.alrexu.throwability.common.network.ItemThrowMessage;
import com.alrexu.throwability.common.network.handler.ItemThrowMessageHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {
	@Override
	public void registerMessages(SimpleChannel instance) {
		instance.registerMessage(
				0,
				ItemThrowMessage.class,
				ItemThrowMessage::encode,
				ItemThrowMessage::decode,
				ItemThrowMessageHandler::handleClient
		);
	}

	@Override
	public void registerHandlers(IEventBus eventBus) {
		eventBus.register(new HandCustomRenderer());
		eventBus.register(new ThrowPowerMeter());
	}
}
