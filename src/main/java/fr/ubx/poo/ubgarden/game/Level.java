package fr.ubx.poo.ubgarden.game;

import fr.ubx.poo.ubgarden.game.go.bonus.EnergyBoost;
import main.java.fr.ubx.poo.ubgarden.game.go.decor.Flowers;
import main.java.fr.ubx.poo.ubgarden.game.go.decor.NestWasp;
import main.java.fr.ubx.poo.ubgarden.game.go.decor.NestHornet;
import main.java.fr.ubx.poo.ubgarden.game.go.decor.DoorNextOpened;
import main.java.fr.ubx.poo.ubgarden.game.go.decor.DoorNextClosed;
import main.java.fr.ubx.poo.ubgarden.game.go.decor.DoorPrevOpened;
import main.java.fr.ubx.poo.ubgarden.game.go.decor.Hedgehog;
import main.java.fr.ubx.poo.ubgarden.game.go.bonus.Carrots;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.go.decor.Tree;
import fr.ubx.poo.ubgarden.game.go.decor.ground.Land;
import fr.ubx.poo.ubgarden.game.go.decor.ground.Grass;
import fr.ubx.poo.ubgarden.game.launcher.MapEntity;
import fr.ubx.poo.ubgarden.game.launcher.MapLevel;
import main.java.fr.ubx.poo.ubgarden.game.go.bonus.PoisonedApple;
import main.java.fr.ubx.poo.ubgarden.game.go.bonus.Insecticide;
import main.java.fr.ubx.poo.ubgarden.game.go.personage.Wasp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Level implements Map {

    private final Game game;
    private final int level;
    private final int width;

    private final int height;

    private Position CloseDoorPos;

    private final java.util.Map<Position, Decor> decors = new HashMap<>();
    private final List<Wasp> wasps = new ArrayList<>();

    private int carrotsCount = 0;
    private int carrotsRemaining = 0;

    public Level(Game game, int level, MapLevel entities) {
        this.game = game;
        this.level = level;
        this.width = entities.width();
        this.height = entities.height();

        int CarrotsNB = 0;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Position position = new Position(level, i, j);
                MapEntity mapEntity = entities.get(i, j);
                switch (mapEntity) {
                    case Grass:
                        decors.put(position, new Grass(position));
                        break;
                    case Tree:
                        decors.put(position, new Tree(position));
                        break;
                    case Apple: {
                        Decor grass = new Grass(position);
                        grass.setBonus(new EnergyBoost(position, grass));
                        decors.put(position, grass);
                        break;
                    }
                    case Land:{
                        decors.put(position, new Land(position));
                        break;
                    }
                    case Flowers:
                        decors.put(position, new Flowers(position));
                        break;
                    case NestWasp:{
                        decors.put(position, new NestWasp(game, position));
                        break;
                    }
                    case NestHornet:{
                        decors.put(position, new NestHornet(position));
                        break;
                    }
                    case DoorNextOpened:{
                        decors.put(position, new DoorNextClosed(position)); // Fermé les portes de passage à un autre level
                        this.CloseDoorPos = position;
                        break;
                    }
                    case DoorPrevOpened:{
                        decors.put(position, new DoorPrevOpened(position));
                        break;
                    }
                    case DoorNextClosed:{
                        decors.put(position, new DoorNextClosed(position));
                        break;
                    }
                    case Hedgehog:{
                        decors.put(position, new Hedgehog(position));
                        break;
                    }
                    case Carrots:{
                        Decor land = new Land(position);
                        land.setBonus(new Carrots(position, land));
                        carrotsCount++;
                        carrotsRemaining++;
                        decors.put(position,land);
                        break;
                    }
                    case PoisonedApple:{
                        Decor grass = new Grass(position);
                        grass.setBonus(new PoisonedApple(position, grass));
                        decors.put(position,grass);
                        break;
                    }
                    case Insecticide:{
                        Decor grass = new Grass(position);
                        grass.setBonus(new Insecticide(position, grass));
                        decors.put(position,grass);
                        break;
                    } 
                    case Wasp:{
                        Wasp wasp = new Wasp(game, position);
                        wasps.add(wasp);
                        decors.put(position, new Grass(position));
                        break;
                    }
                    default:
                        throw new RuntimeException("EntityCode " + mapEntity.name() + " not processed");
                }
            }
        }
    }

    public int getCarrotsCount() {
        return carrotsCount;
    }
    
    public int getCarrotsRemaining() {
        return carrotsRemaining;
    }
    
    public void decrementCarrotsRemaining() {
        carrotsRemaining--;
        if (carrotsRemaining == 0) {

            replaceDecor(getCloseDoorPosition(), new DoorNextOpened(getCloseDoorPosition()));
        }
    }


    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return this.height;
    }

    public Decor get(Position position) {
        return decors.get(position);
    }

    public Collection<Decor> values() {
        return decors.values();
    }


    @Override
    public boolean inside(Position position) {
        return true;
    }

    public void replaceDecor(Position pos, Decor decor) {
        decors.put(pos, decor);
    }

    public Position getCloseDoorPosition (){
        return CloseDoorPos;
    }

    public void addWasp(Wasp wasp) {
        wasps.add(wasp);
    }

    public List<Wasp> getWasps() {
        return wasps;
    }
}
