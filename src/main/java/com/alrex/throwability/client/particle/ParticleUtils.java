package com.alrex.throwability.client.particle;

import com.alrex.throwability.utils.VectorUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class ParticleUtils {
    public static void spawnScatteringParticle(ParticleOptions type, Level level, Vec3 pos, RandomSource random, double baseSpeed, double randomSpeed, int count) {
        spawnScatteringParticle(type, level, pos, random, baseSpeed, randomSpeed, count, (Vec3) null);
    }

    public static void spawnScatteringParticle(ParticleOptions type, Level level, Vec3 pos, RandomSource random, double baseSpeed, double randomSpeed, int count, @Nullable Direction direction) {
        spawnScatteringParticle(type, level, pos, random, baseSpeed, randomSpeed, count, direction != null ? VectorUtil.create3dFrom3i(direction.getNormal()) : null);
    }

    public static void spawnScatteringParticle(ParticleOptions type, Level level, Vec3 pos, RandomSource random, double baseSpeed, double randomSpeed, int count, @Nullable Vec3 normal) {
        for (int i = 0; i < count; i++) {
            Vec3 movement = VectorUtil.getRandomNormalizedVec(random).scale(baseSpeed + randomSpeed * random.nextDouble());
            if (normal != null && movement.dot(normal) > 0) {
                movement = VectorUtil.reflect(movement, normal);
            }
            level.addParticle(type, pos.x, pos.y, pos.z, movement.x, movement.y, movement.z);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static ParticleOptions getItemParticle(@Nullable ParticleOptions defaultParticle, ItemStack itemStack) {
        return (itemStack.isEmpty() && defaultParticle != null ? defaultParticle : new ItemParticleOption(ParticleTypes.ITEM, itemStack));
    }
}
