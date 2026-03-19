package com.alrex.throwability.client.animation;


import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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
        private Quaternionf q = new Quaternionf(0, 0, 0, 1);

        public Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder apply(Vector3f axis, float angleRad) {
            var newQ = new Quaternionf(new AxisAngle4f((float) Math.toRadians(angleRad), axis));
            ;
            newQ.mul(q);
            q = newQ;
            return this;
        }

        public Rotation build() {
            float xRot, zRot, yRot = (float) Math.asin(2 * (-q.x() * q.z() + q.y() * q.w()));
            double cosY = Math.cos(yRot);
            if (Math.abs(cosY) > 1e-4) {
                xRot = (float) Math.atan2(
                        q.y() * q.z() + q.x() * q.w(),
                        q.w() * q.w() + q.z() * q.z() - 0.5
                );
                zRot = (float) Math.atan2(
                        q.x() * q.y() + q.z() * q.w(),
                        q.w() * q.w() + q.x() * q.x() - 0.5
                );
            } else {
                xRot = 0;
                zRot = (float) Math.atan2(
                        -q.x() * q.y() + q.z() * q.w(),
                        q.w() * q.w() + q.y() * q.y() - 0.5
                );
            }
            return new Rotation(xRot, yRot, zRot);
        }

    }
}
