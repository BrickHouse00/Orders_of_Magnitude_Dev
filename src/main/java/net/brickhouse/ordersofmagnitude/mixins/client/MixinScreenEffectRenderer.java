package net.brickhouse.ordersofmagnitude.mixins.client;

import net.brickhouse.ordersofmagnitude.sizechange.SizeUtility;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScreenEffectRenderer.class)
public abstract class MixinScreenEffectRenderer {

    //This modifies the code that ends up forcing the viewer to have the south face of a block completely take up the screen for whatever reason.  Must modify the GetY() line as it takes eyeheight into account
    @ModifyConstant(method="getOverlayBlock", constant=@Constant(floatValue = 0.1F), remap = false)
    private static float modEyeHeight(float value, Player player){
        float scale = SizeUtility.getScale(player);
        return value*scale;
    }

    //Remove the overlay block when scaled below the threshold for blocks like soul sand and paths.  These blocks cause the overlay to kick on since the player camera is technically inside the block AABB
    @Inject(method="getOverlayBlock", at=@At("RETURN"), cancellable = true, remap = false)
    private static void onGetOverlayBlock(Player pPlayer, CallbackInfoReturnable<Pair<BlockState, BlockPos>> cir){
        float scale = SizeUtility.getScale(pPlayer);
        if(scale < 0.7F){
            cir.setReturnValue(null);
        }
    }
}
