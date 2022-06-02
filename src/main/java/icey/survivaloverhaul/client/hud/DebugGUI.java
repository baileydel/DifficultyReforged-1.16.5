package icey.survivaloverhaul.client.hud;

import com.google.common.collect.ImmutableMap;
import icey.survivaloverhaul.api.CapabilityUtil;
import icey.survivaloverhaul.api.temperature.ModifierBase;
import icey.survivaloverhaul.api.temperature.TemperatureUtil;
import icey.survivaloverhaul.api.temperature.TemporaryModifier;
import icey.survivaloverhaul.client.RenderUtil;
import icey.survivaloverhaul.common.capability.temperature.TemperatureCapability;
import icey.survivaloverhaul.common.capability.thirst.ThirstCapability;
import icey.survivaloverhaul.common.util.WorldUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Collection;

public class DebugGUI {

    private int WHITE = RenderUtil.color(0, 0, 0);

    @SubscribeEvent
    public void onPreRenderGameOverlay(RenderGameOverlayEvent.Post event)
    {
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT)
        {
            Minecraft mc = Minecraft.getInstance();
            PlayerEntity player = mc.player;
            FontRenderer font = mc.fontRenderer;

            if (player != null)
            {
                ThirstCapability thirst = CapabilityUtil.getThirstCapability(player);
                TemperatureCapability temp = CapabilityUtil.getTempCapability(player);

                int i = 20;

                //THIRST
                font.drawString(event.getMatrixStack(), "Thirst Exhaustion: " + thirst.getThirstExhaustion(), 10, i += 10, WHITE);
                font.drawString(event.getMatrixStack(), "Thirst Saturation: " + thirst.getThirstSaturation(), 10, i += 10, WHITE);

                //TEMPERATURE
                float targetTemp = TemperatureUtil.getPlayerTargetTemperature(player);

                font.drawString(event.getMatrixStack(), "Temperature: " + temp.getTemperatureLevel(), 10, i += 20, WHITE);
                font.drawString(event.getMatrixStack(), "Target Temperature: " + targetTemp, 10, i += 10, WHITE);

                // MODIFIERS
                Collection<ModifierBase> baseMods = GameRegistry.findRegistry(ModifierBase.class).getValues();
                if (baseMods.size() > 0)
                {
                    i += 20;
                    font.drawString(event.getMatrixStack(), "Base Modifiers : ", 10, i, WHITE);

                    World world = player.getEntityWorld();
                    BlockPos pos = WorldUtil.getSidedBlockPos(world, player);

                    for (ModifierBase modifier : GameRegistry.findRegistry(ModifierBase.class).getValues())
                    {
                        if (modifier.getWorldInfluence(world, pos) > 0 || modifier.getPlayerInfluence(player) > 0)
                        {
                            //TODO their influences
                            font.drawString(event.getMatrixStack(), modifier.getRegistryName().toString(), 10, i + 10, WHITE);
                            i += 10;
                        }
                    }
                }

                //TEMPORARY MODIFIERS

                ImmutableMap<String, TemporaryModifier> mods =  temp.getTemporaryModifiers();
                if (mods.size() > 0)
                {
                    i += 20;
                    font.drawString(event.getMatrixStack(), "Temporary Modifiers : ", 10, i, WHITE);

                    for (String entry : mods.keySet())
                    {
                        font.drawString(event.getMatrixStack(), entry + ": Temp: " + mods.get(entry).temperature + ", Duration: " + mods.get(entry).duration, 10, i + 10, WHITE);
                        i += 10;
                    }
                }
            }
        }
    }
}
