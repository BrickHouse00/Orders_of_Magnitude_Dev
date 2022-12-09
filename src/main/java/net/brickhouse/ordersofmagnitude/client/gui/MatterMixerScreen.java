package net.brickhouse.ordersofmagnitude.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.brickhouse.ordersofmagnitude.OrdersOfMagnitude;
import net.brickhouse.ordersofmagnitude.client.MatterMixerMenu;
import net.brickhouse.ordersofmagnitude.client.gui.elements.EnergyInfoArea;
import net.brickhouse.ordersofmagnitude.client.gui.elements.FluidTankRenderer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MatterMixerScreen extends AbstractContainerScreen<MatterMixerMenu> {

    private static ResourceLocation texture_path = new ResourceLocation(OrdersOfMagnitude.MOD_ID,"textures/gui/matter_mixer.png");

    private EnergyInfoArea energyInfoArea;
    private FluidTankRenderer renderer;

    public MatterMixerScreen(MatterMixerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        pMenu.addSlotListener(new ContainerListener() {
            @Override
            public void slotChanged(AbstractContainerMenu pContainerToSend, int pDataSlotIndex, ItemStack pStack) {

            }

            @Override
            public void dataChanged(AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue) {

            }
        });
    }

    @Override
    protected void init(){
        super.init();
        leftPos = (width - imageWidth) / 2;
        topPos = (height - imageHeight) / 2;

        assignEnergyInfoArea();
        assignFluidRenderer();
    }

    private void assignEnergyInfoArea() {
        int i = this.leftPos;
        int j = this.topPos;
        energyInfoArea = new EnergyInfoArea(i+166, j+5, menu.getBlockEntity().getEnergyStorage(), 4, 49);
    }

    private void assignFluidRenderer() {
        int i = this.leftPos;
        int j = this.topPos;
        renderer = new FluidTankRenderer(8* FluidAttributes.BUCKET_VOLUME, true, i+127, j+17, 10, 53);
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



        if(menu.isCrafting()){
            int l = this.menu.getScaledProgress();
            this.blit(pPoseStack, i + 42, j + 17, 176, 2, l + 1, 38);
        }

        renderer.render(pPoseStack, i + 127, j + 17, menu.getFluidStack());
        drawTankOverlay(pPoseStack);
    }

    public void drawTankOverlay(PoseStack pPoseStack){
        int i = this.leftPos;
        int j = this.topPos;
        RenderSystem.setShaderTexture(0, texture_path);
        this.blit(pPoseStack, i + 126, j +16, 180, 40, 11, 54);
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);


        List<Component> tooltip = new ArrayList<>();
        energyInfoArea.fillTooltip(mouseX, mouseY, tooltip);
        renderer.fillTooltip(mouseX, mouseY, tooltip, menu.getFluidStack());

        if(!tooltip.isEmpty()){
            renderTooltip(pPoseStack, tooltip, Optional.empty(), mouseX, mouseY);
        } else {
            renderTooltip(pPoseStack, mouseX, mouseY);
        }
    }

}
