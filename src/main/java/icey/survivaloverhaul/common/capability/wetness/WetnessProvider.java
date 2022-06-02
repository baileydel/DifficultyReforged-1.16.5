package icey.survivaloverhaul.common.capability.wetness;

import icey.survivaloverhaul.Main;
import icey.survivaloverhaul.api.CapabilityUtil;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class WetnessProvider implements ICapabilitySerializable<INBT> {
    private LazyOptional<WetnessCapability> instance = LazyOptional.of(CapabilityUtil.WETNESS_CAP::getDefaultInstance);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return CapabilityUtil.WETNESS_CAP.orEmpty(cap, instance);
    }

    @Override
    public INBT serializeNBT() {
        return CapabilityUtil.WETNESS_CAP.getStorage().writeNBT(CapabilityUtil.WETNESS_CAP, instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional cannot be empty!")), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        CapabilityUtil.WETNESS_CAP.getStorage().readNBT(CapabilityUtil.WETNESS_CAP, instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional cannot be empty!")), null, nbt);
    }
}
