package com.alrex.throwability.proxy;

import com.alrex.throwability.client.animation.ThrowabilityAnimation;
import com.alrex.throwability.client.hud.ThrowPowerMeter;
import com.alrex.throwability.client.input.KeyBindings;
import com.alrex.throwability.client.input.KeyRecorder;
import com.alrex.throwability.common.capability.Capabilities;
import com.alrex.throwability.common.event.EventAttachCapability;
import com.alrex.throwability.common.logic.Processor;
import com.alrex.throwability.common.network.ItemThrowMessage;
import com.alrex.throwability.common.network.SyncThrowStateMessage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
				ItemThrowMessage::handleClient
		);
		instance.registerMessage(
				1,
				SyncThrowStateMessage.class,
				SyncThrowStateMessage::encode,
				SyncThrowStateMessage::decode,
				SyncThrowStateMessage::handleClient
		);
	}

	@Override
	public void registerHandlers(IEventBus eventBus) {
		eventBus.register(ThrowabilityAnimation.class);
		eventBus.register(new ThrowPowerMeter());
	}

	@Override
	public void onCreated() {
		MinecraftForge.EVENT_BUS.register(new Processor());
		MinecraftForge.EVENT_BUS.register(new EventAttachCapability());
		MinecraftForge.EVENT_BUS.register(KeyRecorder.class);
		FMLJavaModLoadingContext.get().getModEventBus().register(KeyBindings.class);
		FMLJavaModLoadingContext.get().getModEventBus().register(Capabilities.class);
	}
}
