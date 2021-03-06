package icey.survivaloverhaul.common.temperature.dynamic;

import icey.survivaloverhaul.api.temperature.DynamicModifierBase;
import icey.survivaloverhaul.common.registry.EnchantRegistry;
import icey.survivaloverhaul.config.Config;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class AdaptiveEnchantmentModifier extends DynamicModifierBase {
    public AdaptiveEnchantmentModifier() {
        super();
    }

    @Override
    public float applyDynamicPlayerInfluence(PlayerEntity player, float currentTemperature) {
        float value = 0.0f;

        value += checkArmorSlot(player.getItemStackFromSlot(EquipmentSlotType.HEAD), currentTemperature);
        value += checkArmorSlot(player.getItemStackFromSlot(EquipmentSlotType.CHEST), currentTemperature);
        value += checkArmorSlot(player.getItemStackFromSlot(EquipmentSlotType.LEGS), currentTemperature);
        value += checkArmorSlot(player.getItemStackFromSlot(EquipmentSlotType.FEET), currentTemperature);

        return value;
    }

    private float checkArmorSlot(ItemStack stack, float currentTemperature) {
        if (stack.isEmpty())
            return 0.0f;

        float sum = 0.0f;

        int adaptiveLevel = EnchantmentHelper.getEnchantmentLevel(EnchantRegistry.ADAPTIVE_BARRIER.get(), stack);

        if (adaptiveLevel > 0) {
            int diff = (int) (currentTemperature - defaultTemperature);
            if (currentTemperature > defaultTemperature) {
                sum -= Math.min(adaptiveLevel * Config.Baked.enchantmentMultiplier, Math.abs(diff));
            } else if (currentTemperature < defaultTemperature) {
                sum += Math.min(adaptiveLevel * Config.Baked.enchantmentMultiplier, Math.abs(diff));
            }
        }

        return sum;
    }
}
