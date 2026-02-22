package com.alrex.throwability.common.capability.throwable;

import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.extern.AdditionalMods;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class BlockThrowable implements IThrowable {
    @Override
    public Entity throwAsEntity(PlayerEntity thrower, ItemStack stack, int chargedTick) {
        Item item = stack.getItem();
        if (item instanceof BlockItem) {
            Vector3d pos = ThrowUtil.getBasicThrowingPosition(thrower);
            BlockItem blockItem = (BlockItem) item;
            FallingBlockEntity entity = new FallingBlockEntity(
                    thrower.level, pos.x(), pos.y(), pos.z(), blockItem.getBlock().defaultBlockState()
            );

            Vector3d throwVec = ThrowUtil.getBasicThrowingVector(thrower);
            double speedScale = 3. * MathHelper.clamp(chargedTick / (double) getMaxChargeTick(), 0, 1);
            Vector3d deltaMovement = throwVec.scale(speedScale);

            entity.time = 1;
            entity.setPos(pos.x(), pos.y(), pos.z());
            entity.setDeltaMovement(deltaMovement);

            AdditionalMods.Naturot().rotateEntity(entity, deltaMovement);

            return entity;
        }
        return throwAsItem(thrower, stack, chargedTick);
    }
}
