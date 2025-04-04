package main.java.fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;

public class NestWasp extends Decor {
    public NestWasp(Position position) {
        super(position);
    }

    public boolean isWalkable() {
        return false;
    }

}
