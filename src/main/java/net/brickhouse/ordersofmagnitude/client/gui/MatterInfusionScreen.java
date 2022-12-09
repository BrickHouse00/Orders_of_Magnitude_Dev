package net.brickhouse.ordersofmagnitude.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.brickhouse.ordersofmagnitude.OrdersOfMagnitude;
import net.brickhouse.ordersofmagnitude.world.inventory.MatterInfusionMenu;
import net.brickhouse.ordersofmagnitude.client.gui.elements.*;
import net.brickhouse.ordersofmagnitude.config.OMServerConfig;
import net.brickhouse.ordersofmagnitude.networking.ModMessages;
import net.brickhouse.ordersofmagnitude.networking.packet.ServerboundInfusionMenuSelectPacket;
import net.brickhouse.ordersofmagnitude.sizechange.SizeUtility;
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
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MatterInfusionScreen extends AbstractContainerScreen<MatterInfusionMenu> {

    private static ResourceLocation texture_path;

    private EnergyInfoArea energyInfoArea;
    private FluidTankRenderer fluidTankRenderer;
    private InfuserEditBoxInfoArea inputEditBoxInfoArea;
    protected InfuserEditBox input;
    protected InfuserEditBox result;
    private final String initial = "";
    private TranslatableComponent menuTitle;
    private int tier;
    private double minTierLimit;
    private double maxTierLimit;
    private double resultScale;
    private Player player;
    private float resultLabelXPosition=48.0F;
    private float resultLabelYPosition=84.0F;
    private float inputLabelXPosition=117.0F;
    private float inputLabelYPosition=84.0F;

    private boolean isShiftHeld=false;      //Lazy way around adding a key handler to info box handlers
    public MatterInfusionScreen(MatterInfusionMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        tier = pMenu.tier;
        if(tier == 2){
            texture_path = new ResourceLocation(OrdersOfMagnitude.MOD_ID,"textures/gui/matter_infuser.png");
            menuTitle = new TranslatableComponent("block.ordersofmagnitude.advanced_matter_infusion");
            minTierLimit = OMServerConfig.MINIMUM_SIZE.get()*100;
            maxTierLimit = OMServerConfig.MAXIMUM_SIZE.get()*100;;
        } else if(tier == 1){
            texture_path = new ResourceLocation(OrdersOfMagnitude.MOD_ID,"textures/gui/matter_infuser.png");
            menuTitle = new TranslatableComponent("block.ordersofmagnitude.upgraded_matter_infusion");
            minTierLimit = OMServerConfig.MINIMUM_NETHER_SIZE.get()*100;  //transition all of these to configs
            maxTierLimit = OMServerConfig.MAXIMUM_NETHER_SIZE.get()*100;
        } else{
            texture_path = new ResourceLocation(OrdersOfMagnitude.MOD_ID,"textures/gui/matter_infuser.png");
            menuTitle = new TranslatableComponent("block.ordersofmagnitude.basic_matter_infusion");
            minTierLimit = OMServerConfig.MINIMUM_IRON_SIZE.get()*100;
            maxTierLimit = OMServerConfig.MAXIMUM_IRON_SIZE.get()*100;
        }
        player = pPlayerInventory.player;
        resultScale = menu.getSelectedSize();
        pMenu.addSlotListener(new ContainerListener() {
            @Override
            public void slotChanged(AbstractContainerMenu pContainerToSend, int pDataSlotIndex, ItemStack pStack) {

            }

            @Override
            public void dataChanged(AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue) {
                if(pMenu.getSelectedSize() == 0.0) {
                    result.setValue("NONE");
                } else {
                    System.out.println("as double: " + menu.getSelectedSize() + " as string: " + Double.toString(menu.getSelectedSize()));
                    result.setValue(Double.toString(menu.getSelectedSize()));
                }
            }
        });
    }

    @Override
    protected void init(){
        this.imageHeight = 191;     //This menu uses a non-standard height screen
        this.titleLabelY = 4;
        this.inventoryLabelY = this.imageHeight - 94;
        super.init();
        addKeypad();
        addInputBox();
        addResultBox();
        assignEnergyInfoArea();
        assignFluidRenderer();
    }

    private void assignEnergyInfoArea() {
        int i = this.leftPos;
        int j = this.topPos;
        energyInfoArea = new EnergyInfoArea(i+166, j+14, menu.getBlockEntity().getEnergyStorage(), 4, 50);
    }

    private void assignFluidRenderer() {
        int i = this.leftPos;
        int j = this.topPos;
        fluidTankRenderer = new FluidTankRenderer(8* FluidAttributes.BUCKET_VOLUME, true, i+29, j+20, 10, 53);
    }

    private void addInputBox(){
        int i = this.leftPos;
        int j = this.topPos;
        this.input = new InfuserEditBox(this.font, i+117, j+70, 50, 12, TextComponent.EMPTY);
        assignInputBoxInfoArea(i+117, j+70, 50, 12);
        this.input.setMaxLength(7);
        this.input.setBordered(true);
        this.input.setValue(this.initial);
        this.input.setResponder(this::onInputEdited);
        this.addWidget(this.input);
    }

    private void assignInputBoxInfoArea(int pXMin, int pYMin, int pWidth, int pHeight){
        inputEditBoxInfoArea = new InfuserEditBoxInfoArea(pXMin, pYMin, pWidth, pHeight);
    }

    private void addResultBox(){
        int i = this.leftPos;
        int j = this.topPos;
        this.result = new InfuserEditBox(this.font, i+48, j+70, 50, 12, TextComponent.EMPTY);
        this.result.setMaxLength(7);
        this.result.setBordered(true);
        this.result.setCanBeFocus(false);
        this.result.setValue(getResultString(resultScale));
        this.result.setEditable(false);
        this.result.setResponder(this::onOutputEdited);
        this.addWidget(this.result);
    }

    private String getResultString(double resultDouble){
        if(resultDouble == 0.0){
            return "NONE";
        }
        return Double.toString(resultDouble);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture_path);
        int i = this.leftPos;
        int j = this.topPos;
        this.blit(pPoseStack, i, j, 0, 0, imageWidth, imageHeight);
        energyInfoArea.draw(pPoseStack);


        if(this.result.getValue() == ""){
            if(menu.getSelectedSize() == 0.0) {
                result.setValue("NONE");
            } else {
                result.setValue(Double.toString(menu.getSelectedSize()));
            }
        }
        if(menu.isCrafting()){
            int l = this.menu.getScaledProgress();
            this.blit(pPoseStack, i + 44, j + 19, 176, 2, l + 1, 38);
        }

        fluidTankRenderer.render(pPoseStack, i + 29, j + 20, menu.getFluidStack());
        drawTankOverlay(pPoseStack);

    }

    public void drawTankOverlay(PoseStack pPoseStack){
        RenderSystem.setShaderTexture(0, texture_path);
        this.blit(pPoseStack, leftPos + 28, topPos +19, 180, 40, 11, 54);
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        this.input.render(pPoseStack, mouseX, mouseY, delta);
        this.result.render(pPoseStack, mouseX, mouseY, delta);

        List<Component> tooltip = new ArrayList<>();
        energyInfoArea.fillTooltip(mouseX, mouseY, tooltip);
        fluidTankRenderer.fillTooltip(mouseX, mouseY, tooltip, menu.getFluidStack());
        if(isShiftHeld){
            inputEditBoxInfoArea.fillAltToolTip(mouseX, mouseY, tooltip, minTierLimit, maxTierLimit);
        } else {
            inputEditBoxInfoArea.fillTooltip(mouseX, mouseY, tooltip);
        }

        if(!tooltip.isEmpty()){
            renderTooltip(pPoseStack, tooltip, Optional.empty(), mouseX, mouseY);
        } else {
            renderTooltip(pPoseStack, mouseX, mouseY);
        }
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if(pKeyCode == InputConstants.KEY_RETURN || pKeyCode == InputConstants.KEY_NUMPADENTER){
            addResult();
        } else if(pKeyCode == InputConstants.KEY_LSHIFT || pKeyCode == InputConstants.KEY_LSHIFT){
            isShiftHeld = true;
        }
            return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers){
        if(pKeyCode == InputConstants.KEY_LSHIFT || pKeyCode == InputConstants.KEY_RETURN){
            isShiftHeld = false;
        }
        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }
    private void addResult(){
        if(input.getValue() != "") {
            double newValue = Double.parseDouble(input.getValue());
            if (newValue <= maxTierLimit && newValue >= minTierLimit){
                String resultString = input.getValue();
                double resultDouble = Double.parseDouble(resultString);
                if(newValue != 0.0) {
                    result.setValue(resultString);
                } else {
                    result.setValue("NONE");
                }
                ModMessages.sendToServer(new ServerboundInfusionMenuSelectPacket(resultDouble, menu.getBlockEntity().getBlockPos()));
            } else {
                player.displayClientMessage(new TranslatableComponent("gui.ordersofmagnitude.matter_infusion_menu.out_of_bounds"), false);
            }
            input.setValue("");
        }
    }
    @Override
    protected void insertText(String pText, boolean pOverwrite) {
        super.insertText(pText, pOverwrite);
    }

    //Make sure the value is only a number or a period
    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        if (SizeUtility.allowableCharacter(pCodePoint)) {
            return this.getFocused() != null && this.getFocused().charTyped(pCodePoint, pModifiers);
        }
        return false;
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        this.font.draw(pPoseStack, this.menuTitle, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
        this.font.draw(pPoseStack, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
        this.font.draw(pPoseStack, new TranslatableComponent("gui.ordersofmagnitude.matter_infusion_menu.label.result"), resultLabelXPosition, resultLabelYPosition, 4210752);
        this.font.draw(pPoseStack, new TranslatableComponent("gui.ordersofmagnitude.matter_infusion_menu.label.input"), inputLabelXPosition, inputLabelYPosition, 4210752);
    }
    private void onInputEdited(String p_95611_) {
        String s = this.input.getValue();
    }

    private void onOutputEdited(String p_95611_) {
        String s = this.result.getValue();
    }

    public void addKeypad(){
        int count = 9;

        for(int i=0; i<3;i++) {
            for(int j=0; j<3;j++) {
                String number = Integer.toString(count);
                addKeyPadButton(leftPos+149-j*12, topPos+14+i*12, 12, 12, new TextComponent(number), button -> {
                    onKeypadEntry(number);
                });
                count--;
            }
        }
        addKeyPadButton(leftPos+149-0*12, topPos+14+3*12, 12, 12, new TextComponent("."), button -> {
            onKeypadEntry(".");});
        addKeyPadButton(leftPos+149-1*12, topPos+14+3*12, 12, 12, new TextComponent("0"), button -> {
            onKeypadEntry("0");});
        addKeyPadButton(leftPos+149-2*12, topPos+14+3*12, 12, 12, new TextComponent(String.valueOf('\u232B')), button -> {
            onKeypadNonNumber('\u232B');});  //Backspace
        addKeyPadButton(leftPos+102, topPos+70, 12, 12, new TextComponent(String.valueOf('\u21A9')), button -> {
            onKeypadNonNumber('\u21A9');});  //Enter|return
    }
    public void addKeyPadButton(int pX , int pY, int pWidth, int pHeight, Component message, Button.OnPress buttonResult){
        KeyPadButton keyPadButton = new KeyPadButton(pX, pY, pWidth, pHeight, message, buttonResult);
        addRenderableWidget(keyPadButton);
    }

    public void onKeypadEntry(String pMessage){
        input.insertText(pMessage);
    }
    public void onKeypadNonNumber(char pInput){
        if(pInput == '\u232b') {
            input.deleteChars(-1);
        } else if(pInput == '\u21A9'){
            addResult();
        }
    }
}
