package main.java.fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.go.decor.ground.Grass;
import fr.ubx.poo.ubgarden.game.engine.Timer;
import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Level;
import main.java.fr.ubx.poo.ubgarden.game.go.bonus.Insecticide;
import main.java.fr.ubx.poo.ubgarden.game.go.personage.Hornet;

import java.util.ArrayList;
import java.util.List;

public class NestHornet extends Decor {
    private final Timer dropTimer = new Timer(5000);
    private final Game game;

    public NestHornet(Game game, Position position) {
        super(position);
        this.game = game;
    }

    public boolean isWalkable() {
        return false;
    }

    public void update(long now) {
        dropTimer.update(now);
        if (!dropTimer.isRunning()) {
            dropTimer.start();

            // Nouvelle Hornet
            Hornet hornet = new Hornet(game, this.getPosition());
            var level = (Level) game.world().getGrid();
            if(level.getHornets().size() >= game.configuration().maxhornets()){
                return;
            }
            level.addHornet(hornet);

            // Insecticide sur une case d'herbe libre
            Position freePos = findRandomFreePosition(level);
            if (freePos != null) {
                Decor decor = level.get(freePos);
                if (decor != null && decor.getBonus() == null) {
                    decor.setBonus(new Insecticide(freePos, decor));
                }
            }
        }
    }

    private Position findRandomFreePosition(Level level) {
        List<Position> free = new ArrayList<>();
        for (int i = 0; i < level.width(); i++) {
            for (int j = 0; j < level.height(); j++) {
                Position pos = new Position(level.getLevel(), i, j);
                Decor decor = level.get(pos);
                if (decor instanceof Grass && decor.getBonus() == null) {
                    free.add(pos);
                }
            }
        }
        if (free.isEmpty()) return null;
        return free.get(new java.util.Random().nextInt(free.size()));
    }
}