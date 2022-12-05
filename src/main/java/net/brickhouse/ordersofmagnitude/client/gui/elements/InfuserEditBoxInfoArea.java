package net.brickhouse.ordersofmagnitude.client.gui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import net.brickhouse.ordersofmagnitude.config.OMServerConfig;
import net.brickhouse.ordersofmagnitude.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.List;

public class InfuserEditBoxInfoArea extends InfoArea{

    public InfuserEditBoxInfoArea(int pXMin, int pYMin, int pWidth, int pHeight) {
        super(new Rect2i(pXMin, pYMin, pWidth, pHeight));
    }

    @Override
    protected void fillTooltipOverArea(int mouseX, int mouseY, List<Component> tooltip) {
        tooltip.add(new TranslatableComponent("gui.ordersofmagnitude.matter_infusion_menu.tootip.line1"));
        tooltip.add(new TranslatableComponent("gui.ordersofmagnitude.matter_infusion_menu.tootip.line2"));
        tooltip.add(new TextComponent("").append(ModItems.INFUSED_MODULE.get().getDefaultInstance().getHoverName()).withStyle(ChatFormatting.AQUA));
        tooltip.add(new TranslatableComponent("gui.ordersofmagnitude.matter_infusion_menu.tootip.line3"));
    }

    @Override
    public void draw(PoseStack transform) {

    }

    //This was me being lazy.  Ideally, there should be a key input handler in fillTooltipOverArea that determines if the alt text should be shown
    //The screen already has a keyhandler baked in, so it's easier to let the screen determine and call the appropriate tooltip type
    public void fillAltToolTip(int mouseX, int mouseY, List<Component> tooltip, double minSize, double maxSize) {
        if(area.contains(mouseX, mouseY)) {
            tooltip.add(new TranslatableComponent("gui.ordersofmagnitude.matter_infusion_menu.tootip.alt.line1").append(Double.toString(minSize)));
            tooltip.add(new TranslatableComponent("gui.ordersofmagnitude.matter_infusion_menu.tootip.alt.line2").append(Double.toString(maxSize)));
        }
    }
}
