package icey.survivaloverhaul.common.network.packets;

import icey.survivaloverhaul.Main;
import icey.survivaloverhaul.api.CapabilityUtil;
import icey.survivaloverhaul.common.capability.wetness.WetnessCapability;
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

public class PacketUpdateWetness {
    private CompoundNBT compound;

    public PacketUpdateWetness() {}

    public PacketUpdateWetness(INBT compound) {
        this.compound = (CompoundNBT) compound;
    }

    public static PacketUpdateWetness decode(PacketBuffer buffer) {
        return new PacketUpdateWetness(buffer.readCompoundTag());
    }

    public static void encode(PacketUpdateWetness message, PacketBuffer buffer) {
        buffer.writeCompoundTag(message.compound);
    }

    public static void handle(PacketUpdateWetness message, Supplier<NetworkEvent.Context> supplier) {
        final NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> syncWetness(message.compound)));

        supplier.get().setPacketHandled(true);
    }


    public static void send(PlayerEntity player) {
        if (!player.world.isRemote()) {
            PacketUpdateWetness packet = new PacketUpdateWetness(CapabilityUtil.WETNESS_CAP.getStorage().writeNBT(CapabilityUtil.WETNESS_CAP, CapabilityUtil.getWetnessCapability(player), null));

            Network.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), packet);
        }
    }

    public static DistExecutor.SafeRunnable syncWetness(CompoundNBT compound) {
        return new DistExecutor.SafeRunnable() {
            private static final long serialVersionUID = 1L;

            @Override
            public void run() {
                ClientPlayerEntity player = Minecraft.getInstance().player;

                WetnessCapability wetness = player.getCapability(CapabilityUtil.WETNESS_CAP).orElse(new WetnessCapability());

                wetness.readNBT(compound);
            }
        };
    }
}
