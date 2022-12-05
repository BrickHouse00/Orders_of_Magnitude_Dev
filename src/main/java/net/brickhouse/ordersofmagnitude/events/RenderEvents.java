package net.brickhouse.ordersofmagnitude.events;

import net.brickhouse.ordersofmagnitude.sizechange.SizeChangeCapability;
import net.brickhouse.ordersofmagnitude.sizechange.SizeUtility;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderEvents {
    @SubscribeEvent
    public void RenderPlayerPre(RenderPlayerEvent.Pre event)
    {
        try {
            Player player = event.getPlayer();
            player.getCapability(SizeChangeCapability.INSTANCE).ifPresent(sizeChange ->
            {
                float sizeMod = SizeUtility.getScale(player);
                if (sizeMod != 1.0) {
                    //float newScale = (float) sizeChange.getTargetScale();
                    event.getPoseStack().pushPose();
                    event.getPoseStack().scale(sizeMod, sizeMod, sizeMod);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();;
        }
    }

    @SubscribeEvent
    public void RenderPlayerPost(RenderPlayerEvent.Post event)
    {
        try {
            Player player = event.getPlayer();
            player.getCapability(SizeChangeCapability.INSTANCE).ifPresent(sizeChange ->
            {
                float sizeMod = SizeUtility.getScale(player);
                if (sizeMod != 1.0) {
                //if (sizeChange.getIsScaled()) {     //need to check both pre and post.  Otherwise the stack will error
                    event.getPoseStack().popPose();
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @SubscribeEvent
    public void RenderLivingPre(RenderLivingEvent.Pre event)
    {
        try {
            LivingEntity livingEntity = event.getEntity();
            if (livingEntity != null && livingEntity instanceof Mob) {
                livingEntity.getCapability(SizeChangeCapability.INSTANCE).ifPresent(sizeChange ->
                {
                    float sizeMod = SizeUtility.getScale(livingEntity);
                    if (sizeMod != 1.0) {
                    //if (sizeChange.getIsScaled()) {
                        //float newScale = (float) sizeChange.getTargetScale();
                        event.getPoseStack().pushPose();
                        event.getPoseStack().scale(sizeMod, sizeMod, sizeMod);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void RenderLivingPost(RenderLivingEvent.Post event)
    {
        try {
            LivingEntity livingEntity = event.getEntity();
            if (livingEntity != null && livingEntity instanceof Mob) {
                livingEntity.getCapability(SizeChangeCapability.INSTANCE).ifPresent(sizeChange ->
                {
                    float sizeMod = SizeUtility.getScale(livingEntity);
                    if (sizeMod != 1.0) {
                    //if (sizeChange.getIsScaled()) {     //need to check both pre and post.  Otherwise the stack will error
                        event.getPoseStack().popPose();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
