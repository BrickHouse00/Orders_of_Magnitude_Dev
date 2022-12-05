package net.brickhouse.ordersofmagnitude.mixins.block;

import net.brickhouse.ordersofmagnitude.sizechange.SizeUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoulSandBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoulSandBlock.class)
public abstract class MixinSoulSandBlock {

    //Update soul sand to have a full cube of height when a small is standing on it.  This will prevent geometry collision
    @Inject(method="getCollisionShape", at=@At("RETURN"), cancellable = true)
    public void onGetCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext, CallbackInfoReturnable<VoxelShape> cir) {
        EntityCollisionContext entityCollisionContext = (EntityCollisionContext) pContext;
        Entity entity = entityCollisionContext.getEntity();
        if (entity != null && entity instanceof LivingEntity && SizeUtility.getScale(entity) <= 0.25F) {
            cir.setReturnValue(Block.box(0.0,0.0,0.0,16.0,16.0,16.0));
        }
    }

}
