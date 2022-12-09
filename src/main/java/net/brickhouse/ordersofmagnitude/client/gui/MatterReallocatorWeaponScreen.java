package net.brickhouse.ordersofmagnitude.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.brickhouse.ordersofmagnitude.OrdersOfMagnitude;
import net.brickhouse.ordersofmagnitude.world.inventory.MatterWeaponMenu;
import net.brickhouse.ordersofmagnitude.networking.ModMessages;
import net.brickhouse.ordersofmagnitude.networking.packet.ServerboundItemMenuSizeSelectPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class MatterReallocatorWeaponScreen extends AbstractContainerScreen<MatterWeaponMenu> {

    private static ResourceLocation texture_path = new ResourceLocation(OrdersOfMagnitude.MOD_ID, "textures/gui/gui_reallocating_weapon.png");
    private static TranslatableComponent menuTitle = new TranslatableComponent("item.ordersofmagnitude.matter_reallocator_weapon");
    //private ItemStack weaponInstance;
    public MatterReallocatorWeaponScreen(MatterWeaponMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        //weaponInstance = pPlayerInventory.player.getMainHandItem();
    }

    @Override
    public void onClose()
    {
        if(Minecraft.getInstance().player == null){ return;}
        ItemStack itemStack = this.menu.slots.get(36).getItem();
        System.out.println("itemStack: " + itemStack);
        if(!itemStack.isEmpty()) {
            double targetScale = itemStack.getOrCreateTag().getDouble("designScale");
            //System.out.println("weaponInstance " + weaponInstance + " targetScale " + targetScale);
            ModMessages.sendToServer(new ServerboundItemMenuSizeSelectPacket(targetScale));
            if (targetScale != 0.0) {
                Minecraft.getInstance().player.displayClientMessage(new TranslatableComponent("gui.ordersofmagnitude.matter_reallocator_weapon.activated.line1").append(Double.toString(targetScale * 100D))
                        .append(new TranslatableComponent("gui.ordersofmagnitude.matter_reallocator_weapon.activated.line2")), true);
            }
        } else {
            Minecraft.getInstance().player.displayClientMessage(new TranslatableComponent("gui.ordersofmagnitude.matter_reallocator_weapon.empty.equipped"), true);
        }
        super.onClose();
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture_path);

        int x = this.leftPos;
        int y = this.topPos;

        this.blit(pPoseStack, x, y, 0, 0, 176, 169);    //imageWidth, imageHeight);
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        this.font.draw(pPoseStack, this.menuTitle, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
        this.font.draw(pPoseStack, new TranslatableComponent("gui.ordersofmagnitude.matter_reallocator_weapon.activeslot"), 128,26,4210752);
        this.font.draw(pPoseStack, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
    }
}
