package icey.survivaloverhaul.common.capability.thirst;

import icey.survivaloverhaul.api.thirst.IThirstCapability;
import icey.survivaloverhaul.common.util.DamageUtil;
import icey.survivaloverhaul.config.Config;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;

public class ThirstCapability implements IThirstCapability
{
    private float exhaustion = 0.0f;
    private int thirst = 20;
    private float saturation = 5.0f;
    private int ticktimer = 0;
    private int damagecounter = 0;

    //Unsaved data
    private int oldthirst = 0;
    private float oldsaturation = 0.0f;
    Vector3d position = null;
    private int packetTimer = 0;

    @Override
    public void tickUpdate(PlayerEntity player, World world, TickEvent.Phase phase)
    {
        if (phase == TickEvent.Phase.START)
        {
            //checkSprint(player);
            packetTimer++;
            return;
        }

        //Initialize position
        if (position == null) {
            position = new Vector3d(player.getPosX(), player.getPosY(), player.getPosZ());
        }

        //Get the new position
        Vector3d newPosition = new Vector3d(player.getPosX(), player.getPosY(), player.getPosZ());

        //Find the movement distance and set the old position to the new position
        position = position.subtract(Math.abs(newPosition.x), Math.abs(newPosition.y), Math.abs(newPosition.z));

        //Movement is sensitive to every hundredth of a block
        int moveDistance = (int)Math.round(position.length() * 100);
        position = newPosition;

        //Avoid getting thirsty on teleport (if you can move 10 blocks in a tick, you win!)
        if (moveDistance > 1000)
        {
            moveDistance = 0;
        }

        if (moveDistance > 0)
        {
            //Manage exhaustion
            float moveSensitivity = (float)Config.COMMON.thirstBaseMovement.get().doubleValue();
            if (player.isInWater())
            {
                moveSensitivity = (float)Config.COMMON.thirstSwimmingMovement.get().doubleValue();
            }
            else if (player.isOnGround())
            {
                if (player.isSprinting())
                {
                    moveSensitivity = (float)Config.COMMON.thirstSprintingMovement.get().doubleValue();
                }
                else
                {
                    moveSensitivity = (float)Config.COMMON.thirstWalkingMovement.get().doubleValue();
                }
            }
            //Sensitive to every hundredth of a block, so multiply by 1/100
            this.addThirstExhaustion(moveSensitivity * 0.01f * moveDistance);
        }

        //Process exhaustion to determine whether to make thirsty
        if (this.getThirstExhaustion() > (float)Config.COMMON.thirstExhaustionLimit.get().intValue())
        {
            //Exhausted, do a thirst tick
            this.addThirstExhaustion(-1.0f * (float)Config.COMMON.thirstExhaustionLimit.get().intValue());

            if (this.getThirstSaturation() > 0.0f)
            {
                //Exhaust from saturation
                this.addThirstSaturation(-1.0f);
            }
            else if (DamageUtil.isModDangerous(world))
            {
                //Exhaust from thirst
                this.addThirstLevel(-1);
            }
        }

        //Hurt ticking
        if (this.getThirstLevel() <= 0)
        {
            this.addThirstTickTimer(1);
            if (this.getThirstTickTimer() >= 80)
            {
                this.setThirstTickTimer(0);

                if (DamageUtil.isModDangerous(world) && DamageUtil.healthAboveDifficulty(world, player))
                {
                    float thirstDamageToApply = 1.0f + (1.0f * (float)this.getThirstDamageCounter() * (float)Config.COMMON.thirstDamageScaling.get().doubleValue());
                    //player.attackEntityFrom(SDDamageSources.DEHYDRATION, thirstDamageToApply);
                    this.addThirstDamageCounter(1);
                }
            }
        }
        else
        {
            //Reset the timer if not dying of thirst
            this.setThirstTickTimer(0);
            this.setThirstDamageCounter(0);
        }
        //checkSprint(player);
    }

    private void checkSprint(PlayerEntity player)
    {
        //Server side sprinting check
        if (player.isSprinting() && this.getThirstLevel() <= 6)
        {
            player.setSprinting(false);
        }
    }

    @Override
    public boolean isDirty()
    {
        return (this.thirst!=this.oldthirst || this.saturation!=this.oldsaturation);
    }

    @Override
    public void setClean()
    {
        this.oldthirst = this.thirst;
        this.oldsaturation=this.saturation;
    }

    @Override
    public float getThirstExhaustion()
    {
        return exhaustion;
    }

    @Override
    public int getThirstLevel()
    {
        return thirst;
    }

    @Override
    public float getThirstSaturation()
    {
        return saturation;
    }

    @Override
    public int getThirstTickTimer()
    {
        return ticktimer;
    }

    @Override
    public int getThirstDamageCounter()
    {
        return damagecounter;
    }

    @Override
    public void setThirstExhaustion(float exhaustion)
    {
        this.exhaustion = Math.max(exhaustion,0.0f);

        if (!Float.isFinite(this.exhaustion))
            this.exhaustion = 0.0f;
    }

    @Override
    public void setThirstLevel(int thirst)
    {
        this.thirst = MathHelper.clamp(thirst, 0, 20);
    }

    @Override
    public void setThirstSaturation(float saturation)
    {
        this.saturation = MathHelper.clamp(saturation, 0.0f, 20.0f);

        if (!Float.isFinite(this.saturation))
            this.saturation = 0.0f;
    }

    @Override
    public void setThirstTickTimer(int ticktimer)
    {
        this.ticktimer = ticktimer;
    }

    @Override
    public void setThirstDamageCounter(int damagecounter)
    {
        this.damagecounter = damagecounter;
    }

    @Override
    public void addThirstExhaustion(float exhaustion) {
        this.setThirstExhaustion(this.getThirstExhaustion() + exhaustion);
    }

    @Override
    public void addThirstLevel(int thirst) {
        this.setThirstLevel(this.getThirstLevel() + thirst);
    }

    @Override
    public void addThirstSaturation(float saturation) {
        this.setThirstSaturation(this.getThirstSaturation() + saturation);
    }

    @Override
    public void addThirstTickTimer(int ticktimer) {
        this.setThirstTickTimer(this.getThirstTickTimer() + ticktimer);
    }

    @Override
    public void addThirstDamageCounter(int damagecounter) {
        this.setThirstDamageCounter(this.getThirstDamageCounter() + damagecounter);
    }

    @Override
    public boolean isThirsty() {
        return this.getThirstLevel() < 20;
    }

    @Override
    public int getPacketTimer()
    {
        return packetTimer;
    }

    public CompoundNBT writeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putFloat("exhaustion", this.exhaustion);
        nbt.putInt("thirst", this.thirst);
        nbt.putFloat("saturation", this.saturation);
        nbt.putInt("ticktimer", this.ticktimer);
        nbt.putInt("damagecounter", this.damagecounter);
        return nbt;
    }

    public void readNBT(CompoundNBT nbt) {
        if (nbt.contains("exhaustion")) {
            this.setThirstExhaustion(nbt.getFloat("exhaustion"));
        }

        if (nbt.contains("thirst")) {
            this.setThirstLevel(nbt.getInt("thirst"));
        }

        if (nbt.contains("saturation")) {
            this.setThirstSaturation(nbt.getFloat("saturation"));
        }

        if (nbt.contains("ticktimer")) {
            this.setThirstTickTimer(nbt.getInt("ticktimer"));
        }

        if (nbt.contains("damagecounter")) {
            this.setThirstDamageCounter(nbt.getInt("damagecounter"));
        }
    }
}

