package com.alrex.throwability.utils;

import com.alrex.throwability.shrewd.FallingBlockEntitySub;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ThrowUtil {
	public static ItemEntity getItemEntity(ItemStack stack, Level world, Vec3 pos) {
		return new ItemEntity(
				world, pos.x(), pos.y(), pos.z(), stack
		);
	}

	public static Entity getThrownEntityOf(Player thrower, ItemStack stack, Level world, Vec3 pos, Vec3 projectileAngle) {
		Item item = stack.getItem();
		if (item instanceof FireChargeItem) {
			SmallFireball entity = new SmallFireball(
					world, thrower, projectileAngle.x(), projectileAngle.y(), projectileAngle.z()
			);
			entity.setPos(pos.x(), pos.y(), pos.z());
			return entity;
		}

		if (item instanceof ArrowItem arrowItem) {
			AbstractArrow arrow = arrowItem.createArrow(world, stack, thrower);
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
		if (item instanceof EnderpearlItem) {
			ThrownEnderpearl pearlEntity = new ThrownEnderpearl(
					world, thrower
			);
			pearlEntity.setPos(pos.x(), pos.y(), pos.z());
			return pearlEntity;
		}
		if (item instanceof SnowballItem) {
			return new Snowball(
					world, pos.x(), pos.y(), pos.z()
			);
		}
		if (item instanceof EggItem) {
			return new ThrownEgg(
					world, pos.x(), pos.y(), pos.z()
			);
		}
		if (item instanceof ExperienceBottleItem) {
			return new ThrownExperienceBottle(
					world, pos.x(), pos.y(), pos.z()
			);
		}
		if (item instanceof ThrowablePotionItem) {
			ThrownPotion potionEntity = new ThrownPotion(world, thrower);
			potionEntity.setItem(stack);
			potionEntity.setPos(pos.x(), pos.y(), pos.z());
			return potionEntity;
		}
		if (item == Items.TNT) {
			PrimedTnt entity = new PrimedTnt(
					world, pos.x(), pos.y(), pos.z(), thrower
			);
			entity.setRemainingFireTicks(30);
			return entity;
		}
		if (item instanceof TridentItem) {
			var trident = new ThrownTrident(
					world, thrower, stack
			);
			trident.setPos(pos);
			return trident;
		}
		if (item instanceof BlockItem blockItem) {
			return FallingBlockEntitySub.construct(
					world, blockItem.getBlock().defaultBlockState(), pos
			);
		}
		ItemEntity itemEntity = new ItemEntity(
				world, pos.x(), pos.y(), pos.z(), stack
		);
		Entity customEntity = item.createEntity(world, itemEntity, stack);
		if (customEntity == null) return itemEntity;
		else return customEntity;
	}
}
