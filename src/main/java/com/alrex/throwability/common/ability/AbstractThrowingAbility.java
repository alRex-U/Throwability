package com.alrex.throwability.common.ability;

public class AbstractThrowingAbility {
    protected boolean charging = false;
    protected boolean oldCharging = false;
    protected int chargingTick = 0;
    protected int maxChargingTick = 20;

    public void tick() {
        if (charging) {
            if ((++chargingTick) > maxChargingTick) chargingTick = maxChargingTick;
        } else {
            if ((--chargingTick) < 0) chargingTick = 0;
        }
        oldCharging = charging;
    }

    public boolean isCharging() {
        return charging;
    }

    public int getChargingTick() {
        return chargingTick;
    }

    public int getMaxChargingTick() {
        return maxChargingTick;
    }

    public void setMaxChargingTick(int maxChargingTick) {
        if (maxChargingTick >= 1) {
            this.maxChargingTick = maxChargingTick;
        }
    }

    public void startCharging() {
        charging = true;
        chargingTick++;
    }

    public void stopCharging() {
        charging = false;
        chargingTick = 0;
    }
}
