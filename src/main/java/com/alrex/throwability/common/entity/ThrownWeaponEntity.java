package com.alrex.throwability.common.entity;

import com.alrex.throwability.common.capability.throwable.WeaponThrowable;
import com.alrex.throwability.common.util.DamageSources;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

public class ThrownWeaponEntity extends AbstractArrowEntity {
    private static DataParameter<ItemStack> DATA_ITEM_STACK;

    static {
        DATA_ITEM_STACK = EntityDataManager.defineId(ThrownWeaponEntity.class, DataSerializers.ITEM_STACK);
    }

    public ThrownWeaponEntity(EntityType<? extends AbstractArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    public ThrownWeaponEntity(World world, double x, double y, double z) {
        super(EntityTypes.THROWN_WEAPON.get(), x, y, z, world);
    }

    public ThrownWeaponEntity(World world, LivingEntity thrower, ItemStack weapon) {
        super(EntityTypes.THROWN_WEAPON.get(), thrower, world);
        setWeapon(weapon);
    }

    @Override
    protected void onHitEntity(@Nonnull EntityRayTraceResult entityRayTraceResult) {
        ItemStack weapon = getWeapon();
        double damageAmount = WeaponThrowable.getBaseAttackDamage(weapon);
        Entity owner = getOwner();
        Entity hitEntity = entityRayTraceResult.getEntity();
        if (owner instanceof LivingEntity) {
            damageAmount += ((LivingEntity) owner).getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
        }
        if (hitEntity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) hitEntity;
            damageAmount += EnchantmentHelper.getDamageBonus(weapon, livingEntity.getMobType());
        }
        DamageSource damageSource = DamageSources.thrownWeapon(this, owner != null ? owner : this);
        if (hitEntity.hurt(damageSource, (float) damageAmount)) {
            if (hitEntity instanceof EndermanEntity) {
                return;
            }
            if (hitEntity instanceof LivingEntity) {
                LivingEntity hitLivingEntity = (LivingEntity) hitEntity;
                if (owner instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(hitLivingEntity, owner);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity) owner, hitLivingEntity);
                }
                this.doPostHurtEffects(hitLivingEntity);
            }
        }
        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
        //this.playSound(lvt_6_1_, lvt_7_2_, 1.0F);
    }

    @Override
    protected void tickDespawn() {
        if (this.pickup != PickupStatus.ALLOWED) {
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
    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
        tag.put("weapon", getWeapon().save(new CompoundNBT()));
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
        setWeapon(ItemStack.of(tag.getCompound("weapon")));
    }

    @Override
    @Nonnull
    protected ItemStack getPickupItem() {
        return getWeapon().copy();
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
