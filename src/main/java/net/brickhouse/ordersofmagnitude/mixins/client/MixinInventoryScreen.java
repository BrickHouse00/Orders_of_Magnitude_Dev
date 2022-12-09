package net.brickhouse.ordersofmagnitude.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.brickhouse.ordersofmagnitude.sizechange.SizeUtility;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(InventoryScreen.class)
public abstract class MixinInventoryScreen {


    //Rescaling with a redirect.  Tried modifyargs and would get crashes
    @Redirect(method="renderEntityInInventory",at=@At(value="INVOKE", target="Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V", ordinal=1))
    private static void updateScale(PoseStack instance, float pX, float pY, float pZ, int pPosX, int pPosY, int pScale, float pMouseX, float pMouseY, LivingEntity pLivingEntity){
        float scaleMod = SizeUtility.getScale(pLivingEntity);
        instance.scale(pX/scaleMod, pY/scaleMod, pZ/scaleMod);
    }
}
