package com.alrex.throwability.mixin.common;

import com.alrex.throwability.common.thrown.ICollidedDirectionProvider;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.INameable;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.extensions.IForgeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Entity.class)
public abstract class EntityMixin extends CapabilityProvider<Entity> implements INameable, ICommandSource, IForgeEntity, ICollidedDirectionProvider {
    @Shadow
    public boolean horizontalCollision;
    @Unique
    private boolean throwability$oldHorizontalCollision = false;
    @Unique
    @Nullable
    private Direction throwability$collidedDirection = null;

    protected EntityMixin(Class<Entity> baseClass) {
        super(baseClass);
    }

    @Shadow
    public abstract Vector3d getDeltaMovement();

    @Inject(method = "move", at = @At(value = "HEAD"))
    public void onMoveHead(MoverType moverType, Vector3d movement, CallbackInfo ci) {
        throwability$oldHorizontalCollision = horizontalCollision;
    }

    @Inject(method = "move", at = @At(value = "RETURN"))
    public void onMoveReturn(MoverType moverType, Vector3d movement, CallbackInfo ci) {
        if (!((Object) this instanceof FallingBlockEntity)) return;
        if (!throwability$oldHorizontalCollision && horizontalCollision) {
            Vector3d deltaMovement = getDeltaMovement();
            Direction direction;
            if (Math.abs(movement.x()) > 1e-4 && Math.abs(deltaMovement.x()) <= 1e-4) {
                direction = movement.x() > 0 ? Direction.EAST : Direction.WEST;
            } else if (Math.abs(movement.z()) > 1e-4 && Math.abs(deltaMovement.z()) <= 1e-4) {
                direction = movement.z() > 0 ? Direction.SOUTH : Direction.NORTH;
            } else {
                return;
            }
            throwability$collidedDirection = direction;
        }
    }

    @Override
    public Direction getCollidedDirection() {
        return throwability$collidedDirection;
    }
}
