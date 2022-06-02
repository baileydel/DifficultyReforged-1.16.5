package icey.survivaloverhaul.common.registry;

import icey.survivaloverhaul.Main;
import icey.survivaloverhaul.common.enchantments.GenericMagic;
import icey.survivaloverhaul.common.enchantments.InsulationMagic;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EnchantRegistry {
    public static final DeferredRegister<Enchantment> ENCHANTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Main.MOD_ID);

    public static final RegistryObject<Enchantment> ADAPTIVE_BARRIER = ENCHANTS.register("adaptive_barrier", () -> new InsulationMagic(InsulationMagic.MagicType.Both, Rarity.VERY_RARE, new GenericMagic.EnchantOptions(4)));
    public static final RegistryObject<Enchantment> THERMAL_BARRIER = ENCHANTS.register("thermal_barrier", () -> new InsulationMagic(InsulationMagic.MagicType.Heat, Rarity.RARE, new GenericMagic.EnchantOptions(3)));
    public static final RegistryObject<Enchantment> COLD_BARRIER = ENCHANTS.register("cold_barrier", () -> new InsulationMagic(InsulationMagic.MagicType.Cool, Rarity.RARE, new GenericMagic.EnchantOptions(3)));


}
