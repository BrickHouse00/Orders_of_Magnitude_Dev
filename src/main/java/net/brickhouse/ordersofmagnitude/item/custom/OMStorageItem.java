package net.brickhouse.ordersofmagnitude.item.custom;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public class OMStorageItem extends Item {
    public int NUM_SLOTS;
    public OMStorageItem(Properties pProperties, int pSlotCount) {
        super(pProperties);
        NUM_SLOTS = pSlotCount;
    }

    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt){

        return new OMItemStackHandler(stack, NUM_SLOTS);
    }

    @Nullable
    @Override
    public CompoundTag getShareTag(ItemStack stack){
        return saveModules(stack, super.getShareTag(stack));
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt){
        super.readShareTag(stack, nbt);
        loadModules(stack, nbt);
    }

    public static CompoundTag saveModules(ItemStack pItemStack, CompoundTag pTag){

        if(pTag == null){
            pTag = new CompoundTag();
        }
        CompoundTag saveTag = pTag;

        pItemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            NonNullList<ItemStack> modulesList = NonNullList.withSize(handler.getSlots(), ItemStack.EMPTY);
            for(int i = 0; i < handler.getSlots(); i++){
                modulesList.set(i, handler.getStackInSlot(i));
            }
            saveTag.put("modules", ContainerHelper.saveAllItems(new CompoundTag(), modulesList));
        });
        return saveTag;
    }

    public static void loadModules(ItemStack stack, @Nullable CompoundTag pTag){
        if(pTag != null) {
            stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
                NonNullList<ItemStack> modulesList = NonNullList.withSize(handler.getSlots(), ItemStack.EMPTY);
                ContainerHelper.loadAllItems(pTag.getCompound("modules"), modulesList);
                for (int i = 0; i < handler.getSlots(); i++) {
                    handler.insertItem(i, modulesList.get(i), false);
                }
            });
        }
    }
}
