package icey.survivaloverhaul.common.util;

public final class MathUtil {
    public static float invLerp(float from, float to, float value) {
        return (value - from) / (to - from);
    }
}
