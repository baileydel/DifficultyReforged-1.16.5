package icey.survivaloverhaul.config.json;

import com.google.gson.reflect.TypeToken;
import icey.survivaloverhaul.api.config.json.temperature.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class JsonTypeToken {
    public static Type get(JsonFileName jcfn) {
        switch (jcfn) {
            case ARMOR:
                return new TypeToken<Map<String, List<JsonArmorIdentity>>>() {
                }.getType();
            case BLOCK:
                return new TypeToken<Map<String, List<JsonPropertyTemperature>>>() {
                }.getType();
            case LIQUID:
                return new TypeToken<Map<String, JsonTemperature>>() {
                }.getType();
            case BIOME:
                return new TypeToken<Map<String, JsonBiomeIdentity>>() {
                }.getType();
            case CONSUMABLE:
                return new TypeToken<Map<String, List<JsonConsumableTemperature>>>() {
                }.getType();
            default:
                return null;
        }
    }
}
