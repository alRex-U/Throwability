package com.alrex.throwability.extern.carryon.throwable;

import com.alrex.throwability.Throwability;
import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import tschipp.carryon.common.item.ItemCarryonEntity;

public class CarryonEntityThrowable implements IThrowable {
    public static boolean match(ItemStack stack) {
        return stack.getItem() instanceof ItemCarryonEntity;
    }

    @Override
    public Entity throwAsEntity(PlayerEntity thrower, ItemStack stack, int chargedTick) {
        Item item = stack.getItem();
        if (item instanceof ItemCarryonEntity) {
            Entity entity = ItemCarryonEntity.getEntity(stack, thrower.level);

            Vector3d throwVec = ThrowUtil.getBasicThrowingVector(thrower);
            Vector3d pos = ThrowUtil.getBasicThrowingPosition(thrower).add(throwVec);
            double speedScale = 3.2 * MathHelper.clamp(chargedTick / (double) getMaxChargeTick(stack), 0, 1);

            entity.setPos(pos.x, pos.y, pos.z);
            entity.setDeltaMovement(throwVec.scale(speedScale));

            thrower.getPersistentData().remove("carrySlot");
            ItemCarryonEntity.clearEntityData(stack);

            return entity;
        }
        Throwability.LOGGER.warn("Failed to throw item; ItemCarryonBlock is expected but {} is tried throw", item.getRegistryName());
        return IThrowable.super.throwAsItem(thrower, stack, chargedTick);
    }

    @Override
    public Entity throwAsItem(PlayerEntity thrower, ItemStack stack, int chargedTick) {
        return throwAsEntity(thrower, stack, chargedTick);
    }

    @Override
    public void onThrownOnClient(PlayerEntity thrower, ItemStack stack) {
        IThrowable.super.onThrownOnClient(thrower, stack);
        thrower.getPersistentData().remove("carrySlot");
        ItemCarryonEntity.clearEntityData(stack);
    }

    @Override
    public int getMaxChargeTick(ItemStack stack) {
        return 40;
    }
}
