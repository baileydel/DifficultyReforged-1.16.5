package icey.survivaloverhaul.api.item;

public class TemperatureConsumable extends DrinkableBase {
    String tempGroup;
    float temperature;
    int duration;


    public TemperatureConsumable(TemperatureConsumable.Properties properties) {
        super(properties);
        this.tempGroup = properties.tempGroup;
        this.temperature = properties.temperature;
        this.duration = properties.duration;
    }


    public String  getTempGroup() {
        return this.tempGroup;
    }

    public float getTemperature() {
        return this.temperature;
    }
    public int getDuration() {
        return this.duration;
    }

    public static class Properties extends DrinkableBase.Properties {
        String tempGroup;
        float temperature;
        int duration;

        public Properties tempGroup(String tempGroup) {
            this.tempGroup = tempGroup;
            return this;
        }

        public Properties temperature(float temperature) {
            this.temperature = temperature;
            return this;
        }

        public Properties duration(int duration) {
            this.duration = duration;
            return this;
        }
    }
}
