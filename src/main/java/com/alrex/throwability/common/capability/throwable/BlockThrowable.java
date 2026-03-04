package com.alrex.throwability.common.capability.throwable;

import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.common.thrown.IThrown;
import com.alrex.throwability.extern.AdditionalMods;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class BlockThrowable implements IThrowable {
    @Override
    public Entity throwAsEntity(PlayerEntity thrower, ItemStack stack, int chargedTick) {
        Item item = stack.getItem();
        if (item instanceof BlockItem) {
            Vector3d pos = ThrowUtil.getBasicThrowingPosition(thrower);
            BlockItem blockItem = (BlockItem) item;
            BlockState blockState = blockItem.getPlacementState(
                    new BlockItemUseContext(
                            thrower, Hand.MAIN_HAND, stack,
                            new BlockRayTraceResult(
                                    Vector3d.atBottomCenterOf(thrower.blockPosition()),
                                    Direction.UP,
                                    thrower.blockPosition(),
                                    false
                            )
                    )
            );
            if (blockState == null) {
                blockState = blockItem.getBlock().defaultBlockState();
            }
            FallingBlockEntity entity = new FallingBlockEntity(
                    thrower.level, pos.x(), pos.y(), pos.z(), blockState
            );

            Vector3d throwVec = ThrowUtil.getBasicThrowingVector(thrower);
            double speedScale = 3. * MathHelper.clamp(chargedTick / (double) getMaxChargeTick(stack), 0, 1);
            Vector3d deltaMovement = throwVec.scale(speedScale);

            entity.time = 1;
            entity.setPos(pos.x(), pos.y(), pos.z());
            entity.setDeltaMovement(deltaMovement);
            if (entity instanceof IThrown) {
                ((IThrown) entity).setThrown(true);
            }

            AdditionalMods.Naturot().rotateEntity(entity, deltaMovement);

            return entity;
        }
        return throwAsItem(thrower, stack, chargedTick);
    }
}
