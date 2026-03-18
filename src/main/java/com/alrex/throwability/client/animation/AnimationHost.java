package com.alrex.throwability.client.animation;


import net.minecraft.world.entity.player.Player;

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

    public void startAnimationSection(Player player) {
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

    public void finishAnimationSection(Player player) {
        if (session != null) {
            session.onFinishAnimation(player);
        }
        session = null;
    }

    public boolean shouldStopVanillaModelAnimation(Player player) {
        if (session == null) return false;
        return session.stopVanillaModelAnimation(player);
    }

    public boolean shouldStopVanillaRotation(Player player) {
        if (session == null) return false;
        return session.stopVanillaRotation(player);
    }

    @Nullable
    public PlayerRotation getRotation(Player player, float partialTick) {
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
        PlayerRotation getRotation(Player player, float partialTick);

        boolean stopVanillaModelAnimation(Player player);

        boolean stopVanillaRotation(Player player);

        void onStartAnimation(Player player);

        void onFinishAnimation(Player player);
    }

    private record SingleAnimationSession(IAnimation animation) implements IAnimationSession {

        @Override
        public void animateModel(PlayerModelAnimator animator) {
            animation.animateModel(animator);
        }

        @Nullable
        @Override
        public PlayerRotation getRotation(Player player, float partialTick) {
            return animation.getModelRotation(player, null, partialTick);
        }

        @Override
        public boolean stopVanillaModelAnimation(Player player) {
            return true;
        }

        @Override
        public boolean stopVanillaRotation(Player player) {
            return false;
        }

        @Override
        public void onStartAnimation(Player player) {
            this.animation.onStartAnimation(player);
        }

        @Override
        public void onFinishAnimation(Player player) {
            this.animation.onFinishAnimation(player);
        }
    }

    private record MultiAnimationSession(List<IAnimation> animations) implements IAnimationSession {

        @Override
        public void animateModel(PlayerModelAnimator animator) {
            for (IAnimation animation : animations) {
                animation.animateModel(animator);
            }
        }

        @Nullable
        @Override
        public PlayerRotation getRotation(Player player, float partialTick) {
            PlayerRotation rot = null;
            for (IAnimation animation : animations) {
                rot = animation.getModelRotation(player, rot, partialTick);
            }
            return rot;
        }

        @Override
        public boolean stopVanillaModelAnimation(Player player) {
            return false;
        }

        @Override
        public boolean stopVanillaRotation(Player player) {
            return false;
        }

        @Override
        public void onStartAnimation(Player player) {
            for (IAnimation animation : animations) {
                animation.onStartAnimation(player);
            }
        }

        @Override
        public void onFinishAnimation(Player player) {
            for (IAnimation animation : animations) {
                animation.onFinishAnimation(player);
            }
        }
    }
}
