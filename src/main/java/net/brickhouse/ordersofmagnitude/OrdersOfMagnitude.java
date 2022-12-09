package net.brickhouse.ordersofmagnitude;

import com.mojang.logging.LogUtils;
import net.brickhouse.ordersofmagnitude.advancements.ModCriteriaTriggers;
import net.brickhouse.ordersofmagnitude.block.ModBlocks;
import net.brickhouse.ordersofmagnitude.block.blockEntity.ModBlockEntities;
import net.brickhouse.ordersofmagnitude.client.ModMenuTypes;
import net.brickhouse.ordersofmagnitude.config.OMServerConfig;
import net.brickhouse.ordersofmagnitude.events.RenderEvents;
import net.brickhouse.ordersofmagnitude.fluid.ModFluids;
import net.brickhouse.ordersofmagnitude.item.ModItems;
import net.brickhouse.ordersofmagnitude.networking.ModMessages;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(OrdersOfMagnitude.MOD_ID)
public class OrdersOfMagnitude
{
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "ordersofmagnitude";

    public OrdersOfMagnitude()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        //Event Bus registrations.  Call before adding listeners
        ModItems.register(eventBus);
        ModBlocks.register(eventBus);
        ModBlockEntities.register(eventBus);
        ModMenuTypes.register(eventBus);
        //ModRecipes.register(eventBus);

        ModFluids.register(eventBus);

        // Register the setup method for modloading
        eventBus.addListener(this::setup);
        eventBus.addListener(this::clientSetup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, OMServerConfig.SPEC, "ordersofmagnitude-server.toml");
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(()->{
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.BLUE_MUSHROOM.getId(), ModBlocks.POTTED_BLUE_MUSHROOM);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.GREEN_MUSHROOM.getId(), ModBlocks.POTTED_GREEN_MUSHROOM);
            CriteriaTriggers.register(ModCriteriaTriggers.CHANGE_SIZE);
        });
        ModMessages.register();
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new RenderEvents());
        //KeyBindings.init();

        ItemBlockRenderTypes.setRenderLayer(ModFluids.REALLOCATING_BLOCK.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModFluids.REALLOCATING_FLUID.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModFluids.REALLOCATING_FLOWING.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLUE_MUSHROOM.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.POTTED_BLUE_MUSHROOM.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.GREEN_MUSHROOM.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.POTTED_GREEN_MUSHROOM.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.BASIC_MATTER_INFUSION_BLOCK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.UPGRADED_MATTER_INFUSION_BLOCK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.ADVANCED_MATTER_INFUSION_BLOCK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.MATTER_MIXER_BLOCK.get(), RenderType.cutout());

        ModMenuTypes.initializeMenus();
    }

}
