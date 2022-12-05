package net.brickhouse.ordersofmagnitude.mixins;

import net.brickhouse.ordersofmagnitude.sizechange.SizeUtility;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {


    //adjust the bounding box of smalls so they aren't accidentally cut out by the frustrum
    @ModifyVariable(method = "shouldRender", ordinal = 0, at=@At(value="STORE"))
    private AABB shouldRenderScaledHeight(AABB aabb, Entity pLivingEntity) {
        double scaleMod = SizeUtility.getScale(pLivingEntity);
        if(scaleMod < 1.0D) {
            aabb = aabb.inflate(pLivingEntity.getBbHeight() / scaleMod);
        }
        return aabb;
    }


    @ModifyVariable(method = "renderNameTag", ordinal = 0, at=@At(value="STORE"))
    private float renderNameTagScaledHeight(float f, Entity pEntity){
        float scaleMod = SizeUtility.getScale(pEntity);
        if(scaleMod != 1.0F) {
            f = ((pEntity.getBbHeight() / scaleMod) + (0.5F));
        }
        return f;
    }
}
