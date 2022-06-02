package icey.survivaloverhaul.common.items.armor;

import icey.survivaloverhaul.Main;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;

public class ItemSnowArmor extends ArmorItem {
    public ItemSnowArmor(IArmorMaterial material, EquipmentSlotType slot) {
        super(material, slot, new Item.Properties().group(ItemGroup.COMBAT));
    }

    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        return Main.MOD_ID + ":textures/models/armor/snow_armor_" + (slot == EquipmentSlotType.LEGS ? "2" : "1") + ".png";
    }
}
