package net.brickhouse.ordersofmagnitude.networking.packet;

import net.brickhouse.ordersofmagnitude.block.blockEntity.matterinfusion.AbstractMatterInfusionBlockEntity;
import net.brickhouse.ordersofmagnitude.world.inventory.MatterInfusionMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundInfusionBlockEntityResultSyncPacket {

    private final double bufferScale;
    private final BlockPos blockPos;
    public ClientboundInfusionBlockEntityResultSyncPacket(double pBufferScale, BlockPos pBlockPos) {
        this.bufferScale = pBufferScale;
        this.blockPos = pBlockPos;
    }

    public static void encoder(ClientboundInfusionBlockEntityResultSyncPacket message, FriendlyByteBuf buffer) {
        // Write to buffer
        buffer.writeDouble(message.bufferScale);
        buffer.writeBlockPos(message.blockPos);
    }

    public static ClientboundInfusionBlockEntityResultSyncPacket decoder(FriendlyByteBuf buffer) {
        // Create packet from buffer data
        return new ClientboundInfusionBlockEntityResultSyncPacket(buffer.readDouble(), buffer.readBlockPos());
    }
    public void handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        //ServerPlayer player = context.getSender();
        //ServerLevel level = player.getLevel();
        context.enqueueWork(() -> {
            // Client execution
            if(Minecraft.getInstance().level.getBlockEntity(blockPos) instanceof AbstractMatterInfusionBlockEntity abstractMatterInfusionBlockEntity){
                //Minecraft.getInstance().level.getBlockEntity(blockPos).getTileData().putDouble("matter_infusion_machine.bufferSelectedScale",bufferScale);
                abstractMatterInfusionBlockEntity.setBufferedScale(bufferScale);
                if(Minecraft.getInstance().player.containerMenu instanceof MatterInfusionMenu menu && menu.getBlockEntity().getBlockPos().equals(blockPos)){
                    //Minecraft.getInstance().level.getBlockEntity(blockPos).getTileData().putDouble("matter_infusion_machine.bufferSelectedScale",bufferScale);
                }

            }

        });
        context.setPacketHandled(true);
    }
}
