package com.alrex.throwability.common.thrown;

import net.minecraft.util.Direction;

import javax.annotation.Nullable;

public interface ICollidedDirectionProvider {
    @Nullable
    Direction getCollidedDirection();
}
