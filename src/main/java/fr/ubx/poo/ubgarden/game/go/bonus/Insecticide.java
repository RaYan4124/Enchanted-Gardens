package main.java.fr.ubx.poo.ubgarden.game.go.bonus;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.bonus.Bonus;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;

public class Insecticide extends Bonus {
    public Insecticide(Position position, Decor decor) {
        super(position, decor);
    }

    
    public void collect() {
        // Logic to collect carrots
        // For example, increase the player's score or inventory
    }
    
}
