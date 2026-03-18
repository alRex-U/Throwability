package com.alrex.throwability.common.eventhandle;

import com.alrex.throwability.common.entity.ThrownWeaponEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class InteractionHandler {
    @SubscribeEvent
    public static void onInteractEntity(PlayerInteractEvent.EntityInteractSpecific event) {
        var entity = event.getTarget();

        if (entity.isRemoved()) return;
        if (entity instanceof ItemEntity || entity instanceof ThrownWeaponEntity) {
            entity.playerTouch(event.getPlayer());
        }
    }
}
