package net.brickhouse.ordersofmagnitude.networking.packet;

import net.brickhouse.ordersofmagnitude.item.custom.MatterReallocatorTabletItem;
import net.brickhouse.ordersofmagnitude.sizechange.SizeChangeCapability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundChangeSizePacket {

    public ServerboundChangeSizePacket() {

    }

    public static void encoder(ServerboundChangeSizePacket message, FriendlyByteBuf buffer) {
        // Write to buffer

    }

    public static ServerboundChangeSizePacket decoder(FriendlyByteBuf buffer) {
        // Create packet from buffer data
        return new ServerboundChangeSizePacket();
    }
    public void handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // Server execution
            ServerPlayer player = supplier.get().getSender();
            ServerLevel level = player.getLevel();

            //System.out.print("ChangeSizeC2SPacket clientside: " + level.isClientSide() + "\n");

                player.getCapability(SizeChangeCapability.INSTANCE).ifPresent(sizeChange ->
                {
                    ItemStack tablet = player.getMainHandItem();

                    //System.out.print("ChangeSizeC2SPacket hasTag: " + tablet.hasTag() + "\n");
                    if(tablet.hasTag()) {
                        double targetScale = tablet.getTag().getDouble("targetScale");
                        if(!((Player) player).isCreative() && targetScale != sizeChange.getTargetScale()){
                            ((MatterReallocatorTabletItem) tablet.getItem()).usePower(tablet);
                        }
                        sizeChange.ChangeSize((ServerPlayer) player, targetScale); //add property for target size once GUI is set up
                    }
                });



        });
        context.setPacketHandled(true);
    }
}
