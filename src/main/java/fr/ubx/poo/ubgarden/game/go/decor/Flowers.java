package main.java.fr.ubx.poo.ubgarden.game.go.decor;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;

public class Flowers extends Decor {

    public Flowers(Position position) {
        super(position);
    }
    
    @Override
    public boolean walkableBy(Gardener gardener) {
        return false;
    }
}
