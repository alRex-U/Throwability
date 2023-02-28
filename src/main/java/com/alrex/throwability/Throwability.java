package com.alrex.throwability;

import com.alrex.throwability.proxy.ClientProxy;
import com.alrex.throwability.proxy.CommonProxy;
import com.alrex.throwability.proxy.ServerProxy;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
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
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

		PROXY.onCreated();
	}

	private void setup(final FMLCommonSetupEvent event) {
		PROXY.registerMessages(CHANNEL);
		PROXY.registerHandlers(MinecraftForge.EVENT_BUS);
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
	}

	private void enqueueIMC(final InterModEnqueueEvent event) {
	}

	private void processIMC(final InterModProcessEvent event) {
	}

}
