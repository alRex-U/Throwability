package com.alrex.throwability.common.entity;

import com.alrex.throwability.Throwability;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityTypes {
    private static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Throwability.MOD_ID);
    public static RegistryObject<EntityType<ThrownWeaponEntity>> THROWN_WEAPON = REGISTRY.register(
            "thrown_weapon",
            () -> EntityType.Builder
                    .of((EntityType.EntityFactory<ThrownWeaponEntity>) ThrownWeaponEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build("thrown_weapon")
    );
    public static RegistryObject<EntityType<ThrownSpawnEggEntity>> THROWN_SPAWN_EGG = REGISTRY.register(
            "thrown_spawn_egg",
            () -> EntityType.Builder
                    .of((EntityType.EntityFactory<ThrownSpawnEggEntity>) ThrownSpawnEggEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("thrown_spawn_egg")
    );
    public static RegistryObject<EntityType<ThrownGlowstoneDustEntity>> THROWN_GLOWSTONE_DUST = REGISTRY.register(
            "thrown_glowstone_dust",
            () -> EntityType.Builder
                    .of((EntityType.EntityFactory<ThrownGlowstoneDustEntity>) ThrownGlowstoneDustEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("thrown_glowstone_dust")
    );
    public static RegistryObject<EntityType<ThrownBlazePowderEntity>> THROWN_BLAZE_POWDER = REGISTRY.register(
            "thrown_blaze_powder",
            () -> EntityType.Builder
                    .of((EntityType.EntityFactory<ThrownBlazePowderEntity>) ThrownBlazePowderEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("thrown_blaze_powder")
    );
    public static RegistryObject<EntityType<ThrownBlazeRodEntity>> THROWN_BLAZE_ROD = REGISTRY.register(
            "thrown_blaze_rod",
            () -> EntityType.Builder
                    .of((EntityType.EntityFactory<ThrownBlazeRodEntity>) ThrownBlazeRodEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("thrown_blaze_rod")
    );
    public static RegistryObject<EntityType<ThrownGunPowderEntity>> THROWN_GUNPOWDER = REGISTRY.register(
            "thrown_gunpowder",
            () -> EntityType.Builder
                    .of((EntityType.EntityFactory<ThrownGunPowderEntity>) ThrownGunPowderEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("thrown_gunpowder")
    );
    public static RegistryObject<EntityType<ThrownGhastTearEntity>> THROWN_GHAST_TEAR = REGISTRY.register(
            "thrown_ghast_tear",
            () -> EntityType.Builder
                    .of((EntityType.EntityFactory<ThrownGhastTearEntity>) ThrownGhastTearEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("thrown_ghast_tear")
    );
    public static RegistryObject<EntityType<ThrownSlimeballEntity>> THROWN_SLIMEBALL = REGISTRY.register(
            "thrown_slimeball",
            () -> EntityType.Builder
                    .of((EntityType.EntityFactory<ThrownSlimeballEntity>) ThrownSlimeballEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("thrown_slimeball")
    );
    public static RegistryObject<EntityType<ThrownInkSacEntity>> THROWN_INK_SAC = REGISTRY.register(
            "thrown_ink_sac",
            () -> EntityType.Builder
                    .of((EntityType.EntityFactory<ThrownInkSacEntity>) ThrownInkSacEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("thrown_ink_sac")
    );
    public static RegistryObject<EntityType<ThrownDispensibleContainerEntity>> THROWN_BUCKET = REGISTRY.register(
            "thrown_bucket",
            () -> EntityType.Builder
                    .of((EntityType.EntityFactory<ThrownDispensibleContainerEntity>) ThrownDispensibleContainerEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("thrown_bucket")
    );
    public static RegistryObject<EntityType<ThrownSpiderEyeEntity>> THROWN_SPIDER_EYE = REGISTRY.register(
            "thrown_spider_eye",
            () -> EntityType.Builder
                    .of((EntityType.EntityFactory<ThrownSpiderEyeEntity>) ThrownSpiderEyeEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("thrown_spider_eye")
    );
    public static RegistryObject<EntityType<ThrownGlowInkSacEntity>> THROWN_GLOW_INK_SAC = REGISTRY.register(
            "thrown_glow_ink_sac",
            () -> EntityType.Builder
                    .of((EntityType.EntityFactory<ThrownGlowInkSacEntity>) ThrownGlowInkSacEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("thrown_glow_ink_sac")
    );

    public static void register(IEventBus bus) {
        REGISTRY.register(bus);
    }
}
