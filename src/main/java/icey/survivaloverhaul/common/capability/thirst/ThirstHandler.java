package icey.survivaloverhaul.common.capability.thirst;

import icey.survivaloverhaul.api.CapabilityUtil;
import icey.survivaloverhaul.api.thirst.IThirstCapability;
import icey.survivaloverhaul.common.network.packets.PacketDrink;
import icey.survivaloverhaul.config.Config;
import icey.survivaloverhaul.internal.ThirstUtilInternal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ThirstHandler {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event)
    {
        if (Config.COMMON.thirstEnabled.get())
        {
            PlayerEntity player = event.getPlayer();
            if (event.getHand() == Hand.MAIN_HAND)
            {
                if (canDrink(player))
                {
                    PacketDrink.send();
                    player.swingArm(Hand.MAIN_HAND);
                    player.playSound(SoundEvents.ENTITY_GENERIC_DRINK, 0.5f, 1.0f);
                }
            }
        }
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event)
    {
        if (!event.getEntityLiving().world.isRemote)
        {
            if (Config.COMMON.thirstEnabled.get()) {
                PlayerEntity player = event.getPlayer();

                if (!shouldSkipThirst(player))
                {
                    Entity monster = event.getTarget();

                    if (monster.canBeAttackedWithItem() && !monster.hitByEntity(player))
                    {
                        addExhaustion(player, Config.COMMON.attackingExhaustion.get());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event)
    {
        if (!event.getPlayer().world.isRemote)
        {
            if (Config.COMMON.thirstEnabled.get())
            {
                PlayerEntity player = event.getPlayer();

                if (!shouldSkipThirst(player))
                {
                    addExhaustion(player, Config.COMMON.blockBreakExhaustion.get());
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event)
    {
        if (!event.getEntityLiving().world.isRemote)
        {
            if (event.getEntity() instanceof PlayerEntity)
            {
                if (Config.COMMON.thirstEnabled.get())
                {
                    PlayerEntity player = (PlayerEntity) event.getEntity();

                    if (event.getAmount() > 0.0F)
                    {
                        if (!shouldSkipThirst(player))
                        {
                            addExhaustion(player, event.getSource().getHungerDamage());
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event)
    {
        if (!event.getEntityLiving().world.isRemote)
        {
            if (event.getEntityLiving() instanceof PlayerEntity)
            {
                if (Config.COMMON.thirstEnabled.get())
                {
                    PlayerEntity player = (PlayerEntity) event.getEntity();

                    if (!shouldSkipThirst(player))
                    {
                        if (player.isSprinting())
                        {
                            addExhaustion(player, Config.COMMON.sprintJumpExhaustion.get());
                        }
                        else
                        {
                            addExhaustion(player,  Config.COMMON.jumpExhaustion.get());
                        }
                    }
                }
            }
        }
    }

    private boolean canDrink(PlayerEntity player)
    {
        return player.isCrouching() && ThirstUtilInternal.traceWaterToDrink(player) != null;
    }

    private void addExhaustion(PlayerEntity player, float exhaustion)
    {
        IThirstCapability capability = CapabilityUtil.getThirstCapability(player);
        capability.addThirstExhaustion(exhaustion);
    }

    private boolean shouldSkipThirst(PlayerEntity player)
    {
        return player.isCreative() || player.isSpectator();
    }
}
