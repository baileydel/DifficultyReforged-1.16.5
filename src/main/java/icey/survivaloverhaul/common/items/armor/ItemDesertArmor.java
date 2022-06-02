package icey.survivaloverhaul.common.items.armor;

import icey.survivaloverhaul.Main;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;

public class ItemDesertArmor extends ArmorItem {
    public ItemDesertArmor(IArmorMaterial material, EquipmentSlotType slot) {
        super(material, slot, new Item.Properties().group(ItemGroup.COMBAT));
    }

    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        return Main.MOD_ID + ":textures/models/armor/desert_armor_" + (slot == EquipmentSlotType.LEGS ? "2" : "1") + ".png";
    }
}
