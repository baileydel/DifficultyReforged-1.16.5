package icey.survivaloverhaul.common.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public final class DamageUtil {
    // Utility class for dealing with damaging players based on their
    // difficulty/mod settings

    public static boolean isModDangerous(World world) {
        return world.getDifficulty() != Difficulty.PEACEFUL;
    }

    public static boolean healthAboveDifficulty(World world, PlayerEntity player) {
        // does the world's current difficulty and player's current health allow for environmental damage?

        Difficulty difficulty = world.getDifficulty();

        return difficulty == Difficulty.HARD ||
                (difficulty == Difficulty.NORMAL && player.getHealth() > 1.0F) ||
                ((difficulty == Difficulty.EASY || difficulty == Difficulty.PEACEFUL) && player.getHealth() > 10.0F);
    }
}
