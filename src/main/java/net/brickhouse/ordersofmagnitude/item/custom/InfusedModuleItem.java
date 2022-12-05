package net.brickhouse.ordersofmagnitude.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class InfusedModuleItem extends Item {

    private double designScale;

    public InfusedModuleItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if(pStack.hasTag()) {
            String hoverText;
            double infusedScale = pStack.getTag().getDouble("designScale")*100D;
            if(infusedScale % 2 > 0){        //Check if this is fractional.  We can kill the trailing zeroes
                hoverText = String.valueOf(infusedScale);
            } else {
                hoverText = String.valueOf((int) infusedScale);
            }
            pTooltipComponents.add(new TextComponent(hoverText + "%"));
        } else {
            pTooltipComponents.add(new TextComponent("Uninitialized"));
        }
    }
}
