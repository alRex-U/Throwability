package com.alrex.throwability.utils;

import com.alrex.throwability.Throwability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockUtils {
    public static void applyTagToTileEntity(BlockEntity tileEntity, CompoundTag tileEntityData, BlockState blockState) {
        var compoundnbt = tileEntity.saveWithoutMetadata();
        for (String key : tileEntityData.getAllKeys()) {
            compoundnbt.put(key, tileEntityData.get(key).copy());
        }
        try {
            tileEntity.load(compoundnbt);
        } catch (Exception e) {
            Throwability.LOGGER.error("Failed to load block entity from falling block", e);
        }
        tileEntity.setChanged();
    }
}
