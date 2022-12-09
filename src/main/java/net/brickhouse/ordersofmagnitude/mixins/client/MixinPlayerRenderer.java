package net.brickhouse.ordersofmagnitude.mixins.client;

import net.brickhouse.ordersofmagnitude.sizechange.SizeUtility;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PlayerRenderer.class)

public abstract class MixinPlayerRenderer {

    @ModifyConstant(method="getRenderOffset(Lnet/minecraft/client/player/AbstractClientPlayer;F)Lnet/minecraft/world/phys/Vec3;", constant=@Constant(doubleValue = -0.125D))
    private double updateCrouchingHeightOffset(double value, AbstractClientPlayer pEntity){
        double scale = (double) SizeUtility.getScale(pEntity);
        return value*scale;
    }
}
