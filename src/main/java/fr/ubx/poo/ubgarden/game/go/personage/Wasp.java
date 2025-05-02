package main.java.fr.ubx.poo.ubgarden.game.go.personage;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import java.util.ArrayList;
import fr.ubx.poo.ubgarden.game.Level;
import fr.ubx.poo.ubgarden.game.engine.Timer;
import fr.ubx.poo.ubgarden.game.Direction;
import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.GameObject;
import fr.ubx.poo.ubgarden.game.go.Movable;
import fr.ubx.poo.ubgarden.game.go.WalkVisitor;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
public class Wasp extends GameObject implements Movable, WalkVisitor {

    private Direction direction;
    private boolean moveRequested = false;
    private final Timer moveTimer;

    public Wasp(Game game, Position position) {
        super(game, position);
        this.direction = Direction.DOWN;
        // Calcule la période en millisecondes (ex: 1 mouvement/sec)
        int freq = game.configuration().waspMoveFrequency();
        int period = 1000 / Math.max(1, freq);
        this.moveTimer = new Timer(period);
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
            if (canMove(dir)) {
                move(dir);
                break;
            }
        }
    }

}
