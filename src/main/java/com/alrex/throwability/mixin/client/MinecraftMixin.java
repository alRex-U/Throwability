package com.alrex.throwability.mixin.client;

import com.alrex.throwability.client.input.KeyBindings;
import com.alrex.throwability.client.input.KeyRecorder;
import com.mojang.blaze3d.platform.WindowEventHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraftforge.client.extensions.IForgeMinecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin extends ReentrantBlockableEventLoop<Runnable> implements WindowEventHandler, IForgeMinecraft {
    @Shadow
    @Final
    public Options options;
    @Unique
    private boolean throwability$handledOnce = false;

    public MinecraftMixin(String p_i50401_1_) {
        super(p_i50401_1_);
    }

    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void onHandleKeyBindsHead(CallbackInfo ci) {
        throwability$handledOnce = false;
    }

    @Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z"))
    private boolean onConsumeClick(KeyMapping instance) {
        if (instance != options.keyDrop) return instance.consumeClick();
        if (!KeyBindings.throwAndDropAreConflicting()) return instance.consumeClick();

        if (throwability$handledOnce) return false;
        throwability$handledOnce = true;

        return KeyRecorder.getStateDrop().isJustReleased();
    }
}
