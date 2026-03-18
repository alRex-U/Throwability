package com.alrex.throwability.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;

import javax.annotation.Nullable;

public class DamageSources {
    public static DamageSource thrownWeapon(Entity thrownWeapon, @Nullable Entity owner) {
        return new IndirectEntityDamageSource("throwability.thrown_weapon", thrownWeapon, owner).setProjectile();
    }
}
