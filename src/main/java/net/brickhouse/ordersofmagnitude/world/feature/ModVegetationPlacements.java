package net.brickhouse.ordersofmagnitude.world.feature;

import com.google.common.collect.ImmutableList;
import net.brickhouse.ordersofmagnitude.config.OMServerConfig;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.world.level.levelgen.placement.*;

import javax.annotation.Nullable;
import java.util.List;

public class ModVegetationPlacements{

    public static final Holder<PlacedFeature> BLUE_MUSHROOM_NETHER = PlacementUtils.register("blue_mushroom_nether",
            ModVegetationFeatures.PATCH_BLUE_MUSHROOM,
            RarityFilter.onAverageOnceEvery(OMServerConfig.OOM_MUSHROOM_NETHER_PLACEMENT_RARITY.get()), InSquarePlacement.spread(),
            PlacementUtils.FULL_RANGE, BiomeFilter.biome());
    public static final Holder<PlacedFeature> BLUE_MUSHROOM_NORMAL = PlacementUtils.register("blue_mushroom_normal",
            ModVegetationFeatures.PATCH_BLUE_MUSHROOM,
            getMushroomPlacement(OMServerConfig.OOM_MUSHROOM_NORMAL_PLACEMENT_RARITY.get(), (PlacementModifier)null));
    public static final Holder<PlacedFeature> BLUE_MUSHROOM_TAIGA = PlacementUtils.register("blue_mushroom_taiga",
            ModVegetationFeatures.PATCH_BLUE_MUSHROOM,
            getMushroomPlacement(OMServerConfig.OOM_MUSHROOM_TAIGA_PLACEMENT_RARITY.get(), (PlacementModifier)null));
    public static final Holder<PlacedFeature> BLUE_MUSHROOM_OLD_GROWTH = PlacementUtils.register("blue_mushroom_old_growth",
            ModVegetationFeatures.PATCH_BLUE_MUSHROOM,
            getMushroomPlacement(OMServerConfig.OOM_MUSHROOM_OLD_GROWTH_PLACEMENT_RARITY.get(), CountPlacement.of(OMServerConfig.OOM_MUSHROOM_OLD_GROWTH_PLACEMENT_COUNT.get())));
    public static final Holder<PlacedFeature> BLUE_MUSHROOM_SWAMP = PlacementUtils.register("blue_mushroom_swamp",
            ModVegetationFeatures.PATCH_BLUE_MUSHROOM,
            getMushroomPlacement(OMServerConfig.OOM_MUSHROOM_SWAMP_PLACEMENT_RARITY.get(), CountPlacement.of(OMServerConfig.OOM_MUSHROOM_SWAMP_PLACEMENT_COUNT.get())));
    public static final Holder<PlacedFeature> HUGE_BLUE_MUSHROOM_DARK_FOREST = PlacementUtils.register("huge_blue_mushroom_dark_forest",
            ModTreeFeatures.HUGE_BLUE_MUSHROOM,
            //ModVegetationFeatures.DARK_FOREST_HUGE_MUSHROOM,
            VegetationPlacements.treePlacement(
                    RarityFilter.onAverageOnceEvery(6)));  //OMServerConfig.HUGE_GREY_MUSHROOM_DARK_FOREST_PLACEMENT_RARITY.get())));
    public static final Holder<PlacedFeature> HUGE_BLUE_MUSHROOM_FIELD = PlacementUtils.register("huge_blue_mushroom_field",
            ModTreeFeatures.HUGE_BLUE_MUSHROOM,
            VegetationPlacements.treePlacement(
                    //PlacementUtils.countExtra(1,0.05f,0)));      // per chunk, percent chance spawn extra, quantity extra
                    RarityFilter.onAverageOnceEvery(OMServerConfig.HUGE_OOM_MUSHROOM_FIELD_PLACEMENT_RARITY.get())));
    public static final Holder<PlacedFeature> BLUE_MUSHROOM_FIELD = PlacementUtils.register("blue_mushroom_field",
            ModVegetationFeatures.PATCH_BLUE_MUSHROOM,
            getMushroomPlacement(OMServerConfig.OOM_MUSHROOM_FIELD_PLACEMENT_RARITY.get(), (PlacementModifier)null));

    public static final Holder<PlacedFeature> GREEN_MUSHROOM_NETHER = PlacementUtils.register("green_mushroom_nether",
            ModVegetationFeatures.PATCH_GREEN_MUSHROOM,
            RarityFilter.onAverageOnceEvery(OMServerConfig.OOM_MUSHROOM_NETHER_PLACEMENT_RARITY.get()), InSquarePlacement.spread(),
            PlacementUtils.FULL_RANGE, BiomeFilter.biome());
    public static final Holder<PlacedFeature> GREEN_MUSHROOM_NORMAL = PlacementUtils.register("green_mushroom_normal",
            ModVegetationFeatures.PATCH_GREEN_MUSHROOM,
            getMushroomPlacement(OMServerConfig.OOM_MUSHROOM_NORMAL_PLACEMENT_RARITY.get(), (PlacementModifier)null));
    public static final Holder<PlacedFeature> GREEN_MUSHROOM_TAIGA = PlacementUtils.register("green_mushroom_taiga",
            ModVegetationFeatures.PATCH_GREEN_MUSHROOM,
            getMushroomPlacement(OMServerConfig.OOM_MUSHROOM_TAIGA_PLACEMENT_RARITY.get(), (PlacementModifier)null));
    public static final Holder<PlacedFeature> GREEN_MUSHROOM_OLD_GROWTH = PlacementUtils.register("green_mushroom_old_growth",
            ModVegetationFeatures.PATCH_GREEN_MUSHROOM,
            getMushroomPlacement(OMServerConfig.OOM_MUSHROOM_OLD_GROWTH_PLACEMENT_RARITY.get(), CountPlacement.of(OMServerConfig.OOM_MUSHROOM_OLD_GROWTH_PLACEMENT_COUNT.get())));
    public static final Holder<PlacedFeature> GREEN_MUSHROOM_SWAMP = PlacementUtils.register("green_mushroom_swamp",
            ModVegetationFeatures.PATCH_GREEN_MUSHROOM,
            getMushroomPlacement(OMServerConfig.OOM_MUSHROOM_SWAMP_PLACEMENT_RARITY.get(), CountPlacement.of(OMServerConfig.OOM_MUSHROOM_SWAMP_PLACEMENT_COUNT.get())));
    public static final Holder<PlacedFeature> HUGE_GREEN_MUSHROOM_DARK_FOREST = PlacementUtils.register("huge_green_mushroom_dark_forest",
            ModTreeFeatures.HUGE_GREEN_MUSHROOM,
            //ModVegetationFeatures.DARK_FOREST_HUGE_MUSHROOM,
            VegetationPlacements.treePlacement(
                    RarityFilter.onAverageOnceEvery(6)));  //OMServerConfig.HUGE_GREY_MUSHROOM_DARK_FOREST_PLACEMENT_RARITY.get())));

    public static final Holder<PlacedFeature> HUGE_GREEN_MUSHROOM_FIELD = PlacementUtils.register("huge_green_mushroom_field",
            ModTreeFeatures.HUGE_GREEN_MUSHROOM,
            VegetationPlacements.treePlacement(
                    //PlacementUtils.countExtra(1,0.05f,0)));      // per chunk, percent chance spawn extra, quantity extra
                    RarityFilter.onAverageOnceEvery(OMServerConfig.HUGE_OOM_MUSHROOM_FIELD_PLACEMENT_RARITY.get())));
    public static final Holder<PlacedFeature> GREEN_MUSHROOM_FIELD = PlacementUtils.register("green_mushroom_field",
            ModVegetationFeatures.PATCH_GREEN_MUSHROOM,
            getMushroomPlacement(OMServerConfig.OOM_MUSHROOM_FIELD_PLACEMENT_RARITY.get(), (PlacementModifier)null));
    public static final Holder<PlacedFeature> DARK_FOREST_VEGETATION = PlacementUtils.register("mod_dark_forest_vegetation",
            ModVegetationFeatures.DARK_FOREST_HUGE_MUSHROOM,
            CountPlacement.of(16), InSquarePlacement.spread(), VegetationPlacements.TREE_THRESHOLD, PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome());
    private static List<PlacementModifier> getMushroomPlacement(int pRarityFilter, @Nullable PlacementModifier pPlacement) {
        ImmutableList.Builder<PlacementModifier> builder = ImmutableList.builder();
        if (pPlacement != null) {
            builder.add(pPlacement);
        }

        if (pRarityFilter != 0) {
            builder.add(RarityFilter.onAverageOnceEvery(pRarityFilter));
        }

        builder.add(InSquarePlacement.spread());
        builder.add(PlacementUtils.HEIGHTMAP);
        builder.add(BiomeFilter.biome());
        return builder.build();
    }

}
