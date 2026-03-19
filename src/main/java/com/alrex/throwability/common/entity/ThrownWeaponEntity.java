package com.alrex.throwability.common.entity;

import com.alrex.throwability.common.capability.throwable.WeaponThrowable;
import com.alrex.throwability.common.sound.SoundEvents;
import com.alrex.throwability.common.util.DamageSources;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class ThrownWeaponEntity extends AbstractArrow {
    private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK;

    static {
        DATA_ITEM_STACK = SynchedEntityData.defineId(ThrownWeaponEntity.class, EntityDataSerializers.ITEM_STACK);
    }

    public ThrownWeaponEntity(EntityType<? extends ThrownWeaponEntity> entityType, Level world) {
        super(entityType, world);
    }

    public ThrownWeaponEntity(Level world, double x, double y, double z) {
        super(EntityTypes.THROWN_WEAPON.get(), x, y, z, world);
    }

    public ThrownWeaponEntity(Level world, LivingEntity thrower, ItemStack weapon) {
        super(EntityTypes.THROWN_WEAPON.get(), thrower, world);
        setWeapon(weapon);
    }

    @Override
    protected void onHitEntity(@Nonnull EntityHitResult entityHitResult) {
        ItemStack weapon = getWeapon();
        double damageAmount = WeaponThrowable.getBaseAttackDamage(weapon);
        var owner = getOwner();
        var hitEntity = entityHitResult.getEntity();
        if (owner instanceof LivingEntity) {
            damageAmount += ((LivingEntity) owner).getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
        }
        if (hitEntity instanceof LivingEntity livingEntity) {
            damageAmount += EnchantmentHelper.getDamageBonus(weapon, livingEntity.getMobType());
        }
        if (hitEntity.hurt(hitEntity.level().damageSources().source(DamageSources.THROWN_WEAPON, this, owner), (float) damageAmount)) {
            if (hitEntity instanceof EnderMan) {
                return;
            }
            if (hitEntity instanceof LivingEntity hitLivingEntity) {
                if (owner instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(hitLivingEntity, owner);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity) owner, hitLivingEntity);
                }
                this.doPostHurtEffects(hitLivingEntity);
            }
            playSound(SoundEvents.WEAPON_HIT_ENTITY.get(), 1f, 1f);
        }
        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        setSoundEvent(SoundEvents.WEAPON_HIT_BLOCK.get());
    }

    @Override
    protected @NotNull SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.WEAPON_HIT_BLOCK.get();
    }

    @Override
    protected void tickDespawn() {
        if (this.pickup != Pickup.ALLOWED) {
            super.tickDespawn();
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(DATA_ITEM_STACK, ItemStack.EMPTY);
    }

    public ItemStack getWeapon() {
        return getEntityData().get(DATA_ITEM_STACK);
    }

    private void setWeapon(ItemStack itemStack) {
        getEntityData().set(DATA_ITEM_STACK, itemStack);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("weapon", getWeapon().save(new CompoundTag()));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setWeapon(ItemStack.of(tag.getCompound("weapon")));
    }

    @Override
    @Nonnull
    protected ItemStack getPickupItem() {
        return getWeapon().copy();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
