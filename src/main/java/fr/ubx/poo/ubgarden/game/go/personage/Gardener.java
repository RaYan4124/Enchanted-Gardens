/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubgarden.game.go.personage;
import fr.ubx.poo.ubgarden.game.Level;
import main.java.fr.ubx.poo.ubgarden.game.go.bonus.PoisonedApple;
import main.java.fr.ubx.poo.ubgarden.game.go.decor.DoorNextOpened;
import main.java.fr.ubx.poo.ubgarden.game.go.decor.DoorPrevOpened;
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
    private long lastHurtTime = 0;
    private static final long INVULNERABILITY_DURATION = 1_000_000_000L; //une seconde en ns
    private boolean justHurt = false;


    public Gardener(Game game, Position position) {

        super(game, position);
        this.direction = Direction.DOWN;
        this.energy = game.configuration().gardenerEnergy();
        this.timer = new Timer(game.configuration().energyRecoverDuration());
        this.currentEnergy = energy;
        this.diseaseLevel = 1;
        System.out.println("Gardener created with energy: " + game.configuration().gardenerEnergy());
    }

    public void setJustHurt(boolean value) {
        this.justHurt = value;
    }

    public boolean isJustHurt() {
        return justHurt;
    }

    @Override
    public void pickUp(EnergyBoost eb) {
        if(this.getEnergy() < game.configuration().gardenerEnergy()){
            this.currentEnergy += game.configuration().energyBoost();
            if(this.currentEnergy > game.configuration().gardenerEnergy()){
                this.currentEnergy = game.configuration().gardenerEnergy();
            }
        }
        this.diseaseLevel = 1;
        System.out.println("Apple collected!"); 
        eb.remove();
    }

    public void pickUp(Carrots carrots) {
        if (this.getPosition().equals(carrots.getPosition())) {
            System.out.println("Carrot collected!");
            //game.decrementCarrotsRemaining();
            //System.out.println(game.getCarrotsRemaining() + " Carrots left ...");
            Level level = (Level) game.world().getGrid();
            level.decrementCarrotsRemaining();
            System.out.println(level.getCarrotsRemaining() + " Carrots left ...");
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

    public void useInsecticide(){
        if(currentInsecticide > 0){
            currentInsecticide -= 1;
        }else{
            System.out.println("No insecticides left!");
        }
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
            return getPosition(); // retourne la position actuelle sans effectuer de deplacement
        }

        // recupere l'objet Decor a la position cible
        Decor next = game.world().getGrid().get(nextPos);
        // met à jour la position du jardinier
        setPosition(nextPos);
        this.setJustHurt(false);

        // interagit avec l'objet Decor s'il existe
        if(next instanceof Grass || next instanceof Land){
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
        }

        if(next instanceof DoorNextOpened) {
            int nextLevel = game.world().currentLevel() + 1;
            game.requestSwitchLevel(nextLevel);
            return nextPos;
        }
        if (next instanceof DoorPrevOpened) {
            int prevLevel = game.world().currentLevel() - 1;
            game.requestSwitchLevel(prevLevel);
            return nextPos;
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
            if(currentEnergy < game.configuration().gardenerEnergy()){
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

    public void hurt(int damage, long now) {
        System.out.println("before hurt: " + this.currentEnergy);
        if (now - lastHurtTime < INVULNERABILITY_DURATION) return;
        if (this.currentEnergy > 0) {
            this.currentEnergy -= damage * this.diseaseLevel;
            if (this.currentEnergy < 0) {
                this.currentEnergy = 0;
            }
        }
        lastHurtTime = now;
        System.out.println("after hurt: " + this.currentEnergy);
    }

    /*public void hurt() {
        hurt(1);
    }*/

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

            if (this.currentEnergy < 0) {
                this.currentEnergy = 0;
            }
        }
    }

}
