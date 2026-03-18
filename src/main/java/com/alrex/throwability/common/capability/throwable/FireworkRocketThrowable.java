package com.alrex.throwability.common.capability.throwable;

import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class FireworkRocketThrowable implements IThrowable {
    @Override
    public Entity throwAsEntity(Player thrower, ItemStack stack, int chargedTick) {
        Item item = stack.getItem();
        if (item instanceof FireworkRocketItem) {
            Vec3 lookAngle = thrower.getLookAngle();
            FireworkRocketEntity entity = new FireworkRocketEntity(
                    thrower.level, thrower,
                    lookAngle.x(), lookAngle.y(), lookAngle.z(),
                    stack
            );
            Vec3 pos = ThrowUtil.getBasicThrowingPosition(thrower);
            Vec3 throwVec = ThrowUtil.getBasicThrowingVector(thrower);
            double speedScale = 3.
                    * ThrowUtil.getSpeedScale(thrower)
                    * Mth.clamp(chargedTick / (double) getMaxChargeTick(stack), 0, 1);

            entity.setPos(pos.x(), pos.y(), pos.z());
            entity.setDeltaMovement(throwVec.scale(speedScale));

            return entity;
        }
        return throwAsItem(thrower, stack, chargedTick);
    }
}
