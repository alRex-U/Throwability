package com.alrex.throwability.common.util;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public class DamageSources {
    public static DamageSource thrownWeapon(Entity thrownWeapon, @Nullable Entity owner) {
        return new IndirectEntityDamageSource("throwability.thrown_weapon", thrownWeapon, owner).setProjectile();
    }
}
