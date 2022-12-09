package net.brickhouse.ordersofmagnitude.block.blockEntity;

import net.brickhouse.ordersofmagnitude.block.ModBlocks;
import net.brickhouse.ordersofmagnitude.world.inventory.MatterMixerMenu;
import net.brickhouse.ordersofmagnitude.config.OMServerConfig;
import net.brickhouse.ordersofmagnitude.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MatterMixerBlockEntity extends OMBlockEntity implements MenuProvider {

    private int maxProgress = 200;      //We want 10 seconds of production
    private int progress = maxProgress;
    private boolean isMixing = false;



    public MatterMixerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.MATTER_MIXER_BLOCK_ENTITY.get(), pPos, pBlockState, OMServerConfig.MATTER_MIXER_ENERGY_CAPACITY, OMServerConfig.MATTER_MIXER_FLUID_TANK_CAPACITY, 5, false);//new boolean[]{true,true,true,false,false}, new boolean[]{false,false,false,false,true},false);
        FLUID_TANK.setCanAcceptExternal(false);
    }

    @Override
    public int dataGet(int pIndex){
        switch (pIndex){
            case 0: return MatterMixerBlockEntity.this.progress;
            case 1: return MatterMixerBlockEntity.this.maxProgress;
            default: return 0;
        }
    }

    @Override
    public void dataSet(int pIndex, int pValue){
        switch (pIndex){
            case 0: MatterMixerBlockEntity.this.progress = pValue;
            case 1: MatterMixerBlockEntity.this.maxProgress = pValue;
        }
    }

    @Override
    public int dataGetCount(){
        return 2;
    }

    @Override
    public boolean getIsItemValid(int slot, @Nullable ItemStack stack){
        return switch(slot){
          case 0 -> stack.getItem() == ModBlocks.BLUE_MUSHROOM.get().asItem();
          case 1 -> stack.getItem() == Items.LAPIS_LAZULI;
          case 2 -> stack.getItem() == ModBlocks.GREEN_MUSHROOM.get().asItem();
          case 3 -> stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent();
          default -> true;
        };
    }

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, MatterMixerBlockEntity pBlockEntity) {
        if(pBlockEntity.hasRecipe(pBlockEntity, pLevel) && !pBlockEntity.isMixing && pBlockEntity.hasFluidRoom(pBlockEntity) && pBlockEntity.hasEnoughEnergy(pBlockEntity)) {
            //System.out.println("onTick setting isMixing true clientside: " + pLevel.isClientSide() + " tank amount: " + pBlockEntity.FLUID_TANK.getFluidAmount());
            pBlockEntity.isMixing = true;
            pBlockEntity.useItemsInSlots(pBlockEntity);
        }
        if(pBlockEntity.isMixing && pBlockEntity.progress > 0){
            pBlockEntity.progress--;
            extractEnergy(pBlockEntity, pBlockEntity.ENERGY_STORAGE, OMServerConfig.MATTER_INFUSER_ENERGY_PER_TICK, false);
            craftItem(pBlockEntity);
        } else if(pBlockEntity.progress <= 0){
            pBlockEntity.resetProgress();
        }
        if(!pLevel.isClientSide()) {
            checkForFluidItemTransfer(pBlockEntity, 3, 4);
        }
        if(pBlockEntity.FLUID_TANK.getFluidAmount() > 0) {
            tryOutputFluid(pBlockEntity.FLUID_TANK, pLevel, pPos, null, pBlockEntity.FLUID_TANK.getFluid());
        }

    }

    private static void checkForFluidItemTransfer(OMBlockEntity pEntity, int pSlotIn, int pSlotOut){
        ItemStack itemStackIn = pEntity.itemHandler.getStackInSlot(pSlotIn);
        ItemStack itemStackOut = pEntity.itemHandler.getStackInSlot(pSlotOut);
        itemStackIn.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(handler -> {
            if(handler.getFluidInTank(0).getAmount() < handler.getTankCapacity(0)) {                    //Intercept the fluid into an item first.  This way we can place a partially filled item back into the input slot
                pEntity.transferTankFluidToContainer(pEntity.FLUID_TANK, pEntity.itemHandler, itemStackIn, itemStackOut, pSlotIn, pSlotOut);
            } //else {//if(handler.getFluidInTank(0).isEmpty() && itemStackOut.isEmpty()){              //going to remove the whole aspect of allowing the player to add fluids to the internal tank.  It's dumb and unnecessary for the crafting block
                //pEntity.transferFluidTanktoItemFluid(pEntity, pSlotIn, pSloutOut);
                //pEntity.transferItemFluidToFluidTank(pEntity, pSlotIn, pSlotOut);
            //}

        });
    }
/*
    private static boolean hasRecipe(MatterMixerBlockEntity pBlockEntity, Level pLevel){
        Level level = pBlockEntity.level;
        SimpleContainer container = new SimpleContainer(pBlockEntity.itemHandler.getSlots());
        for(int i = 0; i < pBlockEntity.itemHandler.getSlots(); i++){
            container.setItem(i, pBlockEntity.itemHandler.getStackInSlot(i));
        }
        Optional<MatterMixerRecipe> match = level.getRecipeManager().getRecipeFor(MatterMixerRecipe.Type.INSTANCE, container, level);
        System.out.println("match " + match);
        return match.isPresent();
    }
*/
    private static boolean hasRecipe(MatterMixerBlockEntity pBlockEntity, Level pLevel){
        //Ideally this needs to be converted to a recipe so we can use tags instead of hard coded vanilla items
        boolean[] slots = {false, false, false};
        List<Item> recipeList = List.of(ModBlocks.GREEN_MUSHROOM.get().asItem(), Items.LAPIS_LAZULI, ModBlocks.BLUE_MUSHROOM.get().asItem());
        for(int i = 0; i < 3; i++){
            slots[i] = recipeList.contains(pBlockEntity.itemHandler.getStackInSlot(i).getItem());
        }
        return slots[0] && slots[1] && slots[2];
    }

    private static boolean hasFluidRoom(MatterMixerBlockEntity pBlockEntity){
        return (pBlockEntity.FLUID_TANK.getFluidAmount() + pBlockEntity.maxProgress * OMServerConfig.MATTER_INFUSER_FLUID_PER_TICK.get()) <= pBlockEntity.FLUID_TANK.getCapacity();
    }

    private static boolean hasEnoughEnergy(MatterMixerBlockEntity pBlockEntity){
        return pBlockEntity.ENERGY_STORAGE.getEnergyStored() > pBlockEntity.maxProgress*OMServerConfig.MATTER_INFUSER_ENERGY_PER_TICK.get();
    }
    private static void useItemsInSlots(MatterMixerBlockEntity pBlockEntity){
        for(int i = 0; i < 3; i++){
            pBlockEntity.itemHandler.getStackInSlot(i).shrink(1);
        }
    }

    private static void craftItem(MatterMixerBlockEntity pBlockEntity){
        pBlockEntity.FLUID_TANK.fillInternal(new FluidStack(ModFluids.REALLOCATING_FLUID.get().getFlowing(), OMServerConfig.MATTER_MIXER_FLUID_PER_TICK.get()), IFluidHandler.FluidAction.EXECUTE);
    }

    @Override
    public void resetProgress(){
        progress = maxProgress;
        isMixing = false;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        //ModMessages.sendToClients(new ClientboundOMBlockEntitySyncPacket(this.ENERGY_STORAGE.getEnergyStored(), getBlockPos()));
        //ModMessages.sendToClients(new ClientboundOMBlockEntityFluidSyncPacket(this.FLUID_TANK.getFluid(), getBlockPos()));
        syncEntity();
        return new MatterMixerMenu(pContainerId, pPlayerInventory, this, this.data, ContainerLevelAccess.create(this.level, this.getBlockPos()));
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("Matter Mixer");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.putInt("progress", progress);
        tag.putInt("maxProgress", maxProgress);
        tag.putBoolean("isMixing", isMixing);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        progress = nbt.getInt("progress");
        maxProgress = nbt.getInt("maxProgress");
        isMixing = nbt.getBoolean("isMixing");
    }
/*
    @Override
    public void changedEnergy(){
        setChanged();
        ModMessages.sendToClients(new ClientboundOMBlockEntitySyncPacket(this.ENERGY_STORAGE.getEnergy(), getBlockPos()));
    }

    @Override
    public void changedFluid(){
        setChanged();
        ModMessages.sendToClients(new ClientboundOMBlockEntityFluidSyncPacket(this.FLUID_TANK.getFluid(), getBlockPos()));
    }

 */
}
