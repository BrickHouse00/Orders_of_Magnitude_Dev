package net.brickhouse.ordersofmagnitude.world.inventory;

import net.brickhouse.ordersofmagnitude.OrdersOfMagnitude;
import net.brickhouse.ordersofmagnitude.client.gui.MatterInfusionScreen;
import net.brickhouse.ordersofmagnitude.client.gui.MatterMixerScreen;
import net.brickhouse.ordersofmagnitude.client.gui.MatterReallocatorTabletScreen;
import net.brickhouse.ordersofmagnitude.client.gui.MatterReallocatorWeaponScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.CONTAINERS, OrdersOfMagnitude.MOD_ID);

    public static final RegistryObject<MenuType<MatterReallocatorTabletMenu>> MATTER_REALLOCATOR_TABLET_MENU =
            registerMenuType(MatterReallocatorTabletMenu::new, "matter_reallocator_tablet_menu");

    public static final RegistryObject<MenuType<MatterWeaponMenu>> MATTER_WEAPON_MENU = registerMenuType(MatterWeaponMenu::new, "matter_weapon_menu");

    public static final RegistryObject<MenuType<MatterInfusionMenu>> MATTER_INFUSION_MENU =
            registerMenuType(MatterInfusionMenu::new, "matter_infusion_menu");

    public static final RegistryObject<MenuType<MatterMixerMenu>> MATTER_MIXER_MENU =
            registerMenuType(MatterMixerMenu::new, "matter_mixer_menu");
    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }

    public static void initializeMenus(){
        MenuScreens.register(MATTER_WEAPON_MENU.get(), MatterReallocatorWeaponScreen::new);
        MenuScreens.register(MATTER_REALLOCATOR_TABLET_MENU.get(), MatterReallocatorTabletScreen::new);
        MenuScreens.register(MATTER_INFUSION_MENU.get(), MatterInfusionScreen::new);
        MenuScreens.register(MATTER_MIXER_MENU.get(), MatterMixerScreen::new);
    }
}
