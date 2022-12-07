package net.brickhouse.ordersofmagnitude.mixins.block;

import net.brickhouse.ordersofmagnitude.sizechange.SizeUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BasePressurePlateBlock.class)
public abstract class MixinBasePressurePlateBlock {

    @Inject(method="checkPressed", at=@At("HEAD"), cancellable = true)
    private void onCheckPressed(Entity pEntity, Level pLevel, BlockPos pPos, BlockState pState, int pCurrentSignal, CallbackInfo ci){
        if(pEntity != null && SizeUtility.getScale(pEntity) <= 0.25){       //check if entity is null.  Will be null when entity leaves the pressure plate
            ci.cancel();
        }
    }

}
