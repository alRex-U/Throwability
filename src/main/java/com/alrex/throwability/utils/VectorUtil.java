package com.alrex.throwability.utils;

import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public class VectorUtil {
	public static double toYawDegree(Vec3 vec) {
		return (Math.atan2(vec.z(), vec.x()) * 180.0 / Math.PI - 90);
	}

	public static double toPitchDegree(Vec3 vec) {
		return -(Math.atan2(vec.y(), Math.sqrt(vec.x() * vec.x() + vec.z() * vec.z())) * 180.0 / Math.PI);
	}

	public static Vec3 fromYawDegree(double degree) {
		return new Vec3(-Math.sin(Math.toRadians(degree)), 0, Math.cos(Math.toRadians(degree)));
	}

	public static Vec3 getRandomNormalizedVec(RandomSource random) {
		var vec = new Vec3(random.nextDouble() - 0.5, random.nextDouble() - 0.5, random.nextDouble() - 0.5).normalize();
		if (vec == Vec3.ZERO) {
			return new Vec3(0, 1, 0);
		}
		return vec;
	}

    public static Vec3 create3dFrom3i(Vec3i vec) {
        return new Vec3(vec.getX(), vec.getY(), vec.getZ());
    }

    public static Vec3 reflect(Vec3 in, Vec3 normal) {
        return in.subtract(normal.scale(2. * normal.dot(in)));
    }
}