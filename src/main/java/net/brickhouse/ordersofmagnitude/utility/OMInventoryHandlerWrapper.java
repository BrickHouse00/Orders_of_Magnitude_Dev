package net.brickhouse.ordersofmagnitude.utility;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;


public class OMInventoryHandlerWrapper extends ItemStackHandler {

    //Issues with this method.  It works for preventing hoppers from shuffline stuff unnecessarily, but it causes issues with the menu as you can't remove items from their stacks in the menu
    //Additionally, this was causing an issue with data saving on the block entities.  No data was being serialized
    boolean[] canInsertSlot;
    boolean[] canExtractSlot;

    public OMInventoryHandlerWrapper(){
        this(1, new boolean[]{true}, new boolean[]{true});
    }

    public OMInventoryHandlerWrapper(int size, boolean[] pCanInsertSlot, boolean pCanExtractSlot[]){
        super(size);
        canInsertSlot = pCanInsertSlot;
        canExtractSlot = pCanExtractSlot;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack){
        if(slot < canInsertSlot.length && canInsertSlot[slot]){             //TODO remove the first conditional.  This is needed to get older worlds working with this wrapper
           super.setStackInSlot(slot, stack);
        }
    }

    public void setStackInSlot(int slot, @Nonnull ItemStack stack, boolean forceSet){
        if(!forceSet){
            setStackInSlot(slot, stack);
        }
        validateSlotIndex(slot);
        this.stacks.set(slot, stack);
        onContentsChanged(slot);
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if(slot < canInsertSlot.length && canInsertSlot[slot]){             //TODO remove the first conditional.  This is needed to get older worlds working with this wrapper
            return super.insertItem(slot, stack, simulate);
        }
        return ItemStack.EMPTY;
    }

    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate, boolean forceInsert){
        if(!forceInsert){
            return insertItem(slot, stack, simulate);
        }
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!isItemValid(slot, stack))
            return stack;

        validateSlotIndex(slot);

        ItemStack existing = this.stacks.get(slot);

        int limit = getStackLimit(slot, stack);

        if (!existing.isEmpty())
        {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate)
        {
            if (existing.isEmpty())
            {
                this.stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            }
            else
            {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            onContentsChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount()- limit) : ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if(slot < canExtractSlot.length && canExtractSlot[slot]){                                            //TODO remove the first conditional.  This is needed to get older worlds working with this wrapper
            return super.extractItem(slot, amount, simulate);
        }
        return ItemStack.EMPTY;
    }

    public ItemStack extractItem(int slot, int amount, boolean simulate, boolean forceExtraction){
        if(!forceExtraction){
            return extractItem(slot, amount, simulate);
        }
        if (amount == 0)
            return ItemStack.EMPTY;
        validateSlotIndex(slot);
        ItemStack existing = this.stacks.get(slot);
        if (existing.isEmpty())
            return ItemStack.EMPTY;
        int toExtract = Math.min(amount, existing.getMaxStackSize());
        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                this.stacks.set(slot, ItemStack.EMPTY);
                onContentsChanged(slot);
                return existing;
            }
            else {
                return existing.copy();
            }
        }
        else {
            if (!simulate) {
                this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                onContentsChanged(slot);
            }
            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = super.serializeNBT();
        tag.put("canInsertSlot", serializeBooleanArray(canInsertSlot));
        tag.put("canExtractSlot", serializeBooleanArray(canExtractSlot));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        canInsertSlot = deserializeBooleanArray(nbt.getList("canInsertSlot", Tag.TAG_COMPOUND));
        canExtractSlot = deserializeBooleanArray(nbt.getList("canExtractSlot", Tag.TAG_COMPOUND));
        super.deserializeNBT(nbt);
    }

    public ListTag serializeBooleanArray(boolean[] boolList){
        ListTag listTag = new ListTag();
        for(int i = 0; i < boolList.length; i++){
            CompoundTag newTag = new CompoundTag();
            newTag.putBoolean("boolean",boolList[i]);
            listTag.add(newTag);
        }
        return listTag;
    }

    public boolean[] deserializeBooleanArray(ListTag boolTag){
        boolean[] returnArray = new boolean[]{};
        for(int i = 0; i < boolTag.size(); i++){
            returnArray[i] = boolTag.getCompound(i).getBoolean("boolean");
        }
        return returnArray;
    }
}
