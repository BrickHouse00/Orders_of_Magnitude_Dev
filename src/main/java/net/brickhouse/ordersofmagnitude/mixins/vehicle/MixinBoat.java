package net.brickhouse.ordersofmagnitude.mixins.vehicle;

import net.brickhouse.ordersofmagnitude.config.OMServerConfig;
import net.brickhouse.ordersofmagnitude.sizechange.SizeUtility;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Boat.class)
public abstract class MixinBoat {

    @Redirect(method="positionRider", at=@At(value="INVOKE", target = "Lnet/minecraft/world/entity/Entity;setPos(DDD)V"))
    private void onSetPos(Entity pPassenger, double pX, double pY, double pZ){
        if(OMServerConfig.ALLOW_SCALED_RIDING.get()) {
            Boat thisBoat = (Boat) (Object) this;
            float vehicleScale = SizeUtility.getScale(thisBoat);
            float riderScale = SizeUtility.getScale(pPassenger);
            if (riderScale == 1.0 && vehicleScale == 1.0) {
                pPassenger.setPos(pX, pY, pZ);
            } else {
                float f1 = (float) ((thisBoat.isRemoved() ? (double) 0.01F : SizeUtility.getNewPassengersRidingOffset(thisBoat)) + SizeUtility.getNewMyRidingOffset(pPassenger));
                pPassenger.setPos(pX, thisBoat.getY() + (double) f1, pZ);
            }
        } else {
            pPassenger.setPos(pX, pY, pZ);
        }
    }
}
