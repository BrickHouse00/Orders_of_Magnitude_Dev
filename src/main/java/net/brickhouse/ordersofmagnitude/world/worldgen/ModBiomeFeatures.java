package net.brickhouse.ordersofmagnitude.world.worldgen;

import net.brickhouse.ordersofmagnitude.world.feature.ModVegetationPlacements;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.List;
import java.util.Set;

public class ModBiomeFeatures {

    public static void addModMushrooms(final BiomeLoadingEvent event){
        ResourceKey<Biome> key = ResourceKey.create(Registry.BIOME_REGISTRY, event.getName());
        Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(key);

        List<Holder<PlacedFeature>> base = event.getGeneration().getFeatures(GenerationStep.Decoration.VEGETAL_DECORATION);
        if(isNormalPlacement(types)) {
            base.add(ModVegetationPlacements.BLUE_MUSHROOM_NORMAL);
            base.add(ModVegetationPlacements.GREEN_MUSHROOM_NORMAL);
        }
        if(isTaigaPlacement(types, key)){
            base.add(ModVegetationPlacements.BLUE_MUSHROOM_TAIGA);
            base.add(ModVegetationPlacements.GREEN_MUSHROOM_TAIGA);
        }
        if(isOldGrowthTaigaPlacement(types, key)){
            base.add(ModVegetationPlacements.BLUE_MUSHROOM_OLD_GROWTH);
            base.add(ModVegetationPlacements.GREEN_MUSHROOM_OLD_GROWTH);
        }
        if(isSwampPlacement(types, key)){
            base.add(ModVegetationPlacements.BLUE_MUSHROOM_SWAMP);
            base.add(ModVegetationPlacements.GREEN_MUSHROOM_SWAMP);
        }
        if(isNetherPlacement(types, key)){
            base.add(ModVegetationPlacements.BLUE_MUSHROOM_NETHER);
            base.add(ModVegetationPlacements.GREEN_MUSHROOM_NETHER);
        }
        if(isDarkForestPlacement(types, key)){
            //System.out.println("base before " + base);

            //base.add(ModVegetationPlacements.HUGE_GREY_MUSHROOM_DARK_FOREST);
            checkAndReplace(base);
            //System.out.println("base after " + base);
        }
        if(isMushroomBiomePlacement(types, key)){
            base.add(ModVegetationPlacements.HUGE_BLUE_MUSHROOM_FIELD);
            base.add(ModVegetationPlacements.HUGE_GREEN_MUSHROOM_FIELD);
            base.add(ModVegetationPlacements.BLUE_MUSHROOM_FIELD);
            base.add(ModVegetationPlacements.GREEN_MUSHROOM_FIELD);
        }
    }

    private static boolean isNormalPlacement(Set<BiomeDictionary.Type> pTypes){
        if(pTypes.contains(BiomeDictionary.Type.PLAINS)) {return true;}
        else if(pTypes.contains(BiomeDictionary.Type.FOREST)) {return true;}
        else if(pTypes.contains(BiomeDictionary.Type.SAVANNA)) {return true;}
        else if(pTypes.contains(BiomeDictionary.Type.SANDY)) {return true;}
        else if(pTypes.contains(BiomeDictionary.Type.JUNGLE)) {return true;}
        else if(pTypes.contains(BiomeDictionary.Type.SWAMP)) {return true;}
        else if(pTypes.contains(BiomeDictionary.Type.UNDERGROUND)) {return true;}
        return false;
    }

    private static boolean isTaigaPlacement(Set<BiomeDictionary.Type> pTypes, ResourceKey<Biome> pKey){
        if(pKey == Biomes.TAIGA){return true;}
        else if(pTypes.contains(BiomeDictionary.Type.CONIFEROUS) && pTypes.contains(BiomeDictionary.Type.COLD)) {return true;}
        return false;
    }

    private static boolean isOldGrowthTaigaPlacement(Set<BiomeDictionary.Type> pTypes, ResourceKey<Biome> pKey){
        if(pKey == Biomes.OLD_GROWTH_PINE_TAIGA || pKey == Biomes.OLD_GROWTH_SPRUCE_TAIGA){ return true;}
        else if(pTypes.contains(BiomeDictionary.Type.CONIFEROUS) && pTypes.contains(BiomeDictionary.Type.COLD)) {return true;}
        return false;
    }

    private static boolean isSwampPlacement(Set<BiomeDictionary.Type> pTypes, ResourceKey<Biome> pKey){
        if(pKey == Biomes.SWAMP) { return true;}
        else if(pTypes.contains(BiomeDictionary.Type.SWAMP)) {return true;}
        return false;
    }

    private static boolean isDarkForestPlacement(Set<BiomeDictionary.Type> pTypes, ResourceKey<Biome> pKey){
        if(pKey == Biomes.DARK_FOREST) { return true;}
        //else if(pTypes.contains(BiomeDictionary.Type.SPOOKY) && pTypes.contains(BiomeDictionary.Type.FOREST) && pTypes.contains(BiomeDictionary.Type.DENSE)) {return true;}
        return false;
    }

    private static boolean isNetherPlacement(Set<BiomeDictionary.Type> pTypes, ResourceKey<Biome> pKey){
        if(pKey == Biomes.BASALT_DELTAS || pKey == Biomes.NETHER_WASTES) { return true;}
        else if(pTypes.contains(BiomeDictionary.Type.NETHER) && !pTypes.contains(BiomeDictionary.Type.FOREST)) { return true;}
        return false;
    }

    private static boolean isMushroomBiomePlacement(Set<BiomeDictionary.Type> pTypes, ResourceKey<Biome> pKey){
        if(pKey == Biomes.MUSHROOM_FIELDS) {return  true;}
        else if(pTypes.contains((BiomeDictionary.Type.MUSHROOM))){ return true;}
        return false;
    }

    private static void checkAndReplace(List<Holder<PlacedFeature>> pList){
        /* Not the best way to handle this.  Would rather inject into VegetationFeatures.DARK_FOREST_VEGETATION's configured list.
        Problems: final variables.  Mixin unable to inject via surrogates as well.
        Is there a way to overwrite a placed feature?  The issue with dark forest is that it's so dense, additional trees/huge mushrooms can't spawn via normal methods
        Current method is to copy the vanilla configured feature list, add the new mushroom to it, set it to my vanilla duplicated placement, and then replace here*/
        for(int i = 0; i<pList.size(); i++){
            if(pList.get(i).is(ResourceLocation.of("dark_forest_vegetation", ':'))){ // == VegetationPlacements.DARK_FOREST_VEGETATION){
                pList.remove(i);
                pList.add(ModVegetationPlacements.DARK_FOREST_VEGETATION);
            }
        }
    }
}
