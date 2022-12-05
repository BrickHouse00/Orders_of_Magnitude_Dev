package net.brickhouse.ordersofmagnitude.item.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.EnergyStorage;

public class OMEnergyItem extends EnergyStorage {

    private final ItemStack thisStack;
    public OMEnergyItem(ItemStack pStack, int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
        thisStack = pStack;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive())
            return 0;
        int energyStored = getEnergyStored();
        int energyReceived = Math.min(capacity - energyStored, Math.min(this.maxReceive, maxReceive));
        if (!simulate) {
            energyStored += energyReceived;
            putEnergyStored(energyStored);
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;
        int energyStored = getEnergyStored();
        int energyExtracted = Math.min(energyStored, Math.min(this.maxExtract, maxExtract));
        if (!simulate) {
            energyStored -= energyExtracted;
            putEnergyStored(energyStored);
        }
        return energyExtracted;
    }

    @Override
    public boolean canReceive(){
        return true;
    }

    @Override
    public boolean canExtract(){
        return true;
    }

    @Override
    public int getEnergyStored() {
        if(thisStack.hasTag()){
            return thisStack.getTag().getInt("ItemEnergy");
        }
        return energy;
    }

    public void putEnergyStored(int pStoredEnergy){
        thisStack.getOrCreateTag().putInt("ItemEnergy", pStoredEnergy);
    }




}
