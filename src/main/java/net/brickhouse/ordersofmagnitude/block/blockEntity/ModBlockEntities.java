package net.brickhouse.ordersofmagnitude.block.blockEntity;

import net.brickhouse.ordersofmagnitude.OrdersOfMagnitude;
import net.brickhouse.ordersofmagnitude.block.ModBlocks;
import net.brickhouse.ordersofmagnitude.block.blockEntity.matterinfusion.AdvancedMatterInfusionBlockEntity;
import net.brickhouse.ordersofmagnitude.block.blockEntity.matterinfusion.BasicMatterInfusionBlockEntity;
import net.brickhouse.ordersofmagnitude.block.blockEntity.matterinfusion.UpgradedMatterInfusionBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, OrdersOfMagnitude.MOD_ID);

    //The matter infusion block entities were a single block entity class, but this was causing issues with the tick creator.  Only the basic class could get a handler
    //I subdivided the class out based on Furnace entities (AbstractFurnaceEntity being extended by Furnace, Smelter, and Smoker)
    //Ideally, in the future, I'd like to get back to one entity to rule them all to keep things minimalist
    public static final RegistryObject<BlockEntityType<BasicMatterInfusionBlockEntity>> BASIC_MATTER_INFUSION_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("basic_matter_infusion", () ->
                    BlockEntityType.Builder.of(BasicMatterInfusionBlockEntity::new, ModBlocks.BASIC_MATTER_INFUSION_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<UpgradedMatterInfusionBlockEntity>> UPGRADED_MATTER_INFUSION_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("upgraded_matter_infusion", () ->
                    BlockEntityType.Builder.of(UpgradedMatterInfusionBlockEntity::new,
                            ModBlocks.UPGRADED_MATTER_INFUSION_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<AdvancedMatterInfusionBlockEntity>> ADVANCED_MATTER_INFUSION_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("advanced_matter_infusion", () ->
                    BlockEntityType.Builder.of(AdvancedMatterInfusionBlockEntity::new,
                            ModBlocks.ADVANCED_MATTER_INFUSION_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<MatterMixerBlockEntity>> MATTER_MIXER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("matter_mixer", () ->
                    BlockEntityType.Builder.of(MatterMixerBlockEntity::new,
                            ModBlocks.MATTER_MIXER_BLOCK.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
