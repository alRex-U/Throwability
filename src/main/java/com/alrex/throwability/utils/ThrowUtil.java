package com.alrex.throwability.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.item.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ThrowUtil {
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
