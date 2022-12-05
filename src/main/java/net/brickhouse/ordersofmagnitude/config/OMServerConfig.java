package net.brickhouse.ordersofmagnitude.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class OMServerConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> OOM_MUSHROOM_NETHER_PLACEMENT_RARITY;
    public static final ForgeConfigSpec.ConfigValue<Integer> OOM_MUSHROOM_NORMAL_PLACEMENT_RARITY;
    public static final ForgeConfigSpec.ConfigValue<Integer> OOM_MUSHROOM_TAIGA_PLACEMENT_RARITY;
    public static final ForgeConfigSpec.ConfigValue<Integer> OOM_MUSHROOM_OLD_GROWTH_PLACEMENT_RARITY;
    public static final ForgeConfigSpec.ConfigValue<Integer> OOM_MUSHROOM_OLD_GROWTH_PLACEMENT_COUNT;
    public static final ForgeConfigSpec.ConfigValue<Integer> OOM_MUSHROOM_SWAMP_PLACEMENT_RARITY;
    public static final ForgeConfigSpec.ConfigValue<Integer> OOM_MUSHROOM_SWAMP_PLACEMENT_COUNT;
    public static final ForgeConfigSpec.ConfigValue<Integer> OOM_MUSHROOM_FIELD_PLACEMENT_RARITY;
    public static final ForgeConfigSpec.ConfigValue<Integer> HUGE_OOM_MUSHROOM_FIELD_PLACEMENT_RARITY;
    public static final ForgeConfigSpec.ConfigValue<Float> HUGE_OOM_MUSHROOM_DARK_FOREST_PLACEMENT_WEIGHT;
    public static final ForgeConfigSpec.ConfigValue<Integer> MATTER_INFUSER_ENERGY_CAPACITY;
    public static final ForgeConfigSpec.ConfigValue<Integer> MATTER_INFUSER_ENERGY_TRANSFER_RATE;
    public static final ForgeConfigSpec.ConfigValue<Integer> MATTER_INFUSER_FLUID_TANK_CAPACITY;
    public static final ForgeConfigSpec.ConfigValue<Integer> MATTER_MIXER_ENERGY_CAPACITY;
    public static final ForgeConfigSpec.ConfigValue<Integer> MATTER_MIXER_ENERGY_TRANSFER_RATE;
    public static final ForgeConfigSpec.ConfigValue<Integer> MATTER_MIXER_FLUID_TANK_CAPACITY;
    public static final ForgeConfigSpec.ConfigValue<Integer> MATTER_INFUSER_ENERGY_PER_TICK;
    public static final ForgeConfigSpec.ConfigValue<Integer> MATTER_INFUSER_FLUID_PER_TICK;
    public static final ForgeConfigSpec.ConfigValue<Integer> MATTER_MIXER_ENERGY_PER_TICK;
    public static final ForgeConfigSpec.ConfigValue<Integer> MATTER_MIXER_FLUID_PER_TICK;
    public static final ForgeConfigSpec.ConfigValue<Double> SMALL_JUMP_MODIFIER;
    public static final ForgeConfigSpec.ConfigValue<Double> LARGE_JUMP_MODIFIER;
    public static final ForgeConfigSpec.ConfigValue<Double> MAXIMUM_SIZE;
    public static final ForgeConfigSpec.ConfigValue<Double> MINIMUM_SIZE;
    public static final ForgeConfigSpec.ConfigValue<Double> MAXIMUM_NETHER_SIZE;
    public static final ForgeConfigSpec.ConfigValue<Double> MINIMUM_NETHER_SIZE;
    public static final ForgeConfigSpec.ConfigValue<Double> MAXIMUM_IRON_SIZE;
    public static final ForgeConfigSpec.ConfigValue<Double> MINIMUM_IRON_SIZE;
    public static final ForgeConfigSpec.ConfigValue<Integer> PLAYER_STOMP_SETTING;
    public static final ForgeConfigSpec.ConfigValue<Integer> ITEM_MAXIMUM_ENERGY;
    public static final ForgeConfigSpec.ConfigValue<Integer> ITEM_ENERGY_EXTRACT;
    public static final ForgeConfigSpec.ConfigValue<Integer> ITEM_ENERGY_RECEIVE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ALLOW_SCALED_RIDING;

    static{
        BUILDER.push("Orders of Magnitude Server Configuration");
        BUILDER.push("Size Change Settings");

        MAXIMUM_SIZE = BUILDER.comment("Sets the maximum size allowed.  Be careful that sizes too large can cause extreme lag.  This is also the maximum size allowed by the Advanced Matter Infuser.")
                .define("Maximum Size", 1500D);

        MINIMUM_SIZE = BUILDER.comment("Sets the minimum size allowed.  Be careful that sizes too small can cause players to become stuck and create visual glitches on the client.    This is also the minimum size allowed by the Advanced Matter Infuser.")
                .define("Minimum Size", 0.000001D);

        MAXIMUM_NETHER_SIZE = BUILDER.comment("Sets the maximum size allowed by the Upgraded Matter Infuser.")
                .define("Maximum Upgraded Size", 10.0D);

        MINIMUM_NETHER_SIZE = BUILDER.comment("Sets the minimum size allowed by the Upgraded Matter Infuser.")
                .define("Minimum Upgraded  Size", 0.05D);

        MAXIMUM_IRON_SIZE = BUILDER.comment("Sets the maximum size allowed by the Basic Matter Infuser.")
                .define("Maximum Basic Size", 4.0D);

        MINIMUM_IRON_SIZE = BUILDER.comment("Sets the minimum size allowed by the Basic Matter Infuser.")
                .define("Minimum Basic Size", 0.25D);

        SMALL_JUMP_MODIFIER = BUILDER.comment("Set the damper that affects player and mob jump heights when shrunk.  Larger numbers will make jumping more difficult.")
                .define("Small Jump Modifier", 0.1D);

        LARGE_JUMP_MODIFIER = BUILDER.comment("Set the damper that affects player and mob jump heights when enlarged.  Smaller numbers will make jumping more difficult.")
                .define("Large Jump Modifier", 0.015D);

        PLAYER_STOMP_SETTING = BUILDER.comment("Determines if the player takes stomp damage from entities that are larger.  0=No Damage, 1=Some Damage, 2=Death")
                .define("Player Stomp Setting", 1);

        ALLOW_SCALED_RIDING = BUILDER.comment("Allow scaled entities to ride vehicles and rideable animals.")
                .define("Allow Scaled Riding", true);

        BUILDER.push("Equipment Settings");
        ITEM_MAXIMUM_ENERGY = BUILDER.comment("Set the energy capacity of items.")
                .define("item maximum energy", 24000);
        ITEM_ENERGY_EXTRACT = BUILDER.comment("Set the amount of energy consumed by items.")
                .define("item energy extract", 600);
        ITEM_ENERGY_RECEIVE = BUILDER.comment("Set the amount of energy refilled per tick for items.")
                .define("item energy receive", 400);
        MATTER_INFUSER_ENERGY_CAPACITY = BUILDER.comment("Sets the energy capacity of the Matter Infuser.")
                .define("Matter Infuser Energy Capacity", 16000);
        MATTER_INFUSER_ENERGY_TRANSFER_RATE = BUILDER.comment("Sets the max energy transfer rate of the Matter Infuser.")
                .define("Matter Infuser Energy Transfer Rate", 110);
        MATTER_INFUSER_ENERGY_PER_TICK = BUILDER.comment("Sets the energy used per tick for the Matter Infuser.")
                .define("Matter Infuser Energy Per Tick", 32);
        MATTER_INFUSER_FLUID_TANK_CAPACITY = BUILDER.comment("Sets the fluid tank capacity of the Matter Infuser.")
                .define("Matter Infuser Fluid Tank Capacity", 8000);
        MATTER_INFUSER_FLUID_PER_TICK = BUILDER.comment("Sets the fluid used per tick for the Matter Infuser.")
                .define("Matter Infuser Fluid Per Tick", 10);
        MATTER_MIXER_ENERGY_CAPACITY = BUILDER.comment("Sets the energy capacity of the Matter Mixer.")
                .define("Matter Mixer Energy Capacity", 16000);
        MATTER_MIXER_ENERGY_TRANSFER_RATE = BUILDER.comment("Sets the max energy transfer rate of the Matter Mixer.")
                .define("Matter Mixer Energy Transfer Rate", 110);
        MATTER_MIXER_ENERGY_PER_TICK = BUILDER.comment("Sets the energy used per tick for the Matter Mixer.")
                .define("Matter Mixer Energy Per Tick", 32);
        MATTER_MIXER_FLUID_TANK_CAPACITY = BUILDER.comment("Sets the fluid tank capacity of the Matter Mixer.")
                .define("Matter Mixer Fluid Tank Capacity", 8000);
        MATTER_MIXER_FLUID_PER_TICK = BUILDER.comment("Sets the fluid used per tick for the Matter Mixer.")
                .define("Matter Mixer Fluid Per Tick", 10);

        BUILDER.push("World Spawn Settings");
        OOM_MUSHROOM_NETHER_PLACEMENT_RARITY = BUILDER.comment("Set the rarity filter for Nether biome placement of Blue and Green Mushrooms.  Larger numbers are more rare.")
                .define("Nether Rarity Filer", 2);
        OOM_MUSHROOM_NORMAL_PLACEMENT_RARITY = BUILDER.comment("Set the rarity filter for normal biome placement of Blue and Green Mushrooms.  Larger numbers are more rare.")
                .define("Normal Rarity Filer", 256);
        OOM_MUSHROOM_TAIGA_PLACEMENT_RARITY = BUILDER.comment("Set the rarity filter for taiga biome placement of Blue and Green Mushrooms.  Larger numbers are more rare.")
                .define("Taiga Rarity Filer", 4);
        OOM_MUSHROOM_OLD_GROWTH_PLACEMENT_RARITY = BUILDER.comment("Set the rarity filter for old growth biome placement of Blue and Green Mushrooms.  Larger numbers are more rare.")
                .define("Old Growth Rarity Filer", 4);
        OOM_MUSHROOM_OLD_GROWTH_PLACEMENT_COUNT = BUILDER.comment("Set the number of spawms for Blue and Green Mushrooms in an old growth biome chunk.")
                .define("Old Growth Count", 2);
        OOM_MUSHROOM_SWAMP_PLACEMENT_RARITY = BUILDER.comment("Set the rarity filter for swamp biome placement of Blue and Green Mushrooms.  Larger numbers are more rare.")
                .define("Swamp Rarity Filer", 0);
        OOM_MUSHROOM_SWAMP_PLACEMENT_COUNT = BUILDER.comment("Set the number of spawms for Blue and Green Mushrooms in a swamp biome chunk.")
                .define("Swamp Count", 2);
        OOM_MUSHROOM_FIELD_PLACEMENT_RARITY = BUILDER.comment("Set the rarity filter for mushroom field biome placement of Blue and Green Mushrooms.  Larger numbers are more rare.")
                .define("Mushroom Field Rarity Filer", 4);
        HUGE_OOM_MUSHROOM_FIELD_PLACEMENT_RARITY = BUILDER.comment("Set the rarity filter for mushroom biome placement of Huge Blue and Huge Green Mushrooms.  Larger numbers are more rare.")
                .define("Huge Mushroom Field Rarity Filer", 16);
        HUGE_OOM_MUSHROOM_DARK_FOREST_PLACEMENT_WEIGHT = BUILDER.comment("Set the placement weight for dark forest biome placement of Huge Blue and Huge Green Mushrooms.  Larger numbers are more rare.")
                .define("Huge Dark Forest Placement Weight", 0.025F);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
