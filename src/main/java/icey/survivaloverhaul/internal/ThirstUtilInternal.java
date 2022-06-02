package icey.survivaloverhaul.internal;

import icey.survivaloverhaul.api.CapabilityUtil;
import icey.survivaloverhaul.api.thirst.*;
import icey.survivaloverhaul.common.registry.EffectRegistry;
import icey.survivaloverhaul.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;

public class ThirstUtilInternal implements IThirstUtil {
    public static ThirstEnumBlockPos traceWaterToDrink(PlayerEntity player)
    {
        if (player.getHeldItemMainhand().isEmpty())
        {
            IThirstCapability capability = CapabilityUtil.getThirstCapability(player);

            if (capability.isThirsty())
            {
                ThirstEnumBlockPos traceResult = ThirstUtil.traceWater(player);

                if (traceResult == null)
                {
                    return null;
                }

                if (traceResult.thirstEnum == ThirstEnum.PURIFIED)
                {
                    if (!Config.COMMON.canDrinkWaterSources.get()) {
                        return null;
                    }

                    if (!Config.COMMON.isPurifiedWaterInfinite.get()) {
                        player.world.updateBlock(traceResult.pos, Blocks.AIR);
                    }
                }
                else if (traceResult.thirstEnum == ThirstEnum.RAIN && !Config.COMMON.canDrinkRain.get())
                {
                    return null;
                }
                else if (traceResult.thirstEnum == ThirstEnum.NORMAL && !Config.COMMON.canDrinkWaterSources.get())
                {
                    return null;
                }
                return traceResult;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public ThirstEnumBlockPos traceWater(PlayerEntity player) {
        //Check if player is looking up, if it's raining, if they can see sky, and if THIRST_DRINK_RAIN is enabled
        //This essentially means rain can't be a trace result for drinking or for a canteen

        //Drinking rain
        if (player.rotationPitch < -75.0f && player.world.isRainingAt(player.getPosition()) && player.world.canSeeSky(player.getPosition()) && Config.COMMON.canDrinkRain.get()) {
            return new ThirstEnumBlockPos(ThirstEnum.RAIN, player.getPosition());
        }

        //Get the player's reach distance attribute and cut it in half
        double reach = player.getAttributeValue(ForgeMod.REACH_DISTANCE.get()) * 0.5d;

        //Similar to Entity.rayTrace
        Vector3d eyevec = player.getEyePosition(1.0f);
        Vector3d lookvec = player.getLook(1.0f);
        Vector3d targetvec = eyevec.add(lookvec.x * reach, lookvec.y * reach, lookvec.z * reach);


        //Ray trace from the player's eyepos to where they are looking, and stop at liquids
        RayTraceResult trace = player.getEntityWorld().rayTraceBlocks(new RayTraceContext(eyevec, targetvec, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.ANY, player));

       //if (trace.getType() != RayTraceResult.Type.BLOCK)
       //    return null;

        //Hit a block
        Block traceBlock = player.world.getBlockState(new BlockPos(trace.getHitVec())).getBlock();
        if (traceBlock == Blocks.WATER)
        {
            return new ThirstEnumBlockPos(ThirstEnum.NORMAL, new BlockPos(trace.getHitVec()));
        }
        //else if (traceBlock == SDFluids.blockPurifiedWater)
        //{
        //    return new ThirstEnumBlockPos(ThirstEnum.PURIFIED, trace.getBlockPos());
        //}
        return null;
    }

    @Override
    public void takeDrink(PlayerEntity player, int thirst, float saturation, float dirtyChance)
    {
        if (Config.COMMON.thirstEnabled.get()) {
            IThirstCapability capability = CapabilityUtil.getThirstCapability(player);

            if (capability.isThirsty()) {
                capability.addThirstLevel(thirst);
                capability.addThirstSaturation(saturation);
            }
            else
            {
                if (capability.getThirstSaturation() < saturation) {
                    capability.setThirstSaturation(saturation);
                }
            }

            //TODO add parasites and thirst
            if (dirtyChance != 0.0f && player.world.rand.nextFloat() < dirtyChance) {
                player.addPotionEffect(new EffectInstance(EffectRegistry.THIRST.get(), 600));

                if (player.world.rand.nextDouble() < 0.2d) {
                    player.addPotionEffect(new EffectInstance(EffectRegistry.PARASITES.get(), 1200));
                }
            }
        }
    }

    @Override
    public void takeDrink(PlayerEntity player, int thirst, float saturation) {
        takeDrink(player, thirst, saturation, 0.0F);
    }

    @Override
    public void takeDrink(PlayerEntity player, ThirstEnum type) {
        takeDrink(player, type.getThirst(), type.getSaturation(), type.getThirstyChance());
    }

    @Override
    public ItemStack createPurifiedWaterBucket() {
        return null;
    }
}
