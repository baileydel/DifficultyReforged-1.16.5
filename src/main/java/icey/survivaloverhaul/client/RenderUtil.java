package icey.survivaloverhaul.client;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public final class RenderUtil {
    /**
     * Basically a more sensibly-named version of Minecraft's included blit function
     *
     * @param matrix The matrix this should be drawn from
     * @param x      Horizontal position on the screen where this texture should be drawn
     * @param y      Vertical position on the screen where this texture should be drawn
     * @param texX   Horizontal position of the texture on the currently bound texture sheet.
     * @param texY   Vertical position of the texture on the currently bound texture sheet.
     * @param width  Width of the given texture, in pixels
     * @param height Height of the given texture, in pixels
     */
    public static void drawTexturedModalRect(Matrix4f matrix, float x, float y, int texX, int texY, int width, int height) {
        float f = 0.00390625f;
        float f1 = 0.00390625f;
        float z = 0.0f;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferBuilder.pos(matrix, x, y + height, z).tex((texX * f), (texY + height) * f1).endVertex();
        bufferBuilder.pos(matrix, (x + width), y + height, z).tex((texX + width) * f, (texY + height) * f1).endVertex();
        bufferBuilder.pos(matrix, (x + width), y, z).tex((texX + width) * f, (texY * f1)).endVertex();
        bufferBuilder.pos(matrix, x, y, z).tex((texX * f), (texY * f1)).endVertex();
        tessellator.draw();
    }

    public static int color(int r, int g, int b) {
        return color(r, g, b, 255);
    }

    public static int color(int r, int g, int b, int a) {
        return ((a&0x0ff)<<24)|((r&0x0ff)<<16)|((g&0x0ff)<<8)|(b&0x0ff);
    }
}
