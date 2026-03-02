package com.alrex.throwability.common.sound;

import com.alrex.throwability.Throwability;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SoundEvents {
    private static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Throwability.MOD_ID);
    public static final RegistryObject<SoundEvent> THROW = REGISTRY.register("throw", () -> new SoundEvent(
            new ResourceLocation(Throwability.MOD_ID, "throwability.throw"))
    );
    public static final RegistryObject<SoundEvent> WEAPON_THROW = REGISTRY.register("weapon.throw", () -> new SoundEvent(
            new ResourceLocation(Throwability.MOD_ID, "throwability.weapon.throw"))
    );
    public static final RegistryObject<SoundEvent> WEAPON_HIT_ENTITY = REGISTRY.register("weapon_hit.entity", () -> new SoundEvent(
            new ResourceLocation(Throwability.MOD_ID, "throwability.weapon.hit.entity"))
    );
    public static final RegistryObject<SoundEvent> WEAPON_HIT_BLOCK = REGISTRY.register("weapon_hit.block", () -> new SoundEvent(
            new ResourceLocation(Throwability.MOD_ID, "throwability.weapon.hit.block"))
    );

    public static void register(IEventBus bus) {
        REGISTRY.register(bus);
    }
}
