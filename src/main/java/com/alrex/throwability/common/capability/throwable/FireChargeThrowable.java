package com.alrex.throwability.common.capability.throwable;

import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class FireChargeThrowable implements IThrowable {
    @Override
    public Entity throwAsEntity(Player thrower, ItemStack stack, int chargedTick) {
        Item item = stack.getItem();
        if (item instanceof FireChargeItem) {
            Vec3 lookAngle = thrower.getLookAngle();
            Vec3 pos = ThrowUtil.getBasicThrowingPosition(thrower);
            var entity = new SmallFireball(
                    thrower.level, thrower, lookAngle.x(), lookAngle.y(), lookAngle.z()
            );
            Vec3 throwVec = ThrowUtil.getBasicThrowingVector(thrower);
            double speedScale = 3.
                    * ThrowUtil.getSpeedScale(thrower)
                    * Mth.clamp(chargedTick / (double) getMaxChargeTick(stack), 0, 1);

            entity.setDeltaMovement(throwVec.scale(speedScale));
            entity.setPos(pos.x, pos.y, pos.z);

            return entity;
        }
        return throwAsItem(thrower, stack, chargedTick);
    }
}
