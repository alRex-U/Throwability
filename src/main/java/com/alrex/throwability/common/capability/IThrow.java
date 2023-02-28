package com.alrex.throwability.common.capability;

import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public interface IThrow {
	@Nullable
	public static IThrow get(Player player) {
		return player.getCapability(Capabilities.THROW_CAPABILITY).orElse(null);
	}

	public void tick();

	public float getOldPower();

	public void chargeThrowPower();

	public void throwItem(int inventoryIndex, ThrowType type, float strength);

	public void setPower(float value);

	public void cancel();

	public boolean isCharging();

	public void setCharging(boolean value);

	public float getChargingPower();

	public float getMaxPower();
}
