package net.brickhouse.ordersofmagnitude.item.custom;

import net.brickhouse.ordersofmagnitude.config.OMServerConfig;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class OMItemStackHandler extends ItemStackHandler implements ICapabilityProvider{

    public final LazyOptional<IItemHandler> ITEM_HANDLER = LazyOptional.of(() -> this );

    private final ItemStack thisStack;
    public OMItemStackHandler(ItemStack itemStack, int size) {
        super(size);
        thisStack = itemStack;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityEnergy.ENERGY){
            return LazyOptional.of(() -> new OMEnergyItem(thisStack, OMServerConfig.ITEM_MAXIMUM_ENERGY.get(), OMServerConfig.ITEM_ENERGY_RECEIVE.get(), OMServerConfig.ITEM_ENERGY_EXTRACT.get())).cast();
        } else if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, ITEM_HANDLER);
        }
        return LazyOptional.empty();
    }
}
