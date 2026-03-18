package com.alrex.throwability.common.thrown;


import net.minecraft.core.Direction;

import javax.annotation.Nullable;

public interface ICollidedDirectionProvider {
    @Nullable
    Direction getCollidedDirection();
}
