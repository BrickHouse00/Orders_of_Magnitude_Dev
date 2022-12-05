package net.brickhouse.ordersofmagnitude.mixins;

import net.brickhouse.ordersofmagnitude.sizechange.SizeUtility;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class MixinServerGamePacketListernerImpl {

    @Shadow public ServerPlayer player;

    @ModifyConstant(method="handleMovePlayer", constant = @Constant(doubleValue = 0.0625D))
    public double handleScaledPlayerDelta(double value, ServerboundMovePlayerPacket pPacket){
        ServerPlayer player = this.player;

        double scaleMod = SizeUtility.getScale(player);
        if(scaleMod > 1.0){
            return value*scaleMod*scaleMod;         //value is compared to a sum of squares.  Square up the scaleMod to be sure we're not going to violate this.  Even with modifying the move function, we see a non-zero D11 delta
        }
        return value;
    }

    //Commenting this out for now.  Only used to monitor the move values prior to LOGGER throws when the player exceeds maximum delta changes
/*    @Inject(method="handleMovePlayer", at = @At(value="INVOKE", target="Lnet/minecraft/server/level/ServerPlayer;absMoveTo(DDDFF)V", ordinal = 1), locals=LocalCapture.CAPTURE_FAILHARD)
    private void monitorDeltas(ServerboundMovePlayerPacket pPacket, CallbackInfo ci, ServerLevel serverlevel, double d0, double d1, double d2, float f, float f1, double d3, double d4, double d5, double d6, double d7, double d8, double d9, double d10, double d11, int i, AABB aabb, boolean flag, boolean flag1, boolean flag2){
        System.out.println("handleScaledPlayerDelta d0: " + d0 + " d1: " + d1 +  " d2: " + d2 + " d7: " + d7 + " d8: " + d8 + " d9: " + d9 + " d11: " + d11);
    }

 */

    @ModifyArg(method = "handleMovePlayer", at=@At(value="INVOKE", target="Lnet/minecraft/server/level/ServerPlayer;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"), index=1)
    private Vec3 handlePlayerMove(MoverType par1, Vec3 vec3){
        ServerPlayer player = this.player;
        double scaleMod = SizeUtility.getScale(player);
        return scaleMod != 1.0F ? vec3.multiply(1.0/scaleMod, 1.0/scaleMod, 1.0/scaleMod) : vec3;
    }
}
