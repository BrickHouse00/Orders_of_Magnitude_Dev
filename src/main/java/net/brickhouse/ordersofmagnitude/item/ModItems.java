package net.brickhouse.ordersofmagnitude.item;

import net.brickhouse.ordersofmagnitude.OrdersOfMagnitude;
import net.brickhouse.ordersofmagnitude.fluid.ModFluids;
import net.brickhouse.ordersofmagnitude.item.custom.EmptyModuleItem;
import net.brickhouse.ordersofmagnitude.item.custom.InfusedModuleItem;
import net.brickhouse.ordersofmagnitude.item.custom.MatterReallocatorTabletItem;
import net.brickhouse.ordersofmagnitude.item.custom.MatterReallocatorWeaponItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, OrdersOfMagnitude.MOD_ID);

    public static final RegistryObject<Item> MATTER_REALLOCATOR_TABLET_BASIC = ITEMS.register("matter_reallocator_tablet_basic",
            () -> new MatterReallocatorTabletItem(new Item.Properties().stacksTo(1).tab(ModCreativeModeTab.ORDERSOFMAGNITUDE_TAB), 4, "textures/gui/gui_tablet_basic.png"));
    public static final RegistryObject<Item> MATTER_REALLOCATOR_TABLET_GOLD = ITEMS.register("matter_reallocator_tablet_gold",
            () -> new MatterReallocatorTabletItem(new Item.Properties().stacksTo(1).tab(ModCreativeModeTab.ORDERSOFMAGNITUDE_TAB), 6, "textures/gui/gui_tablet_gold.png"));
    public static final RegistryObject<Item> MATTER_REALLOCATOR_TABLET_REDSTONE = ITEMS.register("matter_reallocator_tablet_redstone",
            () -> new MatterReallocatorTabletItem(new Item.Properties().stacksTo(1).tab(ModCreativeModeTab.ORDERSOFMAGNITUDE_TAB), 8, "textures/gui/gui_tablet_redstone.png"));
    public static final RegistryObject<Item> MATTER_REALLOCATOR_TABLET_DIAMOND = ITEMS.register("matter_reallocator_tablet_diamond",
            () -> new MatterReallocatorTabletItem(new Item.Properties().stacksTo(1).tab(ModCreativeModeTab.ORDERSOFMAGNITUDE_TAB), 10, "textures/gui/gui_tablet_diamond.png"));

    public static final RegistryObject<Item> MATTER_REALLOCATOR_WEAPON = ITEMS.register("matter_reallocator_weapon",
            () -> new MatterReallocatorWeaponItem(new Item.Properties().tab(ModCreativeModeTab.ORDERSOFMAGNITUDE_TAB)));

    public static final RegistryObject<Item> MATTER_REALLOCATOR_DEVICE = ITEMS.register("matter_reallocator_device",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ORDERSOFMAGNITUDE_TAB)));

    public static final RegistryObject<Item> INFUSED_MODULE = ITEMS.register("infused_module",
            () -> new InfusedModuleItem(new Item.Properties().tab(ModCreativeModeTab.ORDERSOFMAGNITUDE_TAB).stacksTo(1)));

    public static final RegistryObject<Item> EMPTY_MODULE = ITEMS.register("empty_module",
            () -> new EmptyModuleItem(new Item.Properties().tab(ModCreativeModeTab.ORDERSOFMAGNITUDE_TAB)));

    public static final RegistryObject<Item> CENTRIFUGE = ITEMS.register("centrifuge",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.ORDERSOFMAGNITUDE_TAB)));
    public static final RegistryObject<Item> REALLOCATING_BUCKET = ITEMS.register("reallocating_bucket",
            () -> new BucketItem(ModFluids.REALLOCATING_FLUID,
                    new Item.Properties().tab(ModCreativeModeTab.ORDERSOFMAGNITUDE_TAB).stacksTo(1)));
    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}
