package net.brickhouse.ordersofmagnitude.world.feature;

import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;

public class ModFeature <FC extends FeatureConfiguration> extends net.minecraftforge.registries.ForgeRegistryEntry<Feature<?>>{

    //TODO change this to work off of the Forge registries.  This cannot be registered to Vanilla.  Crashes on startup
    /*public static final Feature<HugeMushroomFeatureConfiguration> HUGE_GREY_MUSHROOM = register("huge_grey_mushroom", new HugeGreyMushroomFeature(HugeMushroomFeatureConfiguration.CODEC));

    private static <C extends FeatureConfiguration, F extends Feature<C>> F register(String pKey, F pValue) {
        return Registry.register(Registry.FEATURE, pKey, pValue);
    }*/
}
