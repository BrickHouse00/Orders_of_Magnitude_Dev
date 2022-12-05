package net.brickhouse.ordersofmagnitude.mixins.vehicle;

import net.brickhouse.ordersofmagnitude.config.OMServerConfig;
import net.brickhouse.ordersofmagnitude.sizechange.SizeUtility;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.Llama;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Llama.class)
public abstract class MixinLlama {

    @Redirect(method="positionRider", at=@At(value="INVOKE", target = "Lnet/minecraft/world/entity/Entity;setPos(DDD)V"))
    private void onSetPos(Entity pPassenger, double pX, double pY, double pZ){
        if(OMServerConfig.ALLOW_SCALED_RIDING.get()) {
            Llama thisLlama = (Llama) (Object) this;
            float vehicleScale = SizeUtility.getScale(thisLlama);
            float riderScale = SizeUtility.getScale(pPassenger);
            if (riderScale == 1.0 && vehicleScale == 1.0) {
                pPassenger.setPos(pX, pY, pZ);
            } else {
                float f = Mth.cos(thisLlama.yBodyRot * ((float) Math.PI / 180F));
                float f1 = Mth.sin(thisLlama.yBodyRot * ((float) Math.PI / 180F));
                float f2 = 0.3F * vehicleScale;
                double d0 = thisLlama.getY() + SizeUtility.getNewPassengersRidingOffset(thisLlama) + SizeUtility.getNewMyRidingOffset(pPassenger);
                pPassenger.setPos(thisLlama.getX() + (double) (0.3F * f1 * vehicleScale),
                        thisLlama.getY() + SizeUtility.getNewPassengersRidingOffset(thisLlama) + SizeUtility.getNewMyRidingOffset(pPassenger),
                        thisLlama.getZ() - (double) (0.3F * f * vehicleScale));
            }
        } else {
            pPassenger.setPos(pX, pY, pZ);
        }
    }
}
