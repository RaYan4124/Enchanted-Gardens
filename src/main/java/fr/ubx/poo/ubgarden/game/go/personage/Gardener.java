/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubgarden.game.go.personage;

import main.java.fr.ubx.poo.ubgarden.game.go.bonus.PoisonedApple;
import main.java.fr.ubx.poo.ubgarden.game.go.bonus.Insecticide;
import fr.ubx.poo.ubgarden.game.go.bonus.Bonus;
import fr.ubx.poo.ubgarden.game.Direction;
import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.GameObject;
import fr.ubx.poo.ubgarden.game.go.Movable;
import fr.ubx.poo.ubgarden.game.go.PickupVisitor;
import fr.ubx.poo.ubgarden.game.go.WalkVisitor;
import fr.ubx.poo.ubgarden.game.go.bonus.EnergyBoost;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.go.decor.ground.Grass;
import fr.ubx.poo.ubgarden.game.go.decor.ground.Land;
import main.java.fr.ubx.poo.ubgarden.game.go.bonus.Carrots;
import fr.ubx.poo.ubgarden.game.engine.Timer;
import fr.ubx.poo.ubgarden.game.go.Walkable;
import fr.ubx.poo.ubgarden.game.engine.StatusBar;


public class Gardener extends GameObject implements Movable, PickupVisitor, WalkVisitor {

    private final int energy;
    private int currentEnergy;
    private int currentInsecticide;
    private int diseaseLevel;
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
        this.diseaseLevel = 1;
    }

    @Override
    public void pickUp(EnergyBoost energyBoost) {
        if(this.getEnergy() < 100){
            this.currentEnergy += 50;
            if(this.currentEnergy > 100){
                this.currentEnergy = 100;
            }
        }
        this.diseaseLevel = 1;
        System.out.println("Apple collected!"); 
        energyBoost.remove();
    }

    public void pickUp(Carrots carrots) {
        if (this.getPosition().equals(carrots.getPosition())) {
            System.out.println("Carrot collected!");  
            carrots.remove();  
        }
    }

    
    public void pickUp(Insecticide ins){
        currentInsecticide += 1;
            System.out.println("Insecticides collected!"); 
            ins.remove();
    }

    public void pickUp(PoisonedApple pa){
    
        diseaseLevel += 1 ;
        System.out.println("Oupss Poisoned Apple collected!"); 
        pa.remove();
        
    }

    public int getEnergy() {
        return this.currentEnergy;
    }
    
    public int getInsecticide(){
        return this.currentInsecticide;
    }

    public int getDiseaseLevel(){
        return this.diseaseLevel;
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
        if (!this.canMove(direction)) {
            return getPosition(); // retourne la position actuelle sans effectuer de déplacement
        }

        // recupere l'objet Decor à la position cible
        Decor next = game.world().getGrid().get(nextPos);
        // met à jour la position du jardinier
        setPosition(nextPos);

        // interagit avec l'objet Decor s'il existe
        Bonus bonus = next.getBonus();
        if(bonus instanceof Carrots){
            this.pickUp((Carrots)bonus);
        }else if(bonus instanceof EnergyBoost){
            this.pickUp((EnergyBoost)bonus);
        }else if(bonus instanceof Insecticide){
            this.pickUp((Insecticide)bonus);
        }else if(bonus instanceof PoisonedApple){
            this.pickUp((PoisonedApple)bonus);
        }


        if (next != null) {
            next.walkableBy(this);
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

    public void ReduceEnergy() {
        if (this.currentEnergy > 0) {

            Decor currentDecor = game.world().getGrid().get(getPosition());
            if (currentDecor instanceof Grass) {
                this.currentEnergy -= 1 * this.diseaseLevel;
            } else if (currentDecor instanceof Land) {
                this.currentEnergy -= 2 * this.diseaseLevel;
            }

            // energie égale 0 au pire
            if (this.currentEnergy < 0) {
                this.currentEnergy = 0;
            }
        }
    }

}
