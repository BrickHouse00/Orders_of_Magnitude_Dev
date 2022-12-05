package net.brickhouse.ordersofmagnitude.item.custom;

import net.brickhouse.ordersofmagnitude.sizechange.SizeChangeCapability;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class MatterReallocatorDeviceItem extends Item{

    public MatterReallocatorDeviceItem(Item.Properties pProperties)
    {
        super(pProperties.stacksTo(1));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack pStack, Player pPlayer, Entity pEntity){

        if(pEntity instanceof LivingEntity && !pEntity.level.isClientSide){
            pEntity.getCapability(SizeChangeCapability.INSTANCE).ifPresent(sizeChange ->
            {
                sizeChange.ChangeSize((LivingEntity) pEntity, 0.25); //add property for target size once GUI is set up.  2 - 0.25 target is place holder
                //TODO fix issue where player is not setting the entity's target scale properly.  Likely would need new variable and function in API to send the player's device scale without overriding an entity's own target or device scale
                //TODO current functionality is a target's scale is set by their own initialized provider, not the attacker's. This leads the target to scale to something NOT what the player selected.
                //TODO Likely will rename the function used by tablet to ChangeSizeSelf() and make new function ChangeSizeTarget() for device/potions
            });
            return true;
        }
        return false;
    }
}
