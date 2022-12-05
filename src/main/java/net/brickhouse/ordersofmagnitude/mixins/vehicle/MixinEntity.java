package net.brickhouse.ordersofmagnitude.mixins.vehicle;

import net.brickhouse.ordersofmagnitude.config.OMServerConfig;
import net.brickhouse.ordersofmagnitude.sizechange.SizeUtility;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class MixinEntity {


    /*
        @Inject(method="getPassengersRidingOffset", at=@At("RETURN"), cancellable = true)
        private void modPassengerRidingOffset(CallbackInfoReturnable<Double> cir){
            Entity thisVehicle = (Entity)(Object)this;
            double scaleMod = SizeUtility.getScale(thisVehicle);
            if(thisVehicle instanceof AbstractHorse)
                cir.setReturnValue((double)thisVehicle.getBbHeight()-(0.05*scaleMod));      //base eyeheight of the horse seems like a great place to line up to the saddle.  but eyeheight lowers rapidly the smaller the mob gets.  This math should place the offset around where eyeheight is
            else if(thisVehicle instanceof Pig)
                cir.setReturnValue((double)thisVehicle.getBbHeight());                      //pig's hitbox is perfectly set for saddle height
        }
    */

    //This is called when an entity is riding a vehicle or animal.  getPassengersRidingOffset is 3/4 of the vehicle height by default.  Separate Mixins will modify constants that need modifying for individual entities
    @Redirect(method="positionRider(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity$MoveFunction;)V", at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity$MoveFunction;accept(Lnet/minecraft/world/entity/Entity;DDD)V"))
    private void onAccept(Entity.MoveFunction pCallback, Entity pPassenger, double pX, double pY, double pZ){
        if(OMServerConfig.ALLOW_SCALED_RIDING.get()) {
            Entity thisVehicle = (Entity) (Object) this;
            double riderScale = SizeUtility.getScale(pPassenger);
            double vehicleScale = SizeUtility.getScale(thisVehicle);
            //Use vanilla offsets when we can.  If the scales of either passenger or vehicle are scaled, then we launch into custom offsets
            if (riderScale == 1.0 && vehicleScale == 1.0) {
                pCallback.accept(pPassenger, pX, pY, pZ);
            } else {
                double d0 = thisVehicle.getY() + SizeUtility.getNewPassengersRidingOffset(thisVehicle) + SizeUtility.getNewMyRidingOffset(pPassenger);
                pCallback.accept(pPassenger, pX, d0, pZ);
            }
        } else {
            pCallback.accept(pPassenger, pX, pY, pZ);
        }
    }



}
