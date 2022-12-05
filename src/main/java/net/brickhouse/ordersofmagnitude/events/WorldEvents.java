package net.brickhouse.ordersofmagnitude.events;

import net.brickhouse.ordersofmagnitude.OrdersOfMagnitude;
import net.brickhouse.ordersofmagnitude.world.worldgen.ModBiomeFeatures;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OrdersOfMagnitude.MOD_ID)
public class WorldEvents {
    @SubscribeEvent
    public static void onBiomeLoadingEvent(final BiomeLoadingEvent event){
        ModBiomeFeatures.addModMushrooms(event);

    }
}
