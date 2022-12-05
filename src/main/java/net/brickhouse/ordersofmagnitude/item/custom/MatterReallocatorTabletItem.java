package net.brickhouse.ordersofmagnitude.item.custom;

import net.brickhouse.ordersofmagnitude.client.MatterReallocatorTabletMenu;
import net.brickhouse.ordersofmagnitude.config.OMServerConfig;
import net.brickhouse.ordersofmagnitude.networking.ModMessages;
import net.brickhouse.ordersofmagnitude.networking.packet.ServerboundChangeSizePacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MatterReallocatorTabletItem extends OMStorageItem implements MenuProvider {

    public int NUM_SLOTS;
    public String TEXTURE_PATH;

    public MatterReallocatorTabletItem(Item.Properties pProperties, int numSlots, String sPath)
    {
        super(pProperties, numSlots);
        NUM_SLOTS = numSlots;
        TEXTURE_PATH = sPath;
    }

    //Click action functions.  Right click for menu, left click to change size. Left click is called from event handlers in EntityEvents
    //All events are designed around the player having the tablet in the main hand.
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand){
        if(!pLevel.isClientSide() && pHand == InteractionHand.MAIN_HAND){
            NetworkHooks.openGui((ServerPlayer) pPlayer, this);
        }
        return new InteractionResultHolder<>(InteractionResult.PASS, pPlayer.getItemInHand(pHand));
    }

    //This is called from onClickInputEvent client side event
    public void LeftClickAction(LivingEntity livingEntity)
    {
        if(canUseSizeChange(livingEntity.getMainHandItem(), livingEntity, false)){
            ModMessages.sendToServer(new ServerboundChangeSizePacket());
        }
    }

    //Returns true is the player is set to creative, or if the tablet has enough energy to function
    public boolean canUseSizeChange(ItemStack pItemStack, LivingEntity livingEntity, boolean simulate){
        if(livingEntity instanceof Player){
            if(((Player) livingEntity).isCreative()){
                return true;
            }
        }
        LazyOptional <IEnergyStorage> lazyOptional = pItemStack.getCapability(CapabilityEnergy.ENERGY);
        if(lazyOptional.isPresent()){
            IEnergyStorage iEnergyStorage = lazyOptional.orElseThrow(IllegalStateException::new);
            if(iEnergyStorage.getEnergyStored() >= 1000){
                return true;
            }
        }
        if(livingEntity instanceof Player player && !simulate) {
            player.displayClientMessage(new TranslatableComponent("gui.ordersofmagnitude.matter_reallocator_tablet.no_power").withStyle(ChatFormatting.RED), false);
        }
        return false;
    }

    public void usePower(ItemStack pItemStack){
        pItemStack.getCapability(CapabilityEnergy.ENERGY).ifPresent(energyHandler ->{
            energyHandler.extractEnergy(OMServerConfig.ITEM_ENERGY_EXTRACT.get(), false);
        });
    }

    //Misc Functions
    @Override
    public Component getDisplayName() {
        return new TextComponent(this.getOrCreateDescriptionId());
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced){
        //Do I want to add a description with hotkey?  This would be the place to add it

        pStack.getCapability(CapabilityEnergy.ENERGY, null).ifPresent( energyStorage -> {
            MutableComponent mutableComponent = new TranslatableComponent("gui.ordersofmagnitude.energy_toolip").withStyle(ChatFormatting.YELLOW);
            mutableComponent.append(new TextComponent(" "+ ((double) energyStorage.getEnergyStored())/1000D + " / " + ((double) energyStorage.getMaxEnergyStored())/1000 + " kFE")
                    .withStyle(ChatFormatting.GRAY));
            pTooltipComponents.add(mutableComponent);

        });

    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new MatterReallocatorTabletMenu(pContainerId, pPlayerInventory, pPlayer.getMainHandItem());
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, Player player)
    {
        //not sure if this is really needed now that ClickInputEvent is being used to detect the client using the tablet
        return true;
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
/*
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

 */
}
