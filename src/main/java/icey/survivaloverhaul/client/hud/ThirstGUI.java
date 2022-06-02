package icey.survivaloverhaul.client.hud;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import icey.survivaloverhaul.Main;
import icey.survivaloverhaul.api.CapabilityUtil;
import icey.survivaloverhaul.api.thirst.IThirstCapability;
import icey.survivaloverhaul.client.RenderUtil;
import icey.survivaloverhaul.common.registry.EffectRegistry;
import icey.survivaloverhaul.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;

public class ThirstGUI {
    public static final ResourceLocation ICONS = new ResourceLocation("survivaloverhaul:textures/gui/icons.png");
    public static final ResourceLocation THIRSTHUD = new ResourceLocation("survivaloverhaul:textures/gui/thirsthud.png");

    Minecraft mc = Minecraft.getInstance();

    private Random rand = new Random();
    private int updateCounter = 0;

    //Position on the icons sheet
    private static final int texturepos_X = 0;
    private static final int texturepos_Y = 0;
    //Dimensions of the icon
    private static final int textureWidth = 9;
    private static final int textureHeight = 9;

    int thirstlevel = 0;
    float saturation = 0;

    @SubscribeEvent
    public void onPreRenderGameOverlay(RenderGameOverlayEvent.Post event) {

        if (event.getType() == RenderGameOverlayEvent.ElementType.AIR && Config.COMMON.thirstEnabled.get() /*&& SDCompatibility.defaultThirstDisplay*/) {
            rand.setSeed(updateCounter * 445L);

            boolean classic = false;

            if (classic)
            {
                mc.getTextureManager().bindTexture(ICONS);
            }
            else
            {
                mc.getTextureManager().bindTexture(THIRSTHUD);
            }

            // Render thirst at the scaled resolution
            ClientPlayerEntity player = Minecraft.getInstance().player;

            if (player != null)
            {
                if (player.getCapability(CapabilityUtil.THIRST_CAP).isPresent())
                {
                    IThirstCapability capability = CapabilityUtil.getThirstCapability(player);

                    thirstlevel = capability.getThirstLevel();
                    saturation = capability.getThirstSaturation();
                }

                int getScaledWidth = event.getWindow().getScaledWidth();
                int getScaledHeight = event.getWindow().getScaledHeight();

                renderThirst(event.getMatrixStack(), getScaledWidth, getScaledHeight,  thirstlevel, saturation);

                ForgeIngameGui.right_height += 10;
            }
        }
    }

    private void renderThirst(MatrixStack matrix, int width, int height, int thirst, float thirstSaturation)
    {
        RenderSystem.enableBlend();

        //Many mods set this and forget to set it back.
        //I'm setting it back pre-emptively because this has been reported with two mods.
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);

        int left = width / 2 + 82; //Same x offset as the hunger bar
        int top = height - ForgeIngameGui.right_height;

        Matrix4f m4f = matrix.getLast().getMatrix();

        //Draw the 10 thirst bubbles
        for (int i = 0; i < 10; i++)
        {
            int halfIcon = i * 2 + 1;
            int x = left - i * 8;
            int y = top;

            int bgXOffset = 0;
            int xOffset = 0;

            assert mc.player != null;
            if (mc.player.getActivePotionEffect(EffectRegistry.THIRST.get()) != null)
            {
                xOffset += (textureWidth * 4);
                bgXOffset = (textureWidth * 13);
            }

            //Shake based on saturation and thirst level
            if (thirstSaturation <= 0.0F && updateCounter % (thirst * 3 + 1) == 0)
            {
                y = top + (rand.nextInt(3) - 1);
            }

            //Background
            RenderUtil.drawTexturedModalRect(m4f, x, y, texturepos_X + bgXOffset, texturepos_Y, textureWidth, textureHeight);

            if (halfIcon < thirst)
            {
                RenderUtil.drawTexturedModalRect(m4f, x, y, texturepos_X + xOffset + (textureWidth * 4), texturepos_Y, textureWidth, textureHeight);
            }

            else if (halfIcon == thirst)
            {
                RenderUtil.drawTexturedModalRect(m4f, x, y, texturepos_X + xOffset + (textureWidth * 5), texturepos_Y, textureWidth, textureHeight);
            }
        }

        if (Config.CLIENT.shouldDrawThirstSaturation.get()) {
            int thirstSaturationInt = (int)thirstSaturation;

            if (thirstSaturationInt > 0)
            {
                for (int i = 0; i < 10; i++)
                {
                    int halfIcon = i * 2 + 1;
                    int x = left - i * 8;
                    int y = top;

                    //Foreground
                    if (halfIcon < thirstSaturationInt) //Full
                        RenderUtil.drawTexturedModalRect(m4f, x, y, texturepos_X + (textureWidth * 14), texturepos_Y, textureWidth, textureHeight);
                    else if (halfIcon == thirstSaturationInt) //Half
                        RenderUtil.drawTexturedModalRect(m4f, x, y, texturepos_X + (textureWidth * 15), texturepos_Y, textureWidth, textureHeight);
                }
            }
        }
        RenderSystem.disableBlend();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            if (!mc.isGamePaused()) {
                updateCounter++;
            }
        }
    }
}
