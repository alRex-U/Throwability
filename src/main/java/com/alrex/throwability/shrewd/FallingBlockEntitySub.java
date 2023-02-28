package com.alrex.throwability.shrewd;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public abstract class FallingBlockEntitySub {
	public static boolean valueShouldSet = false;
	public static BlockState state = null;

	public static FallingBlockEntity construct(Level level, @Nonnull BlockState blockState, Vec3 pos) {
		FallingBlockEntity entity = new FallingBlockEntity(EntityType.FALLING_BLOCK, level);
		entity.blocksBuilding = true;
		entity.setDeltaMovement(Vec3.ZERO);
		entity.time = 1;
		entity.xo = pos.x();
		entity.yo = pos.y();
		entity.zo = pos.z();
		entity.setPos(pos);
		{
			valueShouldSet = true;
			state = blockState;
			entity.setStartPos(new BlockPos(pos));
			valueShouldSet = false;
		}
		return entity;
	}
}
