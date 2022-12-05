package net.brickhouse.ordersofmagnitude.mixins.block;

import net.brickhouse.ordersofmagnitude.sizechange.SizeUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;



@Mixin(SnowLayerBlock.class)
public abstract class MixinSnowLayerBlock {

    @Shadow @Final protected static VoxelShape[] SHAPE_BY_LAYER;

    @Shadow @Final public static IntegerProperty LAYERS;

    /*Update the collision box for snow to prevent smalls from clipping into the geometry.  Snow collision works by creating a collision surface that is always 1 layer shorter than what is visible
    * Here, we check if the entity requesting the snow's collision shape is scaled down.  If it is, then we tell the collision builder to increase the snow collision height to be equal to its rendered height
    * Needs testing: scaled and non-scaled entities need to be tested on the same square.  Will this create conflicts?  Unsure if collision is handled client side or server side at this point
    * Requires potential fixing: players who exceed the scale cutoff will not be moved above the snow when scaling down.  This causes them to clip the geometry.  Unsure of how to fix at this point unless it's handled during size change event
     */
    @Inject(method="getCollisionShape", at=@At("RETURN"), cancellable = true)
    public void onGetCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext, CallbackInfoReturnable<VoxelShape> cir){
        EntityCollisionContext entityCollisionContext = (EntityCollisionContext) pContext;
        Entity entity = entityCollisionContext.getEntity();
        if(entity != null && entity instanceof LivingEntity && SizeUtility.getScale(entity) <= 0.25F)
        {
            cir.setReturnValue(SHAPE_BY_LAYER[pState.getValue(LAYERS)]);
        }
    }
}
