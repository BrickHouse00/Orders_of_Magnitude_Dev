package net.brickhouse.ordersofmagnitude.world.feature;

import net.brickhouse.ordersofmagnitude.block.ModBlocks;
import net.brickhouse.ordersofmagnitude.config.OMServerConfig;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.TreePlacements;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.ArrayList;
import java.util.List;

public class ModVegetationFeatures {

    public static final Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> PATCH_BLUE_MUSHROOM =
            FeatureUtils.register("blue_mushroom", Feature.RANDOM_PATCH,
                    FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                            new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.BLUE_MUSHROOM.get()))));

    public static final Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> PATCH_GREEN_MUSHROOM =
            FeatureUtils.register("green_mushroom", Feature.RANDOM_PATCH,
                    FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                            new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.GREEN_MUSHROOM.get()))));

    public static final Holder<ConfiguredFeature<RandomFeatureConfiguration, ?>> DARK_FOREST_HUGE_MUSHROOM =
            FeatureUtils.register("huge_oom_mushroom_dark_forest", Feature.RANDOM_SELECTOR,
                    new RandomFeatureConfiguration(/*List.of(
                            new WeightedPlacedFeature(PlacementUtils.inlinePlaced(TreeFeatures.HUGE_BROWN_MUSHROOM), 0.025F),
                            new WeightedPlacedFeature(PlacementUtils.inlinePlaced(TreeFeatures.HUGE_RED_MUSHROOM), 0.05F),
                            new WeightedPlacedFeature(PlacementUtils.inlinePlaced(ModTreeFeatures.HUGE_GREY_MUSHROOM), 0.025F),
                            new WeightedPlacedFeature(TreePlacements.DARK_OAK_CHECKED, 0.6666667F),
                            new WeightedPlacedFeature(TreePlacements.BIRCH_CHECKED, 0.2F),
                            new WeightedPlacedFeature(TreePlacements.FANCY_OAK_CHECKED, 0.1F)),*/
                            addWeightedFeatureToListCopy(VegetationFeatures.DARK_FOREST_VEGETATION, ModTreeFeatures.HUGE_BLUE_MUSHROOM,
                                    ModTreeFeatures.HUGE_GREEN_MUSHROOM,
                                    OMServerConfig.HUGE_OOM_MUSHROOM_DARK_FOREST_PLACEMENT_WEIGHT.get()),
                            TreePlacements.OAK_CHECKED));

    public static List<WeightedPlacedFeature> addWeightedFeatureToListCopy(Holder<ConfiguredFeature<RandomFeatureConfiguration, ?>> pHolder, Holder<ConfiguredFeature<HugeMushroomFeatureConfiguration, ?>> pFeature1, Holder<ConfiguredFeature<HugeMushroomFeatureConfiguration, ?>> pFeature2, float pWeight){
        List<WeightedPlacedFeature> newFeatureList = new ArrayList<>(pHolder.value().config().features);
        newFeatureList.add(new WeightedPlacedFeature(PlacementUtils.inlinePlaced(pFeature1), pWeight));
        newFeatureList.add(new WeightedPlacedFeature(PlacementUtils.inlinePlaced(pFeature2), pWeight));
        return newFeatureList;
    }

/*
    public static final Holder<ConfiguredFeature<HugeMushroomFeatureConfiguration, ?>> HUGE_GREY_MUSHROOM =
            FeatureUtils.register("huge_grey_mushroom", Feature.HUGE_BROWN_MUSHROOM,        //Use huge brown mushroom for settings
                    new HugeMushroomFeatureConfiguration(
                            BlockStateProvider.simple(ModBlocks.GREY_MUSHROOM_BLOCK.get().defaultBlockState().setValue(HugeMushroomBlock.UP,
                                Boolean.valueOf(true)).setValue(HugeMushroomBlock.DOWN, Boolean.valueOf(false))),
                            BlockStateProvider.simple(Blocks.MUSHROOM_STEM.defaultBlockState().setValue(HugeMushroomBlock.UP,
                                Boolean.valueOf(false)).setValue(HugeMushroomBlock.DOWN, Boolean.valueOf(false))),
                            3));
*/

}
