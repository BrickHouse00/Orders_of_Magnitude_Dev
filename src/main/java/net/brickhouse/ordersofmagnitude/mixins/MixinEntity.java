//Credit for this function goes to gigabit101 for his work on Shrink.  This class was utilized while I work out a way to handle AABB issues through a Forge implementation

//isPoseClear fires constantly.  There must be a better way to handle the needs of this function and reduce computational load.  Setting the AABB is doable with the right logic, but this function does help with overhead collision.  Maybe using the deflate command when recalcing the BB
package net.brickhouse.ordersofmagnitude.mixins;

import net.brickhouse.ordersofmagnitude.config.OMServerConfig;
import net.brickhouse.ordersofmagnitude.sizechange.SizeUtility;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow public abstract double getX();

    @Shadow public abstract double getY();

    @Shadow public abstract double getZ();

    @Shadow public Level level;
    @Shadow public abstract EntityDimensions getDimensions(Pose pPose);


    @Inject(at = @At("RETURN"), method = "canEnterPose", cancellable = true)
    public void isPoseClear(Pose pose, CallbackInfoReturnable<Boolean> cir) {
        if((Object) this instanceof Player player){
            float scaleMod = SizeUtility.getScale(player);
            if(scaleMod != 1.0){
                EntityDimensions entityDimensions = this.getDimensions(pose);
                entityDimensions = entityDimensions.scale(scaleMod);
                float f = entityDimensions.width / 2.0F;
                Vec3 vector3d = new Vec3(this.getX() - (double)f, this.getY(), this.getZ() - (double)f);
                Vec3 vector3d1 = new Vec3(this.getX() + (double)f, this.getY() + (double)entityDimensions.height, this.getZ() + (double)f);
                AABB box = new AABB(vector3d, vector3d1);
                cir.setReturnValue(this.level.noCollision(player, box.deflate(1.0E-7D)));
            }
        }
    }

    /**
     * Taller entities can do step damage on smaller entities.  Currently based on scales rather than hitbox sizes.  This way we can filter out entities not scaled by the mod
     */
    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At(value="INVOKE", target = "Lnet/minecraft/world/entity/Entity;push(DDD)V", ordinal = 0), cancellable = true)
    public void stompDamage(Entity pEntity, CallbackInfo ci){

        double thisEntitySize = SizeUtility.getScale((Entity)(Object)this);
        double passedEntitySize = SizeUtility.getScale(pEntity);
        if(thisEntitySize/passedEntitySize >= 9.99D){               //set it slightly less than 10x to grab any significant figures
            int playerStompSetting = OMServerConfig.PLAYER_STOMP_SETTING.get();
            if(pEntity instanceof Player && playerStompSetting < 2){
                if(playerStompSetting==1){
                    pEntity.hurt(DamageSource.GENERIC, ((LivingEntity) pEntity).getMaxHealth()*0.1F);
                }
            } else {
                pEntity.hurt(DamageSource.GENERIC, ((LivingEntity) pEntity).getMaxHealth());
            }
        }
    }

    //Can't change particle sizes on the fly I don't think.  So kill the sprint particle when shrunk so it doesn't eat up the field of view
    @Inject(at = @At("HEAD"), method = "spawnSprintParticle", cancellable = true)
    public void cancelWhenShrunk(CallbackInfo ci){
        if (SizeUtility.getScale((Entity) (Object) this) < 1.0) {
            ci.cancel();
        }
    }

    /**
     * The entities will de-render a short distance from the player when small.  This is due to the game using bounding box
     * scales to determine distance to camera.  Smaller entities will derender first once they're small enough is the base logic I think
     * We can either scale d0 back to what we THINK the bounding box would be, or we can scale down pDistance to make the game think
     * scaled entities are closer to the camera.  Larger sizes seem to be less effected by this and will only derender at the far clip
     */
    @ModifyArg(method="shouldRender", at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;shouldRenderAtSqrDistance(D)Z"))
    private double distanceCorrection(double pDistance){
        if((Object)this instanceof LivingEntity livingEntity) {
            double scaleMod = SizeUtility.getScale(livingEntity);
            return scaleMod < 1.0D ? pDistance*scaleMod : pDistance;
        }
        return pDistance;
    }


    //TODO Fix these values.  The movement is way too slow at small scales
    //Updated the position vector.  There are three places in the move() function that store the position vector after some calculation: limitPistonMovement, maybeBackOffFromEdge, and multiply(stuckSpeedMultiplier)
    //maybeBackOffFromEdge is the last one that calculates and stores the value again.  After that, the variable is only used.
    @ModifyVariable(method="move", at = @At("HEAD"))
    public Vec3 onMove(Vec3 pPos){
        Entity entity = (Entity)(Object) this;
        if(entity instanceof LivingEntity livingEntity && SizeUtility.getScale(entity) != 1.0) {
            double scale = getDeltaMovement(SizeUtility.getScale(livingEntity)); //.getBbHeight()/1.8;
            pPos = pPos.multiply(scale, scale, scale);
        }
        return pPos;
    }

    @ModifyConstant(method="move", constant = @Constant(floatValue = 0.6F, ordinal = 1))
    public float scaleNextStepRequirements(float value, MoverType pType){
        //MoverType Player is the type expected of a server player
        if(pType == MoverType.SELF && SizeUtility.getScale((Entity)(Object)this) != 1.0) {
            float scaleMod = SizeUtility.getScale((Entity) (Object) this);
            return value / getDeltaMovement(scaleMod);
        }
        return value;
    }

    private static float getDeltaMovement(float pScale){
        if (pScale > 1.0) {
            pScale *= 0.75F;             //for bigs, don't let them speed up too much.  Lower factor dampens more
        } else if (pScale < 0.002) {     //for micros, we need to scale it up a bit more or else they spend years trying to move around
            pScale *= 20;
        } else if (pScale < 0.03) {      //tinies still need a bump
            pScale *= 5;
        }else if (pScale < 0.12) {
            pScale*=2;                   //for smalls, don't let the factor get too low.  It's oppressively slow
        }
        return pScale;
    }

}
