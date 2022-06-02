package icey.survivaloverhaul;

import icey.survivaloverhaul.common.registry.ItemRegistry;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandler {

    @SubscribeEvent
    public void onEntityRightClick(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof CowEntity) {
            ItemStack stack = event.getItemStack();

            if (stack.getItem() instanceof GlassBottleItem) {
                PlayerEntity player = event.getPlayer();

                if (!player.isCreative()) {
                    stack.shrink(1);
                }

                ItemStack milk = new ItemStack(ItemRegistry.MILK_BOTTLE.get());

                if (!player.addItemStackToInventory(milk)) {
                    player.dropItem(milk, false);
                }
                else {
                    player.addItemStackToInventory(milk);
                }
            }
        }
    }
}
