package com.alrex.throwability.common.capability.throwable;

import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.common.entity.ThrownWeaponEntity;
import com.alrex.throwability.utils.ThrowUtil;
import com.google.common.collect.Multimap;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class WeaponThrowable implements IThrowable {
    public static double getBaseAttackDamage(ItemStack stack) {
        Multimap<Attribute, AttributeModifier> attrMap = stack.getAttributeModifiers(EquipmentSlotType.MAINHAND);
        double damageBonus = EnchantmentHelper.getDamageBonus(stack, CreatureAttribute.UNDEFINED);
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
    public Entity throwAsEntity(PlayerEntity thrower, ItemStack stack, int chargedTick) {
        if (hasAttackDamage(stack)) {
            ThrownWeaponEntity entity = new ThrownWeaponEntity(thrower.level, thrower, stack);

            Vector3d throwVec = ThrowUtil.getBasicThrowingVector(thrower);
            double speedScale = 4. * MathHelper.clamp(chargedTick / (double) getMaxChargeTick(), 0, 1);

            entity.setDeltaMovement(throwVec.scale(speedScale));
            entity.setOwner(thrower);

            return entity;
        }
        return throwAsItem(thrower, stack, chargedTick);
    }
}
