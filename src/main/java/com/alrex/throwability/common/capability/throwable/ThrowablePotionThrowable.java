package com.alrex.throwability.common.capability.throwable;

import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.phys.Vec3;

public class ThrowablePotionThrowable implements IThrowable {
    @Override
    public Entity throwAsEntity(Player thrower, ItemStack stack, int chargedTick) {
        Item item = stack.getItem();
        if (item instanceof ThrowablePotionItem) {
            Vec3 pos = ThrowUtil.getBasicThrowingPosition(thrower);
            var entity = new ThrownPotion(thrower.level(), thrower);
            Vec3 throwVec = ThrowUtil.getBasicThrowingVector(thrower);
            double speedScale = 5.
                    * ThrowUtil.getSpeedScale(thrower)
                    * Mth.clamp(chargedTick / (double) getMaxChargeTick(stack), 0, 1);

            entity.setItem(stack);
            entity.setPos(pos.x(), pos.y(), pos.z());
            entity.setDeltaMovement(throwVec.scale(speedScale));

            return entity;
        }
        return throwAsItem(thrower, stack, chargedTick);
    }
}
