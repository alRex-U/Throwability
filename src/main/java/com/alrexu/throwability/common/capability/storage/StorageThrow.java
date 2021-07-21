package com.alrexu.throwability.common.capability.storage;

import com.alrexu.throwability.common.capability.IThrow;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class StorageThrow implements Capability.IStorage<IThrow> {
	@Nullable
	@Override
	public INBT writeNBT(Capability<IThrow> capability, IThrow instance, Direction side) {
		return null;
	}

	@Override
	public void readNBT(Capability<IThrow> capability, IThrow instance, Direction side, INBT nbt) {
	}
}
