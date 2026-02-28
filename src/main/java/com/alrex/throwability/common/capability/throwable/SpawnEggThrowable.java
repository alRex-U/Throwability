package com.alrex.throwability.common.capability.throwable;

import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.common.entity.ThrownSpawnEggEntity;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class SpawnEggThrowable implements IThrowable {
    @Override
    public Entity throwAsEntity(PlayerEntity thrower, ItemStack stack, int chargedTick) {
        Item item = stack.getItem();
        if (item instanceof SpawnEggItem) {
            ThrownSpawnEggEntity entity = new ThrownSpawnEggEntity(thrower.level, thrower, stack);

            Vector3d throwVec = ThrowUtil.getBasicThrowingVector(thrower);
            double speedScale = 3. * MathHelper.clamp(chargedTick / (double) getMaxChargeTick(stack), 0, 1);

            entity.setDeltaMovement(throwVec.scale(speedScale));
           
            return entity;
        }
        return throwAsItem(thrower, stack, chargedTick);
    }
}
