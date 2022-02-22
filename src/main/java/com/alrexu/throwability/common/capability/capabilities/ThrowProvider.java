package com.alrexu.throwability.common.capability.capabilities;

import com.alrexu.throwability.ThrowabilityMod;
import com.alrexu.throwability.common.capability.IThrow;
import com.alrexu.throwability.common.capability.impl.Throw;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ThrowProvider implements ICapabilityProvider {
	public static final ResourceLocation CAPABILITY_LOCATION = new ResourceLocation(ThrowabilityMod.MOD_ID, "throw");
	public static final Capability<IThrow> THROW_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});
	private LazyOptional<IThrow> instance = LazyOptional.of(Throw::new);

	@Nullable
	public static IThrow get(Entity entity) {
		LazyOptional<IThrow> lazyOptional = entity.getCapability(THROW_CAPABILITY);
		if (!lazyOptional.isPresent()) return null;
		return lazyOptional.orElseThrow(IllegalStateException::new);
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		return cap == THROW_CAPABILITY ? instance.cast() : LazyOptional.empty();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
		return cap == THROW_CAPABILITY ? instance.cast() : LazyOptional.empty();
	}
}
