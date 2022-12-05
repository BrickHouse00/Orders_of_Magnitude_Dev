package net.brickhouse.ordersofmagnitude.fluid;

import net.brickhouse.ordersofmagnitude.OrdersOfMagnitude;
import net.brickhouse.ordersofmagnitude.block.ModBlocks;
import net.brickhouse.ordersofmagnitude.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluids {
    public static final ResourceLocation WATER_STILL_RL = new ResourceLocation(OrdersOfMagnitude.MOD_ID, "block/reallocating_still");
    public static final ResourceLocation WATER_FLOWING_RL = new ResourceLocation(OrdersOfMagnitude.MOD_ID,"block/reallocating_flow");
    public static final ResourceLocation WATER_OVERLAY_RL = new ResourceLocation(OrdersOfMagnitude.MOD_ID,"block/water_overlay");

    public static final DeferredRegister<Fluid> FLUIDS
            = DeferredRegister.create(ForgeRegistries.FLUIDS, OrdersOfMagnitude.MOD_ID);


    public static final RegistryObject<FlowingFluid> REALLOCATING_FLUID
            = FLUIDS.register("reallocating_fluid", () -> new ForgeFlowingFluid.Source(ModFluids.REALLOCATING_PROPERTIES));

    public static final RegistryObject<FlowingFluid> REALLOCATING_FLOWING
            = FLUIDS.register("reallocating_flowing", () -> new ForgeFlowingFluid.Flowing(ModFluids.REALLOCATING_PROPERTIES));


    public static final ForgeFlowingFluid.Properties REALLOCATING_PROPERTIES = new ForgeFlowingFluid.Properties(
            () -> REALLOCATING_FLUID.get(), () -> REALLOCATING_FLOWING.get(), FluidAttributes.builder(WATER_STILL_RL, WATER_FLOWING_RL)
            .density(15).luminosity(2).viscosity(5).sound(SoundEvents.HONEY_DRINK).overlay(WATER_OVERLAY_RL)
            .color(0xbfffffff)).slopeFindDistance(2).levelDecreasePerBlock(2)
            .block(() -> ModFluids.REALLOCATING_BLOCK.get()).bucket(() -> ModItems.REALLOCATING_BUCKET.get());

    public static final RegistryObject<LiquidBlock> REALLOCATING_BLOCK = ModBlocks.BLOCKS.register("reallocating_fluid_block",
            () -> new LiquidBlock(() -> ModFluids.REALLOCATING_FLUID.get(), BlockBehaviour.Properties.of(Material.WATER)
                    .noCollission().strength(100f).noDrops()));



    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }
}