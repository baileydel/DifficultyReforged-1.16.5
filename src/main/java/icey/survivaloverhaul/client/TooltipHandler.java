package icey.survivaloverhaul.client;

import icey.survivaloverhaul.api.item.DrinkableBase;
import icey.survivaloverhaul.api.item.ItemCanteen;
import icey.survivaloverhaul.api.thirst.ThirstEnum;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class TooltipHandler {

    public static boolean DEBUG = true;


    @SubscribeEvent
    public void onConsumableToolTip(ItemTooltipEvent event) {
        List<ITextComponent> lines = event.getToolTip();


        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();

        if (DEBUG) {
            if (item instanceof DrinkableBase) {
                DrinkableBase drink = ((DrinkableBase) item);

                //TODO look for overrides

                ThirstEnum thirstenum = drink.getThirstEnum(stack);

                lines.add(stc("Purification: " + thirstenum.getName()));
                lines.add(stc("Thirst: " + thirstenum.getThirst()));
                lines.add(stc("Saturation: " + thirstenum.getSaturation()));

                if (drink instanceof ItemCanteen) {
                    lines.add(stc("alwaysPurify: " + ((ItemCanteen)drink).getAlwaysPurify()));
                }

            }
        }
    }



    public IFormattableTextComponent stc(String text) {
        return new StringTextComponent(text).mergeStyle(TextFormatting.DARK_GRAY);
    }
}
