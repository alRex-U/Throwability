package com.alrex.throwability.utils;

import com.alrex.throwability.common.ability.ThrowType;
import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.common.network.ItemThrowMessage;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ThrowUtil {
	public static void throwItem(Player player, int inventoryIndex, ItemStack selectedItem, IThrowable itemThrowable, ThrowType type, int chargingTick) {
        if ((type == ThrowType.ONE_AS_ENTITY || type == ThrowType.ONE_AS_ITEM) && selectedItem.getCount() > 1) {
			selectedItem = selectedItem.split(1);
		} else {
			player.getInventory().removeItem(selectedItem);
		}
		if (player.isLocalPlayer()) {
			ItemThrowMessage.send(player, inventoryIndex, chargingTick, type);
		}
		player.swing(InteractionHand.MAIN_HAND);
		if (player.level.isClientSide()) {
			itemThrowable.onThrownOnClient(player, selectedItem, type, chargingTick);
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

	public static Vec3 getBasicThrowingPosition(Player thrower) {
		var pos = thrower.position();
		return new Vec3(pos.x, pos.y + thrower.getEyeHeight() * 0.833, pos.z);
	}

	public static Vec3 getBasicThrowingVector(Player thrower) {
		float pitchSin = Mth.sin(thrower.getXRot() * ((float) Math.PI / 180F));
		float pitchCos = Mth.cos(thrower.getXRot() * ((float) Math.PI / 180F));
		float yawSin = Mth.sin(thrower.getYRot() * ((float) Math.PI / 180F));
		float yawCos = Mth.cos(thrower.getYRot()* ((float) Math.PI / 180F));
		float random = thrower.getRandom().nextFloat() * ((float) Math.PI * 2F);
		float random2 = 0.02F * thrower.getRandom().nextFloat();
		return new Vec3(
				(-yawSin * pitchCos * 0.3F) + 0.5f * Math.cos(random) * (double) random2,
				(-pitchSin * 0.3F + 0.02f + (thrower.getRandom().nextFloat() - 0.5) * 0.025F),
				(yawCos * pitchCos * 0.3F) + 0.5f * Math.sin(random) * (double) random2
		);
	}

	private static final double getSpeedScale$log_e3 = Math.log(3.);

	public static double getSpeedScale(Player thrower) {
		var effect = thrower.getEffect(MobEffects.DAMAGE_BOOST);
		if (effect != null) {
			return Math.log(effect.getAmplifier() + 4) / getSpeedScale$log_e3;
		} else {
			return 1.;
		}
	}
}
