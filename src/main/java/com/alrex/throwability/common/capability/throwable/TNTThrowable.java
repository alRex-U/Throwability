package com.alrex.throwability.common.capability.throwable;

import com.alrex.throwability.common.ability.ThrowType;
import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.extern.AdditionalMods;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class TNTThrowable implements IThrowable {
    @Override
    public Entity throwAsEntity(Player thrower, ItemStack stack, int chargedTick) {
        Item item = stack.getItem();
        if (item == Items.TNT) {
            Vec3 pos = ThrowUtil.getBasicThrowingPosition(thrower);
            var entity = new PrimedTnt(
                    thrower.level(), pos.x(), pos.y(), pos.z(), thrower
            );
            Vec3 throwVec = ThrowUtil.getBasicThrowingVector(thrower);
            double phase = Mth.clamp(chargedTick / (double) getMaxChargeTick(stack), 0, 1);
            double speedScale = 3.
                    * ThrowUtil.getSpeedScale(thrower)
                    * phase;
            Vec3 deltaMovement = throwVec.scale(speedScale);

            entity.setDeltaMovement(deltaMovement);
            entity.setFuse((int) (40 + phase * 40));

            AdditionalMods.Naturot().rotateEntity(entity, deltaMovement);

            return entity;
        }
        return throwAsItem(thrower, stack, chargedTick);
    }

    @Override
    public void onThrownOnClient(Player thrower, ItemStack stack, ThrowType type, int chargedTick) {
        IThrowable.super.onThrownOnClient(thrower, stack, type, chargedTick);
        if (type == ThrowType.ONE_AS_ENTITY) {
            thrower.playSound(SoundEvents.FLINTANDSTEEL_USE, Mth.clamp(chargedTick / (float) getMaxChargeTick(stack), 0, 1f), 1);
        }
    }
}
