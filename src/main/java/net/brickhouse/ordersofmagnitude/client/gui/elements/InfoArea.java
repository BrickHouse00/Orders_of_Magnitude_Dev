package net.brickhouse.ordersofmagnitude.client.gui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

import java.util.List;


/*
 *  BluSunrize
 *  Copyright (c) 2021
 *
 *  This code is licensed under "Blu's License of Common Sense"
 *  Details can be found in the license file in the root folder of this project
 */
public abstract class InfoArea extends GuiComponent {
    protected final Rect2i area;

    protected InfoArea(Rect2i area) {
        this.area = area;
    }

    public final void fillTooltip(int mouseX, int mouseY, List<Component> tooltip){
        if(area.contains(mouseX, mouseY)){
            fillTooltipOverArea(mouseX, mouseY, tooltip);
        }
    }

    protected abstract void fillTooltipOverArea(int mouseX, int mouseY, List<Component> tooltip);


    public abstract void draw(PoseStack transform);
}