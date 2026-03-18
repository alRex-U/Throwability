package com.alrex.throwability;

import com.alrex.throwability.client.input.EntityThrowControlType;
import net.minecraftforge.common.ForgeConfigSpec;

public class ThrowabilityConfig {
    public static class Client {
        public static final ForgeConfigSpec.BooleanValue HUD_FADE_IN;
        public static final ForgeConfigSpec.EnumValue<EntityThrowControlType> ENTITY_THROW_CONTROL;

        private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        public static ForgeConfigSpec BUILT_CONFIG;

        static {
            BUILDER.push("hud");
            {
                HUD_FADE_IN = BUILDER.define("fade_in", true);
            }
            BUILDER.pop();
            BUILDER.push("control");
            {
                ENTITY_THROW_CONTROL = BUILDER.defineEnum("entity_throw_control", EntityThrowControlType.PRESS_DEDICATED_KEY);
            }
            BUILDER.pop();

            BUILT_CONFIG = BUILDER.build();
        }
    }
}
