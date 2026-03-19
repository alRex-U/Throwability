package com.alrex.throwability.common.capability.throwable;

import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.common.entity.ThrownDispensibleContainerEntity;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class DispensibleContainerThrowable implements IThrowable {
    @Override
    public Entity throwAsEntity(Player thrower, ItemStack stack, int chargedTick) {
        if (stack.getItem() instanceof DispensibleContainerItem) {
            ThrownDispensibleContainerEntity entity = new ThrownDispensibleContainerEntity(thrower.level(), thrower, stack);
            Vec3 throwVec = ThrowUtil.getBasicThrowingVector(thrower);
            double speedScale = 3.
                    * ThrowUtil.getSpeedScale(thrower)
                    * Mth.clamp(chargedTick / (double) getMaxChargeTick(stack), 0, 1);

            entity.setDeltaMovement(throwVec.scale(speedScale));

            return entity;
        }
        return throwAsItem(thrower, stack, chargedTick);
    }
}
