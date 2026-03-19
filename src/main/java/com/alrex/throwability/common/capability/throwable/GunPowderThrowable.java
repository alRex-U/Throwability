package com.alrex.throwability.common.capability.throwable;

import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.common.entity.ThrownGunPowderEntity;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class GunPowderThrowable implements IThrowable {
    @Override
    public Entity throwAsEntity(Player thrower, ItemStack stack, int chargedTick) {
        if (stack.getItem() == Items.GUNPOWDER) {
            ThrownGunPowderEntity entity = new ThrownGunPowderEntity(thrower.level(), thrower);
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
