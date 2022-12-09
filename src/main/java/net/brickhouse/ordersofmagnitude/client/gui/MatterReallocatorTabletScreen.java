package net.brickhouse.ordersofmagnitude.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.brickhouse.ordersofmagnitude.OrdersOfMagnitude;
import net.brickhouse.ordersofmagnitude.world.inventory.MatterReallocatorTabletMenu;
import net.brickhouse.ordersofmagnitude.client.gui.elements.TabletButton;
import net.brickhouse.ordersofmagnitude.item.custom.MatterReallocatorTabletItem;
import net.brickhouse.ordersofmagnitude.networking.ModMessages;
import net.brickhouse.ordersofmagnitude.networking.packet.ServerboundItemMenuSizeSelectPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

public class MatterReallocatorTabletScreen extends AbstractContainerScreen<MatterReallocatorTabletMenu>{
    private static ResourceLocation texture_path;

    private int numSlots;
    private int BUTTON_LEFT_JUSTIFICATION = 27;
    private int BUTTON_TOP_JUSTIFICATION = -18;
    private int BUTTON_WIDGET_HEIGHT = 22;
    private int BUTTON_WIDGET_WIDTH = 80;
    private double targetScale;

    private Player menuPlayer;

    //private ItemStack tabletInstance;


    public MatterReallocatorTabletScreen(MatterReallocatorTabletMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        menuPlayer = pPlayerInventory.player;
        if(menuPlayer.getMainHandItem().getItem() instanceof MatterReallocatorTabletItem tablet) {
            //tabletInstance = menuPlayer.getMainHandItem();
            texture_path = new ResourceLocation(OrdersOfMagnitude.MOD_ID, tablet.TEXTURE_PATH);
            numSlots = tablet.NUM_SLOTS;
        }
        pMenu.addSlotListener(new ContainerListener() {
            @Override
            public void slotChanged(AbstractContainerMenu pContainerToSend, int pDataSlotIndex, ItemStack pStack) {
                RefreshButtons();
            }

            @Override
            public void dataChanged(AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue) {

            }
        });
    }


    @Override
    protected void init() {
        super.init();
        Player player = Minecraft.getInstance().player;
        if(menuPlayer != null && menuPlayer.getMainHandItem().hasTag()){
            targetScale = player.getMainHandItem().getTag().getDouble("targetScale");
        } else {
            targetScale = 1.0;
        }
    }

    public void RefreshButtons(){
        clearWidgets();

        if(this.menu.slots.size() > 35) { //Skip the player inventory and hotbar

            this.menu.tabletItemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler->{
                int invSlotCount = 0;
                double oldTargetScale = targetScale;

            targetScale = 1.0;                          //we want to clear the targetScale to prevent the player from unequipping a module and then being able to use the resize
            for(int j = 0; j < 2; j++){
                for(int i = 0; i < numSlots/2; i++){
                    ItemStack itemStack = handler.getStackInSlot(invSlotCount);
                    if(itemStack.hasTag()) {
                        double designScale = itemStack.getTag().getDouble("designScale");
                        AddButtonWidget(leftPos + BUTTON_LEFT_JUSTIFICATION + j * BUTTON_WIDGET_WIDTH,
                                topPos + BUTTON_TOP_JUSTIFICATION + i * BUTTON_WIDGET_HEIGHT,
                                40,
                                20,
                                new TextComponent(String.valueOf(designScale*100D)+"%"),
                                button -> {onButtonSetTarget(designScale);}
                        );
                        if(designScale == oldTargetScale){      //here's where we check the tablet for the old selection.  If the selection still exists in the updated list, we set it back to the target
                            targetScale = designScale;
                        }
                    } else {
                        AddButtonWidget(leftPos + BUTTON_LEFT_JUSTIFICATION + j * BUTTON_WIDGET_WIDTH,
                                topPos + BUTTON_TOP_JUSTIFICATION + i * BUTTON_WIDGET_HEIGHT,
                                40,
                                20,
                                new TranslatableComponent("gui.ordersofmagnitude.matter_reallocator_tablet.none"),
                                button -> {onButtonSetNoTarget();}
                        );
                    }
                    invSlotCount++;
                }
            }
            });
        }
    }

    protected void AddButtonWidget(int xPos, int yPos, int width, int height, Component message, Button.OnPress buttonResult){
        TabletButton buttonToAdd = new TabletButton(xPos, yPos, width, height, message, buttonResult);
        addRenderableWidget(buttonToAdd);
    }

    private void onButtonSetNoTarget(){
        //placeholder since you can't pass a null to the button field of widget
    }

    private void onButtonSetTarget(double newTarget){
        targetScale = newTarget;
        ModMessages.sendToServer(new ServerboundItemMenuSizeSelectPacket(targetScale));
    }

    @Override
    protected void slotClicked(Slot pSlot, int pSlotId, int pMouseButton, ClickType pType) {
        super.slotClicked(pSlot, pSlotId, pMouseButton, pType);
    }

    @Override
    public void onClose()
    {
        super.onClose();
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture_path);
        int x = this.leftPos;
        int y = this.topPos-30;

        this.blit(pPoseStack, x, y, 0, 0, 176, 222);//imageWidth, imageHeight);
        RefreshButtons();
        renderLights(pPoseStack);
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        float invLabelY = 100;
        font.draw(pPoseStack, this.playerInventoryTitle, (float)this.inventoryLabelX, invLabelY, 9145227);
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
    }

    public void renderLights(PoseStack pPoseStack){
        int invSlotCount = 36;
        int i = this.leftPos;
        int j = this.topPos-30;
        int xOffset = 230;
        int yOffset = 6;
        for(int k = 0; k < 2; k++) {
            for (int m = 0; m < numSlots / 2; m++) {
                ItemStack itemStack = this.menu.slots.get(invSlotCount).getItem();
                if(itemStack.hasTag()){
                    double designScale = itemStack.getTag().getDouble("designScale");
                    if(designScale == targetScale){
                        xOffset = 198;
                    }
                }
                invSlotCount++;
                this.blit(pPoseStack, i+10+(80*k), j+14+(23*m), xOffset, yOffset, 15, 15);
                xOffset = 230; //reset the offset
            }
        }
    }

}
