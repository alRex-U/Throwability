package com.alrex.throwability.client.animation;

import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class AnimationHost {
    private final List<IAnimation> animations;
    private final List<IAnimation> singleAnimations;
    @Nullable
    private IAnimationSession session = null;
    public AnimationHost(List<IAnimation> animations, List<IAnimation> singleAnimations) {
        this.animations = animations;
        this.singleAnimations = singleAnimations;
    }

    public void startAnimationSection(PlayerEntity player) {
        session = null;
        for (IAnimation animation : singleAnimations) {
            if (animation.isActive(player)) {
                session = new SingleAnimationSession(animation);
            }
        }
        if (session == null) {
            session = new MultiAnimationSession(animations.stream().filter(anim -> anim.isActive(player)).collect(Collectors.toList()));
        }
        session.onStartAnimation(player);
    }

    public void finishAnimationSection(PlayerEntity player) {
        if (session != null) {
            session.onFinishAnimation(player);
        }
        session = null;
    }

    public boolean shouldStopVanillaModelAnimation(PlayerEntity player) {
        if (session == null) return false;
        return session.stopVanillaModelAnimation(player);
    }

    public boolean shouldStopVanillaRotation(PlayerEntity player) {
        if (session == null) return false;
        return session.stopVanillaRotation(player);
    }

    @Nullable
    public ModelRotation getRotation(PlayerEntity player, float partialTick) {
        if (session == null) return null;
        return session.getRotation(player, partialTick);
    }

    public void animateModel(PlayerModelAnimator playerModelAnimator) {
        if (session == null) return;
        session.animateModel(playerModelAnimator);
    }

    private interface IAnimationSession {
        void animateModel(PlayerModelAnimator animator);

        @Nullable
        ModelRotation getRotation(PlayerEntity player, float partialTick);

        boolean stopVanillaModelAnimation(PlayerEntity player);

        boolean stopVanillaRotation(PlayerEntity player);

        void onStartAnimation(PlayerEntity player);

        void onFinishAnimation(PlayerEntity player);
    }

    private static final class SingleAnimationSession implements IAnimationSession {
        private final IAnimation animation;

        public SingleAnimationSession(IAnimation animation) {
            this.animation = animation;
        }

        @Override
        public void animateModel(PlayerModelAnimator animator) {
            animation.animateModel(animator);
        }

        @Nullable
        @Override
        public ModelRotation getRotation(PlayerEntity player, float partialTick) {
            return animation.getModelRotation(player, null, partialTick);
        }

        @Override
        public boolean stopVanillaModelAnimation(PlayerEntity player) {
            return true;
        }

        @Override
        public boolean stopVanillaRotation(PlayerEntity player) {
            return false;
        }

        @Override
        public void onStartAnimation(PlayerEntity player) {
            this.animation.onStartAnimation(player);
        }

        @Override
        public void onFinishAnimation(PlayerEntity player) {
            this.animation.onFinishAnimation(player);
        }
    }

    private static final class MultiAnimationSession implements IAnimationSession {
        private final List<IAnimation> animations;

        public MultiAnimationSession(List<IAnimation> animations) {
            this.animations = animations;
        }

        @Override
        public void animateModel(PlayerModelAnimator animator) {
            for (IAnimation animation : animations) {
                animation.animateModel(animator);
            }
        }

        @Nullable
        @Override
        public ModelRotation getRotation(PlayerEntity player, float partialTick) {
            ModelRotation rot = null;
            for (IAnimation animation : animations) {
                rot = animation.getModelRotation(player, rot, partialTick);
            }
            return rot;
        }

        @Override
        public boolean stopVanillaModelAnimation(PlayerEntity player) {
            return false;
        }

        @Override
        public boolean stopVanillaRotation(PlayerEntity player) {
            return false;
        }

        @Override
        public void onStartAnimation(PlayerEntity player) {
            for (IAnimation animation : animations) {
                animation.onStartAnimation(player);
            }
        }

        @Override
        public void onFinishAnimation(PlayerEntity player) {
            for (IAnimation animation : animations) {
                animation.onFinishAnimation(player);
            }
        }
    }
}
