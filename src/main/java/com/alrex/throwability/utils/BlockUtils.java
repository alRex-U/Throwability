package com.alrex.throwability.utils;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;

public class BlockUtils {
    public static void applyTagToTileEntity(TileEntity tileEntity, CompoundNBT tileEntityData, BlockState blockState) {
        CompoundNBT compoundnbt = tileEntity.save(new CompoundNBT());
        for (String key : tileEntityData.getAllKeys()) {
            INBT inbt = tileEntityData.get(key);
            if (!"x".equals(key) && !"y".equals(key) && !"z".equals(key)) {
                if (inbt != null) {
                    compoundnbt.put(key, inbt.copy());
                }
            }
        }
        tileEntity.load(blockState, compoundnbt);
        tileEntity.setChanged();
    }
}
