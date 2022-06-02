package icey.survivaloverhaul.common.effects;

import icey.survivaloverhaul.api.CapabilityUtil;
import icey.survivaloverhaul.api.thirst.IThirstCapability;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectType;

public class ThirstEffect extends GenericEffect {
    
    public ThirstEffect() {
        super(0x2B9500, EffectType.HARMFUL);
    }

    @Override
    public void performEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = ((PlayerEntity) entity);
            IThirstCapability capability = CapabilityUtil.getThirstCapability(player);
            capability.addThirstExhaustion((float) (0.025d * (1 + amplifier)));
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }
}
