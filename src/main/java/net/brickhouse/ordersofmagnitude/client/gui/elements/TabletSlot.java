package net.brickhouse.ordersofmagnitude.client.gui.elements;

import net.brickhouse.ordersofmagnitude.item.custom.InfusedModuleItem;
import net.brickhouse.ordersofmagnitude.utility.OMInventoryHandlerWrapper;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class TabletSlot extends SlotItemHandler {

    //private static Container emptyInventory = new SimpleContainer(0);

    //OMInventoryHandlerWrapper itemHandler;
    //private final int index;

    public TabletSlot(IItemHandler itemHandler, int pSlot, int pX, int pY) {
        super(itemHandler, pSlot, pX, pY);
        //this.itemHandler = itemHandler;
        //index = pSlot;
    }

    @Override
    public boolean mayPlace(ItemStack itemStack){
        return !itemStack.isEmpty() && itemStack.getItem() instanceof InfusedModuleItem;
    }
/*
    @Override
    public void set(@Nonnull ItemStack stack)
    {
        this.getItemHandler().setStackInSlot(index, stack, true);
        this.setChanged();
    }

 */

    @Override
    public int getMaxStackSize() {
        return 1;
    }
/*
    @Override
    public int getMaxStackSize(@Nonnull ItemStack stack)
    {
        ItemStack maxAdd = stack.copy();
        int maxInput = stack.getMaxStackSize();
        maxAdd.setCount(maxInput);

        OMInventoryHandlerWrapper handler = this.getItemHandler();
        ItemStack currentStack = handler.getStackInSlot(index);
        if (handler instanceof IItemHandlerModifiable) {

            handler.setStackInSlot(index, ItemStack.EMPTY, true);

            ItemStack remainder = handler.insertItem(index, maxAdd, true, true);

            handler.setStackInSlot(index, currentStack, true);

            return maxInput - remainder.getCount();
        }
        else
        {
            ItemStack remainder = handler.insertItem(index, maxAdd, true, true);

            int current = currentStack.getCount();
            int added = maxInput - remainder.getCount();
            return current + added;
        }
    }

 */
/*
    @Override
    public boolean mayPickup(Player playerIn)
    {
        return !this.getItemHandler().extractItem(index, 1, true, true).isEmpty();
    }

 */
/*
    @Override
    @Nonnull
    public ItemStack remove(int amount)
    {
        return this.getItemHandler().extractItem(index, amount, false, true);
    }

 */
/*   public OMInventoryHandlerWrapper getItemHandler()
    {
        return itemHandler;
    }

 */
}
