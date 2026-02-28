package com.alrex.throwability.common.entity;

import com.alrex.throwability.Throwability;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityTypes {
    private static final DeferredRegister<net.minecraft.entity.EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITIES, Throwability.MOD_ID);
    public static RegistryObject<EntityType<ThrownWeaponEntity>> THROWN_WEAPON = REGISTRY.register(
            "thrown_weapon",
            () -> EntityType.Builder
                    .of((EntityType.IFactory<ThrownWeaponEntity>) ThrownWeaponEntity::new, EntityClassification.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build("thrown_weapon")
    );
    public static RegistryObject<EntityType<ThrownSpawnEggEntity>> THROWN_SPAWN_EGG = REGISTRY.register(
            "thrown_spawn_egg",
            () -> EntityType.Builder
                    .of((EntityType.IFactory<ThrownSpawnEggEntity>) ThrownSpawnEggEntity::new, EntityClassification.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("thrown_spawn_egg")
    );

    public static RegistryObject<EntityType<ThrownGlowstoneDustEntity>> THROWN_GLOWSTONE_DUST = REGISTRY.register(
            "thrown_glowstone_dust",
            () -> EntityType.Builder
                    .of((EntityType.IFactory<ThrownGlowstoneDustEntity>) ThrownGlowstoneDustEntity::new, EntityClassification.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("thrown_glowstone_dust")
    );

    public static void register(IEventBus bus) {
        REGISTRY.register(bus);
    }
}
