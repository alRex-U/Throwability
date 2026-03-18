package com.alrex.throwability.client.hud;

import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;

public class ThrowabilityHUDs {
    @Nullable
    private static ThrowabilityHUDs INSTANCE;
    @Nullable
    private IGuiOverlay throwPowerMeter;

    public static ThrowabilityHUDs getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ThrowabilityHUDs();
        }
        return INSTANCE;
    }

    @SubscribeEvent
    public void onSetup(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("power_meter", throwPowerMeter = new ThrowPowerMeter());
    }
}
