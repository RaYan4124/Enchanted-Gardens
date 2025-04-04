package main.java.fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;


public class DoorNextOpened extends Decor {
    public DoorNextOpened(Position position) {
        super(position);
    }

    public boolean isOpen() {
        return true;
    }
}
