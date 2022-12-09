package net.brickhouse.ordersofmagnitude.world.inventory;

import net.brickhouse.ordersofmagnitude.block.blockEntity.matterinfusion.AbstractMatterInfusionBlockEntity;
import net.brickhouse.ordersofmagnitude.item.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MatterInfusionMenu extends AbstractContainerMenu {

    private final AbstractMatterInfusionBlockEntity blockEntity;
    private final ContainerData data;

    IItemHandler inv;
    public double selectedSize;

    private final ContainerLevelAccess access;

    private FluidStack fluidStack;

    public int tier;

    public MatterInfusionMenu(int pContainerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(pContainerId, playerInventory, playerInventory.player.level.getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(3), ContainerLevelAccess.NULL);
    }
    public MatterInfusionMenu(int pContainerId, Inventory playerInventory, BlockEntity blockEntity, ContainerData pData, ContainerLevelAccess pAccess) {
        super(ModMenuTypes.MATTER_INFUSION_MENU.get(), pContainerId);
        checkContainerDataCount(pData, 3);
        this.blockEntity = (AbstractMatterInfusionBlockEntity) blockEntity;
        this.data = pData;
        this.access = pAccess;
        this.fluidStack = this.blockEntity.getFluidStack();
        this.tier = this.blockEntity.tier;

        addDataSlots(pData);
        addInventoryHotbar(playerInventory);
        addSlots();

    }

    public double getSelectedSize(){
        //return ((double) this.data.get(2))/10000D;
        //System.out.println("getSelectedSize clientside: " + this.blockEntity.getLevel().isClientSide() + " buffer scale: " + this.blockEntity.getBufferSelectedScale());//this.blockEntity.getUpdateTag().getDouble("matter_infusion_machine.bufferSelectedScale"));
        //return this.blockEntity.getUpdateTag().getDouble("matter_infusion_machine.bufferSelectedScale");
        return this.blockEntity.getBufferSelectedScale();
    }

    /*public double initializeSelection(){
        int tempSelection = this.data.get(2);
        selectedSize = ((double) tempSelection)/10000D;
        System.out.print("Menu Const data size: " + this.data.getCount() + " selectedScale: " + (tempSelection)/10000D + "\n");
        return selectedSize;
    }*/

    //Only call this from the server side.  Workflow for menu is to call a data packet to call this function from enqueue
    public void updateDesignScale(double designScale){
        //int tempScale = (int) (designScale*10000D);
        //this.data.set(2, tempScale);
        //this.access.execute(Level::blockEntityChanged);
        this.blockEntity.getTileData().putDouble("matter_infusion_machine.bufferSelectedScale", designScale);
        //System.out.println("updateDesignScale: " + (this.data.get(2)));
    }

    public boolean isCrafting() {
        return this.data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);  // Max Progress
        int progressArrowSize = 50; // This is the width in pixels of your arrow

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    public void addSlots(){
        this.blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            addSlot(new InfuserSlot(handler, 0, 46, 20, true, 64, ModItems.EMPTY_MODULE.get()));     //empty module input
            addSlot(new SlotItemHandler(handler, 1, 8, 20));                        //filled bucket input.  Need to update InfuserSlot() to handle items that are not empty modules
            addSlot(new InfuserSlot(handler, 2, 8, 57, false, 64, null));     //Empty Bucket output
            addSlot(new InfuserSlot(handler, 3, 100, 20, false, 64, null));    //Result output
        });
    }

    private void addInventoryHotbar(Inventory pPlayerInventory){
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(pPlayerInventory, j + i * 9 + 9, 8 + j * 18, 110 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(pPlayerInventory, k, 8 + k * 18, 168));
        }
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 4;  // must be the number of slots you have!

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            //System.out.println("Invalid slotIndex:" + index);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    public void setFluid(FluidStack fluidStack) {
        this.fluidStack = fluidStack;
    }

    public FluidStack getFluidStack() {
        return this.blockEntity.getFluidStack();
    }
    public AbstractMatterInfusionBlockEntity getBlockEntity() {
        return this.blockEntity;
    }
}
