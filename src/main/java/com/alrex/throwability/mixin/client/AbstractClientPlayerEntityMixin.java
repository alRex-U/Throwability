package com.alrex.throwability.mixin.client;

import com.alrex.throwability.client.animation.AnimationHost;
import com.alrex.throwability.client.animation.Animations;
import com.alrex.throwability.client.animation.IAnimationHostProvider;
import com.alrex.throwability.common.ability.AbstractThrowingAbility;
import com.alrex.throwability.common.ability.IThrowabilityProvider;
import com.alrex.throwability.common.ability.LocalThrowingAbility;
import com.alrex.throwability.common.ability.RemoteThrowingAbility;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity implements IThrowabilityProvider, IAnimationHostProvider {
    @Unique
    private final AnimationHost throwability$animationHost = Animations.newAnimationHost();
    @Unique
    @Nullable
    private AbstractThrowingAbility throwability$throwingAbility = null;

    public AbstractClientPlayerEntityMixin(World world, BlockPos blockPos, float p_i241920_3_, GameProfile gameProfile) {
        super(world, blockPos, p_i241920_3_, gameProfile);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(ClientWorld world, GameProfile gameProfile, CallbackInfo ci) {
        if ((Object) this instanceof ClientPlayerEntity) {
            throwability$throwingAbility = new LocalThrowingAbility((ClientPlayerEntity) (Object) this);
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
