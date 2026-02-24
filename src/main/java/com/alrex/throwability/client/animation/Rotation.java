package com.alrex.throwability.client.animation;

import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

public class Rotation {
    private final float x, y, z;

    public Rotation(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getXRot() {
        return x;
    }

    public float getYRot() {
        return y;
    }

    public float getZRot() {
        return z;
    }

    public static class Builder {
        private Quaternion q = Quaternion.ONE.copy();

        public Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder apply(Vector3f axis, float angleRad) {
            Quaternion newQ = axis.rotation(angleRad);
            newQ.mul(q);
            q = newQ;
            return this;
        }

        public Rotation build() {
            float xRot, zRot, yRot = (float) Math.asin(2 * (-q.i() * q.k() + q.j() * q.r()));
            double cosY = Math.cos(yRot);
            if (Math.abs(cosY) > 1e-4) {
                xRot = (float) Math.atan2(
                        q.j() * q.k() + q.i() * q.r(),
                        q.r() * q.r() + q.k() * q.k() - 0.5
                );
                zRot = (float) Math.atan2(
                        q.i() * q.j() + q.k() * q.r(),
                        q.r() * q.r() + q.i() * q.i() - 0.5
                );
            } else {
                xRot = 0;
                zRot = (float) Math.atan2(
                        -q.i() * q.j() + q.k() * q.r(),
                        q.r() * q.r() + q.j() * q.j() - 0.5
                );
            }
            return new Rotation(xRot, yRot, zRot);
        }

    }
}
