package com.alrex.throwability.common.sound;

import com.alrex.throwability.Throwability;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SoundEvents {
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Throwability.MOD_ID);
    public static final RegistryObject<SoundEvent> THROW = SOUNDS.register("throw", () -> new SoundEvent(
            new ResourceLocation(Throwability.MOD_ID, "throwability.throw"))
    );

    public static void register(IEventBus bus) {
        SOUNDS.register(bus);
    }
}
