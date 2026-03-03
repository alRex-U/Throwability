package com.alrex.throwability;

import net.minecraftforge.common.ForgeConfigSpec;

public class ThrowabilityConfig {
    public static class Client {
        public static final ForgeConfigSpec.BooleanValue HUD_FADE_IN;
        private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        public static ForgeConfigSpec BUILT_CONFIG;

        static {
            BUILDER.push("hud");
            {
                HUD_FADE_IN = BUILDER.define("fade_in", true);
            }
            BUILT_CONFIG = BUILDER.build();
        }
    }
}
