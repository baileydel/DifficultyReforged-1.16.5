package icey.survivaloverhaul.api.item;

import icey.survivaloverhaul.api.config.json.JsonConsumableThirst;
import icey.survivaloverhaul.api.thirst.ThirstEnum;
import icey.survivaloverhaul.api.thirst.ThirstUtil;
import icey.survivaloverhaul.config.json.JsonConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

public class DrinkableBase extends Item {
    ThirstEnum thirstEnum;

    public DrinkableBase(Properties properties) {
        super(properties);
        this.thirstEnum = properties.thirstEnum;
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
        return DrinkHelper.startDrinking(world, player, hand);
    }

    @Override
    @Nonnull
    public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World world, @Nonnull LivingEntity entityLiving) {
        if (world.isRemote || !(entityLiving instanceof PlayerEntity)) {
            return stack;
        }

        PlayerEntity player = (PlayerEntity) entityLiving;

        boolean override = false;

        //Check if the JSON has overridden the drink's defaults, and if so, allow ThirstHandler to take over
        List<JsonConsumableThirst> jctList = JsonConfig.consumableThirst.get(this.getRegistryName().toString());
        if (jctList != null)
        {
            for (JsonConsumableThirst jct : jctList)
            {
                if (jct == null)
                    continue;

                if (jct.matches(stack))
                {
                    override = true;
                    break;
                }
            }
        }

        if (!override) {
            ThirstUtil.takeDrink(player, this.getThirstLevel(stack), this.getSaturationLevel(stack), this.getDirtyChance(stack));
        }

        this.runSecondaryEffect(player, stack);
        this.shrink(player, stack);
        return stack;
    }

    public void shrink(PlayerEntity player, ItemStack stack) {
        if (!player.abilities.isCreativeMode) {
            stack.shrink(1);

            ItemStack glassBottle = new ItemStack(Items.GLASS_BOTTLE);

            if (!player.inventory.addItemStackToInventory(glassBottle)) {
                player.dropItem(glassBottle, false);
            }
            else {
                player.inventory.addItemStackToInventory(glassBottle);
            }
        }
    }

    /**
        Can be overridden to run a special task
    */
    //TODO Runnable in builder
    public void runSecondaryEffect(PlayerEntity player, ItemStack stack) {}

    @Override
    @Nonnull
    public UseAction getUseAction(@Nonnull ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public int getUseDuration(@Nonnull ItemStack stack) {
        return 32;
    }

    @Override
    public boolean isEnchantable(@Nonnull ItemStack stack) {
        return false;
    }

    /*
        This is for static items like water bottles
        Canteen's thirst enums are always changing
     */
    public ThirstEnum getThirstEnum(ItemStack stack) {
        return this.thirstEnum;
    }

    public void setThirstEnum(ThirstEnum thirstEnum) {
        this.thirstEnum = thirstEnum;
    }

    public int getThirstLevel(ItemStack stack) {
        return getThirstEnum(stack).getThirst();
    }
    public float getSaturationLevel(ItemStack stack) {
        return getThirstEnum(stack).getSaturation();
    }
    public float getDirtyChance(ItemStack stack) {
        return getThirstEnum(stack).getThirstyChance();
    }


    public static class Properties extends Item.Properties {
        ThirstEnum thirstEnum;

        public DrinkableBase.Properties thirst(ThirstEnum thirst) {
            this.thirstEnum = thirst;
            return this;
        }
    }
}
