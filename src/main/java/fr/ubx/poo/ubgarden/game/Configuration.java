package fr.ubx.poo.ubgarden.game;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public record Configuration(int gardenerEnergy, int energyBoost, long energyRecoverDuration, long diseaseDuration, int waspMoveFrequency, int hornetMoveFrequency) {
    public static Configuration fromProperties(String filePath) {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration file: " + filePath, e);
        }

        return new Configuration(
            Integer.parseInt(properties.getProperty("gardenerEnergy", "100")),
            Integer.parseInt(properties.getProperty("energyBoost", "50")),
            Long.parseLong(properties.getProperty("energyRecoverDuration", "1000")),
            Long.parseLong(properties.getProperty("diseaseDuration", "5000")),
            Integer.parseInt(properties.getProperty("waspMoveFrequency", "1")),
            Integer.parseInt(properties.getProperty("hornetMoveFrequency", "2"))
        );
    }
}
