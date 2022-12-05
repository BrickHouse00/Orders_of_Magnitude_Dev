package net.brickhouse.ordersofmagnitude.networking;

import net.brickhouse.ordersofmagnitude.OrdersOfMagnitude;
import net.brickhouse.ordersofmagnitude.networking.packet.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModMessages {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(OrdersOfMagnitude.MOD_ID, "main_channel"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(WS -> true)
                .simpleChannel();

        INSTANCE = net;

        //Clientbound packets
        registerMessage(ClientboundChangeSizePacket.class, ClientboundChangeSizePacket::encoder, ClientboundChangeSizePacket::decoder, ClientboundChangeSizePacket::handle);
        registerMessage(ClientboundOMBlockEntitySyncPacket.class, ClientboundOMBlockEntitySyncPacket::encoder, ClientboundOMBlockEntitySyncPacket::decoder, ClientboundOMBlockEntitySyncPacket::handle);
        registerMessage(ClientboundInfusionBlockEntityResultSyncPacket.class, ClientboundInfusionBlockEntityResultSyncPacket::encoder, ClientboundInfusionBlockEntityResultSyncPacket::decoder, ClientboundInfusionBlockEntityResultSyncPacket::handle);
        //Serverbound packets
        registerMessage(ServerboundChangeSizePacket.class, ServerboundChangeSizePacket::encoder, ServerboundChangeSizePacket::decoder, ServerboundChangeSizePacket::handle);
        registerMessage(ServerboundItemMenuSizeSelectPacket.class, ServerboundItemMenuSizeSelectPacket::encoder, ServerboundItemMenuSizeSelectPacket::decoder, ServerboundItemMenuSizeSelectPacket::handle);
        registerMessage(ServerboundInfusionMenuSelectPacket.class, ServerboundInfusionMenuSelectPacket::encoder, ServerboundInfusionMenuSelectPacket::decoder, ServerboundInfusionMenuSelectPacket::handle);
    }

    public static void sendToServer(Object message) {
        INSTANCE.sendToServer(message);
    }

    public static void sendToClients(Object message){INSTANCE.send(PacketDistributor.ALL.noArg(), message);}

    private static <MSG> void registerMessage(Class<MSG> type, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer)
    {
        INSTANCE.registerMessage(id(), type, encoder, decoder, consumer);
    }
    public static void send(PacketDistributor.PacketTarget target, ClientboundChangeSizePacket message)
    {
        //send is a server to client function
        INSTANCE.send(target, message);
    }
}
