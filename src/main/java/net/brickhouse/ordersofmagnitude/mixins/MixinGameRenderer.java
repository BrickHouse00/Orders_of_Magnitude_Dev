package net.brickhouse.ordersofmagnitude.mixins;

import net.brickhouse.ordersofmagnitude.sizechange.SizeUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Shadow    @Final    @Mutable
    Minecraft minecraft;

    //Need to modify getProjectionMatrix here and in CubeMap (not sure if the latter is NEEDED)
    @ModifyConstant(method="getProjectionMatrix", constant=@Constant(floatValue = 0.05F))
    private float changeNearPlane(float value){
        float scaleMod = SizeUtility.getScale(minecraft.getCameraEntity());
        return scaleMod < 1.0F ? (value * scaleMod) : value;
    }

    @ModifyVariable(method="bobView", ordinal = 3, at = @At(value="LOAD", ordinal = 0))
    private float scaleWalkDist(float f){
        float scaleMod = SizeUtility.getScale(minecraft.getCameraEntity());
        if(scaleMod < 1.0) {
            return (f * scaleMod);
        }
        return f;
    }

    //TODO Determine if bobHurt also needs modifying

}
