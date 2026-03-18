package com.alrex.throwability.common.eventhandle;

import com.alrex.throwability.common.ability.AbstractThrowingAbility;
import com.alrex.throwability.common.ability.IThrowabilityProvider;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class InputHandler {
    @SubscribeEvent
    public static void onInput(InputEvent.ClickInputEvent event) {
        var player = Minecraft.getInstance().player;
        if (!(player instanceof IThrowabilityProvider provider)) return;
        AbstractThrowingAbility ability = (provider).getThrowAbility();
        if (ability.isCharging()) {
            event.setSwingHand(false);
            event.setCanceled(true);
        }
    }
}
