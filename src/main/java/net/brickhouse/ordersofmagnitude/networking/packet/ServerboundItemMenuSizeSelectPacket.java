package net.brickhouse.ordersofmagnitude.networking.packet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundItemMenuSizeSelectPacket {

    double newSelection;
    //ItemStack heldItem;
    CompoundTag itemProperties;

    //public MenuSizeSelectC2SPacket(double newValue, ItemStack newHeldItem) {
    public ServerboundItemMenuSizeSelectPacket(double newValue) {
        this.newSelection = newValue;
        //this.heldItem = newHeldItem;
    }

    public static void encoder(ServerboundItemMenuSizeSelectPacket message, FriendlyByteBuf buffer) {
        // Write to buffer
        buffer.writeDouble(message.newSelection);
        //buffer.writeItemStack(message.heldItem, false);
    }

    public static ServerboundItemMenuSizeSelectPacket decoder(FriendlyByteBuf buffer) {
        // Create packet from buffer data
        //return new MenuSizeSelectC2SPacket(buffer.readDouble(), buffer.readItem());
        return new ServerboundItemMenuSizeSelectPacket(buffer.readDouble());
    }

    public void handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // Server execution
            //System.out.println("newSelection " + newSelection + " heldItem " + heldItem);
            ServerPlayer player = supplier.get().getSender();
            ServerLevel level = player.getLevel();
            ItemStack heldItem = player.getMainHandItem();
            /*CompoundTag nbt = new CompoundTag();
            nbt.putDouble("targetScale", newSelection);
            tablet.setTag(nbt);*/;
            heldItem.getOrCreateTag().putDouble("targetScale", newSelection);
            //System.out.print("MenuSizeSelectC2SPacket hasTag: " + tablet.hasTag() + "\n");
            //TODO remove the capability call.  Will be removing selected scale variable
            /*player.getCapability(SizeChangeCapability.INSTANCE).ifPresent(sizeChange ->
            {
                sizeChange.setSelectedScale(newSelection); //all this just to update a value

            });

             */

        });
        context.setPacketHandled(true);
    }
}
