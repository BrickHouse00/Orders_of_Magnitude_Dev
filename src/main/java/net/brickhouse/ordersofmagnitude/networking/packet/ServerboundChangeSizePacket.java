package net.brickhouse.ordersofmagnitude.networking.packet;

import net.brickhouse.ordersofmagnitude.advancements.ModCriteriaTriggers;
import net.brickhouse.ordersofmagnitude.item.custom.MatterReallocatorTabletItem;
import net.brickhouse.ordersofmagnitude.sizechange.SizeChangeCapability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundChangeSizePacket {

    private final double targetScale;
    private final int entityID;
    public ServerboundChangeSizePacket(int pEntityID, double pTargetScale) {
        entityID = pEntityID;
        targetScale = pTargetScale;
    }

    public static void encoder(ServerboundChangeSizePacket message, FriendlyByteBuf buffer) {
        // Write to buffer
        buffer.writeInt(message.entityID);
        buffer.writeDouble(message.targetScale);
    }

    public static ServerboundChangeSizePacket decoder(FriendlyByteBuf buffer) {
        // Create packet from buffer data
        return new ServerboundChangeSizePacket(buffer.readInt(), buffer.readDouble());
    }
    public void handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // Server execution
            ServerPlayer player = supplier.get().getSender();
            ServerLevel level = player.getLevel();
            Entity entity = level.getEntity(entityID);
            //System.out.print("ChangeSizeC2SPacket clientside: " + level.isClientSide() + "\n");

            entity.getCapability(SizeChangeCapability.INSTANCE).ifPresent(sizeChange ->
                {
                    //ItemStack tablet = player.getMainHandItem();

                    //System.out.print("ChangeSizeC2SPacket hasTag: " + tablet.hasTag() + "\n");
                    //if(tablet.hasTag()) {
                        //double targetScale = tablet.getTag().getDouble("targetScale");
                        //if(!((Player) player).isCreative() && targetScale != sizeChange.getTargetScale()){
                          //  ((MatterReallocatorTabletItem) tablet.getItem()).usePower(tablet);
                        //}
                        sizeChange.ChangeSize((LivingEntity) entity, targetScale); //add property for target size once GUI is set up
                        ModCriteriaTriggers.CHANGE_SIZE.trigger(player, targetScale);
                    //}
                });
        });
        context.setPacketHandled(true);
    }
}
