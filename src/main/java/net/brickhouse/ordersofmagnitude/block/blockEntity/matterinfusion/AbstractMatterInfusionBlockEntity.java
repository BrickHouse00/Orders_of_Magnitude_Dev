package net.brickhouse.ordersofmagnitude.block.blockEntity.matterinfusion;

import net.brickhouse.ordersofmagnitude.block.blockEntity.OMBlockEntity;
import net.brickhouse.ordersofmagnitude.world.inventory.MatterInfusionMenu;
import net.brickhouse.ordersofmagnitude.config.OMServerConfig;
import net.brickhouse.ordersofmagnitude.item.ModItems;
import net.brickhouse.ordersofmagnitude.networking.ModMessages;
import net.brickhouse.ordersofmagnitude.networking.packet.ClientboundInfusionBlockEntityResultSyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractMatterInfusionBlockEntity extends OMBlockEntity {

    @Override
    public int dataGet(int pIndex){
        switch (pIndex){
            case 0: return AbstractMatterInfusionBlockEntity.this.progress;
            case 1: return AbstractMatterInfusionBlockEntity.this.maxProgress;
            case 2: return (int)(AbstractMatterInfusionBlockEntity.this.bufferSelectedScale*10000D);
            default: return 0;
        }
    }

    @Override
    public void dataSet(int pIndex, int pValue){
        switch (pIndex){
            case 0: AbstractMatterInfusionBlockEntity.this.progress = pValue; break;
            case 1: AbstractMatterInfusionBlockEntity.this.maxProgress = pValue; break;
            case 2: AbstractMatterInfusionBlockEntity.this.bufferSelectedScale = ((double) (pValue))/10000D; break;
        }
    }

    @Override
    public int dataGetCount(){
        return 3;
    }

    private int progress = 0;
    private int maxProgress = 80;
    double selectedScale = 0.0;
    double bufferSelectedScale = 0.0;
    public int tier;

    public AbstractMatterInfusionBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, int pTier) {
        this(pType, pPos, pBlockState);
        this.tier = pTier;
    }

    public AbstractMatterInfusionBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState){
        super(pType, pPos, pBlockState, OMServerConfig.MATTER_INFUSER_ENERGY_CAPACITY, OMServerConfig.MATTER_INFUSER_FLUID_TANK_CAPACITY, 4, true);//new boolean[]{true,false,false,false}, new boolean[]{false,true,false,true},true);
    }

    public void setBufferedScale(double newBufferScale){
        this.bufferSelectedScale = newBufferScale;
        //this.saveWithoutMetadata();
    }

    public double getBufferSelectedScale(){
        return this.bufferSelectedScale;
    }
/*
    @Override
    public void changedEnergy(){
        setChanged();
        ModMessages.sendToClients(new ClientboundOMBlockEntityEnergySyncPacket(this.ENERGY_STORAGE.getEnergy(), getBlockPos()));
    }

    @Override
    public void changedFluid(){
        setChanged();
        ModMessages.sendToClients(new ClientboundOMBlockEntityFluidSyncPacket(this.FLUID_TANK.getFluid(), getBlockPos()));
    }
*/


    @Override
    public boolean getIsItemValid(int slot, @Nullable ItemStack stack){
        return switch (slot) {
            case 0 -> stack.getItem() == ModItems.EMPTY_MODULE.get();
            case 1 -> stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent();
            case 3 -> stack.getItem() == ModItems.INFUSED_MODULE.get();
            default -> true;
        };

    }

    //Tick Handler
    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, AbstractMatterInfusionBlockEntity pBlockEntity) {
        /*boolean canCraftOnStack = pBlockEntity.itemHandler.getStackInSlot(3).is(Items.AIR);  //Check if we have an empty slot to craft on
        if(!canCraftOnStack){         //If it isn't empty, check if the stack in it has the same value
            ItemStack craftedStack = pBlockEntity.itemHandler.getStackInSlot(3);
            if(craftedStack.hasTag() && pBlockEntity.selectedScale/100D == craftedStack.getTag().getDouble("designScale")){
                canCraftOnStack = true;
            }
        }*/
        if(pBlockEntity.selectedScale != 0.0 && pBlockEntity.itemHandler.getStackInSlot(3).is(Items.AIR) && canCraft(pBlockEntity)){
            //HasRecipe
            pBlockEntity.progress++;
            extractEnergy(pBlockEntity, pBlockEntity.ENERGY_STORAGE, OMServerConfig.MATTER_INFUSER_ENERGY_PER_TICK, false);
            drainFluid(pBlockEntity, pBlockEntity.FLUID_TANK, OMServerConfig.MATTER_INFUSER_FLUID_PER_TICK, IFluidHandler.FluidAction.EXECUTE);
            if(pBlockEntity.progress > pBlockEntity.maxProgress){
                craftItem(pBlockEntity);
            }

        } else {
            pBlockEntity.resetProgress();
        }
        if(hasFluidItemInSourceSlot(pBlockEntity) && !pLevel.isClientSide()) {
            //transferItemFluidToFluidTank(pBlockEntity, 1);
            checkForFluidItemTransfer(pBlockEntity, 1, 2);
        }
    }

    private static boolean canCraft(AbstractMatterInfusionBlockEntity pBlockEntity){
        return hasEmptyModule(pBlockEntity) && hasEnoughEnergy(pBlockEntity, pBlockEntity.ENERGY_STORAGE, OMServerConfig.MATTER_INFUSER_ENERGY_PER_TICK) && hasEnoughFluid(pBlockEntity, pBlockEntity.FLUID_TANK, OMServerConfig.MATTER_INFUSER_FLUID_PER_TICK);
    }
    private static boolean hasEmptyModule(AbstractMatterInfusionBlockEntity pBlockEntity){
        return pBlockEntity.itemHandler.getStackInSlot(0).is(ModItems.EMPTY_MODULE.get());
    }

    private void calculateMaxProgress(){
        double calculatingScale = selectedScale/100D;
        if(calculatingScale < 1.0){
            calculatingScale = 1.0D/calculatingScale;
        }
        maxProgress = (int) (Math.round(Math.log10(calculatingScale)*1200));           //take the log of the scale to get the time in minutes.  The progress time should get longer the smaller or the larger the number becomes
        //System.out.print("calculateMaxProgress maxProgress: " + maxProgress);
    }
    @Override
    public void resetProgress() {
        this.progress = 0;
        //double newBufferScale = this.getTileData().getDouble("matter_infusion_machine.bufferSelectedScale");
        if(this.selectedScale != this.bufferSelectedScale){
            //System.out.print("updating selectedScale " + this.selectedScale + " buffer " + this.bufferSelectedScale + "\n");
            this.selectedScale = this.bufferSelectedScale;
            calculateMaxProgress();     //reset the max progress if the selected scale and buffer scale mismatch
        }
    }

    //Crafting function
    private static void craftItem(AbstractMatterInfusionBlockEntity pBlockEntity){
        Level level = pBlockEntity.level;
        SimpleContainer inv = new SimpleContainer(pBlockEntity.itemHandler.getSlots());
        for(int i = 0; i < pBlockEntity.itemHandler.getSlots(); i++){
            inv.setItem(i, pBlockEntity.itemHandler.getStackInSlot(i));
        }

        pBlockEntity.itemHandler.extractItem(0,1, false);

        ItemStack newItem = new ItemStack(ModItems.INFUSED_MODULE.get());
        /*CompoundTag nbt = new CompoundTag();
        nbt.putDouble("designScale", pBlockEntity.selectedScale/100D);
        newItem.setTag(nbt);*/
        newItem.getOrCreateTag().putDouble("designScale", pBlockEntity.selectedScale/100D);
        if(pBlockEntity.itemHandler.getStackInSlot(3).getCount() > 0) {
            newItem.setCount(pBlockEntity.itemHandler.getStackInSlot(3).getCount() + 1);
        }
        pBlockEntity.itemHandler.setStackInSlot(3,  newItem);

        pBlockEntity.resetProgress();

    }

    @Override
    public Component getDisplayName() {
        if(tier == 2){
            return new TranslatableComponent("block.ordersofmagnitude.advanced_matter_infusion");
        } else if(tier == 1){
            return new TranslatableComponent("block.ordersofmagnitude.upgraded_matter_infusion");
        }
        return new TranslatableComponent("block.ordersofmagnitude.basic_matter_infusion");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        //ModMessages.sendToClients(new ClientboundOMBlockEntityEnergySyncPacket(this.ENERGY_STORAGE.getEnergyStored(), getBlockPos()));
        //ModMessages.sendToClients(new ClientboundOMBlockEntityFluidSyncPacket(this.FLUID_TANK.getFluid(), getBlockPos()));
        syncEntity();
        ModMessages.sendToClients(new ClientboundInfusionBlockEntityResultSyncPacket(this.bufferSelectedScale, getBlockPos()));
        return new MatterInfusionMenu(pContainerId, pPlayerInventory, this, this.data, ContainerLevelAccess.create(this.level, this.getBlockPos()));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        //tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("matter_infusion_machine.progress", progress);
        tag.putInt("matter_infusion_machine.maxProgress", maxProgress);
        tag.putDouble("matter_infusion_machine.selectedScale", selectedScale);
        tag.putDouble("matter_infusion_machine.bufferSelectedScale", bufferSelectedScale);
        //tag.putInt("matter_infusion_machine.energy", ENERGY_STORAGE.getEnergyStored());
        //tag = FLUID_TANK.writeToNBT(tag);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        //itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("matter_infusion_machine.progress");
        maxProgress = nbt.getInt("matter_infusion_machine.maxProgress");
        selectedScale = nbt.getDouble("matter_infusion_machine.selectedScale");
        bufferSelectedScale = nbt.getDouble("matter_infusion_machine.bufferSelectedScale");
        //ENERGY_STORAGE.setEnergy(nbt.getInt("matter_infusion_machine.energy"));
        //FLUID_TANK.readFromNBT(nbt);
    }

    /*private static void transferItemFluidToFluidTank(OMBlockEntity pEntity, int pSlot) {
        pEntity.itemHandler.getStackInSlot(pSlot).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(handler -> {
            int drainAmount = Math.min(pEntity.FLUID_TANK.getSpace(), FluidAttributes.BUCKET_VOLUME);

            FluidStack stack = handler.drain(drainAmount, IFluidHandler.FluidAction.SIMULATE);
            if(pEntity.FLUID_TANK.isFluidValid(stack)) {
                stack = handler.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
                //fillTankWithFluid(pEntity, stack, handler.getContainer());
                fillTankWithFluid(pEntity, pEntity.FLUID_TANK, stack, pEntity.itemHandler, handler.getContainer(), pSlot, 1, IFluidHandler.FluidAction.EXECUTE, false);
            }
        });
    }*/

    private static void checkForFluidItemTransfer(OMBlockEntity pEntity, int pSlotIn, int pSlotOut){
        ItemStack itemStackIn = pEntity.itemHandler.getStackInSlot(pSlotIn);
        ItemStack itemStackOut = pEntity.itemHandler.getStackInSlot(pSlotOut);
        itemStackIn.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(handler -> {
            if(!handler.getFluidInTank(0).isEmpty()) {
                pEntity.transferItemFluidToFluidTank(pEntity.FLUID_TANK, pEntity.itemHandler, itemStackIn, itemStackOut, pSlotIn, pSlotOut);
            } //else {//if(handler.getFluidInTank(0).isEmpty() && itemStackOut.isEmpty()){
                //pEntity.transferFluidTanktoItemFluid(pEntity, pSlotIn, pSloutOut);
                //pEntity.transferTankFluidToContainer(pEntity.FLUID_TANK, pEntity.itemHandler, itemStackIn, itemStackOut, pSlotIn, pSlotOut);
            //}

        });
    }

    private static boolean hasFluidItemInSourceSlot(OMBlockEntity pEntity) {
        return pEntity.itemHandler.getStackInSlot(1).getCount() > 0;
    }
}
