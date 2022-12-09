package net.brickhouse.ordersofmagnitude.networking.packet;

import net.brickhouse.ordersofmagnitude.block.blockEntity.matterinfusion.AbstractMatterInfusionBlockEntity;
import net.brickhouse.ordersofmagnitude.world.inventory.MatterInfusionMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundInfusionMenuSelectPacket {

    double newScale;
    BlockPos blockPos;
    public ServerboundInfusionMenuSelectPacket(double newValue, BlockPos pBlockPos) {
        this.newScale = newValue;
        this.blockPos = pBlockPos;
    }

    public static void encoder(ServerboundInfusionMenuSelectPacket message, FriendlyByteBuf buffer) {
        // Write to buffer
        buffer.writeDouble(message.newScale);
        buffer.writeBlockPos(message.blockPos);
    }

    public static ServerboundInfusionMenuSelectPacket decoder(FriendlyByteBuf buffer) {
        // Create packet from buffer data
        return new ServerboundInfusionMenuSelectPacket(buffer.readDouble(), buffer.readBlockPos());
    }
    public void handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // Server execution
            ServerPlayer player = supplier.get().getSender();
            ServerLevel level = player.getLevel();

            System.out.println("menu select: " + level.getBlockEntity(blockPos));
            if(level.getBlockEntity(blockPos) instanceof AbstractMatterInfusionBlockEntity abstractMatterInfusionBlockEntity){
                //abstractMatterInfusionBlockEntity.getTileData().putDouble("matter_infusion_machine.bufferSelectedScale",newScale);
                abstractMatterInfusionBlockEntity.setBufferedScale(newScale);
                if(player.containerMenu instanceof MatterInfusionMenu menu && menu.getBlockEntity().getBlockPos().equals(blockPos)){
                    //abstractMatterInfusionBlockEntity.getTileData().putDouble("matter_infusion_machine.bufferSelectedScale",newScale);
                    abstractMatterInfusionBlockEntity.setBufferedScale(newScale);
                }

            }
            //if(player.containerMenu instanceof MatterInfusionMenu){
            //    ((MatterInfusionMenu) player.containerMenu).updateDesignScale(newScale);
            //}
            /*ItemStack newItem = new ItemStack(ModItems.INFUSED_MODULE.get());
            CompoundTag nbt = new CompoundTag();

            nbt.putDouble("designScale", newScale);
            newItem.setTag(nbt);
            player.getInventory().add(newItem);
            System.out.print("MenuInfusionSelectC2SPacket added " + newItem + " to " + player + "\n");*/

            //TODO remove the capability call.  Will be removing selected scale variable

        });
        context.setPacketHandled(true);
    }
}
