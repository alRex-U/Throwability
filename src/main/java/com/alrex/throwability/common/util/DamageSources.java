package com.alrex.throwability.common.util;

import com.alrex.throwability.Throwability;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class DamageSources {
    public static final ResourceKey<DamageType> THROWN_WEAPON = register("thrown_weapon");

    private static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Throwability.MOD_ID, name));
    }
}
