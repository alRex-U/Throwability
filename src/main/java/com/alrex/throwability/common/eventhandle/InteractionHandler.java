package com.alrex.throwability.common.eventhandle;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class InteractionHandler {
    @SubscribeEvent
    public static void onInteractEntity(PlayerInteractEvent.EntityInteractSpecific event) {
        Entity entity = event.getTarget();
        if (entity instanceof ItemEntity && !entity.removed) {
            ItemEntity itemEntity = (ItemEntity) entity;
            itemEntity.playerTouch(event.getPlayer());
        }
    }
}
