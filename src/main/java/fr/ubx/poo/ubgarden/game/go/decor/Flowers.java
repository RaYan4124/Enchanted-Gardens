package main.java.fr.ubx.poo.ubgarden.game.go.decor;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;

public class Flowers extends Decor {

    public Flowers(Position position) {
        super(position);
    }
    
    public boolean isWalkable() {
        return false;
    }
}
