package com.alrex.throwability.mixin.client;

import com.alrex.throwability.client.input.KeyBindings;
import com.alrex.throwability.common.ability.AbstractThrowingAbility;
import com.alrex.throwability.common.ability.IThrowabilityProvider;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {

    public LocalPlayerMixin(ClientLevel p_234112_, GameProfile p_234113_, @Nullable ProfilePublicKey p_234114_) {
        super(p_234112_, p_234113_, p_234114_);
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
