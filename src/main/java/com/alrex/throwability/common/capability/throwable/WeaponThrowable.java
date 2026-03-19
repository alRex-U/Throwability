package com.alrex.throwability.common.capability.throwable;

import com.alrex.throwability.common.ability.ThrowType;
import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.common.entity.ThrownWeaponEntity;
import com.alrex.throwability.common.sound.SoundEvents;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class WeaponThrowable implements IThrowable {
    public static double getBaseAttackDamage(ItemStack stack) {
        var attrMap = stack.getAttributeModifiers(EquipmentSlot.MAINHAND);
        double damageBonus = EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED);
        double plusDamage = 0;
        double multiplyScale = 1;
        for (AttributeModifier modifier : attrMap.get(Attributes.ATTACK_DAMAGE)) {
            switch (modifier.getOperation()) {
                case ADDITION:
                    plusDamage += modifier.getAmount();
                    break;
                case MULTIPLY_BASE:
                case MULTIPLY_TOTAL:
                    multiplyScale *= modifier.getAmount();
                    break;
            }
        }
        return plusDamage * multiplyScale + damageBonus;
    }

    public static boolean hasAttackDamage(ItemStack stack) {
        return getBaseAttackDamage(stack) > 0.5;
    }

    @Override
    public Entity throwAsEntity(Player thrower, ItemStack stack, int chargedTick) {
        if (hasAttackDamage(stack)) {
            stack.hurtAndBreak(1, thrower, (player) -> player.broadcastBreakEvent(thrower.getUsedItemHand()));
            ThrownWeaponEntity entity = new ThrownWeaponEntity(thrower.level(), thrower, stack);

            var throwVec = ThrowUtil.getBasicThrowingVector(thrower);
            double speedScale = 4.
                    * ThrowUtil.getSpeedScale(thrower)
                    * Mth.clamp(chargedTick / (double) getMaxChargeTick(stack), 0, 1);

            entity.setDeltaMovement(throwVec.scale(speedScale));

            return entity;
        }
        return throwAsItem(thrower, stack, chargedTick);
    }

    @Override
    public void onThrownOnClient(Player thrower, ItemStack stack, ThrowType type, int chargedTick) {
        if (type == ThrowType.ONE_AS_ENTITY) {
            thrower.playSound(SoundEvents.WEAPON_THROW.get(), Mth.clamp(chargedTick / (float) getMaxChargeTick(stack), 0, 1f), 1f);
        } else {
            IThrowable.super.onThrownOnClient(thrower, stack, type, chargedTick);
        }
    }
}
