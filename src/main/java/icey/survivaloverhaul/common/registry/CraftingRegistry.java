package icey.survivaloverhaul.common.registry;

import icey.survivaloverhaul.Main;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CraftingRegistry {

    public static final DeferredRegister<IRecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Main.MOD_ID);




    public static final RegistryObject<IRecipeSerializer<?>> CANTEEN_CHARCOAL = RECIPES.register("canteen_charcoal_recipe",
            () -> new SpecialRecipeSerializer<>(CanteenCharcoalRecipe::new));



}
