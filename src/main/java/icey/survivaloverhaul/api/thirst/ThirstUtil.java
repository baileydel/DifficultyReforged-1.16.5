package icey.survivaloverhaul.api.thirst;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ThirstUtil
{
	public static IThirstUtil internal;
	
	/**
	 * Ray traces the block a player is looking at and returns it as a ThirstEnumBlockPos
	 * < br>
	 * Returns null if there is no trace result
	 * @param player
	 * @return ThirstEnumBlockPos trace result
	 * 
	 */
	@Nullable
	public static ThirstEnumBlockPos traceWater(PlayerEntity player)
	{	
		return internal.traceWater(player);
	}
	
	/**
	 * Player takes a drink with the specified values and a chance to make them thirsty
	 * @param player
	 * @param thirst
	 * @param saturation
	 * @param thirstyChance 0.0f - 1.0f
	 */
	public static void takeDrink(PlayerEntity player, int thirst, float saturation, float thirstyChance)
	{
		internal.takeDrink(player, thirst, saturation, thirstyChance);
	}
	
	/**
	 * Player takes a drink with the specified values and no chance to make them thirsty
	 * @param player
	 * @param thirst
	 * @param saturation
	 */
	public static void takeDrink(PlayerEntity player, int thirst, float saturation)
	{
		internal.takeDrink(player, thirst, saturation);
	}
	
	/**
	 * Player takes a drink with the values of the ThirstEnum
	 * @param player
	 * @param thirstEnum
	 */
	public static void takeDrink(PlayerEntity player, ThirstEnum thirstEnum)
	{
		internal.takeDrink(player, thirstEnum);
	}
	
	/**
	 * Returns a new Purified Water Bucket item
	 * @return ItemStack purified water bucket
	 */
	public static ItemStack createPurifiedWaterBucket()
	{
		return internal.createPurifiedWaterBucket();
	}
}
