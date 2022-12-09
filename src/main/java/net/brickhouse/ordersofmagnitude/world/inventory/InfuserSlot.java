package net.brickhouse.ordersofmagnitude.world.inventory;

import net.brickhouse.ordersofmagnitude.item.custom.EmptyModuleItem;
import net.brickhouse.ordersofmagnitude.item.custom.InfusedModuleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class InfuserSlot extends SlotItemHandler {

    private boolean mayPlace = true;
    private int maxStackSize = 1;

    private Item allowableItem;

    public InfuserSlot(IItemHandler itemHandler, int pSlot, int pX, int pY, boolean pMayPlace, int pMaxStackSize, @Nullable Item pItem) {
        super(itemHandler, pSlot, pX, pY);
        mayPlace = pMayPlace;
        maxStackSize = pMaxStackSize;
        allowableItem = pItem;
    }

    @Override
    public boolean mayPlace(ItemStack itemStack){
        if(!mayPlace){return false;}
        if(allowableItem != null) {
            return !itemStack.isEmpty() && itemStack.is(allowableItem);
        }
        return !itemStack.isEmpty();
    }

    @Override
    public int getMaxStackSize() {
        return maxStackSize;
    }

}
