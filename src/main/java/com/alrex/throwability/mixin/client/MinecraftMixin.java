package com.alrex.throwability.mixin.client;

import com.alrex.throwability.client.input.KeyBindings;
import com.alrex.throwability.client.input.KeyRecorder;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IWindowEventListener;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.util.concurrent.RecursiveEventLoop;
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
public abstract class MinecraftMixin extends RecursiveEventLoop<Runnable> implements ISnooperInfo, IWindowEventListener, IForgeMinecraft {
    @Shadow
    @Final
    public GameSettings options;
    @Unique
    private boolean throwability$handledOnce = false;

    public MinecraftMixin(String p_i50401_1_) {
        super(p_i50401_1_);
    }

    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void onHandleKeyBindsHead(CallbackInfo ci) {
        throwability$handledOnce = false;
    }

    @Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;consumeClick()Z"))
    private boolean onConsumeClick(KeyBinding instance) {
        if (instance != options.keyDrop) return instance.consumeClick();
        if (!KeyBindings.throwAndDropAreConflicting()) return instance.consumeClick();

        if (throwability$handledOnce) return false;
        throwability$handledOnce = true;

        return KeyRecorder.getStateDrop().isJustReleased();
    }
}
