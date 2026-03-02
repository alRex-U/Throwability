package com.alrex.throwability.utils;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

import java.util.Random;

public class VectorUtil {
	public static double toYawDegree(Vector3d vec) {
		return (Math.atan2(vec.z(), vec.x()) * 180.0 / Math.PI - 90);
	}

	public static double toPitchDegree(Vector3d vec) {
		return -(Math.atan2(vec.y(), Math.sqrt(vec.x() * vec.x() + vec.z() * vec.z())) * 180.0 / Math.PI);
	}

	public static Vector3d fromYawDegree(double degree) {
		return new Vector3d(-Math.sin(Math.toRadians(degree)), 0, Math.cos(Math.toRadians(degree)));
	}

	public static Vector3d getRandomNormalizedVec(Random random) {
		Vector3d vec = new Vector3d(random.nextDouble() - 0.5, random.nextDouble() - 0.5, random.nextDouble() - 0.5).normalize();
		if (vec == Vector3d.ZERO) {
			return new Vector3d(0, 1, 0);
		}
		return vec;
	}

    public static Vector3d create3dFrom3i(Vector3i vec) {
        return new Vector3d(vec.getX(), vec.getY(), vec.getZ());
    }

    public static Vector3d reflect(Vector3d in, Vector3d normal) {
        return in.subtract(normal.scale(2. * normal.dot(in)));
    }
}