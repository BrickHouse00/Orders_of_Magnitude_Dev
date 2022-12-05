package net.brickhouse.ordersofmagnitude.networking.packet;

import net.brickhouse.ordersofmagnitude.block.blockEntity.OMBlockEntity;
import net.brickhouse.ordersofmagnitude.client.MatterInfusionMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundOMBlockEntitySyncPacket {

    private final int energy;
    private final BlockPos blockPos;
    private final FluidStack fluidStack;
    public ClientboundOMBlockEntitySyncPacket(int pEnergy, BlockPos pBlockPos, FluidStack pFluidStack) {
        this.energy = pEnergy;
        this.blockPos = pBlockPos;
        this.fluidStack = pFluidStack;
    }

    public static void encoder(ClientboundOMBlockEntitySyncPacket message, FriendlyByteBuf buffer) {
        // Write to buffer
        buffer.writeInt(message.energy);
        buffer.writeBlockPos(message.blockPos);
        buffer.writeFluidStack(message.fluidStack);
    }

    public static ClientboundOMBlockEntitySyncPacket decoder(FriendlyByteBuf buffer) {
        // Create packet from buffer data
        return new ClientboundOMBlockEntitySyncPacket(buffer.readInt(), buffer.readBlockPos(), buffer.readFluidStack());
    }
    public void handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // Client execution
            if(Minecraft.getInstance().level.getBlockEntity(blockPos) instanceof OMBlockEntity omBlockEntity){
                omBlockEntity.setEnergyLevel(energy);
                omBlockEntity.setFluid(fluidStack);
                if(Minecraft.getInstance().player.containerMenu instanceof MatterInfusionMenu menu && menu.getBlockEntity().getBlockPos().equals(blockPos)){
                    omBlockEntity.setEnergyLevel(energy);
                    omBlockEntity.setFluid(fluidStack);
                }

            }

        });
        context.setPacketHandled(true);
    }
}
