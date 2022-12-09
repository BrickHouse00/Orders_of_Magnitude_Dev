package net.brickhouse.ordersofmagnitude.mixins;

import net.brickhouse.ordersofmagnitude.sizechange.SizeUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(CubeMap.class)
public abstract class MixinCubeMap {

    //Need to modify getProjectionMatrix here and in GameRenderer (not sure if here is NEEDED)
    @OnlyIn(Dist.CLIENT)
    @ModifyConstant(method="render", constant=@Constant(floatValue = 0.05F))
    private float changeNearPlane(float value) {
        Player player = Minecraft.getInstance().player;
        if(player != null) {
            float scaleMod = SizeUtility.getScale(player);
            return scaleMod < 1.0 ? (value * scaleMod) : value;
        }
        return value;
    }
}
