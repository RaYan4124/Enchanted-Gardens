package fr.ubx.poo.ubgarden.game.launcher;

import fr.ubx.poo.ubgarden.game.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class GameLauncher {

    private GameLauncher() {
    }

    public static GameLauncher getInstance() {
        return LoadSingleton.INSTANCE;
    }

    private int integerProperty(Properties properties, String name, int defaultValue) {
        return Integer.parseInt(properties.getProperty(name, Integer.toString(defaultValue)));
    }

    private boolean booleanProperty(Properties properties, String name, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(name, Boolean.toString(defaultValue)));
    }

    private Configuration getConfiguration(Properties properties) {

        // Load parameters
        int waspMoveFrequency = integerProperty(properties, "waspMoveFrequency", 2);
        int hornetMoveFrequency = integerProperty(properties, "hornetMoveFrequency", 1);

        int gardenerEnergy = integerProperty(properties, "gardenerEnergy", 100);
        int energyBoost = integerProperty(properties, "energyBoost", 50);
        long energyRecoverDuration = integerProperty(properties, "energyRecoverDuration", 1_000);
        long diseaseDuration = integerProperty(properties, "diseaseDuration", 5_000);

        return new Configuration(gardenerEnergy, energyBoost, energyRecoverDuration, diseaseDuration, waspMoveFrequency, hornetMoveFrequency);
    }

    public Game load(File file) {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(file)) {
            properties.load(input);
            System.out.println("Loading .....");
        } catch (IOException e) {
            throw new RuntimeException("failed to load configuration file: " + file.getAbsolutePath(), e);
        }
    
        Configuration configuration = getConfiguration(properties);
    
        boolean compressed = booleanProperty(properties, "compression", false);
        int levelsCount = integerProperty(properties, "levels", 1);
    
        World world = new World(levelsCount);
    
        MapLevel firstMapLevel = null;
        Position gardenerPosition = null;
    
        for (int lvl = 1; lvl <= levelsCount; lvl++) {
            String levelData = properties.getProperty("level" + lvl);
            if (levelData == null) {
                throw new RuntimeException("Level " + lvl + " not found in the file");
            }
            MapLevel mapLevel;
            if (compressed) {
                mapLevel = MapLevel.fromCompressedString(levelData);
            } else {
                mapLevel = MapLevel.fromString(levelData);
            }
    
            if (lvl == 1) {
                gardenerPosition = mapLevel.getGardenerPosition();
                firstMapLevel = mapLevel;
            }
    
            Game game = new Game(world, configuration, gardenerPosition);
            Map level = new Level(game, lvl, mapLevel);
            world.put(lvl, level);
        }
    
        if (gardenerPosition == null) {
            throw new RuntimeException("gardener not found in level1");
        }
    
        Game game = new Game(world, configuration, gardenerPosition);

        System.out.println("Game successfully loaded !!");
        return game;
    }

    public Game load() {
        Properties emptyConfig = new Properties();
        MapLevel mapLevel = new MapLevelDefaultStart();
        Position gardenerPosition = mapLevel.getGardenerPosition();
        if (gardenerPosition == null)
            throw new RuntimeException("Gardener not found");
        Configuration configuration = getConfiguration(emptyConfig);
        World world = new World(1);
        Game game = new Game(world, configuration, gardenerPosition);
        Map level = new Level(game, 1, mapLevel);
        world.put(1, level);
        return game;
    }

    private static class LoadSingleton {
        static final GameLauncher INSTANCE = new GameLauncher();
    }
}
