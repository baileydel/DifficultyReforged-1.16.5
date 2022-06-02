package icey.survivaloverhaul.common.registry;

import icey.survivaloverhaul.api.item.ItemCanteen;
import icey.survivaloverhaul.api.thirst.ThirstEnum;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.RecipeMatcher;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CanteenCharcoalRecipe extends SpecialRecipe {

    private static final Ingredient INGREDIENT_CANTEEN = Ingredient.fromItems(ItemRegistry.CANTEEN.get());
    private static final Ingredient INGREDIENT_FILTER = Ingredient.fromItems(ItemRegistry.CHARCOAL_FILTER.get());

    boolean isSimple;

    List<Ingredient> recipeItems;

    public CanteenCharcoalRecipe(ResourceLocation idIn) {
        super(idIn);

        recipeItems = new ArrayList<>();
        recipeItems.add(INGREDIENT_CANTEEN);
        recipeItems.add(INGREDIENT_FILTER);

        isSimple = recipeItems.stream().allMatch(Ingredient::isSimple);
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull CraftingInventory inv) {
        ItemStack canteen = ItemStack.EMPTY;
        ItemStack filter = ItemStack.EMPTY;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack ingredient = inv.getStackInSlot(i);

            if (!ingredient.isEmpty()) {
                if (ingredient.getItem() instanceof ItemCanteen) {
                    canteen = ingredient;
                }

                System.out.println(ingredient.getItem().getRegistryName());

                if (ingredient.getItem().getRegistryName().getPath().equals("charcoal_filter")) {
                    filter = ingredient;
                }
            }
        }

        if (!canteen.isEmpty() && !filter.isEmpty()) {
            ItemCanteen c = (ItemCanteen) canteen.getItem();

            if (c.getDoses(canteen) > 0) {
                c.setDoses(canteen, ThirstEnum.PURIFIED, c.getDoses(canteen));
                return canteen.copy();
            }
            else {
                return ItemStack.EMPTY;
            }
        }

        return canteen;
    }

    @Override
    public boolean matches(CraftingInventory inv, @Nonnull World worldIn) {
        RecipeItemHelper recipeitemhelper = new RecipeItemHelper();
        java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
        int i = 0;

        for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                ++i;
                if (isSimple)
                    recipeitemhelper.func_221264_a(itemstack, 1);
                else inputs.add(itemstack);
            }
        }

        return i == 2 && (isSimple ? recipeitemhelper.canCraft(this, null) : RecipeMatcher.findMatches(inputs, this.recipeItems) != null);
    }


    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return CraftingRegistry.CANTEEN_CHARCOAL.get();
    }
}
