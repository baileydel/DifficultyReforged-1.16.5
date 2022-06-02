package icey.survivaloverhaul.api;

import icey.survivaloverhaul.common.capability.temperature.TemperatureCapability;
import icey.survivaloverhaul.common.capability.thirst.ThirstCapability;
import icey.survivaloverhaul.common.capability.wetness.WetnessCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

/**
 * Helper functions for quickly getting player capabilities.
 * @author Icey
 */
public final class CapabilityUtil {

    @CapabilityInject(TemperatureCapability.class)
    public static final Capability<TemperatureCapability> TEMPERATURE_CAP = null;
    @CapabilityInject(WetnessCapability.class)
    public static final Capability<WetnessCapability> WETNESS_CAP = null;

    @CapabilityInject(ThirstCapability.class)
    public static final Capability<ThirstCapability> THIRST_CAP = null;


    /**
     * Gets the temperature capability of the given player.
     * @param player
     * @return The temperature capability of the given player if it exists, or a new dummy capability if it doesn't.
     */
    public static TemperatureCapability getTempCapability(PlayerEntity player) {
        return player.getCapability(TEMPERATURE_CAP).orElse(new TemperatureCapability());
    }

    /**
     * Gets the thirst capability for a player
     * @param player
     * @return IThirstCapability
     */
    public static ThirstCapability getThirstCapability(PlayerEntity player)
    {
        return player.getCapability(THIRST_CAP).orElse(new ThirstCapability());
    }

    /**
     * Gets the wetness capability of the given player.
     * @param player
     * @return The wetness capability of the given player if it exists, or a new dummy capability if it doesn't.
     */
    public static WetnessCapability getWetnessCapability(PlayerEntity player) {
        return player.getCapability(WETNESS_CAP).orElse(new WetnessCapability());
    }
}
