package icey.survivaloverhaul.api.config.json;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class JsonConsumableThirst
{
	public JsonItemIdentity identity;
	
	public int amount;
	public float saturation;
	public float thirstyChance;
	
	public JsonConsumableThirst(int amount, float saturation, float thirstyChance, String nbt)
	{
		this(amount, saturation, thirstyChance, new JsonItemIdentity(nbt));
	}
	
	public JsonConsumableThirst(int amount, float saturation, float thirstyChance, JsonItemIdentity identity)
	{
		this.amount = amount;
		this.saturation = saturation;
		this.thirstyChance = thirstyChance;
		this.identity = identity;
	}

	public boolean matches(ItemStack stack) {
		return identity.matches(stack);
	}

	public boolean matches(JsonItemIdentity sentIdentity) {
		return identity.matches(sentIdentity);
	}

	public boolean matches(CompoundNBT compound) {
		return identity.matches(compound);
	}
}
