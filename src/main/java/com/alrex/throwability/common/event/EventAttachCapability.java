package com.alrex.throwability.common.event;

import com.alrex.throwability.common.capability.Capabilities;
import com.alrex.throwability.common.capability.IThrow;
import com.alrex.throwability.common.capability.impl.Throw;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;


public class EventAttachCapability {
	@SubscribeEvent
	public void attach(AttachCapabilitiesEvent<Entity> event) {
		if (!(event.getObject() instanceof Player)) return;
		{
			Player player = (Player) event.getObject();
			IThrow instance = new Throw(player);

			LazyOptional<IThrow> optional = LazyOptional.of(() -> instance);
			ICapabilityProvider provider = new ICapabilityProvider() {
				@NotNull
				@Override
				public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
					if (cap == Capabilities.THROW_CAPABILITY) {
						return optional.cast();
					}
					return LazyOptional.empty();
				}
			};
			event.addCapability(Capabilities.THROW_LOCATION, provider);
		}
	}
}
