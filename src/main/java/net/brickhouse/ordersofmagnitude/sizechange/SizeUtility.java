package net.brickhouse.ordersofmagnitude.sizechange;

import net.brickhouse.ordersofmagnitude.api.SizeChangeCapabilityInterface;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.horse.*;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraftforge.common.util.LazyOptional;

public class SizeUtility {

    public static float getScale(Entity entity){

        //if(entity instanceof LivingEntity livingEntity) {
            double scale;
            LazyOptional<SizeChangeCapabilityInterface.SizeChangeCapabilityFunctions> cap = entity.getCapability(SizeChangeCapability.INSTANCE);

            scale = cap.map(e -> e.getCurrentScale()).orElse(1.0D);

            return (float) scale;
       // }
        //return 1.0F;
    }

    public static boolean allowableCharacter(char pCodePoint){
        if(pCodePoint == '0' || pCodePoint == '1' || pCodePoint == '2' || pCodePoint == '3' || pCodePoint == '4' || pCodePoint == '5' || pCodePoint == '6' || pCodePoint == '7' || pCodePoint == '8' || pCodePoint == '9' || pCodePoint == '.' || pCodePoint == '?'){
            return true;
        }
        return false;
    }

    public static double getNewPassengersRidingOffset(Entity pVehicle){
        double scaleMod = SizeUtility.getScale(pVehicle);

        if(pVehicle instanceof Mule || pVehicle instanceof Donkey)
            return pVehicle.getBbHeight() * 0.78;
        else if(pVehicle instanceof Llama)
            return pVehicle.getBbHeight()*0.76;
        else if(pVehicle instanceof SkeletonHorse)
            return pVehicle.getBbHeight()*0.87;
        else if(pVehicle instanceof AbstractHorse)
            return pVehicle.getBbHeight()-(0.05*scaleMod);
        else if(pVehicle instanceof Pig)
            return pVehicle.getBbHeight()+0.02;
        else if(pVehicle instanceof Boat)
            return pVehicle.getBbHeight()*0.4;
        else if(pVehicle instanceof AbstractMinecart)
            return pVehicle.getBbHeight()*0.29;
        else if(pVehicle instanceof Strider)
            return calculateStriderOffset((Strider)pVehicle);
        return pVehicle.getBbHeight();
    }

    public static double getNewMyRidingOffset(Entity pPassenger){
        double scaleMod = SizeUtility.getScale(pPassenger);
        if(pPassenger instanceof Player)
            return pPassenger.getBbHeight()*(-0.4);
        return 0.0;
    }

    private static double calculateStriderOffset(Strider pStrider){
        float scaleMod = getScale(pStrider);
        float f = Math.min(0.25F, pStrider.animationSpeed);
        float f1 = pStrider.animationPosition;
        return (double)pStrider.getBbHeight() + 0.1D + (double)(0.12F * Mth.cos(f1 * 1.5F) * 2.0F * f);
    }
}
