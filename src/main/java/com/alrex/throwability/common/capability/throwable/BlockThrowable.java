package com.alrex.throwability.common.capability.throwable;

import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.common.thrown.IThrown;
import com.alrex.throwability.extern.AdditionalMods;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class BlockThrowable implements IThrowable {
    @Override
    public Entity throwAsEntity(Player thrower, ItemStack stack, int chargedTick) {
        Item item = stack.getItem();
        if (item instanceof BlockItem blockItem) {
            var pos = ThrowUtil.getBasicThrowingPosition(thrower);
            var blockState = blockItem.getPlacementState(
                    new BlockPlaceContext(
                            thrower, InteractionHand.MAIN_HAND, stack,
                            new BlockHitResult(
                                    Vec3.atBottomCenterOf(thrower.blockPosition()),
                                    Direction.UP,
                                    thrower.blockPosition(),
                                    false
                            )
                    )
            );
            if (blockState == null) {
                blockState = blockItem.getBlock().defaultBlockState();
            }
            var entity = new FallingBlockEntity(
                    thrower.level(), pos.x(), pos.y(), pos.z(), blockState
            );

            var throwVec = ThrowUtil.getBasicThrowingVector(thrower);
            double speedScale = 3.
                    * ThrowUtil.getSpeedScale(thrower)
                    * Mth.clamp(chargedTick / (double) getMaxChargeTick(stack), 0, 1);
            var deltaMovement = throwVec.scale(speedScale);

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
