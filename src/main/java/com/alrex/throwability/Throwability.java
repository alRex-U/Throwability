package com.alrex.throwability;

import com.alrex.throwability.client.animation.Animations;
import com.alrex.throwability.client.animation.impl.ThrowingAnimation;
import com.alrex.throwability.client.render.entity.EntityRenderers;
import com.alrex.throwability.common.entity.EntityTypes;
import com.alrex.throwability.common.sound.SoundEvents;
import com.alrex.throwability.extern.AdditionalMods;
import com.alrex.throwability.proxy.ClientProxy;
import com.alrex.throwability.proxy.CommonProxy;
import com.alrex.throwability.proxy.ServerProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Throwability.MOD_ID)
public class Throwability {
	public static final String MOD_ID = "throwability";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final CommonProxy PROXY = DistExecutor.unsafeRunForDist(
			() -> ClientProxy::new,
			() -> ServerProxy::new
	);
	private static final String PROTOCOL_VERSION = "1.0";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(MOD_ID, "message"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);

	public Throwability() {
		IEventBus fmlEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		fmlEventBus.addListener(this::setup);
		fmlEventBus.addListener(this::doClientStuff);
		fmlEventBus.addListener(this::onLoadCompleted);

		SoundEvents.register(fmlEventBus);
		EntityTypes.register(fmlEventBus);

		PROXY.onCreated();
	}

	private void setup(final FMLCommonSetupEvent event) {
		PROXY.registerMessages(CHANNEL);
		PROXY.registerHandlers(MinecraftForge.EVENT_BUS);

		EntityRenderers.register(Minecraft.getInstance().getEntityRenderDispatcher());
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		Animations.register(ThrowingAnimation.class, ThrowingAnimation::new);
	}

	private void onLoadCompleted(final FMLLoadCompleteEvent event) {
		AdditionalMods.init();
	}
}
