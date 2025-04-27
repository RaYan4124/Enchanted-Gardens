package main.java.fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
public class DoorNextClosed extends Decor {
    public DoorNextClosed(Position position) {
        super(position);
    }

    public boolean isOpen() {
        return false;
    }

}
