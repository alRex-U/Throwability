package com.alrex.throwability.common.capability.storage;

import com.alrex.throwability.common.capability.IThrowable;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class ThrowableStorage implements Capability.IStorage<IThrowable> {
    @Nullable
    @Override
    public INBT writeNBT(Capability<IThrowable> capability, IThrowable instance, Direction side) {
        return null;
    }

    @Override
    public void readNBT(Capability<IThrowable> capability, IThrowable instance, Direction side, INBT nbt) {
    }
}
