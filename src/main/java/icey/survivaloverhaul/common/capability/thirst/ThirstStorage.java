package icey.survivaloverhaul.common.capability.thirst;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public class ThirstStorage implements Capability.IStorage<ThirstCapability>
{
    @Override
    public INBT writeNBT(Capability<ThirstCapability> capability, ThirstCapability instance, Direction side) {
        return instance.writeNBT();
    }

    @Override
    public void readNBT(Capability<ThirstCapability> capability, ThirstCapability instance, Direction side, INBT nbt) {
        if (nbt instanceof CompoundNBT) {
            instance.readNBT((CompoundNBT) nbt);
        }
    }
}

