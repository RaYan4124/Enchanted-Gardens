package fr.ubx.poo.ubgarden.game.launcher;

import fr.ubx.poo.ubgarden.game.Position;

import static fr.ubx.poo.ubgarden.game.launcher.MapEntity.Gardener;
import static fr.ubx.poo.ubgarden.game.launcher.MapEntity.Grass;

public class MapLevel {

    private final int width;
    private final int height;
    private final MapEntity[][] grid;


    private Position gardenerPosition = null;

    public MapLevel(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new MapEntity[height][width];
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public MapEntity get(int i, int j) {
        return grid[j][i];
    }

    public void set(int i, int j, MapEntity mapEntity) {
        grid[j][i] = mapEntity;
    }

    public Position getGardenerPosition() {
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                if (grid[j][i] == Gardener) {
                    if (gardenerPosition != null)
                        throw new RuntimeException("Multiple definition of gardener");
                    set(i, j, Grass);
                    // Gardener can be only on level 1
                    gardenerPosition = new Position(1, i, j);
                }
        return gardenerPosition;
    }

    public static MapLevel fromString(String data) {
        String[] lines = data.split("x"); // Séparation des lignes
        int height = lines.length;
        int width = lines[0].length(); // supposition que toutes les lignes sont bien de la même longueur
        MapLevel mapLevel = new MapLevel(width, height);
    
        for (int j = 0; j < height; j++) {
            String line = lines[j];
            if (line.length() != width)
                throw new MapException("Inconsistent line width at line " + j);
            for (int i = 0; i < width; i++) {
                char c = line.charAt(i);
                MapEntity entity = MapEntity.fromCode(c);
                mapLevel.set(i, j, entity);
            }
        }
        return mapLevel;
    }

    public static MapLevel fromCompressedString(String data) {
        StringBuilder uncompressed = new StringBuilder();
        int length = data.length();
    
        // Parcours du texte compressé
        for (int i = 0; i < length; i++) {
            char c = data.charAt(i);
            if (Character.isDigit(c)) {
                // Si on rencontre un chiffre, on répète le caractère précédent
                int repeatCount = Character.getNumericValue(c);
                if (i > 0) {
                    char prevChar = data.charAt(i - 1);
                    for (int j = 1; j < repeatCount; j++) {
                        uncompressed.append(prevChar);  // Répète le précédent caractère
                    }
                }
            } else {
                // Si ce n'est pas un chiffre, on l'ajoute à la chaîne
                uncompressed.append(c);
            }
        }
    
        // Maintenant qu'on a l'input décompressé, faire la conversion en MapLevel
        return MapLevel.fromString(uncompressed.toString());
    }
}
