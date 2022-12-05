package net.brickhouse.ordersofmagnitude.client.gui.elements;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class InfuserEditBox extends EditBox {

    private boolean canBeFocus = true;

    public InfuserEditBox(Font pFont, int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        super(pFont, pX, pY, pWidth, pHeight, pMessage);
    }

    public void setCanBeFocus(boolean value){
        canBeFocus = value;
    }

    @Override
    public void setFocus(boolean pIsFocused) {
        if(canBeFocus) {
            this.setFocused(pIsFocused);
        }
    }

}
