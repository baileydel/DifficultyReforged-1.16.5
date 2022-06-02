package icey.survivaloverhaul.common.registry;

import icey.survivaloverhaul.Main;
import icey.survivaloverhaul.api.item.DrinkableBase;
import icey.survivaloverhaul.api.item.TemperatureConsumable;
import icey.survivaloverhaul.api.thirst.ThirstEnum;
import icey.survivaloverhaul.common.items.armor.ArmorMaterialBase;
import icey.survivaloverhaul.common.items.armor.ItemDesertArmor;
import icey.survivaloverhaul.common.items.armor.ItemSnowArmor;
import icey.survivaloverhaul.api.item.ItemCanteen;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MOD_ID);

    public static final RegistryObject<Item> CANTEEN = ITEMS.register("canteen",
            () -> new ItemCanteen(new ItemCanteen.Properties().maxDoses(8).infinitePurification(false)));

    public static final RegistryObject<Item> PURIFIED_WATER_BOTTLE = ITEMS.register("purified_water_bottle",
            () -> new TemperatureConsumable((TemperatureConsumable.Properties) new TemperatureConsumable.Properties().temperature(-4).duration(600).tempGroup("drink").thirst(ThirstEnum.PURIFIED).maxStackSize(8)));

    // MILKS
    public static final RegistryObject<Item> MILK_BOTTLE = ITEMS.register("milk_bottle",
            () -> new TemperatureConsumable((TemperatureConsumable.Properties) new TemperatureConsumable.Properties().temperature(-4).duration(600).tempGroup("drink").thirst(ThirstEnum.PURIFIED).maxStackSize(8)));

    public static final RegistryObject<Item> CHOCOLATE_MILK = ITEMS.register("chocolate_milk",
            () -> new TemperatureConsumable((TemperatureConsumable.Properties) new TemperatureConsumable.Properties().temperature(-4).duration(600).tempGroup("drink").thirst(ThirstEnum.PURIFIED).maxStackSize(8)));

    public static final RegistryObject<Item> HOT_CHOCOLATE = ITEMS.register("hot_chocolate",
            () -> new TemperatureConsumable((TemperatureConsumable.Properties) new TemperatureConsumable.Properties().temperature(12).duration(600).tempGroup("drink").thirst(ThirstEnum.PURIFIED).maxStackSize(8)));


    public static final RegistryObject<Item> CHARCOAL_FILTER = ITEMS.register("charcoal_filter", () -> new Item(new Item.Properties().maxStackSize(64)));


    public static final ArmorMaterialBase CLOTH_ARMOR_MATERIAL = new ArmorMaterialBase("snow", 5.75f, new int[]{1, 1, 2, 1}, 17, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0, 0.0f, null);
    public static final ArmorMaterialBase DESERT_ARMOR_MATERIAL = new ArmorMaterialBase("desert", 5.75f, new int[]{1, 1, 2, 1}, 19, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0, 0.0f, null);
    public static final RegistryObject<Item> THERMOMETER = ITEMS.register("thermometer", () -> new Item(new Item.Properties().group(ItemGroup.TOOLS)));

    public static final RegistryObject<Item> STONE_FERN_LEAF = ITEMS.register("stone_fern_leaf", () -> new Item(new Item.Properties().group(ItemGroup.BREWING)));
    public static final RegistryObject<Item> INFERNAL_FERN_LEAF = ITEMS.register("infernal_fern_leaf", () -> new Item(new Item.Properties().group(ItemGroup.BREWING)));

    public static final RegistryObject<Item> CLOTH_HELMET = ITEMS.register("snow_helmet", () -> new ItemSnowArmor(CLOTH_ARMOR_MATERIAL, EquipmentSlotType.HEAD));
    public static final RegistryObject<Item> CLOTH_CHEST = ITEMS.register("snow_chestplate", () -> new ItemSnowArmor(CLOTH_ARMOR_MATERIAL, EquipmentSlotType.CHEST));
    public static final RegistryObject<Item> CLOTH_LEGGINGS = ITEMS.register("snow_leggings", () -> new ItemSnowArmor(CLOTH_ARMOR_MATERIAL, EquipmentSlotType.LEGS));
    public static final RegistryObject<Item> CLOTH_BOOTS = ITEMS.register("snow_boots", () -> new ItemSnowArmor(CLOTH_ARMOR_MATERIAL, EquipmentSlotType.FEET));

    public static final RegistryObject<Item> DESERT_HELMET = ITEMS.register("desert_helmet", () -> new ItemDesertArmor(DESERT_ARMOR_MATERIAL, EquipmentSlotType.HEAD));
    public static final RegistryObject<Item> DESERT_CHEST = ITEMS.register("desert_chestplate", () -> new ItemDesertArmor(DESERT_ARMOR_MATERIAL, EquipmentSlotType.CHEST));
    public static final RegistryObject<Item> DESERT_LEGGINGS = ITEMS.register("desert_leggings", () -> new ItemDesertArmor(DESERT_ARMOR_MATERIAL, EquipmentSlotType.LEGS));
    public static final RegistryObject<Item> DESERT_BOOTS = ITEMS.register("desert_boots", () -> new ItemDesertArmor(DESERT_ARMOR_MATERIAL, EquipmentSlotType.FEET));


    public static final RegistryObject<Item> COOLING_COIL_ITEM = ITEMS.register("cooling_coil", () -> new BlockItem(BlockRegistry.COOLING_COIL.get(), new Item.Properties().group(ItemGroup.REDSTONE)));
    public static final RegistryObject<Item> HEATING_COIL_ITEM = ITEMS.register("heating_coil", () -> new BlockItem(BlockRegistry.HEATING_COIL.get(), new Item.Properties().group(ItemGroup.REDSTONE)));
}