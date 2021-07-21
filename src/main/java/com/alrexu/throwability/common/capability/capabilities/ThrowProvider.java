package com.alrexu.throwability.common.capability.capabilities;

import com.alrexu.throwability.ThrowabilityMod;
import com.alrexu.throwability.common.capability.IThrow;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ThrowProvider implements ICapabilityProvider {
	public static final ResourceLocation CAPABILITY_LOCATION = new ResourceLocation(ThrowabilityMod.MOD_ID, "throw");
	@CapabilityInject(IThrow.class)
	public static final Capability<IThrow> THROW_CAPABILITY = null;
	private LazyOptional<IThrow> instance = LazyOptional.of(THROW_CAPABILITY::getDefaultInstance);

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
