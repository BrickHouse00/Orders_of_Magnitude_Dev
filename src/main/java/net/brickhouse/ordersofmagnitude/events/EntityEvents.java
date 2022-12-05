package net.brickhouse.ordersofmagnitude.events;

import net.brickhouse.ordersofmagnitude.OrdersOfMagnitude;
import net.brickhouse.ordersofmagnitude.api.SizeChangeCapabilityInterface;
import net.brickhouse.ordersofmagnitude.config.OMServerConfig;
import net.brickhouse.ordersofmagnitude.item.custom.MatterReallocatorTabletItem;
import net.brickhouse.ordersofmagnitude.sizechange.SizeChangeCapability;
import net.brickhouse.ordersofmagnitude.sizechange.SizeUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = OrdersOfMagnitude.MOD_ID)
public class EntityEvents {
    //Capability Events
    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event)
    {
        event.register(SizeChangeCapabilityInterface.class);
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event)
    {
        if(event.getObject() instanceof LivingEntity)
        {
            if(!event.getObject().getCapability(SizeChangeCapability.INSTANCE).isPresent()) {
                event.addCapability(new ResourceLocation(OrdersOfMagnitude.MOD_ID, "mycap"), new SizeChangeCapabilityInterface.SizeChangeCapabilityProvider((LivingEntity) event.getObject()));
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event){
        if(!event.getWorld().isClientSide() && event.getEntity() instanceof LivingEntity livingEntity){
            livingEntity.refreshDimensions();
            syncEntity(livingEntity);
        }
    }

    //Player Events
    @SubscribeEvent
    public static void onPlayerRespawnEvent(PlayerEvent.PlayerRespawnEvent event){
        syncEntity(event.getPlayer());
    }

    @SubscribeEvent
    public static void onPlayerChangedDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event){
        syncEntity(event.getPlayer());
    }

    @SubscribeEvent
    public static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event){
        syncEntity(event.getPlayer());
    }

    @SubscribeEvent
    public static void onPlayerCloneEvent(PlayerEvent.Clone event) {
        if(event.isWasDeath()) {
            event.getOriginal().getCapability(SizeChangeCapability.INSTANCE).ifPresent(oldStore -> {
                event.getOriginal().getCapability(SizeChangeCapability.INSTANCE).ifPresent(newStore -> {
                    newStore.CopyFrom(oldStore);
                });
            });
        }
    }

    //DELETING THE PLAYERINTERACT EVENTS IN FAVOR OF USING CLIENT SIDE INPUT EVENT
/*
    //PlayerInteract Events.  These are used to encapsulate the player left clicking the tablet
    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event){
        System.out.println("onLeftClickEmpty: isClientSide() " + event.getPlayer().getLevel().isClientSide());
        if(event.getPlayer().getLevel().isClientSide()) {
            ItemStack itemStack = event.getItemStack();
            if (itemStack.getItem() instanceof MatterReallocatorTabletItem tablet) {
                //tablet.LeftClickAction((LivingEntity) event.getPlayer());
            }
        }
    }

    //This event fires twice.  Once for client and once for server.  Check for client side and ape the client side impl
    //This event also fires continuously and can lead to additional left click action uses
    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event){

        //Player player = event.getPlayer();
        //Level level = player.getLevel();
        //player.sendMessage(new TextComponent("onLeftClickBlock clientside: " + level.isClientSide()), player.getUUID());
        System.out.println("onLeftClickBlock: isClientSide() " + event.getPlayer().getLevel().isClientSide());
        if(event.getPlayer().getLevel().isClientSide()) {
            ItemStack itemStack = event.getItemStack();
            if (itemStack.getItem() instanceof MatterReallocatorTabletItem tablet) {
                //tablet.LeftClickAction((LivingEntity) event.getPlayer());
            }
        }
    }
*/
    @SubscribeEvent
    public static void onStartTrackingEvent(PlayerEvent.StartTracking event){
        if(event.getPlayer() instanceof ServerPlayer && event.getTarget() instanceof LivingEntity livingEntity) {
            syncEntity(livingEntity);
        }
    }

    //Entity Events
    @SubscribeEvent
    public static void onEntityJoinsWorldEvent(EntityJoinWorldEvent event){
        if(!event.getWorld().isClientSide() && event.getEntity() instanceof LivingEntity livingEntity){

            livingEntity.refreshDimensions();
            syncEntity(livingEntity);
        }
    }

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event){
        if(event.getEntity() instanceof LivingEntity livingEntity) {
            livingEntity.getCapability(SizeChangeCapability.INSTANCE).ifPresent(sizeChange ->
            {
                if(sizeChange.getIsScaled()) {
                    if(sizeChange.getCurrentScale() < 1.0) {
                        float jumpMultiplier = (float) (OMServerConfig.SMALL_JUMP_MODIFIER.get() * (sizeChange.getCurrentScale() - 1D));//-0.07F;
                        if (event.getEntity() instanceof Player) {
                            event.getEntity().push(0, jumpMultiplier, 0);
                        }
                    } else if(sizeChange.getCurrentScale() > 1.0){
                        float jumpMultiplier = (float) (OMServerConfig.LARGE_JUMP_MODIFIER.get() * sizeChange.getCurrentScale());//-0.07F;
                        if (event.getEntity() instanceof Player) {
                            event.getEntity().push(0, jumpMultiplier, 0);
                        }
                    }
                }
            });
        }
    }

    //@SubscribeEvent
    //public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event){
        //if(event.getEntity() instanceof LivingEntity livingEntity && SizeUtility.getScale(livingEntity) < 1.0F){
            /*livingEntity.getCapability(SizeChangeCapability.INSTANCE).ifPresent(sizeChange ->
            {
               if(sizeChange.getIsScaled() && livingEntity.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(UUID.fromString(sizeChange.getSpeedAttributeUUID())) == null){
                   //sizeChange.ModifyAttributes(livingEntity, sizeChange.getCurrentScale());       //this is a hacky work around from sprinting removing the speed attribute modifier for whatever reason
                   //This workaround seems to NOT be working now.  Player sprints at NORMAL speed for some reason, and then reverts back to normal walk rate
               }
            });*/
            //TODO Figure out the best way to pop the smalls above the snow block so the player doesn't clip the geometry in 1st person
            //This method "works" but the player is being moved every tick, so they end up in a cycle of falling since the snow has no physics
            /*BlockPos blockPos = livingEntity.getOnPos();
            BlockState blockState = livingEntity.level.getBlockState(blockPos.above());
            if(blockState.is(Blocks.SNOW)){
                //Vec3 newPos = new Vec3(livingEntity.getX(), livingEntity.getY()+1.0, livingEntity.getZ());
                livingEntity.setPos(livingEntity.getX(), livingEntity.getY()+0.125D, livingEntity.getZ());
            }*/



        //}
    //}

    //This event fires when entity is loaded and when using RefreshDimensions()
    //When fired by refresh dimensions, it appears to be working on a FILO processing.  If client is fired first and server is fired immediately after, server side is processed first.  This leads to unexpected AABB computing behavior.
    //AABB is updated by server side and eye height is updated by client side.  Server must be updated last for AABB to be updated correctly
    @SubscribeEvent
    public static void onSize(EntityEvent.Size event) {
        if (event.getEntity() instanceof LivingEntity livingEntity){
            livingEntity.getCapability(SizeChangeCapability.INSTANCE).ifPresent(sizeChange ->
            {
                if(sizeChange.getIsScaled()) {
                    float newScale = (float) sizeChange.getTargetScale();
                    event.setNewSize(event.getNewSize().scale(newScale), true);
                    event.setNewEyeHeight(event.getNewEyeHeight() * newScale);
                    sizeChange.setCurrentScale((double) newScale);
                    if (livingEntity.getLevel().isClientSide() && livingEntity instanceof Player) {
                        livingEntity.setPos(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                    } //else if (livingEntity instanceof Player) {
                        //sizeChange.ResetAttributes(livingEntity);
                    //}
                    //if (livingEntity instanceof Player) {
                        //double newBase = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED).getValue();
                        //System.out.print("OnSize Event player speed: " + newBase + " isClientSide: " + livingEntity.getLevel().isClientSide() + "\n");
                    //}
                }
            });
        }
    }

    //Private function to sync data for the player events above
    private static void syncEntity(LivingEntity livingEntity){
        livingEntity.getCapability(SizeChangeCapability.INSTANCE).ifPresent(sizeChange -> {
            sizeChange.sync(livingEntity);
        });

    }
}
