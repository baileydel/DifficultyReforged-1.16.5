package icey.survivaloverhaul.common.registry;

import icey.survivaloverhaul.Main;
import icey.survivaloverhaul.common.blocks.BlockTemperatureCoil;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@SuppressWarnings("unused")
public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Main.MOD_ID);
    public static final RegistryObject<Block> HEATING_COIL = BLOCKS.register("heating_coil", () -> new BlockTemperatureCoil(BlockTemperatureCoil.CoilType.HEATING));
    public static final RegistryObject<Block> COOLING_COIL = BLOCKS.register("cooling_coil", () -> new BlockTemperatureCoil(BlockTemperatureCoil.CoilType.COOLING));
}
