package net.brickhouse.ordersofmagnitude.mixins;

import net.brickhouse.ordersofmagnitude.sizechange.SizeUtility;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Shadow protected boolean jumping;

    @Shadow public abstract boolean isFallFlying();

    //Need to modify the animation speed.  Otherwise bigs are too fast and smalls barely animate
    @ModifyConstant(method="calculateEntityAnimation", constant=@Constant(floatValue = 4.0F))
    private float updatedAnimationFloat(float value){
        float scaleMod = SizeUtility.getScale((Entity)(Object) this);
        if(scaleMod < 1.0) {
            return value / scaleMod;
        }
        return value;
    }

    //Normally, the player takes damage after falling more than 3 blocks.  Modify that value to be consistent with the player's scale
    //i.e., at 200%, the value would be 6.0F, etc.
    @ModifyConstant(method="calculateFallDamage", constant=@Constant(floatValue = 3.0F))
    private float scaleFallingMaxNoDamageHeight(float value){
        float scaleMod = SizeUtility.getScale((Entity)(Object)this);
        if(scaleMod > 1.0F){
            return value*scaleMod;
        } else if(scaleMod <= 0.33){
            return 1.0F;             //Special case for smalls.  1 block heights become lethal at tiny sizes, so return 1.0 to prevent accidental deaths regardless of shrunk state
        }
        return value;
    }

    //Let entities grab a vertical surface and climb when small.  This will give mobility to smalls to replace the loss of mobility from scaling jumping
    @Inject(method="onClimbable", at = @At("RETURN"), cancellable = true)
    public void onSmallClimbable(CallbackInfoReturnable<Boolean> cir){
        double scaleMod = SizeUtility.getScale((Entity)(Object)this);
        if(!this.isFallFlying() && scaleMod < 1.0 && !this.jumping && !(((Entity)(Object)this).getDeltaMovement().y <=0.0D)){
            cir.setReturnValue(true);
        }
    }
/*
    @ModifyConstant(method="setPosToBed", constant = @Constant(doubleValue = 0.6875D))
    private double scaleYsetPosToBed(double constant){
        double scaleMod = SizeUtility.getScale((LivingEntity)(Object)this);
        return scaleMod == 1.0 ? constant:constant*scaleMod;
    }

 */
}
