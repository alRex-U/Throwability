package com.alrex.throwability.common.eventhandle;

import com.alrex.throwability.ThrowabilityConfig;
import com.alrex.throwability.common.ability.AbstractThrowingAbility;
import com.alrex.throwability.common.ability.IThrowabilityProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class TickEventHandler {
	@SubscribeEvent
	public static void onTick(TickEvent.PlayerTickEvent event) {
		if (event.side != LogicalSide.CLIENT) return;
		if (event.phase != TickEvent.Phase.START) return;
		var player = event.player;
        if (player instanceof IThrowabilityProvider) {
            AbstractThrowingAbility throwingAbility = ((IThrowabilityProvider) player).getThrowAbility();
            throwingAbility.tick();
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.START) return;
		ThrowabilityConfig.Client.ENTITY_THROW_CONTROL.get().onTick();
	}
}
