package net.brickhouse.ordersofmagnitude.events;

import net.brickhouse.ordersofmagnitude.OrdersOfMagnitude;
import net.brickhouse.ordersofmagnitude.item.custom.MatterReallocatorTabletItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OrdersOfMagnitude.MOD_ID, value= Dist.CLIENT)
public class InputEvents {

    @SubscribeEvent
    public static void onClickInputEvent(InputEvent.ClickInputEvent event){
        if(event.isAttack()) {                                                                          //filter out to include only attack clicks.  This will also filter for remapped keybinds
            Player player = Minecraft.getInstance().player;                                             //check the player's main hand for a tablet
            ItemStack itemStack = player.getMainHandItem();
            if(itemStack.getItem() instanceof MatterReallocatorTabletItem tablet) {
                tablet.LeftClickAction(player);                                                         //We have a tablet, go ahead and activate it
                MouseHandler mouseHandler = Minecraft.getInstance().mouseHandler;                       //Attack click has this annoying feature where it fires every tick until the user releases the attack when aimed at blocks
                mouseHandler.releaseMouse();                                                            //There's no way to filter or stop this as consumeClick() is already called by Minecraft before this event is fired.
                mouseHandler.grabMouse();                                                               //The best way so far is to interrupt the player's mouse.  Not sure if this would have the same effect on remapped keys
            }
        }
    }
}
