package com.alrex.throwability.proxy;

import com.alrex.throwability.common.capability.Capabilities;
import com.alrex.throwability.common.network.ItemThrowMessage;
import com.alrex.throwability.common.network.SyncThrowStateMessage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@OnlyIn(Dist.DEDICATED_SERVER)
public class ServerProxy extends CommonProxy {
	@Override
	public void registerMessages(SimpleChannel instance) {
		instance.registerMessage(
				0,
				ItemThrowMessage.class,
				ItemThrowMessage::encode,
				ItemThrowMessage::decode,
				ItemThrowMessage::handleServer
		);
		instance.registerMessage(
				1,
				SyncThrowStateMessage.class,
				SyncThrowStateMessage::encode,
				SyncThrowStateMessage::decode,
				SyncThrowStateMessage::handleServer
		);
	}

	@Override
	public void registerHandlers(IEventBus eventBus) {

	}

	@Override
	public void onCreated() {
		FMLJavaModLoadingContext.get().getModEventBus().register(Capabilities.class);
	}
}
