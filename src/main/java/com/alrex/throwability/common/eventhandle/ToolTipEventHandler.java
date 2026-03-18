package com.alrex.throwability.common.eventhandle;

import com.alrex.throwability.common.capability.Capabilities;
import com.alrex.throwability.common.capability.throwable.StandardThrowable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ToolTipEventHandler {
    private final static Component THROWABLE_TEXT_COMP = Component.translatable("throwability.item.throwable").withStyle(ChatFormatting.BLUE);

    @SubscribeEvent
    public static void onToolTip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getCapability(Capabilities.THROWABLE_CAPABILITY).isPresent()
                || StandardThrowable.getInstance().matchAnyEntry(stack)) {
            event.getToolTip().add(THROWABLE_TEXT_COMP);
        }
    }
}
