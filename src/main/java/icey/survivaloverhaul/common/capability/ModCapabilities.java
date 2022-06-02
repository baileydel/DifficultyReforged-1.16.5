package icey.survivaloverhaul.common.capability;

import icey.survivaloverhaul.Main;
import icey.survivaloverhaul.api.CapabilityUtil;
import icey.survivaloverhaul.api.config.json.temperature.JsonConsumableTemperature;
import icey.survivaloverhaul.api.item.TemperatureConsumable;
import icey.survivaloverhaul.common.capability.temperature.TemperatureCapability;
import icey.survivaloverhaul.common.capability.temperature.TemperatureProvider;
import icey.survivaloverhaul.common.capability.thirst.ThirstCapability;
import icey.survivaloverhaul.common.capability.thirst.ThirstProvider;
import icey.survivaloverhaul.common.capability.wetness.WetnessCapability;
import icey.survivaloverhaul.common.capability.wetness.WetnessMode;
import icey.survivaloverhaul.common.capability.wetness.WetnessProvider;
import icey.survivaloverhaul.common.network.packets.PacketThirstUpdate;
import icey.survivaloverhaul.common.network.packets.PacketUpdateTemperatures;
import icey.survivaloverhaul.common.network.packets.PacketUpdateWetness;
import icey.survivaloverhaul.config.Config;
import icey.survivaloverhaul.config.json.JsonConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.List;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
public class ModCapabilities {
    public static final ResourceLocation THIRST_RES = new ResourceLocation(Main.MOD_ID, "thirst");
    public static final ResourceLocation TEMPERATURE_RES = new ResourceLocation(Main.MOD_ID, "temperature");
    public static final ResourceLocation WETNESS_RES = new ResourceLocation(Main.MOD_ID, "wetness");

    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity)
        {
            if (event.getObject() instanceof PlayerEntity)
            {
                event.addCapability(THIRST_RES, new ThirstProvider());
                event.addCapability(TEMPERATURE_RES, new TemperatureProvider());
                event.addCapability(WETNESS_RES, new WetnessProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event) {
        // Server Side
        if (!event.player.world.isRemote) {
            PlayerEntity player = event.player;
            World world = player.world;

            if (Config.COMMON.thirstEnabled.get() && !shouldSkipTick(player)) {
                ThirstCapability thirstcap = CapabilityUtil.getThirstCapability(player);

                thirstcap.tickUpdate(player, world, event.phase);

                if (event.phase == Phase.START && (thirstcap.isDirty() || thirstcap.getPacketTimer() % Config.Baked.routinePacketSync == 0)) {
                    thirstcap.setClean();
                    PacketThirstUpdate.send(player);
                }
            }
            
            if (Config.Baked.temperatureEnabled && !shouldSkipTick(player)) {
                TemperatureCapability tempCap = CapabilityUtil.getTempCapability(player);

                tempCap.tickUpdate(player, world, event.phase);

                if (event.phase == Phase.START && (tempCap.isDirty() || tempCap.getPacketTimer() % Config.Baked.routinePacketSync == 0)) {
                    tempCap.setClean();
                    PacketUpdateTemperatures.send(player);
                }
            }

            if (Config.Baked.wetnessMode == WetnessMode.DYNAMIC && !shouldSkipTick(player)) {
                WetnessCapability wetCap = CapabilityUtil.getWetnessCapability(player);

                wetCap.tickUpdate(player, world, event.phase);

                /**
                 * Because of the way wetness is ticked, if it's dirty, it's probably going to be dirty next tick,
                 * and if it's clean, it's probably going to be clean the next tick
                 * Thus, we don't want to clean up the wetness capability every single tick
                 * just because the player is standing out in the rain
                 * since it's not good for performance
                 */
                if (event.phase == Phase.START && wetCap.getPacketTimer() % Config.Baked.routinePacketSync == 0 && wetCap.isDirty()) {
                    wetCap.setClean();
                    PacketUpdateWetness.send(player);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingEntityUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        if (Config.COMMON.temperatureEnabled.get())
        {
            if (event.getEntityLiving() instanceof PlayerEntity && !event.getEntityLiving().world.isRemote)
            {
                ItemStack stack = event.getItem();
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();

                List<JsonConsumableTemperature> consumableList = JsonConfig.consumableTemperature.get(stack.getItem().getRegistryName().toString());

                boolean found = false;

                if (consumableList != null)
                {
                    for (JsonConsumableTemperature jct : consumableList)
                    {
                        if (jct == null)
                            continue;

                        if (jct.matches(stack))
                        {
                            CapabilityUtil.getTempCapability(player).setTemporaryModifier(jct.group, jct.temperature, jct.duration);
                            found = true;
                            break;
                        }
                    }
                }

                if (stack.getItem() instanceof TemperatureConsumable && !found)
                {
                    TemperatureConsumable item = (TemperatureConsumable) stack.getItem();
                    CapabilityUtil.getTempCapability(player).setTemporaryModifier(item.getTempGroup(), item.getTemperature(), item.getDuration());
                }
            }
        }

    }

    @SubscribeEvent
    public static void deathHandler(PlayerEvent.Clone event) {
        PlayerEntity orig = event.getOriginal();
        PlayerEntity player = event.getPlayer();

        if (!event.isWasDeath())
        {
            if (Config.Baked.temperatureEnabled)
            {
                TemperatureCapability oldCap = CapabilityUtil.getTempCapability(orig);
                TemperatureCapability newCap = CapabilityUtil.getTempCapability(player);
                newCap.readNBT(oldCap.writeNBT());
                PacketUpdateTemperatures.send(player);
            }

            if (Config.COMMON.thirstEnabled.get())
            {
                ThirstCapability oldCap = CapabilityUtil.getThirstCapability(orig);
                ThirstCapability newCap = CapabilityUtil.getThirstCapability(player);
                newCap.readNBT(oldCap.writeNBT());
                PacketThirstUpdate.send(player);
            }

            if (Config.Baked.wetnessMode == WetnessMode.DYNAMIC)
            {
                WetnessCapability oldCap = CapabilityUtil.getWetnessCapability(orig);
                WetnessCapability newCap = CapabilityUtil.getWetnessCapability(player);
                newCap.readNBT(oldCap.writeNBT());
                PacketUpdateWetness.send(player);
            }
        }
    }





    @SubscribeEvent
    public static void syncCapsOnDimensionChange(PlayerChangedDimensionEvent event) {
        PlayerEntity player = event.getPlayer();

        if (Config.COMMON.thirstEnabled.get()) {
            PacketThirstUpdate.send(player);
        }

        if (Config.Baked.temperatureEnabled)
            PacketUpdateTemperatures.send(player);

        if (Config.Baked.wetnessMode == WetnessMode.DYNAMIC)
            PacketUpdateWetness.send(player);
    }

    @SubscribeEvent
    public static void syncCapsOnLogin(PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();

        if (Config.COMMON.thirstEnabled.get()) {
            PacketThirstUpdate.send(player);
        }

        if (Config.Baked.temperatureEnabled)
            PacketUpdateTemperatures.send(player);

        if (Config.Baked.wetnessMode == WetnessMode.DYNAMIC)
            PacketUpdateWetness.send(player);
    }

    protected static boolean shouldSkipTick(PlayerEntity player) {
        return player.isCreative() || player.isSpectator();
    }
}
