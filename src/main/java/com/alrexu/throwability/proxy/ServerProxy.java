package com.alrexu.throwability.proxy;

import com.alrexu.throwability.common.capability.registry.CapabilityRegistry;
import com.alrexu.throwability.common.event.EventAttachCapability;
import com.alrexu.throwability.common.logic.processor.ThrowLogic;
import com.alrexu.throwability.common.network.ItemThrowMessage;
import com.alrexu.throwability.common.network.handler.ItemThrowMessageHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

@OnlyIn(Dist.DEDICATED_SERVER)
public class ServerProxy extends CommonProxy {
	@Override
	public void registerMessages(SimpleChannel instance) {
		instance.registerMessage(
				0,
				ItemThrowMessage.class,
				ItemThrowMessage::encode,
				ItemThrowMessage::decode,
				ItemThrowMessageHandler::handleServer
		);
	}

	@Override
	public void registerHandlers(IEventBus eventBus) {

	}

	@Override
	public void onCreated() {
		MinecraftForge.EVENT_BUS.register(new ThrowLogic());
		MinecraftForge.EVENT_BUS.register(new EventAttachCapability());
		FMLJavaModLoadingContext.get().getModEventBus().register(new CapabilityRegistry());
	}
}
