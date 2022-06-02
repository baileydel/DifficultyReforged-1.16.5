package icey.survivaloverhaul.common.network.packets;

import icey.survivaloverhaul.Main;
import icey.survivaloverhaul.api.CapabilityUtil;
import icey.survivaloverhaul.common.network.Network;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class PacketThirstUpdate
{
	private CompoundNBT nbt;

	public PacketThirstUpdate(INBT nbt)
	{
		this.nbt = (CompoundNBT) nbt;
	}

	public PacketThirstUpdate(PacketBuffer buffer)
	{
		this.nbt = buffer.readCompoundTag();
	}

	public void encode(PacketBuffer buffer) {
		buffer.writeCompoundTag(nbt);
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		ClientPlayerEntity player = Minecraft.getInstance().player;

		if (player != null)
		{
			context.get().enqueueWork(() -> CapabilityUtil.THIRST_CAP.getStorage().readNBT(CapabilityUtil.THIRST_CAP, CapabilityUtil.getThirstCapability(player), null, this.nbt));
		}
		context.get().setPacketHandled(true);
	}

	public static void send(PlayerEntity player) {
		Network.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketThirstUpdate(CapabilityUtil.THIRST_CAP.getStorage().writeNBT(CapabilityUtil.THIRST_CAP, CapabilityUtil.getThirstCapability(player), null)));
	}
}
