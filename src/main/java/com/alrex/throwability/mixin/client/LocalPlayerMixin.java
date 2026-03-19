package com.alrex.throwability.mixin.client;

import com.alrex.throwability.client.input.KeyBindings;
import com.alrex.throwability.common.ability.AbstractThrowingAbility;
import com.alrex.throwability.common.ability.IThrowabilityProvider;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {

    public LocalPlayerMixin(ClientLevel p_250460_, GameProfile p_249912_) {
        super(p_250460_, p_249912_);
    }

    @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    public void onDrop(boolean dropAll, CallbackInfoReturnable<Boolean> cir) {
        if (!KeyBindings.throwAndDropAreConflicting()) return;
        if (!(this instanceof IThrowabilityProvider provider)) return;
        AbstractThrowingAbility ability = provider.getThrowAbility();
        if (ability.haveEnoughChargeTime()) {
            cir.setReturnValue(false);
        }
    }
}
