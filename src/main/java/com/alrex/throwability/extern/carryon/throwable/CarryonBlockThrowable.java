package com.alrex.throwability.extern.carryon.throwable;

import com.alrex.throwability.Throwability;
import com.alrex.throwability.common.ability.ThrowType;
import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.common.thrown.IThrown;
import com.alrex.throwability.extern.AdditionalMods;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import tschipp.carryon.common.item.ItemCarryonBlock;
import tschipp.carryon.common.item.ItemCarryonEntity;

public class CarryonBlockThrowable implements IThrowable {
    public static boolean match(ItemStack stack) {
        return stack.getItem() instanceof ItemCarryonBlock;
    }

    @Override
    public Entity throwAsItem(Player thrower, ItemStack stack, int chargedTick) {
        return throwAsEntity(thrower, stack, chargedTick);
    }

    @Override
    public Entity throwAsEntity(Player thrower, ItemStack stack, int chargedTick) {
        Item item = stack.getItem();
        if (item instanceof ItemCarryonBlock) {
            Vec3 pos = ThrowUtil.getBasicThrowingPosition(thrower);
            BlockState blockState = ItemCarryonBlock.getBlockState(stack);
            FallingBlockEntity entity = new FallingBlockEntity(
                    thrower.level, pos.x(), pos.y(), pos.z(), blockState
            );

            Vec3 throwVec = ThrowUtil.getBasicThrowingVector(thrower);
            double speedScale = 3. * Mth.clamp(chargedTick / (double) getMaxChargeTick(stack), 0, 1);
            Vec3 deltaMovement = throwVec.scale(speedScale);

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
    public void onThrownOnClient(Player thrower, ItemStack stack, ThrowType type, int chargedTick) {
        IThrowable.super.onThrownOnClient(thrower, stack, type, chargedTick);
        thrower.getPersistentData().remove("carrySlot");
        ItemCarryonEntity.clearEntityData(stack);
    }
}
