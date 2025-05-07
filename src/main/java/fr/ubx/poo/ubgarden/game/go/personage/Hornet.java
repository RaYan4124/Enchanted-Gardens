package main.java.fr.ubx.poo.ubgarden.game.go.personage;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import fr.ubx.poo.ubgarden.game.Level;
import fr.ubx.poo.ubgarden.game.engine.Timer;
import fr.ubx.poo.ubgarden.game.Direction;
import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.GameObject;
import fr.ubx.poo.ubgarden.game.go.Movable;
import fr.ubx.poo.ubgarden.game.go.WalkVisitor;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import main.java.fr.ubx.poo.ubgarden.game.go.decor.DoorNextClosed;
import main.java.fr.ubx.poo.ubgarden.game.go.decor.DoorNextOpened;
import main.java.fr.ubx.poo.ubgarden.game.go.decor.DoorPrevOpened;

public class Hornet extends GameObject implements Movable, WalkVisitor {

    private Direction direction;
    private boolean moveRequested = false;
    private final Timer moveTimer;
    private int insecticideHits = 0;
    private int gardenerHits = 0; 

    public Hornet(Game game, Position position) {
        super(game, position);
        this.direction = Direction.DOWN;
        int freq = game.configuration().hornetMoveFrequency();
        int period = 1000 / Math.max(1, freq);
        this.moveTimer = new Timer(period);
    }

    public void hitGardener(){
        gardenerHits++;
        System.out.println(gardenerHits);
    }

    public int getGardenerHits() {
        return gardenerHits;
    }

    public boolean isKilledByGardener() {
        return gardenerHits >= 2;
    }

    public int getInsecticideHits() {
        return insecticideHits;
    }   

    public void hitInsecticide() {
        insecticideHits++;
        System.out.println(insecticideHits);
    }

    public boolean isKilledByInsecticide() {
        return insecticideHits >= 2;
    }

    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
            setModified(true);
        }
        moveRequested = true;
    }

    @Override
    public boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        Decor nexDecor = game.world().getGrid().get(nextPos);
        if (nextPos.x() < 0 || nextPos.x() >= game.world().getGrid().width() ||
            nextPos.y() < 0 || nextPos.y() >= game.world().getGrid().height() || 
            nexDecor == null || nexDecor instanceof DoorPrevOpened || nexDecor instanceof DoorNextClosed ||
            nexDecor instanceof DoorNextOpened) {
            return false;
        }
        return true;
    }

    @Override
    public Position move(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        setPosition(nextPos);
        return nextPos;
    }

    public void update(long now) {
        moveTimer.update(now);
        if (!moveTimer.isRunning()) {
            moveRandom();
            moveTimer.start();
        }
    }

    public void hurt(int damage) {
       /*  Level level = (Level) game.world().getGrid();
        if (level.getHornets().size() > 1) {
            level.removeHornet(this);
            setModified(true);
        } else {
            level.removeHornet(this);
            setModified(true);
        }*/
    }

    public void hurt() {
        hurt(1);
    }

    public Direction getDirection() {
        return direction;
    }

    private void moveRandom() {
        List<Direction> dirs = Arrays.asList(Direction.values());
        Collections.shuffle(dirs);
        for (Direction dir : dirs) {
            if (canMove(dir)){
                move(dir);
                break;
            }
        }
    }
}