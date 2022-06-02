package icey.survivaloverhaul.common.network.packets;

import icey.survivaloverhaul.api.thirst.ThirstEnum;
import icey.survivaloverhaul.api.thirst.ThirstEnumBlockPos;
import icey.survivaloverhaul.api.thirst.ThirstUtil;
import icey.survivaloverhaul.common.network.Network;
import icey.survivaloverhaul.internal.ThirstUtilInternal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketDrink {
    public PacketDrink() {}
    public PacketDrink(PacketBuffer buffer) {}
    public void encode(PacketBuffer buffer) {}

    public void handle(Supplier<NetworkEvent.Context> context) {
        PlayerEntity player = context.get().getSender();

        if (player != null)
        {
            context.get().enqueueWork(() ->
            {
                ThirstEnumBlockPos traceResult = ThirstUtilInternal.traceWaterToDrink(player);

                if (traceResult != null) {
                    ThirstEnum result = traceResult.thirstEnum;

                    //Drink
                    ThirstUtil.takeDrink(player, result.getThirst(), result.getSaturation(), result.getThirstyChance());

                    //Play sound to other players
                    player.playSound(SoundEvents.ENTITY_GENERIC_DRINK, 0.5f, 1.0f);
                }
            });
        }
        context.get().setPacketHandled(true);
    }

    public static void send() {
        Network.CHANNEL.sendToServer(new PacketDrink());
    }
}
