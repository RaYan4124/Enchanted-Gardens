package main.java.fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.engine.Timer;
import main.java.fr.ubx.poo.ubgarden.game.go.bonus.Insecticide;
import main.java.fr.ubx.poo.ubgarden.game.go.personage.Wasp;
import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.World;
import fr.ubx.poo.ubgarden.game.Level;


public class NestWasp extends Decor {
    private final Timer dropTimer = new Timer(5000);

    public NestWasp(Position position) {
        super(position);
    }

    public boolean isWalkable() {
        return false;
    }

    public void update(long now) {
        if (!dropTimer.isRunning()) {
            dropTimer.start();

            //nouvelle Wasp
            Wasp wasp = new Wasp(game, this.getPosition());
            var level = (Level) game.world().getGrid();
            level.addWasp(wasp);

            //insecticide
            Insecticide insecticide = new Insecticide(this.getPosition(), this);
            Decor decor = level.get(this.getPosition());
            if (decor != null) {
                decor.setBonus(insecticide);
            }
        }
    }

}
