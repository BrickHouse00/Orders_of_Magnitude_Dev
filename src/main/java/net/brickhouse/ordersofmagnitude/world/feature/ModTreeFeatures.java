package net.brickhouse.ordersofmagnitude.world.feature;

import net.brickhouse.ordersofmagnitude.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class ModTreeFeatures {

    public static final Holder<ConfiguredFeature<HugeMushroomFeatureConfiguration, ?>> HUGE_BLUE_MUSHROOM =
            FeatureUtils.register("huge_blue_mushroom", Feature.HUGE_RED_MUSHROOM,        //Use huge red mushroom for feature configuration
                    new HugeMushroomFeatureConfiguration(
                            BlockStateProvider.simple(ModBlocks.BLUE_MUSHROOM_BLOCK.get().defaultBlockState().setValue(HugeMushroomBlock.DOWN, Boolean.valueOf(false))),
                            BlockStateProvider.simple(Blocks.MUSHROOM_STEM.defaultBlockState().setValue(HugeMushroomBlock.UP, Boolean.valueOf(false)).setValue(HugeMushroomBlock.DOWN, Boolean.valueOf(false))),
                            2));

    public static final Holder<ConfiguredFeature<HugeMushroomFeatureConfiguration, ?>> HUGE_GREEN_MUSHROOM =
            FeatureUtils.register("huge_green_mushroom", Feature.HUGE_RED_MUSHROOM,        //Use huge red mushroom for feature configuration
                    new HugeMushroomFeatureConfiguration(
                            BlockStateProvider.simple(ModBlocks.GREEN_MUSHROOM_BLOCK.get().defaultBlockState().setValue(HugeMushroomBlock.DOWN, Boolean.valueOf(false))),
                            BlockStateProvider.simple(Blocks.MUSHROOM_STEM.defaultBlockState().setValue(HugeMushroomBlock.UP, Boolean.valueOf(false)).setValue(HugeMushroomBlock.DOWN, Boolean.valueOf(false))),
                            2));

    //new HugeMushroomFeatureConfiguration(
    // BlockStateProvider.simple(ModBlocks.GREY_MUSHROOM_BLOCK.defaultBlockState().setValue(HugeMushroomBlock.DOWN,Boolean.valueOf(false))),
    // BlockStateProvider.simple(Blocks.MUSHROOM_STEM.defaultBlockState().setValue(HugeMushroomBlock.UP, Boolean.valueOf(false)).setValue(HugeMushroomBlock.DOWN, Boolean.valueOf(false))),
    // 2)
}
