package icey.survivaloverhaul.common.capability.thirst;

import icey.survivaloverhaul.Main;
import icey.survivaloverhaul.api.CapabilityUtil;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class ThirstProvider implements ICapabilitySerializable<INBT> {

    private LazyOptional<ThirstCapability> instance = LazyOptional.of(CapabilityUtil.THIRST_CAP::getDefaultInstance);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return CapabilityUtil.THIRST_CAP.orEmpty(cap, instance);
    }

    @Override
    public INBT serializeNBT() {
        return CapabilityUtil.THIRST_CAP.getStorage().writeNBT(CapabilityUtil.THIRST_CAP, instance.orElseThrow(
                () -> new IllegalArgumentException("LazyOptional cannot be empty!")
        ), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        CapabilityUtil.THIRST_CAP.getStorage().readNBT(CapabilityUtil.THIRST_CAP, instance.orElseThrow(
                () -> new IllegalArgumentException("LazyOptional cannot be empty!")
        ), null, nbt);
    }
}
