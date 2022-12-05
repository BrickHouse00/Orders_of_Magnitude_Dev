package net.brickhouse.ordersofmagnitude.block;

import net.brickhouse.ordersofmagnitude.OrdersOfMagnitude;
import net.brickhouse.ordersofmagnitude.block.matterinfusion.AdvancedAbstractMatterInfusionBlock;
import net.brickhouse.ordersofmagnitude.block.matterinfusion.BasicAbstractMatterInfusionBlock;
import net.brickhouse.ordersofmagnitude.block.matterinfusion.UpgradedAbstractMatterInfusionBlock;
import net.brickhouse.ordersofmagnitude.item.ModCreativeModeTab;
import net.brickhouse.ordersofmagnitude.item.ModItems;
import net.brickhouse.ordersofmagnitude.world.feature.ModTreeFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, OrdersOfMagnitude.MOD_ID);

    public static final RegistryObject<Block> BASIC_MATTER_INFUSION_BLOCK = registerBlock("basic_matter_infusion",
            () -> new BasicAbstractMatterInfusionBlock(BlockBehaviour.Properties.of(Material.STONE),0), ModCreativeModeTab.ORDERSOFMAGNITUDE_TAB);

    public static final RegistryObject<Block> UPGRADED_MATTER_INFUSION_BLOCK = registerBlock("upgraded_matter_infusion",
            () -> new UpgradedAbstractMatterInfusionBlock(BlockBehaviour.Properties.of(Material.STONE),1), ModCreativeModeTab.ORDERSOFMAGNITUDE_TAB);

    public static final RegistryObject<Block> ADVANCED_MATTER_INFUSION_BLOCK = registerBlock("advanced_matter_infusion",
            () -> new AdvancedAbstractMatterInfusionBlock(BlockBehaviour.Properties.of(Material.STONE),2), ModCreativeModeTab.ORDERSOFMAGNITUDE_TAB);

    public static final RegistryObject<Block> MATTER_MIXER_BLOCK = registerBlock("matter_mixer",
            () -> new MatterMixerBlock(BlockBehaviour.Properties.of(Material.STONE)), ModCreativeModeTab.ORDERSOFMAGNITUDE_TAB);

    public static final RegistryObject<Block> BLUE_MUSHROOM = registerBlock("blue_mushroom",
            ()-> new MushroomBlock(BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_BLUE).noCollission().randomTicks().instabreak().sound(SoundType.GRASS).hasPostProcess(ModBlocks::always), () -> {
                return ModTreeFeatures.HUGE_BLUE_MUSHROOM;
            } ), ModCreativeModeTab.ORDERSOFMAGNITUDE_TAB);

    public static final RegistryObject<Block> POTTED_BLUE_MUSHROOM = registerBlockWithoutBlockItem("potted_blue_mushroom",
            ()-> new FlowerPotBlock(null, BLUE_MUSHROOM, BlockBehaviour.Properties.copy(Blocks.POTTED_BROWN_MUSHROOM).instabreak().noOcclusion()));

    public static final RegistryObject<Block> BLUE_MUSHROOM_BLOCK = registerBlock("blue_mushroom_block",
            ()-> new HugeMushroomBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.DIRT).strength(0.2F).sound(SoundType.WOOD)),
            ModCreativeModeTab.ORDERSOFMAGNITUDE_TAB);

    public static final RegistryObject<Block> GREEN_MUSHROOM = registerBlock("green_mushroom",
            ()-> new MushroomBlock(BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_GREEN).noCollission().randomTicks().instabreak().sound(SoundType.GRASS).hasPostProcess(ModBlocks::always), () -> {
                return ModTreeFeatures.HUGE_GREEN_MUSHROOM;
            } ), ModCreativeModeTab.ORDERSOFMAGNITUDE_TAB);

    public static final RegistryObject<Block> POTTED_GREEN_MUSHROOM = registerBlockWithoutBlockItem("potted_green_mushroom",
            ()-> new FlowerPotBlock(null, GREEN_MUSHROOM, BlockBehaviour.Properties.copy(Blocks.POTTED_BROWN_MUSHROOM).instabreak().noOcclusion()));

    public static final RegistryObject<Block> GREEN_MUSHROOM_BLOCK = registerBlock("green_mushroom_block",
            ()-> new HugeMushroomBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.DIRT).strength(0.2F).sound(SoundType.WOOD)),
            ModCreativeModeTab.ORDERSOFMAGNITUDE_TAB);

    private static <T extends Block> RegistryObject<T> registerBlockWithoutBlockItem(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block,
                                                                     CreativeModeTab tab, String tooltipKey) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, tab, tooltipKey);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block, CreativeModeTab tab, String tooltipKey) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)) {
            @Override
            public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
                pTooltip.add(new TranslatableComponent(tooltipKey));
            }
        });
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, CreativeModeTab tab) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, tab);
        return toReturn;
    }
    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block, CreativeModeTab tab) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
    }


    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    private static boolean always(BlockState p_50775_, BlockGetter p_50776_, BlockPos p_50777_) {
        return true;
    }
}
