package icey.survivaloverhaul.api.temperature;

// Stolen shamelessly from Charles445's SimpleDifficulty mod
// https://github.com/Charles445/SimpleDifficulty/blob/v0.3.4/src/main/java/com/charles445/simpledifficulty/api/temperature/TemperatureEnum.java
public enum TemperatureEnum {
    FROSTBITE(0, 6), // You start dying.
    COLD(7, 12),
    NORMAL(13, 18),
    HOT(19, 24), // The player will begin to sweat. This grows in intensity as the player gets hotter.
    HEAT_STROKE(25, 30); // You start dying.

    private int lowerBound;
    private int upperBound;

    TemperatureEnum(int lowerBound, int upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public boolean matches(int temperature) {
        return (temperature >= this.lowerBound && temperature <= this.upperBound);
    }

    public int getMiddle() {
        return (this.upperBound + this.lowerBound) / 2;
    }

    public int getLowerBound() {
        return this.lowerBound;
    }

    public int getUpperBound() {
        return this.upperBound;
    }
}
