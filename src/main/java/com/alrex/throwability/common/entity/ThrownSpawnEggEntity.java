package com.alrex.throwability.common.entity;

import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class ThrownSpawnEggEntity extends ThrowableItemProjectile {
    public ThrownSpawnEggEntity(EntityType<? extends ThrownSpawnEggEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownSpawnEggEntity(Level level, double x, double y, double z, ItemStack spawnEgg) {
        super(EntityTypes.THROWN_SPAWN_EGG.get(), x, y, z, level);
        setItem(spawnEgg);
    }

    public ThrownSpawnEggEntity(Level level, LivingEntity thrower, ItemStack spawnEgg) {
        super(EntityTypes.THROWN_SPAWN_EGG.get(), thrower, level);
        setItem(spawnEgg);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide) {
            ItemStack egg = getItem();
            Item item = egg.getItem();
            if (item instanceof SpawnEggItem spawnEggItem) {
                Player owner = null;
                var _owner = getOwner();
                if (_owner instanceof Player) {
                    owner = (Player) _owner;
                }
                EntityType<?> entitytype = spawnEggItem.getType(egg.getTag());
                if (entitytype.spawn(
                        (ServerLevel) this.level(),
                        egg,
                        owner,
                        blockPosition(),
                        MobSpawnType.SPAWN_EGG,
                        true,
                        hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getDirection() == Direction.UP
                ) != null) {
                    egg.shrink(1);
                }
            }
            if (!egg.isEmpty()) {
                spawnAtLocation(egg);
            }
            this.discard();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Items.PIG_SPAWN_EGG;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
