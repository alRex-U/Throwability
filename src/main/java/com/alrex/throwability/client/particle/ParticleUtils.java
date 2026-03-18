package com.alrex.throwability.client.particle;

import com.alrex.throwability.utils.VectorUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Random;

public class ParticleUtils {
    public static void spawnScatteringParticle(IParticleData type, World level, Vector3d pos, Random random, double baseSpeed, double randomSpeed, int count) {
        spawnScatteringParticle(type, level, pos, random, baseSpeed, randomSpeed, count, (Vector3d) null);
    }

    public static void spawnScatteringParticle(IParticleData type, World level, Vector3d pos, Random random, double baseSpeed, double randomSpeed, int count, @Nullable Direction direction) {
        spawnScatteringParticle(type, level, pos, random, baseSpeed, randomSpeed, count, direction != null ? VectorUtil.create3dFrom3i(direction.getNormal()) : null);
    }

    public static void spawnScatteringParticle(IParticleData type, World level, Vector3d pos, Random random, double baseSpeed, double randomSpeed, int count, @Nullable Vector3d normal) {
        for (int i = 0; i < count; i++) {
            Vector3d movement = VectorUtil.getRandomNormalizedVec(random).scale(baseSpeed + randomSpeed * random.nextDouble());
            if (normal != null && movement.dot(normal) > 0) {
                movement = VectorUtil.reflect(movement, normal);
            }
            level.addParticle(type, pos.x, pos.y, pos.z, movement.x, movement.y, movement.z);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static IParticleData getItemParticle(@Nullable IParticleData defaultParticle, ItemStack itemStack) {
        return (itemStack.isEmpty() && defaultParticle != null ? defaultParticle : new ItemParticleData(ParticleTypes.ITEM, itemStack));
    }
}
