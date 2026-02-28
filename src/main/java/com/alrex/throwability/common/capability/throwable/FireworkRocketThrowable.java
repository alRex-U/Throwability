package com.alrex.throwability.common.capability.throwable;

import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class FireworkRocketThrowable implements IThrowable {
    @Override
    public Entity throwAsEntity(PlayerEntity thrower, ItemStack stack, int chargedTick) {
        Item item = stack.getItem();
        if (item instanceof FireworkRocketItem) {
            Vector3d lookAngle = thrower.getLookAngle();
            FireworkRocketEntity entity = new FireworkRocketEntity(
                    thrower.level, thrower,
                    lookAngle.x(), lookAngle.y(), lookAngle.z(),
                    stack
            );
            Vector3d pos = ThrowUtil.getBasicThrowingPosition(thrower);
            Vector3d throwVec = ThrowUtil.getBasicThrowingVector(thrower);
            double speedScale = 3. * MathHelper.clamp(chargedTick / (double) getMaxChargeTick(stack), 0, 1);

            entity.setPos(pos.x(), pos.y(), pos.z());
            entity.setDeltaMovement(throwVec.scale(speedScale));

            return entity;
        }
        return throwAsItem(thrower, stack, chargedTick);
    }
}
