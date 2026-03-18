package com.alrex.throwability.client.hud;

import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;

import javax.annotation.Nullable;

public class ThrowabilityHUDs {
    @Nullable
    private static ThrowabilityHUDs INSTANCE;
    @Nullable
    private IIngameOverlay throwPowerMeter;

    public static ThrowabilityHUDs getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ThrowabilityHUDs();
        }
        return INSTANCE;
    }

    public void setup() {
        throwPowerMeter = OverlayRegistry.registerOverlayTop("Throwing Power", new ThrowPowerMeter());
    }
}
