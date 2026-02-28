package com.alrex.throwability.extern.carryon.throwable;

import com.alrex.throwability.Throwability;
import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.common.thrown.IThrown;
import com.alrex.throwability.extern.AdditionalMods;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import tschipp.carryon.common.item.ItemCarryonBlock;
import tschipp.carryon.common.item.ItemCarryonEntity;

public class CarryonBlockThrowable implements IThrowable {
    public static boolean match(ItemStack stack) {
        return stack.getItem() instanceof ItemCarryonBlock;
    }

    @Override
    public Entity throwAsItem(PlayerEntity thrower, ItemStack stack, int chargedTick) {
        return throwAsEntity(thrower, stack, chargedTick);
    }

    @Override
    public Entity throwAsEntity(PlayerEntity thrower, ItemStack stack, int chargedTick) {
        Item item = stack.getItem();
        if (item instanceof ItemCarryonBlock) {
            Vector3d pos = ThrowUtil.getBasicThrowingPosition(thrower);
            BlockState blockState = ItemCarryonBlock.getBlockState(stack);
            FallingBlockEntity entity = new FallingBlockEntity(
                    thrower.level, pos.x(), pos.y(), pos.z(), blockState
            );

            Vector3d throwVec = ThrowUtil.getBasicThrowingVector(thrower);
            double speedScale = 3. * MathHelper.clamp(chargedTick / (double) getMaxChargeTick(stack), 0, 1);
            Vector3d deltaMovement = throwVec.scale(speedScale);

            entity.time = 1;
            entity.setPos(pos.x(), pos.y(), pos.z());
            entity.setDeltaMovement(deltaMovement);
            entity.blockData = ItemCarryonBlock.getTileData(stack);
            if (entity instanceof IThrown) {
                ((IThrown) entity).setThrown(true);
            }
            AdditionalMods.Naturot().rotateEntity(entity, deltaMovement);

            thrower.getPersistentData().remove("carrySlot");
            ItemCarryonBlock.clearTileData(stack);

            return entity;
        }
        Throwability.LOGGER.warn("Failed to throw item; ItemCarryonBlock is expected but {} is tried throw", item.getRegistryName());
        return IThrowable.super.throwAsItem(thrower, stack, chargedTick);
    }

    @Override
    public void onThrownOnClient(PlayerEntity thrower, ItemStack stack) {
        IThrowable.super.onThrownOnClient(thrower, stack);
        thrower.getPersistentData().remove("carrySlot");
        ItemCarryonEntity.clearEntityData(stack);
    }
}
