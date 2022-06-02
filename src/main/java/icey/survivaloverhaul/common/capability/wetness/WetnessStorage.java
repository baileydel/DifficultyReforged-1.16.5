package icey.survivaloverhaul.common.capability.wetness;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public class WetnessStorage implements Capability.IStorage<WetnessCapability> {

    @Override
    public INBT writeNBT(Capability<WetnessCapability> capability, WetnessCapability instance, Direction side) {
        return instance.writeNBT();
    }

    @Override
    public void readNBT(Capability<WetnessCapability> capability, WetnessCapability instance, Direction side, INBT nbt) {
        if (nbt instanceof CompoundNBT) {
            instance.readNBT((CompoundNBT) nbt);
        }
    }
}
