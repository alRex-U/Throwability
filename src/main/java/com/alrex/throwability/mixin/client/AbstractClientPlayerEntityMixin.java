package com.alrex.throwability.mixin.client;

import com.alrex.throwability.client.animation.AnimationHost;
import com.alrex.throwability.client.animation.Animations;
import com.alrex.throwability.client.animation.IAnimationHostProvider;
import com.alrex.throwability.common.ability.AbstractThrowingAbility;
import com.alrex.throwability.common.ability.IThrowabilityProvider;
import com.alrex.throwability.common.ability.LocalThrowingAbility;
import com.alrex.throwability.common.ability.RemoteThrowingAbility;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerEntityMixin extends Player implements IThrowabilityProvider, IAnimationHostProvider {
    @Unique
    private final AnimationHost throwability$animationHost = Animations.newAnimationHost();
    @Unique
    @Nullable
    private AbstractThrowingAbility throwability$throwingAbility = null;

    public AbstractClientPlayerEntityMixin(Level p_219727_, BlockPos p_219728_, float p_219729_, GameProfile p_219730_, @Nullable ProfilePublicKey p_219731_) {
        super(p_219727_, p_219728_, p_219729_, p_219730_, p_219731_);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(ClientLevel clientLevel, GameProfile gameProfile, ProfilePublicKey profilePublicKey, CallbackInfo ci) {
        if ((Object) this instanceof LocalPlayer) {
            throwability$throwingAbility = new LocalThrowingAbility((LocalPlayer) (Object) this);
        } else {
            throwability$throwingAbility = new RemoteThrowingAbility(this);
        }
    }

    @Override
    public AbstractThrowingAbility getThrowAbility() {
        return throwability$throwingAbility;
    }

    @Override
    public AnimationHost getAnimationHost() {
        return throwability$animationHost;
    }
}
