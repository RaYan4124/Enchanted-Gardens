package main.java.fr.ubx.poo.ubgarden.game.view;

import fr.ubx.poo.ubgarden.game.Direction;
import main.java.fr.ubx.poo.ubgarden.game.go.personage.Wasp;
import fr.ubx.poo.ubgarden.game.view.Sprite;
import fr.ubx.poo.ubgarden.game.view.ImageResourceFactory;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class SpriteWasp extends Sprite {
 
    public SpriteWasp(Pane layer, Wasp wasp) {
        super(layer, null, wasp);
        updateImage();
    }
 
    public void updateImage() {
        Wasp wasp = (Wasp) getGameObject();
        Image image = getImage(wasp.getDirection());
        setImage(image);
    }
 
    public Image getImage(Direction direction) {
        return ImageResourceFactory.getInstance().getWasp(direction);
    }
 }
 