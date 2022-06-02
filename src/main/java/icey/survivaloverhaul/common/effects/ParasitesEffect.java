package icey.survivaloverhaul.common.effects;

import icey.survivaloverhaul.api.DamageSources;
import icey.survivaloverhaul.common.util.DamageUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.world.World;

public class ParasitesEffect extends GenericEffect {

    private int savedDuration = 0;

    public ParasitesEffect() {
        super(0xFFE1B7, EffectType.HARMFUL);
    }

    @Override
    public void performEffect(LivingEntity entity, int amplifier)
    {
        if (entity instanceof PlayerEntity)
        {
            World world = entity.getEntityWorld();
            PlayerEntity player = (PlayerEntity) entity;

            double hunger = 0.02d; // Parasites hunger

            //Hunger
            if (hunger > 0.0d)
                player.addExhaustion((float)hunger * (float)(amplifier + 1));

            if (DamageUtil.isModDangerous(world) && DamageUtil.healthAboveDifficulty(world, player))
            {
                double poison = 1.2d; // parasites damage

                if (poison > 0.0d && isReadyVar(savedDuration, amplifier, 25) && player.getRNG().nextDouble() < poison)
                {
                    player.attackEntityFrom(DamageSources.PARASITES, 1.0F);
                }
            }
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier)
    {
        savedDuration = duration;
        return true;
    }

    public boolean isReadyVar(int duration, int amplifier, int var)
    {
        int k = var >> amplifier;

        if (k > 0)
        {
            return duration % k == 0;
        }
        else
        {
            return true;
        }
    }
}
