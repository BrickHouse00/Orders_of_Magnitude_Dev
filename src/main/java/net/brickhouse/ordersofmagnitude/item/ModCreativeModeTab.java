package net.brickhouse.ordersofmagnitude.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeModeTab {
    public static final CreativeModeTab ORDERSOFMAGNITUDE_TAB = new CreativeModeTab("ordersofmagnitudetab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.MATTER_REALLOCATOR_TABLET_DIAMOND.get());
        }
    };
}
