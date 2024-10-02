package com.craftaro.epicfarming.utils;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.epicfarming.EpicFarming;
import com.craftaro.third_party.com.cryptomorin.xseries.XBlock;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import org.bukkit.block.Block;

import java.util.Optional;
import java.util.logging.Level;

public class CropUtils {
    private static final boolean USE_LEGACY_IMPLEMENTATION;

    static {
        boolean useLegacy = false;
        try {
            Class.forName("org.bukkit.block.data.Ageable");
        } catch (ClassNotFoundException ignore) {
            useLegacy = true;
        }
        USE_LEGACY_IMPLEMENTATION = useLegacy;
    }

    public static boolean isFullyGrown(Block crop) {
        if (crop == null) {
            return false;
        }

        if (!USE_LEGACY_IMPLEMENTATION) {
            return CropUtilsModern.isFullyGrown(crop);
        }

        Optional<XMaterial> mat = CompatibleMaterial.getMaterial(crop.getType());
        if (!mat.isPresent() || !XBlock.isCrop(mat.get())) {
            return false;
        }

        int maxAge = (mat.get() == XMaterial.BEETROOTS || mat.get() == XMaterial.NETHER_WART) ? 3 : 7;
        return crop.getData() >= maxAge;
    }

    public static void resetGrowthStage(Block crop) {
        if (crop == null) {
            return;
        }
        setGrowthStage(crop, 0);
    }

    public static void incrementGrowthStage(Block crop) {
        if (crop == null) {
            return;
        }

        if (!USE_LEGACY_IMPLEMENTATION) {
            setGrowthStage(crop, getGrowthStage(crop) + 1);
            return;
        }

        Optional<XMaterial> mat = CompatibleMaterial.getMaterial(crop.getType());
        if (mat.isPresent() && XBlock.isCrop(mat.get()) && crop.getData() < (mat.get() == XMaterial.BEETROOTS || mat.get() == XMaterial.NETHER_WART ? 3 : 7)) {
            try {
                setGrowthStage(crop, getGrowthStage(crop) + 1);
            } catch (Exception ex) {
                EpicFarming.getInstance().getLogger().log(Level.SEVERE, "Unexpected method error", ex);
            }
        }
    }

    private static int getGrowthStage(Block block) {
        if (!USE_LEGACY_IMPLEMENTATION) {
            return CropUtilsModern.getGrowthStage(block);
        }
        return block.getData();
    }

    private static void setGrowthStage(Block block, int stage) {
        if (stage < 0) {
            return;
        }

        if (!USE_LEGACY_IMPLEMENTATION) {
            CropUtilsModern.setGrowthStage(block, stage);
            return;
        }

        Optional<XMaterial> mat = CompatibleMaterial.getMaterial(block.getType());
        if (!mat.isPresent() || !XBlock.isCrop(mat.get())) {
            return;
        }

        try {
            Block.class.getDeclaredMethod("setData", byte.class).invoke(block, (byte) stage);
        } catch (Exception ex) {
            EpicFarming.getInstance().getLogger().log(Level.SEVERE, "Unexpected method error", ex);
        }
    }
}
