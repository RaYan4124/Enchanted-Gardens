/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubgarden.game.view;

import fr.ubx.poo.ubgarden.game.go.GameObject;
import fr.ubx.poo.ubgarden.game.go.bonus.EnergyBoost;
import fr.ubx.poo.ubgarden.game.go.decor.Tree;
import fr.ubx.poo.ubgarden.game.go.decor.ground.Grass;
import fr.ubx.poo.ubgarden.game.go.decor.ground.Land;
import main.java.fr.ubx.poo.ubgarden.game.go.decor.Flowers;
import main.java.fr.ubx.poo.ubgarden.game.go.decor.NestWasp;
import main.java.fr.ubx.poo.ubgarden.game.go.decor.NestHornet;
import main.java.fr.ubx.poo.ubgarden.game.go.decor.DoorNextOpened;
import main.java.fr.ubx.poo.ubgarden.game.go.decor.DoorPrevOpened;
import main.java.fr.ubx.poo.ubgarden.game.go.decor.DoorNextClosed;
import main.java.fr.ubx.poo.ubgarden.game.go.decor.Hedgehog;
import main.java.fr.ubx.poo.ubgarden.game.go.bonus.Carrots;
import main.java.fr.ubx.poo.ubgarden.game.go.bonus.PoisonedApple;
import main.java.fr.ubx.poo.ubgarden.game.go.bonus.Insecticide;
import main.java.fr.ubx.poo.ubgarden.game.go.personage.Wasp;
import javafx.scene.layout.Pane;

import static fr.ubx.poo.ubgarden.game.launcher.MapEntity.DoorPrevOpened;
import static fr.ubx.poo.ubgarden.game.view.ImageResource.*;


public final class SpriteFactory {

    public static Sprite create(Pane layer, GameObject gameObject) {
        ImageResourceFactory factory = ImageResourceFactory.getInstance();
        if (gameObject instanceof Grass)
            return new Sprite(layer, factory.get(GRASS), gameObject);
        if (gameObject instanceof Tree)
            return new Sprite(layer, factory.get(TREE), gameObject);
        if (gameObject instanceof EnergyBoost)
            return new Sprite(layer, factory.get(APPLE), gameObject);
        if (gameObject instanceof Land)
            return new Sprite(layer, factory.get(LAND), gameObject);
        if (gameObject instanceof Flowers)
            return new Sprite(layer, factory.get(FLOWERS), gameObject);
        if (gameObject instanceof NestWasp)
            return new Sprite(layer, factory.get(NESTWASP), gameObject);
        if (gameObject instanceof NestHornet)
            return new Sprite(layer, factory.get(NESTHORNET), gameObject);
        if (gameObject instanceof DoorNextOpened)
            return new Sprite(layer, factory.get(DOOR_OPENED_PLUS), gameObject);
        if (gameObject instanceof DoorPrevOpened) 
            return new Sprite(layer, factory.get(DOOR_OPENED_MINUS), gameObject);
        if (gameObject instanceof DoorNextClosed)
            return new Sprite(layer, factory.get(DOOR_CLOSED_PLUS), gameObject);
        if (gameObject instanceof Hedgehog)
            return new Sprite(layer, factory.get(HEDGEHOG), gameObject);
        if (gameObject instanceof Carrots)
            return new Sprite(layer, factory.get(CARROTS), gameObject);
        if (gameObject instanceof PoisonedApple)
            return new Sprite(layer, factory.get(POISONED_APPLE), gameObject);
        if (gameObject instanceof Insecticide)
            return new Sprite(layer, factory.get(INSECTICIDE), gameObject);  
        
        throw new RuntimeException("Unsupported sprite for decor " + gameObject);
    }
}
