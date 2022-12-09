package net.brickhouse.ordersofmagnitude.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.brickhouse.ordersofmagnitude.sizechange.SizeUtility;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EntityRenderDispatcher.class)
public abstract class MixinEntityRendererDispatcher {


    //Scale the shadow plox
    @ModifyArg(method="render", at=@At(value="INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V"), index=6)
    private float newShadowSize(PoseStack pMatrixStack, MultiBufferSource pBuffer, Entity pEntity, float pWeight, float pPartialTicks, LevelReader pLevel, float pSize){
        float scaleMod = SizeUtility.getScale(pEntity);
        return pSize*scaleMod;
    }

}
