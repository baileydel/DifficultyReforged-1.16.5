package icey.survivaloverhaul.api.thirst;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public interface IThirstUtil
{

	@Nullable
	public ThirstEnumBlockPos traceWater(PlayerEntity player);
	
	public void takeDrink(PlayerEntity player, int thirst, float saturation, float dirtyChance);
	
	public void takeDrink(PlayerEntity player, int thirst, float saturation);
	
	public void takeDrink(PlayerEntity player, ThirstEnum type);
	
	public ItemStack createPurifiedWaterBucket();
	
}
