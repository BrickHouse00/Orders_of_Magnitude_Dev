package net.brickhouse.ordersofmagnitude.client.gui.elements;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class TabletButton extends Button {

    public boolean toggled;
    public TabletButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress);
    }

    @Override
    public void onPress() {
        this.onPress.onPress(this);
    }
}
