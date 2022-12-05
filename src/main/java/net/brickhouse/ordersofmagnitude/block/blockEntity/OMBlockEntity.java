package net.brickhouse.ordersofmagnitude.block.blockEntity;

import net.brickhouse.ordersofmagnitude.energy.OMEnergy;
import net.brickhouse.ordersofmagnitude.fluid.OMFluidTank;
import net.brickhouse.ordersofmagnitude.networking.ModMessages;
import net.brickhouse.ordersofmagnitude.networking.packet.ClientboundOMBlockEntitySyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public abstract class OMBlockEntity extends BlockEntity implements MenuProvider {


    public OMBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, ForgeConfigSpec.ConfigValue<Integer> pEnergyConfig, ForgeConfigSpec.ConfigValue<Integer> pFluidConfig, int pInvSize, boolean pTankAcceptExternal) { //boolean[] pCanInsertSlot, boolean[] pcanExtractSlot, boolean pTankAcceptExternal) {
        super(pType, pPos, pBlockState);
        ENERGY_STORAGE = new OMEnergy(pEnergyConfig.get(), 256) {
            @Override
            public void onEnergyChanged() {
                changedEnergy();
            }
        };

        FLUID_TANK = new OMFluidTank(pFluidConfig.get()){
            @Override
            protected void onContentsChanged(){
                changedFluid();
            }
        };
        this.FLUID_TANK.setCanAcceptExternal(pTankAcceptExternal);

        itemHandler = new ItemStackHandler(pInvSize) { //OMInventoryHandlerWrapper(pInvSize, pCanInsertSlot, pcanExtractSlot){
            @Override
            protected void onContentsChanged(int slot){
                changedContents();
            }

            @Override
            public boolean isItemValid(int slot, @Nullable ItemStack stack){
                return getIsItemValid(slot, stack);
            }
        };

        data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return dataGet(pIndex);
            }

            @Override
            public void set(int pIndex, int pValue) {
                dataSet(pIndex, pValue);
            }

            @Override
            public int getCount() {
                return dataGetCount();
            }
        };

    }

    protected final ContainerData data;
    public final ItemStackHandler itemHandler;

    public final OMEnergy ENERGY_STORAGE;

    public final OMFluidTank FLUID_TANK;
    private int progress = 0;
    private int maxProgress = 80;

    public LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.empty();
    public LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    public LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();

    public void changedEnergy(){
        setChanged();
        syncEntity();
    }

    public void changedFluid(){
        setChanged();
        syncEntity();
    }

    public void changedContents(){
        setChanged();
    }

    public int dataGet(int pIndex){
        return 0;
    }

    public void dataSet(int pIndex, int pValue){

    }

    public int dataGetCount(){
        return 0;
    }

    public boolean getIsItemValid(int slot, @Nullable ItemStack stack){
        return false;
    }

    public void syncEntity(){
        ModMessages.sendToClients(new ClientboundOMBlockEntitySyncPacket(this.ENERGY_STORAGE.getEnergyStored(), getBlockPos(), this.FLUID_TANK.getFluid()));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @javax.annotation.Nullable Direction facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyItemHandler.cast();
        }
        if (capability == CapabilityEnergy.ENERGY){
            return lazyEnergyStorage.cast();
        }

        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            //System.out.println("cap: " + capability + " name: " + capability.getName() + " direction: " + facing);
            return lazyFluidHandler.cast();
        }

        return super.getCapability(capability, facing);
    }

    public void setProgress(int pValue){
        progress = pValue;
    }

    public int getProgress(){
        return progress;
    }

    public void resetProgress(){
        progress = 0;
    }

    public void setMaxProgress(int pValue){
        maxProgress = pValue;
    }

    public int getMaxProgress(){
        return maxProgress;
    }

    public static boolean hasEnoughEnergy(OMBlockEntity pBlockEntity, OMEnergy pEnergyStorage, ForgeConfigSpec.ConfigValue<Integer> pConfig){

        return pEnergyStorage.getEnergyStored() >= pConfig.get();
    }
    public static void extractEnergy(OMBlockEntity pBlockEntity, OMEnergy pEnergyStorage, ForgeConfigSpec.ConfigValue<Integer> pConfig, boolean pSimulate){
        pEnergyStorage.extractEnergy(pConfig.get(), pSimulate);
    }

    public void setEnergyLevel(int energy) {
        this.ENERGY_STORAGE.setEnergy(energy);
    }

    public OMEnergy getEnergyStorage() {
        return this.ENERGY_STORAGE;
    }

    public void setFluid(FluidStack stack) {
        this.FLUID_TANK.setFluid(stack);
    }

    public FluidStack getFluidStack() {
        return this.FLUID_TANK.getFluid();
    }

    //**START OLD FLUID CODE**//
    public static void tryOutputFluid(FluidTank pFluidTank, Level pLevel, BlockPos pBlockPos, @javax.annotation.Nullable Direction pSide, FluidStack pFluidStack){
        if(!pLevel.isClientSide() && pFluidTank.getFluidAmount() > 0){
            for (Direction direction: Direction.values()){              //test all sides for a valid fluid pipe
                BlockEntity adjacentEntity = pLevel.getBlockEntity(pBlockPos.relative(direction));      //grab the entity
                if(adjacentEntity != null){
                    //System.out.println("tryOutputFluid: adjacententity " + adjacentEntity.toString());
                    //System.out.println("found an entity " + direction.toString() + " of the mixer.");
                    //We have an entity.  Create a fresh fluidstack and check if the entity can handle fluids
                    FluidStack outputStack = new FluidStack(pFluidStack, pFluidTank.getFluidAmount());
                    adjacentEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite()).map(outputHandler -> {
                        int simulateFill = outputHandler.fill(outputStack, FluidAction.SIMULATE);

                        //System.out.println("simulateFill " + simulateFill + " outputHandler tank capacity: " + outputHandler.getTankCapacity(0) + " outputHandler tank amount: " + outputHandler.getFluidInTank(0).getAmount());
                        if(simulateFill > 0) {
                            int fluidOut = outputHandler.fill(new FluidStack(outputStack, simulateFill), FluidAction.EXECUTE);
                            pFluidTank.drain(fluidOut, FluidAction.EXECUTE);
                            return true;
                        }
                        return false;
                    });
                }
            }
        }

    }



/*
    public void transferFluidTanktoItemFluid(OMBlockEntity pEntity, int pSlotIn, int pSlotOut){
        pEntity.itemHandler.getStackInSlot(pSlotIn).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent((handler -> {
            if(pEntity.FLUID_TANK.getFluid().getAmount() > FluidAttributes.BUCKET_VOLUME){
                FluidStack fluidStack = new FluidStack(pEntity.FLUID_TANK.getFluid(), FluidAttributes.BUCKET_VOLUME);
                fillBucketWithFluid(pEntity.FLUID_TANK, fluidStack, pEntity.itemHandler, handler.getContainer(), pSlotIn, pSlotOut, 1, IFluidHandler.FluidAction.EXECUTE, false);
            }
        }));
    }
*/
    public static boolean hasEnoughFluid(OMBlockEntity pBlockEntity, OMFluidTank pFluidTank, ForgeConfigSpec.ConfigValue<Integer> pConfig){
        return pFluidTank.getFluidAmount() >= pConfig.get();
    }

    public static void drainFluid(OMBlockEntity pBlockEntity, OMFluidTank pFluidTank, ForgeConfigSpec.ConfigValue<Integer> pConfig, FluidAction pAction){
        pFluidTank.drain(pConfig.get(), pAction);
    }
/*  //MARK FOR DELETION
    public static void fillFluid(OMBlockEntity pBlockEntity, OMFluidTank pFluidTank, FluidStack pResource, ForgeConfigSpec.ConfigValue<Integer> pConfig, FluidAction pAction){
        pFluidTank.fillInternal(pResource, pAction);
    }

    public static void fillTankWithFluid(OMFluidTank pFluidTank, FluidStack stack, ItemStackHandler pItemHandler, ItemStack pContainer, int pSlotIn, int pSlotOut, int pAmount, FluidAction pAction, boolean pSimulate){
        pFluidTank.fillInternal(stack, pAction);

        pItemHandler.extractItem(pSlotIn, pAmount, pSimulate);          //Remove the filled bucket
        pItemHandler.insertItem(pSlotOut, pContainer, pSimulate);       //add empty bucket?  Not sure TBH
    }

 */
/*
    public static void fillBucketWithFluid(OMFluidTank pFluidTank, FluidStack pFluidStack, ItemStackHandler pItemHandler, ItemStack pContainer, int pSlotIn, int pSlotOut, int pAmount, FluidAction pAction, boolean pSimulate){
        //ItemStack filledBucket = new ItemStack(ModItems.REALLOCATING_BUCKET.get());
        ItemStack itemStack = pItemHandler.extractItem(pSlotIn, pAmount, pSimulate);
        itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(handler->{
            handler.fill(pFluidStack, pAction);
        });
        pItemHandler.extractItem(pSlotIn, pAmount, pSimulate);
        pFluidTank.drain(pFluidStack, pAction);
        pItemHandler.insertItem(pSlotOut, itemStack, pSimulate);
    }
*/
    //**END OLD FLUID CODE**//

    public void transferItemFluidToFluidTank(IFluidHandler pFluidHandler, ItemStackHandler pItemHandler, ItemStack pItemStackInput, ItemStack pItemStackOutput, int pSlotInput, int pSlotOutput) {
        ItemStack emptyContainer = emptyFluidContainer(pFluidHandler, pItemStackInput, pItemStackOutput);
        if(!emptyContainer.isEmpty()){
            if(pItemStackInput.getCount()==1 && !isTankEmpty(emptyContainer)){
                pItemHandler.extractItem(pSlotInput,1,false);
                pItemHandler.insertItem(pSlotInput, emptyContainer, false);
            } else {
                if(!pItemStackOutput.isEmpty() && ItemHandlerHelper.canItemStacksStack(emptyContainer, pItemStackOutput)){
                    pItemStackOutput.grow(emptyContainer.getCount());
                } else {
                    pItemHandler.insertItem(pSlotOutput, emptyContainer, false);
                }
                pItemHandler.extractItem(pSlotInput, 1, false);
            }
        }
    }

    public static ItemStack emptyFluidContainer(IFluidHandler pFluidHandler, ItemStack pItemStackInput, ItemStack pItemStackOutput){
        if(pItemStackInput == null || pItemStackInput.isEmpty()) {
            return ItemStack.EMPTY;
        }
        FluidActionResult inputResult = FluidUtil.tryEmptyContainer(pItemStackInput, pFluidHandler, Integer.MAX_VALUE, null, false);
        System.out.println("isSuccess " + inputResult.isSuccess());
        if(inputResult.isSuccess()){
            ItemStack drainedContainer = inputResult.getResult();
            System.out.println("drainedContainer " + drainedContainer + " isEmpty: " + pItemStackOutput.isEmpty() + " canItemsStack: " + ItemHandlerHelper.canItemStacksStack(pItemStackOutput, drainedContainer));
            if(pItemStackOutput.isEmpty() || ItemHandlerHelper.canItemStacksStack(pItemStackOutput, drainedContainer)){
                System.out.println("getCounts > maxstack " + (pItemStackOutput.getCount() + drainedContainer.getCount() > pItemStackOutput.getMaxStackSize()));
                if(!pItemStackOutput.isEmpty() && pItemStackOutput.getCount() + drainedContainer.getCount() > pItemStackOutput.getMaxStackSize()){
                    return ItemStack.EMPTY;
                }
                FluidActionResult realInputResult = FluidUtil.tryEmptyContainer(pItemStackInput, pFluidHandler, Integer.MAX_VALUE, null, true);
                if(realInputResult.isSuccess()){
                    return realInputResult.getResult();
                }
            }
        }
        return ItemStack.EMPTY;
    }
    //**The following fluid transfer code is modified from BluSunrize's Immersive Engineering FluidUtils to work with OMBlockEntity and replace my own clunky fluid handlers**//
    //Current issue is that buckets work just fine.  Tanks from Mekanism do not.  Currently, mekanism tanks will drain the block tank but do not fill
    //This is either a client/server side issue, or there's an issue with the fluid tank in dev mode
    public static void transferTankFluidToContainer(IFluidHandler pFluidHandler, ItemStackHandler pItemHandler, ItemStack pItemStackInput, ItemStack pItemStackOutput, int pSlotInput, int pSlotOutput){
        //System.out.println("pfluidHandler: " + pFluidHandler + " pItemHandler: " + pItemHandler + " pItemStackInput: " + pItemStackInput + " pItemStackOutput: " + pItemStackOutput + " pSlotInput: " + pSlotInput + " pSlotOutput: " + pSlotOutput);
        ItemStack filledContainer = getFluidContainer(pFluidHandler, pItemStackInput, pItemStackOutput);
        if(!filledContainer.isEmpty()){
            if(pItemStackInput.getCount()==1 && !isTankFull(filledContainer)){                                                            //Check if the container is filled up all the way, and if not, plop it back into the
                pItemHandler.extractItem(pSlotInput,1,false);
                pItemHandler.insertItem(pSlotInput, filledContainer, false);                                                                     //input slot.  This will solve my issue with Mekanism Fluid Tanks not filling completely
                //System.out.println("inputInsert: " + inputInsert);
            } else {
                //System.out.println("filledContainer: " + filledContainer + " isEmpty: " + pItemStackOutput.isEmpty() + " canItemStacksStack " + ItemHandlerHelper.canItemStacksStack(filledContainer, pItemStackOutput));
                if(!pItemStackOutput.isEmpty() && ItemHandlerHelper.canItemStacksStack(filledContainer, pItemStackOutput)){
                    pItemStackOutput.grow(filledContainer.getCount());
                } else {
                    pItemHandler.insertItem(pSlotOutput, filledContainer, false);
                    //System.out.println("didItInsert: " + didItInsert);
                }
                pItemHandler.extractItem(pSlotInput, 1, false);
            }
        }
    }

    public static ItemStack getFluidContainer(IFluidHandler pFluidHandler, ItemStack pItemStackInput, ItemStack pItemStackOutput){
        if(pItemStackInput == null || pItemStackInput.isEmpty()) {                                                                                             //Send up an empty immediately if the input is empty or somehow null
            return ItemStack.EMPTY;
        }

        FluidActionResult outputResult = FluidUtil.tryFillContainer(pItemStackInput, pFluidHandler, Integer.MAX_VALUE, null, false);            //simulate the fill to see if we can get something

        if(outputResult.isSuccess()){
            final ItemStack full = outputResult.getResult();
            if (pItemStackOutput.isEmpty() || ItemHandlerHelper.canItemStacksStack(pItemStackOutput, full)){    //Now, we only want to return the filled stack if we have the ability to do so (empty output or stackable output)
                //System.out.println("fluidhandler fluid amount: " + pFluidHandler.getFluidInTank(0).getAmount());
                if(!pItemStackOutput.isEmpty() && pItemStackOutput.getCount() + full.getCount() > pItemStackOutput.getMaxStackSize()){
                    return ItemStack.EMPTY;
                }
                FluidActionResult realOutputResult = FluidUtil.tryFillContainer(pItemStackInput, pFluidHandler, Integer.MAX_VALUE, null, true);                           //do it for real this time.  This will drain the tank
                //System.out.println("output result fluid amount: ");
                if(realOutputResult.isSuccess()){
                    return realOutputResult.getResult();                                                                                                            //returned filled stack
                }
            }
        }

        return ItemStack.EMPTY;
    }

    public static boolean isTankFull(ItemStack pItemStack){
        return FluidUtil.getFluidHandler(pItemStack).map(handler -> {
            if(handler.getFluidInTank(0).getAmount() < handler.getTankCapacity(0)){
                return false;
            }
            return true;
        }).orElse(true);

    }

    public static boolean isTankEmpty(ItemStack pItemStack){
        return FluidUtil.getFluidHandler(pItemStack).map(handler -> {
            if(handler.getFluidInTank(0).getAmount() > 0){
                return false;
            }
            return true;
        }).orElse(true);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyStorage = LazyOptional.of(()-> ENERGY_STORAGE);
        lazyFluidHandler = LazyOptional.of(() -> FLUID_TANK);
    }

    @Override
    public void invalidateCaps()  {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyStorage.invalidate();
        lazyFluidHandler.invalidate();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("energy", ENERGY_STORAGE.getEnergyStored());
        tag = FLUID_TANK.writeToNBT(tag);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        ENERGY_STORAGE.setEnergy(nbt.getInt("energy"));
        FLUID_TANK.readFromNBT(nbt);
    }

    /* //BEGIN DELETION//
    @Override
    public CompoundTag getUpdateTag(){
        CompoundTag tag = super.getUpdateTag();
        writeExtraData(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag){
        //super.handleUpdateTag(tag);
        this.loadExtraData(tag);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        System.out.println("getUpdatePacket");
        return ClientboundBlockEntityDataPacket.create(this, tag -> {
            CompoundTag newTag = new CompoundTag();
            this.writeExtraData(newTag);
            return newTag;
        });
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt){
        CompoundTag tag = pkt.getTag()!=null?pkt.getTag(): new CompoundTag();
        System.out.println("onDataPacket tag: " + tag.toString());
        loadExtraData(tag);
    }

    public void loadExtraData(CompoundTag pTag){
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
    }

    public void writeExtraData(CompoundTag pTag){
        pTag.put("inventory", itemHandler.serializeNBT());
    }

    @Override
    public Component getDisplayName() {
        return null;
    }
    //END DELETE//
*/

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return null;
    }
}
