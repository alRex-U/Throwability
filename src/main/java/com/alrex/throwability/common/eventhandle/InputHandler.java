package com.alrex.throwability.common.eventhandle;

import com.alrex.throwability.common.ability.AbstractThrowingAbility;
import com.alrex.throwability.common.ability.IThrowabilityProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class InputHandler {
    @SubscribeEvent
    public static void onInput(InputEvent.ClickInputEvent event) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (!(player instanceof IThrowabilityProvider)) return;
        AbstractThrowingAbility ability = ((IThrowabilityProvider) player).getThrowAbility();
        if (ability.isCharging()) {
            event.setSwingHand(false);
            event.setCanceled(true);
        }
    }
}
