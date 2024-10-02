package com.craftaro.epicfarming.utils;

import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;

public class CropUtilsModern {
    static boolean isFullyGrown(Block crop) {
        BlockData data = crop.getBlockData();
        if (data instanceof Ageable) {
            return ((Ageable) data).getAge() == ((Ageable) data).getMaximumAge();
        }
        return false;
    }

    static int getGrowthStage(Block block) {
        if (!(block.getBlockData() instanceof Ageable)) {
            return 0;
        }
        return ((Ageable) block.getBlockData()).getAge();
    }

    static void setGrowthStage(Block block, int stage) {
        if (!(block.getBlockData() instanceof Ageable)) {
            return;
        }

        Ageable blockData = (Ageable) block.getBlockData();
        int max = blockData.getMaximumAge();
        if (stage > max) {
            return;
        }

        blockData.setAge(stage);
        block.setBlockData(blockData);
    }
}
