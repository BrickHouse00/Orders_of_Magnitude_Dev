//Credit for this function goes to gigabit101 for his work on Shrink.  This class was utilized while I work out a way to handle AABB issues through a Forge implementation

//isPoseClear fires constantly.  There must be a better way to handle the needs of this function and reduce computational load.  Setting the AABB is doable with the right logic, but this function does help with overhead collision.  Maybe using the deflate command when recalcing the BB
package net.brickhouse.ordersofmagnitude.mixins.client;

import net.brickhouse.ordersofmagnitude.sizechange.SizeUtility;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(Camera.class)
public abstract class MixinCamera {

    @Shadow public abstract Entity getEntity();

    //getMaxZoom is called each tick when viewing in third person.  This can be used to change the offset of the camera to hug in on smalls and pull back for bigs
    @ModifyArg(method = "setup", at =@At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(D)D"), index=0)
    private double adjustZoom(double pStartingDistance){
        double scale = (double) SizeUtility.getScale(this.getEntity());
        if(scale > 1.9){scale *= 0.44;}                                                           //Dampen.  This feels too far out at larger scales
        return pStartingDistance*scale;
    }

    //Fix for the camera deciding it wants to hug the player head when in third person
    @ModifyConstant(method = "getMaxZoom", constant=@Constant(floatValue = 0.1F))
    private float updateMaxZoomConstants(float value){
        float scale = SizeUtility.getScale(this.getEntity());
        return scale < 1.0F ? value*scale : value;
    }

    //Fog fix
    @ModifyConstant(method = "getNearPlane", constant=@Constant(floatValue = 0.05F))
    private float updateNearPlaneConstants(float value){
        float scale = SizeUtility.getScale((LivingEntity) this.getEntity());
        return scale < 1.0F ? value*scale : value;
    }

    @ModifyConstant(method="setup", constant = @Constant(doubleValue=0.3))
    private double scaleYOffsetSleeping(double constant, BlockGetter pLevel, Entity pEntity, boolean pDetached, boolean pThirdPersonReverse, float pPartialTick){
        double scaleMod = SizeUtility.getScale(pEntity);
        return scaleMod == 1.0 ? constant : constant*scaleMod;
    }

    /*
    //Camera on Snow/Soul sand Fix
    @Inject(method="tick", at=@At("TAIL"))
    public void onTick(CallbackInfo ci){
        if(this.entity!=null && SizeUtility.getScale(this.entity) < 0.25F){
            if(this.entity.level.getBlockState(this.entity.getOnPos().above()).is(Blocks.SNOW)){
                this.eyeHeight+=0.075D;
            }else if(this.entity.level.getBlockState(this.entity.getOnPos()).is(Blocks.SOUL_SAND)){
                this.eyeHeight+=0.075D;
            }
        }
    }*/
}
