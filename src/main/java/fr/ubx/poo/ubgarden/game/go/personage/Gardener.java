/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubgarden.game.go.personage;

import fr.ubx.poo.ubgarden.game.Direction;
import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.GameObject;
import fr.ubx.poo.ubgarden.game.go.Movable;
import fr.ubx.poo.ubgarden.game.go.PickupVisitor;
import fr.ubx.poo.ubgarden.game.go.WalkVisitor;
import fr.ubx.poo.ubgarden.game.go.bonus.EnergyBoost;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.engine.Timer;
import fr.ubx.poo.ubgarden.game.go.Walkable;
import fr.ubx.poo.ubgarden.game.engine.StatusBar;


public class Gardener extends GameObject implements Movable, PickupVisitor, WalkVisitor {

    private final int energy;
    private int currentEnergy;
    private Direction direction;
    private boolean moveRequested = false;
    private Timer timer;
    private long lastreduceenergytime = 0;

    public Gardener(Game game, Position position) {

        super(game, position);
        this.direction = Direction.DOWN;
        this.energy = game.configuration().gardenerEnergy();
        this.timer = new Timer(1000);
        this.currentEnergy = energy;
    }

    @Override
    public void pickUp(EnergyBoost energyBoost) {
// TODO
        System.out.println("I am taking the boost, I should do something ...");

    }


    public int getEnergy() {
        return this.currentEnergy;
    }


    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
            setModified(true);
        }
        moveRequested = true;
    }

    @Override
    public final boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        // check si la position cible est dans les limites de la carte
        if (nextPos.x() < 0 || nextPos.x() >= game.world().getGrid().width() ||
            nextPos.y() < 0 || nextPos.y() >= game.world().getGrid().height()) {
            System.out.println("deplacement hors des limites !");
            return false;
        }
        // recupere l'objet Decor à la position cible
        Decor next = game.world().getGrid().get(nextPos);
        // check si la position cible est un Decor
        if (next == null) {
            System.out.println("deplacement vers un decor null !");
            return false;
        }
        // check si la position cible est walkable
        if (!next.walkableBy(this)) {
            System.out.println("deplacement vers un decor non walkable !");
            return false;
        }
        return true;
    }

    @Override
    public Position move(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
    
        // check si la position cible est dans les limites de la carte
        if (nextPos.x() < 0 || nextPos.x() >= game.world().getGrid().width() ||
            nextPos.y() < 0 || nextPos.y() >= game.world().getGrid().height()) {
            System.out.println("deplacement hors des limites !");
            return getPosition(); // retourne la position actuelle sans effectuer de déplacement
        }
        // recupere l'objet Decor à la position cible
        Decor next = game.world().getGrid().get(nextPos);
    
        // Met à jour la position du jardinier
        setPosition(nextPos);
        // Interagit avec l'objet Decor s'il existe
        if (next != null) {
            next.walkableBy(this);
            //next.pickUpBy(this);
        }
    
        return nextPos;
    }

    public void update(long now) {
        if (moveRequested) {
            if (canMove(direction)) {
                move(direction);
                ReduceEnergy();
            }
            moveRequested = false;   
            timer.stop(); //aucune regenaration d'energie en cours
            lastreduceenergytime = now;
        }else{
            if(currentEnergy < 100){
                timer.update(now); //mis a jour a l'instant t
                if(!timer.isRunning() && (now - lastreduceenergytime) > 1000000000){ //si le temps entre la derniere reduction et now est d'en moins 1s
                    currentEnergy++;
                    timer.start();
                }
            }else{
                timer.stop();
            }
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

    public void ReduceEnergy(){
        if(this.currentEnergy > 0){
            this.currentEnergy--;
        }
    }

}
