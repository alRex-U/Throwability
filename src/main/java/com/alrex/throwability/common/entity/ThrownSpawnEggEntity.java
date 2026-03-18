package com.alrex.throwability.common.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.network.IPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

@OnlyIn(value = Dist.CLIENT, _interface = IRendersAsItem.class)
public class ThrownSpawnEggEntity extends ProjectileItemEntity implements IRendersAsItem {
    public ThrownSpawnEggEntity(EntityType<? extends ThrownSpawnEggEntity> entityType, World level) {
        super(entityType, level);
    }

    public ThrownSpawnEggEntity(World level, double x, double y, double z, ItemStack spawnEgg) {
        super(EntityTypes.THROWN_SPAWN_EGG.get(), x, y, z, level);
        setItem(spawnEgg);
    }

    public ThrownSpawnEggEntity(World level, LivingEntity thrower, ItemStack spawnEgg) {
        super(EntityTypes.THROWN_SPAWN_EGG.get(), thrower, level);
        setItem(spawnEgg);
    }

    @Override
    protected void onHit(RayTraceResult rayTraceResult) {
        super.onHit(rayTraceResult);
        if (!this.level.isClientSide) {
            ItemStack egg = getItem();
            Item item = egg.getItem();
            if (item instanceof SpawnEggItem) {
                PlayerEntity owner = null;
                Entity _owner = getOwner();
                if (_owner instanceof PlayerEntity) {
                    owner = (PlayerEntity) _owner;
                }
                EntityType<?> entitytype = ((SpawnEggItem) item).getType(egg.getTag());
                if (entitytype.spawn(
                        (ServerWorld) this.level,
                        egg,
                        owner,
                        blockPosition(),
                        SpawnReason.SPAWN_EGG,
                        true,
                        rayTraceResult instanceof BlockRayTraceResult && ((BlockRayTraceResult) rayTraceResult).getDirection() == Direction.UP
                ) != null) {
                    egg.shrink(1);
                }
            }
            if (!egg.isEmpty()) {
                spawnAtLocation(egg);
            }
            this.remove();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Items.PIG_SPAWN_EGG;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
