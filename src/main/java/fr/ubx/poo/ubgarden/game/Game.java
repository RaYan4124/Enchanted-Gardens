package fr.ubx.poo.ubgarden.game;

import fr.ubx.poo.ubgarden.game.go.personage.Gardener;
import main.java.fr.ubx.poo.ubgarden.game.go.decor.DoorPrevOpened;


public class Game {

    private final Configuration configuration;
    private final World world;
    private final Gardener gardener;
    private boolean switchLevelRequested = false;
    private int switchLevel;
    private boolean GameOver = false;
    
    private int carrotsRemaining = 0;

    public Game(World world, Configuration configuration, Position gardenerPosition) {
        this.configuration = configuration;
        this.world = world;
        gardener = new Gardener(this, gardenerPosition);
    }

    public int getCarrotsRemaining() {
        return carrotsRemaining;
    }
    
    public void decrementCarrotsRemaining() {
        carrotsRemaining--;
        if (carrotsRemaining == 0) {
            Level lev = (Level) world().getGrid();
            lev.replaceclsDecor(lev.getCloseDoorPosition(),  new DoorPrevOpened(lev.getCloseDoorPosition()));
        }
    }

    public void setCarrotsRemaining(int n){
        this.carrotsRemaining = n;
    }

    public boolean isGameOver() {
        return GameOver;
    }

    public void setGameOver(boolean gameOver) {
        GameOver = gameOver;
    }

    public Configuration configuration() {
        return configuration;
    }

    public Gardener getGardener() {
        return this.gardener;
    }

    public World world() {
        return world;
    }

    public boolean isSwitchLevelRequested() {
        return switchLevelRequested;
    }

    public int getSwitchLevel() {
        return switchLevel;
    }

    public void requestSwitchLevel(int level) {
        this.switchLevel = level;
        switchLevelRequested = true;
    }

    public void clearSwitchLevel() {
        switchLevelRequested = false;
    }

}
