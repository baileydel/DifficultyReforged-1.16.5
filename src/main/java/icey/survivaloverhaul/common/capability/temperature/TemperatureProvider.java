package icey.survivaloverhaul.common.capability.temperature;

import icey.survivaloverhaul.Main;
import icey.survivaloverhaul.api.CapabilityUtil;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class TemperatureProvider implements ICapabilitySerializable<INBT> {
    private LazyOptional<TemperatureCapability> instance = LazyOptional.of(CapabilityUtil.TEMPERATURE_CAP::getDefaultInstance);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return CapabilityUtil.TEMPERATURE_CAP.orEmpty(cap, instance);
    }

    @Override
    public INBT serializeNBT() {
        return CapabilityUtil.TEMPERATURE_CAP.getStorage().writeNBT(CapabilityUtil.TEMPERATURE_CAP, instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional cannot be empty!")), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        CapabilityUtil.TEMPERATURE_CAP.getStorage().readNBT(CapabilityUtil.TEMPERATURE_CAP, instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional cannot be empty!")), null, nbt);
    }
}
