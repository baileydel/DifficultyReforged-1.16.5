package icey.survivaloverhaul.api.item;

import icey.survivaloverhaul.api.thirst.ThirstEnum;
import icey.survivaloverhaul.api.thirst.ThirstEnumBlockPos;
import icey.survivaloverhaul.api.thirst.ThirstUtil;
import icey.survivaloverhaul.client.RenderUtil;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

//TODO DO WE HAVE TO USE NBT???
public class ItemCanteen extends DrinkableBase {
    private final int maxDoses;
    //TODO Change to always purify
    private final boolean infinitePurification;

    public ItemCanteen(Properties properties) {
        super(properties);
        this.maxDoses = properties.maxDoses;
        this.infinitePurification = properties.infinitePurification;
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        //TODO REDO
        if (!isCanteenFull(stack) || getThirstEnum(stack) == ThirstEnum.NORMAL)
        {
            ThirstEnumBlockPos traceBlockPos = ThirstUtil.traceWater(player);
            if (traceBlockPos != null)
            {
                ThirstEnum trace = traceBlockPos.thirstEnum;

                if (trace == ThirstEnum.PURIFIED) {
                    player.world.updateBlock(traceBlockPos.pos, Blocks.AIR);
                }

                //Convert Rain to Normal
                if (trace == ThirstEnum.RAIN) {
                    trace = ThirstEnum.NORMAL;
                }

                if (this.infinitePurification) {
                    trace = ThirstEnum.PURIFIED;
                }

                addDose(stack, trace);

                player.playSound(SoundEvents.ITEM_BUCKET_FILL, 0.5f, 1.0f);
                player.setActiveHand(hand);
                player.swingArm(hand);
                player.stopActiveHand();
                return new ActionResult<>(ActionResultType.SUCCESS, stack);
            }
        }

        if (!this.isCanteenEmpty(stack)) {
            return DrinkHelper.startDrinking(world, player, hand);
        }
        return ActionResult.resultFail(player.getHeldItem(hand));
    }

    @Override
    public void shrink(PlayerEntity player, ItemStack stack) {
        if (!player.isCreative() && !this.isCanteenEmpty(stack)) {
            this.removeDose(stack, 1);
        }
    }

    @Override
    @Nonnull
    public UseAction getUseAction(@Nonnull ItemStack stack) {
        if (!this.isCanteenEmpty(stack)) {
            return UseAction.DRINK;
        }
        return UseAction.NONE;
    }

    public void setDoses(ItemStack stack, ThirstEnum thirstEnum, int amount) {
        if (stack.getTag() != null) {
            stack.getTag().putInt("Doses",  amount);
            setThirstEnum(stack, thirstEnum);
        }
        else {
            getTag(stack);
        }
    }

    //TODO AMOUNT
    //TODO ADDING DIRTY WATER TO PURIFIED INCREASES DIRTY CHANCE
    public void addDose(ItemStack stack, ThirstEnum thirstEnum) {
        boolean format = formatCanteen(stack, thirstEnum);

        setDoses(stack, thirstEnum, getDoses(stack) + 1);

        if (!format) {
            getDoses(stack);
        }
    }

    //TODO REMOVE
    protected boolean formatCanteen(ItemStack stack, ThirstEnum thirstEnum)
    {
        getTag(stack);
        if (thirstEnum != getThirstEnum(stack))
        {
            //Set canteen to empty and set new type
            setCanteenEmpty(stack);
            //setTypeTag(stack,thirstEnum);
            return true;
        }

        //Preload doses
        getDoses(stack);

        return false;
    }

    protected void getTag(ItemStack stack) {
        CompoundNBT compoundnbt = stack.getOrCreateTag();
        compoundnbt.putInt("Doses", getDoses(stack));
        compoundnbt.putInt("Purification", getThirstEnum(stack).ordinal());
    }

    public void removeDose(ItemStack stack, int amount) {
        this.setDoses(stack, getThirstEnum(stack), getDoses(stack) - amount);
    }

    public int getDoses(ItemStack stack) {
        if (stack.getTag() != null) {
            return stack.getTag().getInt("Doses");
        }
        else {
            getTag(stack);
            System.out.println("GetDoses - Creating Tag");
        }
        return 0;
    }

    public int getMaxDoses() {
        return this.maxDoses;
    }

    public void setThirstEnum(ItemStack stack, ThirstEnum thirstEnum) {
        if (stack.getTag() != null) {
            System.out.println("setting thirst enum");
            stack.getTag().putInt("Purification", thirstEnum.ordinal());
        }
        else {
            getTag(stack);
            System.out.println("ThirstEnum - Creating Tag");
        }
    }

    @Override
    public ThirstEnum getThirstEnum(ItemStack stack) {
        if (stack.getTag() != null) {
            return ThirstEnum.values()[stack.getTag().getInt("Purification")];
        }
        return ThirstEnum.NORMAL;
    }

    public boolean getAlwaysPurify() {
        return this.infinitePurification;
    }

    public void setCanteenFull(ItemStack stack, ThirstEnum thirstEnum) {
       this.setDoses(stack, thirstEnum, this.maxDoses);
    }

    public void setCanteenEmpty(ItemStack stack) {
       this.setDoses(stack, ThirstEnum.NORMAL, 0);
    }

    public boolean isCanteenFull(ItemStack stack) {
        return this.getDoses(stack) >= this.maxDoses;
    }

    public boolean isCanteenEmpty(ItemStack stack) {
        return this.getDoses(stack) == 0;
    }

    @OnlyIn(Dist.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent("Uses " + this.getDoses(stack) + " / " + this.getMaxDoses()));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return RenderUtil.color(30, 175, 255);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        double max = this.getMaxDoses();

        if (max == 0.0d) {
            return 1.0d;
        }

        return (max - (double)getDoses(stack)) / max;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return !isCanteenEmpty(stack);
    }

    public static class Properties extends DrinkableBase.Properties {
        int maxDoses;
        boolean infinitePurification;

        {
            this.maxStackSize(1);
            this.thirst(ThirstEnum.NORMAL);
        }

        public ItemCanteen.Properties maxDoses(int doses) {
            this.maxDoses = doses;
            return this;
        }

        public ItemCanteen.Properties infinitePurification(boolean infinitePurification) {
            this.infinitePurification = infinitePurification;

            if (infinitePurification) {
                return (Properties) this.thirst(ThirstEnum.PURIFIED);
            }

            return this;
        }
    }
}
