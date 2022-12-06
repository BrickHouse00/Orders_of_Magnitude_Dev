package net.brickhouse.ordersofmagnitude.item.custom;

import net.brickhouse.ordersofmagnitude.config.OMServerConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.List;

public class OMStorageItem extends Item {
    public int NUM_SLOTS;
    public OMStorageItem(Properties pProperties, int pSlotCount) {
        super(pProperties);
        NUM_SLOTS = pSlotCount;
    }

    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt){

        return new OMItemStackHandler(stack, NUM_SLOTS);
    }

    public boolean canUseSizeChange(ItemStack pItemStack, LivingEntity livingEntity, boolean simulate){
        if(livingEntity instanceof Player){
            if(((Player) livingEntity).isCreative()){
                return true;
            }
        }
        LazyOptional<IEnergyStorage> lazyOptional = pItemStack.getCapability(CapabilityEnergy.ENERGY);
        if(lazyOptional.isPresent()){
            IEnergyStorage iEnergyStorage = lazyOptional.orElseThrow(IllegalStateException::new);
            if(iEnergyStorage.getEnergyStored() >= 1000){
                return true;
            }
        }
        return false;
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

    @Override
    public void appendHoverText(ItemStack pStack, @org.jetbrains.annotations.Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced){
        //Do I want to add a description with hotkey?  This would be the place to add it

        pStack.getCapability(CapabilityEnergy.ENERGY, null).ifPresent( energyStorage -> {
            MutableComponent mutableComponent = new TranslatableComponent("gui.ordersofmagnitude.energy_toolip").withStyle(ChatFormatting.YELLOW);
            mutableComponent.append(new TextComponent(" "+ ((double) energyStorage.getEnergyStored())/1000D + " / " + ((double) energyStorage.getMaxEnergyStored())/1000 + " kFE")
                    .withStyle(ChatFormatting.GRAY));
            pTooltipComponents.add(mutableComponent);

        });

    }

    public void usePower(ItemStack pItemStack){
        pItemStack.getCapability(CapabilityEnergy.ENERGY).ifPresent(energyHandler ->{
            energyHandler.extractEnergy(OMServerConfig.ITEM_ENERGY_EXTRACT.get(), false);
        });
    }

    @Override
    public boolean isBarVisible(ItemStack stack){
        IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null).orElse(null);
        return energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored();
    }

    @Override
    public int getBarWidth(ItemStack stack){
        return stack.getCapability(CapabilityEnergy.ENERGY, null).map(energy -> Math.round(13.0F * (float)energy.getEnergyStored()/ (float)energy.getMaxEnergyStored())).orElse(0);
    }
    @Override
    public int getBarColor(ItemStack pStack) {
        return Mth.hsvToRgb(Math.max(0.0F, getBarWidth(pStack)/(float)MAX_BAR_WIDTH)/3.0F, 1.0F, 1.0F);
    }
}
