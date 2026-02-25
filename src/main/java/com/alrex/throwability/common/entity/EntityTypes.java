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

    public static void register(IEventBus bus) {
        REGISTRY.register(bus);
    }
}
