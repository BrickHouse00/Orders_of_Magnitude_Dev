package net.brickhouse.ordersofmagnitude.mixins;

import net.brickhouse.ordersofmagnitude.sizechange.SizeUtility;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.extensions.IForgePlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(IForgePlayer.class)
public interface MixinIForgePlayerMixin {


    /**
     * @author BrickHouse
     * @reason Need to modify the range for scaled players.  Returns the vanilla range if scale is set to 1
     */

    @Overwrite(remap = false)
    default double getAttackRange(){
        Player player = (Player)(Object)this;
        double range = player.getAttributeValue(ForgeMod.ATTACK_RANGE.get());
        double scaleMod = SizeUtility.getScale(player);
        if(scaleMod == 1.0D){
            return range == 0 ? 0 : range + (player.isCreative() ? 3 : 0);
        }
        //if player is scaled, let's do some math!
        if(player.isCreative() && range != 0.0){
            range += 3.0;
        }
        if(scaleMod < 1.0D){
            range *= scaleMod;
        } else if(scaleMod > 1.0D){
            range *= (scaleMod*0.5D);
        }
        return range==0 ? 0.0 : range;
    }

    /**
     * @author Brickhouse
     * @reason Need to modify the reach for scaled players.  Returns the vanilla reach if scale is set to 1
     */

    @Overwrite(remap = false)
    default double getReachDistance(){
        Player player = (Player)(Object)this;
        double reach = player.getAttributeValue(ForgeMod.REACH_DISTANCE.get());
        double scaleMod = SizeUtility.getScale(player);
        if(scaleMod == 1.0D){
            return reach == 0 ? 0 : reach + (player.isCreative() ? 0.5 : 0);
        }
        if(player.isCreative()){
            reach += 0.5;
        }

        if(scaleMod < 1.0D) {
            reach *= scaleMod;
            if(reach < 1.0){
                reach = 1.0;
            }
        } else if (scaleMod > 1.0D){
            reach *= (scaleMod*0.5D);
        }
        return reach;
    }
}
