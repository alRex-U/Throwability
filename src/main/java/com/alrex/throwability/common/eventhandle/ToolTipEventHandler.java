package com.alrex.throwability.common.eventhandle;

import com.alrex.throwability.common.capability.Capabilities;
import com.alrex.throwability.common.capability.throwable.StandardThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ToolTipEventHandler {
    private final static ITextComponent THROWABLE_TEXT_COMP = new TranslationTextComponent("throwability.item.throwable").withStyle(TextFormatting.BLUE);

    @SubscribeEvent
    public static void onToolTip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getCapability(Capabilities.THROWABLE_CAPABILITY).isPresent()
                || StandardThrowable.getInstance().matchAnyEntry(stack)) {
            event.getToolTip().add(THROWABLE_TEXT_COMP);
        }
    }
}
