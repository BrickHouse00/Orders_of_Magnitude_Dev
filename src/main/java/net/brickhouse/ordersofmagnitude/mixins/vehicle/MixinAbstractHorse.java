package net.brickhouse.ordersofmagnitude.mixins.vehicle;

import net.brickhouse.ordersofmagnitude.config.OMServerConfig;
import net.brickhouse.ordersofmagnitude.sizechange.SizeUtility;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractHorse.class)
public abstract class MixinAbstractHorse {

    @Shadow public abstract float getStandAnim(float pPartialTick);

    @Redirect(method="positionRider", at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;setPos(DDD)V", ordinal = 0))
    private void updateScaledPositionRider(Entity pPassenger, double pX, double pY, double pZ){
        if(OMServerConfig.ALLOW_SCALED_RIDING.get()) {
            AbstractHorse thisHorse = (AbstractHorse) (Object) this;
            double riderScale = SizeUtility.getScale(pPassenger);
            double horseScale = SizeUtility.getScale(thisHorse);
            if (riderScale == 1.0 && horseScale == 1.0) {
                pPassenger.setPos(pX, pY, pZ);
            } else {
                float f2 = 0.15F * (float) horseScale * this.getStandAnim(1.0F);
                pPassenger.setPos(pX, thisHorse.getY() + SizeUtility.getNewPassengersRidingOffset(thisHorse) + SizeUtility.getNewMyRidingOffset(pPassenger) + (double) f2, pZ);
            }
        } else {
            pPassenger.setPos(pX, pY, pZ);
        }
    }

    @ModifyConstant(method="positionRider", constant = @Constant(floatValue = 0.7F))
    private float scaleRiderXOffset(float constant){
        if(OMServerConfig.ALLOW_SCALED_RIDING.get()) {
            AbstractHorse thisHorse = (AbstractHorse) (Object) this;
            float horseScale = SizeUtility.getScale(thisHorse);
            return horseScale == 1.0 ? constant : constant * horseScale;
        }
        return constant;
    }

    @ModifyConstant(method="positionRider", constant = @Constant(floatValue = 0.15F))
    private float scaleRiderYOffset(float constant){
        if(OMServerConfig.ALLOW_SCALED_RIDING.get()) {
            AbstractHorse thisHorse = (AbstractHorse) (Object) this;
            float horseScale = SizeUtility.getScale(thisHorse);
            return horseScale == 1.0 ? constant : constant * horseScale;
        }
        return constant;
    }
}
