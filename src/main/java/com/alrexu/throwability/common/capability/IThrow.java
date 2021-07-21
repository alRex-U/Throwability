package com.alrexu.throwability.common.capability;

import net.minecraft.entity.player.PlayerEntity;

public interface IThrow {
	public void chargeThrowPower();

	public void throwItem(PlayerEntity player, boolean all);

	public void throwItem(PlayerEntity player, boolean all, float strength);

	public void cancel();

	public boolean isCharging();

	public int getChargingPower();

	public float getStrength();

	public int getMaxPower();
}
