package icey.survivaloverhaul.common.compat.sereneseasons;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import icey.survivaloverhaul.Main;
import icey.survivaloverhaul.api.temperature.ModifierBase;
import icey.survivaloverhaul.config.Config;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.SeasonHelper;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Modifier;
import java.util.Map;

public class SereneSeasonsModifier extends ModifierBase {
    public static Map<String, SSBiomeIdentity> biomeIdentities = Maps.newHashMap();
    public static String jsonFileName = "biome_info.json";

    public SereneSeasonsModifier() {
        super();
    }

    // This is prone to breaking if Serene Seasons updates how they handle their configuration
    // But unfortunately it's the only method I can think of since whether a biome is tropical or not
    // isn't handled by the api
    public static void prepareBiomeIdentities() {
        try {
            File jsonFile = new File(Main.ssConfigPath.toFile(), jsonFileName);

            if (jsonFile.exists()) {
                Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.PRIVATE).create();
                biomeIdentities = gson.fromJson(new FileReader(jsonFile), new TypeToken<Map<String, SSBiomeIdentity>>() {
                }.getType());
            }
        }
        catch (Exception e) {
            Main.LOGGER.error("Unknown error while reading Serene Seasons config", e);
        }
        Main.LOGGER.debug("Got " + biomeIdentities.size() + " entries from Serene Seasons configs");
    }

    @Override
    public float getWorldInfluence(World world, BlockPos pos) {
        if (!Main.sereneSeasonsLoaded)
            return 0.0f;

        if (!Config.Baked.seasonTemperatureEffects)
            return 0.0f;

        try {
            // In theory, this should only ever run if Serene Seasons is installed
            // However, just to be safe, we put this inside of a try/catch to make
            // sure something weird hasn't happened with the API
            return getUncaughtWorldInfluence(world, pos);
        } catch (Exception e) {
            // If an error somehow occurs, disable compatibility
            Main.LOGGER.error("An error has occured with Serene Seasons compatability, disabling modifier", e);
            Main.sereneSeasonsLoaded = false;

            return 0.0f;
        }
    }

    public float getUncaughtWorldInfluence(World world, BlockPos pos) {
        ISeasonState seasonState = SeasonHelper.getSeasonState(world);

        if (seasonState == null)
            return 0.0f;

        Vector3i[] posOffsets =
                {
                        new Vector3i(0, 0, 0),
                        new Vector3i(10, 0, 0),
                        new Vector3i(-10, 0, 0),
                        new Vector3i(0, 0, 10),
                        new Vector3i(0, 0, -10)
                };

        float value = 0.0f;

        for (Vector3i offset : posOffsets) {
            Biome biome = world.getBiome(pos.add(offset));
            float temperature = biome.getTemperature();

            boolean useTropicalMods;


            if (biomeIdentities.containsKey(biome.getRegistryName().toString())) {
                SSBiomeIdentity identity = biomeIdentities.get(biome.getRegistryName().toString());
                if (!identity.seasonEffects)
                    continue;
                if (identity.isTropical)
                    useTropicalMods = true;
                else
                    useTropicalMods = false;
            } else {
                if (temperature > 0.8f)
                    useTropicalMods = true;
                else
                    useTropicalMods = false;
            }

            if (useTropicalMods) {
                switch (seasonState.getTropicalSeason()) {
                    case EARLY_DRY:
                        value += Config.Baked.earlyDrySeasonModifier;
                        break;
                    case MID_DRY:
                        value += Config.Baked.midDrySeasonModifier;
                        break;
                    case LATE_DRY:
                        value += Config.Baked.lateDrySeasonModifier;
                        break;
                    case EARLY_WET:
                        value += Config.Baked.earlyWetSeasonModifier;
                        break;
                    case MID_WET:
                        value += Config.Baked.midWetSeasonModifier;
                        break;
                    case LATE_WET:
                        value += Config.Baked.lateWetSeasonModifier;
                        break;
                }
            } else {
                switch (seasonState.getSubSeason()) {
                    case EARLY_SPRING:
                        value += Config.Baked.earlySpringModifier;
                        break;
                    case MID_SPRING:
                        value += Config.Baked.midSpringModifier;
                        break;
                    case LATE_SPRING:
                        value += Config.Baked.lateSpringModifier;
                        break;
                    case EARLY_SUMMER:
                        value += Config.Baked.earlySummerModifier;
                        break;
                    case MID_SUMMER:
                        value += Config.Baked.midSummerModifier;
                        break;
                    case LATE_SUMMER:
                        value += Config.Baked.lateSummerModifier;
                        break;
                    case EARLY_AUTUMN:
                        value += Config.Baked.earlyAutumnModifier;
                        break;
                    case MID_AUTUMN:
                        value += Config.Baked.midAutumnModifier;
                        break;
                    case LATE_AUTUMN:
                        value += Config.Baked.lateAutumnModifier;
                        break;
                    case EARLY_WINTER:
                        value += Config.Baked.earlyWinterModifier;
                        break;
                    case MID_WINTER:
                        value += Config.Baked.midWinterModifier;
                        break;
                    case LATE_WINTER:
                        value += Config.Baked.lateWinterModifier;
                        break;
                }
            }
        }

        value /= posOffsets.length;

        return applyUndergroundEffect(value, world, pos);
    }
}
