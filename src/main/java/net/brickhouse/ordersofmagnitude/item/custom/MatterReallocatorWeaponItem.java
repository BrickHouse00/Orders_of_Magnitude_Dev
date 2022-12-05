package net.brickhouse.ordersofmagnitude.item.custom;

import net.brickhouse.ordersofmagnitude.client.MatterWeaponMenu;
import net.brickhouse.ordersofmagnitude.sizechange.SizeChangeCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class MatterReallocatorWeaponItem extends OMStorageItem implements MenuProvider {

    public int NUM_SLOTS;
    public MatterReallocatorWeaponItem(Properties pProperties)
    {
        super(pProperties.stacksTo(1), 5);
        NUM_SLOTS = 5;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack pStack, Player pPlayer, Entity pEntity){

        if(!(pEntity instanceof LivingEntity)){     //block any entity that isn't a living entity, such as boats and minecarts
            return false;
        }
        double targetScale = pStack.getOrCreateTag().getDouble("targetScale");
        if(targetScale != 0.0D && pEntity instanceof LivingEntity livingEntity && !pEntity.level.isClientSide){
            pEntity.getCapability(SizeChangeCapability.INSTANCE).ifPresent(sizeChange ->
            {
                sizeChange.ChangeSize(livingEntity, targetScale);
            });
            return true;
        }
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand){
        if(!pLevel.isClientSide()){
            if(pPlayer.isShiftKeyDown()){
                //open gui
                NetworkHooks.openGui((ServerPlayer) pPlayer, this);
            } else {
                rotateModules(pPlayer.getItemInHand(pHand), pPlayer);
            }
        }

        return new InteractionResultHolder<>(InteractionResult.PASS, pPlayer.getItemInHand(pHand));
    }

    private void rotateModules(ItemStack pWeapon, Player pPlayer){
        pWeapon.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler ->{
            ItemStack activeSlotStack = handler.extractItem(0,1,false);
            for(int i = 0; i < NUM_SLOTS-1; i++){
                if((i+1)<NUM_SLOTS){
                    ItemStack nextSlotStack = handler.extractItem(i+1,1, false);
                    handler.insertItem(i, nextSlotStack, false);
                }
            }
            handler.insertItem(4, activeSlotStack, false);
            double designScale = handler.getStackInSlot(0).getOrCreateTag().getDouble("designScale");
            pWeapon.getOrCreateTag().putDouble("targetScale", designScale);
            if(designScale == 0.0) {
                pPlayer.displayClientMessage(new TranslatableComponent("gui.ordersofmagnitude.matter_reallocator_weapon.empty.equipped"), true);
            } else {
                pPlayer.displayClientMessage(new TranslatableComponent("gui.ordersofmagnitude.matter_reallocator_weapon.activated.line1").append(Double.toString(designScale*100))
                        .append( new TranslatableComponent("gui.ordersofmagnitude.matter_reallocator_weapon.activated.line2")), true);
            }
        });

    }
    //Misc Functions
    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, Player player)
    {
        //not sure if this is really needed now that ClickInputEvent is being used to detect the client using the tablet
        return true;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new MatterWeaponMenu(pContainerId, pPlayerInventory, pPlayer.getMainHandItem());
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent(this.getOrCreateDescriptionId());
    }
}
