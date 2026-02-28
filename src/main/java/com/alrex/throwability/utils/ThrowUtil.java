package com.alrex.throwability.utils;

import com.alrex.throwability.common.ability.ThrowType;
import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.common.network.ItemThrowMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ThrowUtil {
	public static void throwItem(PlayerEntity player, int inventoryIndex, ItemStack selectedItem, IThrowable itemThrowable, ThrowType type, int chargingTick) {
        if ((type == ThrowType.ONE_AS_ENTITY || type == ThrowType.ONE_AS_ITEM) && selectedItem.getCount() > 1) {
			selectedItem = selectedItem.split(1);
		} else {
			player.inventory.removeItem(selectedItem);
		}
		if (player.isLocalPlayer()) {
			ItemThrowMessage.send(player, inventoryIndex, chargingTick, type);
		}
		player.swing(Hand.MAIN_HAND);
		if (player.level.isClientSide()) {
			itemThrowable.onThrownOnClient(player, selectedItem);
		} else {
			Entity entity;
			if (type == ThrowType.ONE_AS_ENTITY) {
				entity = itemThrowable.throwAsEntity(player, selectedItem, chargingTick);
			} else {
                Entity itemEntity = itemThrowable.throwAsItem(player, selectedItem, chargingTick);
                if (itemEntity instanceof ItemEntity) {
                    ((ItemEntity) itemEntity).setThrower(player.getUUID());
                }
				entity = itemEntity;
			}
            if (entity != null) {
                player.level.addFreshEntity(entity);
            }
		}
	}

	public static Vector3d getBasicThrowingPosition(PlayerEntity thrower) {
		Vector3d pos = thrower.position();
		return new Vector3d(pos.x, pos.y + thrower.getEyeHeight() * 0.833, pos.z);
	}

	public static Vector3d getBasicThrowingVector(PlayerEntity thrower) {
		float pitchSin = MathHelper.sin(thrower.xRot * ((float) Math.PI / 180F));
		float pitchCos = MathHelper.cos(thrower.xRot * ((float) Math.PI / 180F));
		float yawSin = MathHelper.sin(thrower.yRot * ((float) Math.PI / 180F));
		float yawCos = MathHelper.cos(thrower.yRot * ((float) Math.PI / 180F));
		float random = thrower.getRandom().nextFloat() * ((float) Math.PI * 2F);
		float random2 = 0.02F * thrower.getRandom().nextFloat();
		return new Vector3d(
				(-yawSin * pitchCos * 0.3F) + 0.5f * Math.cos(random) * (double) random2,
				(-pitchSin * 0.3F + 0.05 + (thrower.getRandom().nextFloat() - thrower.getRandom().nextFloat()) * 0.05F),
				(yawCos * pitchCos * 0.3F) + 0.5f * Math.sin(random) * (double) random2
		);
	}

	public static ItemEntity getItemEntity(ItemStack stack, World world, Vector3d pos) {
		return new ItemEntity(
				world, pos.x(), pos.y(), pos.z(), stack
		);
	}

	public static Entity getThrownEntityOf(PlayerEntity thrower, ItemStack stack, World world, Vector3d pos, Vector3d projectileAngle) {
		Item item = stack.getItem();
		if (item instanceof FireChargeItem) {
			SmallFireballEntity entity = new SmallFireballEntity(
					world, thrower, projectileAngle.x(), projectileAngle.y(), projectileAngle.z()
			);
			entity.setPos(pos.x(), pos.y(), pos.z());
			return entity;
		}

		if (item instanceof ArrowItem) {
			ArrowItem arrowItem = (ArrowItem) item;
			AbstractArrowEntity arrow = arrowItem.createArrow(world, stack, thrower);
			arrow.setPos(pos.x(), pos.y(), pos.z());
			return arrow;
		}
		if (item instanceof FireworkRocketItem) {
			FireworkRocketEntity rocketEntity = new FireworkRocketEntity(
					world, thrower,
					projectileAngle.x(), projectileAngle.y(), projectileAngle.z(),
					stack
			);
			rocketEntity.setPos(pos.x(), pos.y(), pos.z());
			return rocketEntity;
		}
		if (item instanceof EnderPearlItem) {
			EnderPearlEntity pearlEntity = new EnderPearlEntity(
					world, thrower
			);
			pearlEntity.setPos(pos.x(), pos.y(), pos.z());
			return pearlEntity;
		}
		if (item instanceof SnowballItem) {
			return new SnowballEntity(
					world, pos.x(), pos.y(), pos.z()
			);
		}
		if (item instanceof EggItem) {
			return new EggEntity(
					world, pos.x(), pos.y(), pos.z()
			);
		}
		if (item instanceof ExperienceBottleItem) {
			return new ExperienceBottleEntity(
					world, pos.x(), pos.y(), pos.z()
			);
		}
		if (item instanceof ThrowablePotionItem) {
			PotionEntity potionentity = new PotionEntity(world, thrower);
			potionentity.setItem(stack);
			potionentity.setPos(pos.x(), pos.y(), pos.z());
			return potionentity;
		}
		if (item == Items.TNT) {
			TNTEntity entity = new TNTEntity(
					world, pos.x(), pos.y(), pos.z(), thrower
			);
			entity.setRemainingFireTicks(30);
			return entity;
		}
		if (item instanceof TridentItem) {
			return new TridentEntity(
					world, pos.x(), pos.y(), pos.z()
			);
		}
		if (item instanceof BlockItem) {
			BlockItem blockItem = (BlockItem) item;
			FallingBlockEntity entity = new FallingBlockEntity(
					world, pos.x(), pos.y(), pos.z(), blockItem.getBlock().defaultBlockState()
			);
			entity.time = 1;
			return entity;
		}
		ItemEntity itemEntity = new ItemEntity(
				world, pos.x(), pos.y(), pos.z(), stack
		);
		Entity customEntity = item.createEntity(world, itemEntity, stack);
		if (customEntity == null) return itemEntity;
		else return customEntity;
	}
}
