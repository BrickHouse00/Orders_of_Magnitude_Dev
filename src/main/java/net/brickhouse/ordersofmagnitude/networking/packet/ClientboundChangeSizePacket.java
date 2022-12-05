package net.brickhouse.ordersofmagnitude.networking.packet;

import net.brickhouse.ordersofmagnitude.sizechange.SizeChangeCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundChangeSizePacket {

    private final CompoundTag nbt;
    private final int entityID;

    public ClientboundChangeSizePacket(int entityID, CompoundTag nbt) {
        this.nbt = nbt;
        this.entityID = entityID;
    }

    public static void encoder(ClientboundChangeSizePacket message, FriendlyByteBuf buffer) {
        // Write to buffer
        buffer.writeInt(message.entityID);
        buffer.writeNbt(message.nbt);
    }

    public static ClientboundChangeSizePacket decoder(FriendlyByteBuf buffer) {
        // Create packet from buffer data
        return new ClientboundChangeSizePacket(buffer.readInt(), buffer.readNbt());
    }

    public static void handle(final ClientboundChangeSizePacket message, Supplier<NetworkEvent.Context> supplier) {
        // Handle message
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {

            ClientLevel world = Minecraft.getInstance().level;
            if (world != null){
                Entity entity = world.getEntity(message.entityID);
                if (entity instanceof LivingEntity){
                    entity.getCapability(SizeChangeCapability.INSTANCE).ifPresent(sizeChange ->
                    {
                        sizeChange.deserializeNBT(message.nbt);
                        entity.refreshDimensions();
                        double newTarget = sizeChange.getTargetScale();
                        if(newTarget > 1.0F){
                            entity.maxUpStep = 0.6F *(float)newTarget;        //increase the step up for bigs.  Or else they get stuck on EVERYTHING
                        } else if (newTarget < 0.25F){
                            entity.maxUpStep = 0.6F *(float) newTarget*4.0F;   //smalls need to have their step up dampened, but not so much that it destroys gameplay
                        } else {
                            entity.maxUpStep = 0.6F;
                        }
                    });
                }

            }

        });
        context.setPacketHandled(true);
    }
}
