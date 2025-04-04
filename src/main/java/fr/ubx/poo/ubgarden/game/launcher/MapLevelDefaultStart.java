package fr.ubx.poo.ubgarden.game.launcher;


import static fr.ubx.poo.ubgarden.game.launcher.MapEntity.*;

import main.java.fr.ubx.poo.ubgarden.game.go.bonus.Carrots;
import main.java.fr.ubx.poo.ubgarden.game.go.bonus.Insecticide;
import main.java.fr.ubx.poo.ubgarden.game.go.bonus.PoisonedApple;
import main.java.fr.ubx.poo.ubgarden.game.go.decor.DoorNextClosed;
import main.java.fr.ubx.poo.ubgarden.game.go.decor.Hedgehog;


public class MapLevelDefaultStart extends MapLevel {


    private final static int width = 18;
    private final static int height = 8;
        private final MapEntity[][] level1 = {
                {Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass},
                {Grass, Gardener, Grass, Grass, Land, Land, Carrots, Carrots, Grass, Tree, Grass, Grass, Tree, Tree, Grass, Grass, Grass, Grass},
                {Grass, Grass, Grass, Grass, Land, Land, Grass, Flowers, Grass, Grass, Grass, Grass, Grass, Tree, Grass, DoorNextOpened, Grass, DoorNextClosed},
                {PoisonedApple, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Hedgehog, Grass, Grass, Tree, Grass, Grass, Grass, Grass},
                {Grass, Tree, Grass, Tree, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Tree, Grass, Grass, Apple, Grass},
                {Grass, Tree, Tree, Tree, Grass, Insecticide, Grass, NestWasp, Grass, Grass, NestHornet, Grass, Grass, Grass, Grass, Grass, Grass, Grass},
                {Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass},
                {Grass, Tree, Grass, Tree, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Carrots, Grass, Grass, Grass, Grass, Grass}
        };

    public MapLevelDefaultStart() {
        super(width, height);
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                set(i, j, level1[j][i]);
    }


}
