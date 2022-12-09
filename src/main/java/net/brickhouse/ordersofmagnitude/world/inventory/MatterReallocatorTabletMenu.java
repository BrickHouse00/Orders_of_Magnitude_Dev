package net.brickhouse.ordersofmagnitude.world.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class MatterReallocatorTabletMenu extends OMAbstractContainerMenu{//AbstractContainerMenu{
    //private final Level level;
    public final ItemStack tabletItemStack;
    private int numSlots;
    //public final MatterReallocatorTabletItem tabletItem;

    //public IItemHandler inv;

    public MatterReallocatorTabletMenu(int pContainerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        //this(pContainerId, inv, Minecraft.getInstance().player.getMainHandItem());
        this(pContainerId, playerInventory, playerInventory.player.getMainHandItem());
    }

    public MatterReallocatorTabletMenu(int pContainerId, Inventory playerInventory, ItemStack tabletItemStack){
        super(ModMenuTypes.MATTER_REALLOCATOR_TABLET_MENU.get(), pContainerId, playerInventory, tabletItemStack, 0, 26);
        this.tabletItemStack = tabletItemStack;
        //this.tabletItem = (MatterReallocatorTabletItem) tabletItemStack.getItem();

        //this.level = playerInventory.player.level;
        //numSlots = tabletItem.NUM_SLOTS;
        //TE_INVENTORY_SLOT_COUNT = tabletItem.NUM_SLOTS;
        //inv = tabletItemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(null);
        addSlots();
        //addPlayerInventory(playerInventory);
        //addPlayerHotbar(playerInventory);
    }
    public void addSlots(){
        //IItemHandler inv = tabletItemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(null);

            //System.out.print("GetSlots(): " + inv.getSlots() + "\n");
            int slotCounter = 0;
            for (int j = 0; j < 2; j++) {
                for (int i = 0; i < NUM_SLOTS / 2; i++) {
                    addSlot(new TabletSlot(itemInventory, slotCounter, 70 + j * 80, i * 22 - 16));

                    slotCounter++;
                }
            }
    }
/*
    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }
    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 140-30 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 198-30));
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
    private final int TE_INVENTORY_SLOT_COUNT;  // must be the number of slots you have!

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

 */

}
