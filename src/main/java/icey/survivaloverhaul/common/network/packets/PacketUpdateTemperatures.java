package icey.survivaloverhaul.common.network.packets;

import icey.survivaloverhaul.Main;
import icey.survivaloverhaul.api.CapabilityUtil;
import icey.survivaloverhaul.common.capability.temperature.TemperatureCapability;
import icey.survivaloverhaul.common.network.Network;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class PacketUpdateTemperatures {
    private CompoundNBT compound;

    public PacketUpdateTemperatures(INBT compound) {
        this.compound = (CompoundNBT) compound;
    }

    public PacketUpdateTemperatures() {}

    public static PacketUpdateTemperatures decode(PacketBuffer buffer) {
        return new PacketUpdateTemperatures(buffer.readCompoundTag());
    }

    public static void encode(PacketUpdateTemperatures message, PacketBuffer buffer) {
        buffer.writeCompoundTag(message.compound);
    }

    public static void handle(PacketUpdateTemperatures message, Supplier<NetworkEvent.Context> supplier) {
        final NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> syncTemperature(message.compound)));

        supplier.get().setPacketHandled(true);
    }


    public static void send(PlayerEntity player) {
        if (!player.world.isRemote()) {
            PacketUpdateTemperatures packet = new PacketUpdateTemperatures(CapabilityUtil.TEMPERATURE_CAP.getStorage().writeNBT(CapabilityUtil.TEMPERATURE_CAP, CapabilityUtil.getTempCapability(player), null));

            Network.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), packet);
        }
    }

    public static DistExecutor.SafeRunnable syncTemperature(CompoundNBT compound) {
        return new DistExecutor.SafeRunnable() {
            private static final long serialVersionUID = 1L;

            @Override
            public void run() {
                ClientPlayerEntity player = Minecraft.getInstance().player;

                TemperatureCapability temperature = player.getCapability(CapabilityUtil.TEMPERATURE_CAP).orElse(new TemperatureCapability());

                temperature.readNBT(compound);
            }
        };
    }
}
