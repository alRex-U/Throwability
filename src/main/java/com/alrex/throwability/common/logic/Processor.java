package com.alrex.throwability.common.logic;

import com.alrex.throwability.common.ability.AbstractThrowingAbility;
import com.alrex.throwability.common.ability.IThrowabilityProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class Processor {
	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event) {
		if (event.side != LogicalSide.CLIENT) return;
		if (event.phase != TickEvent.Phase.START) return;
        PlayerEntity player = event.player;
        if (player instanceof IThrowabilityProvider) {
            AbstractThrowingAbility throwingAbility = ((IThrowabilityProvider) player).getThrowAbility();
            throwingAbility.tick();
		}
	}
}
