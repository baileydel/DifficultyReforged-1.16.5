package icey.survivaloverhaul.common.network;

import icey.survivaloverhaul.Main;
import icey.survivaloverhaul.common.network.packets.PacketDrink;
import icey.survivaloverhaul.common.network.packets.PacketThirstUpdate;
import icey.survivaloverhaul.common.network.packets.PacketUpdateTemperatures;
import icey.survivaloverhaul.common.network.packets.PacketUpdateWetness;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Network {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Main.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    public static void register() {
        CHANNEL.registerMessage(0, PacketThirstUpdate.class, PacketThirstUpdate::encode, PacketThirstUpdate::new, PacketThirstUpdate::handle);
        CHANNEL.registerMessage(1, PacketUpdateTemperatures.class, PacketUpdateTemperatures::encode, PacketUpdateTemperatures::decode, PacketUpdateTemperatures::handle);
        CHANNEL.registerMessage(2, PacketUpdateWetness.class, PacketUpdateWetness::encode, PacketUpdateWetness::decode, PacketUpdateWetness::handle);

        CHANNEL.registerMessage(3, PacketDrink.class, PacketDrink::encode, PacketDrink::new, PacketDrink::handle);
    }
}