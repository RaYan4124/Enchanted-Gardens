/*
 * Copyright (c) 2020. Laurent Réveillère
 */
package fr.ubx.poo.ubgarden.game.engine;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.bonus.Bonus;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.Level;
import fr.ubx.poo.ubgarden.game.Direction;
import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;
import fr.ubx.poo.ubgarden.game.view.ImageResource;
import fr.ubx.poo.ubgarden.game.view.Sprite;
import fr.ubx.poo.ubgarden.game.view.SpriteFactory;
import fr.ubx.poo.ubgarden.game.view.SpriteGardener;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import main.java.fr.ubx.poo.ubgarden.game.go.bonus.Insecticide;
import main.java.fr.ubx.poo.ubgarden.game.go.decor.Hedgehog;
import main.java.fr.ubx.poo.ubgarden.game.go.personage.Hornet;
import main.java.fr.ubx.poo.ubgarden.game.go.personage.Wasp;
import main.java.fr.ubx.poo.ubgarden.game.view.SpriteHornet;
import main.java.fr.ubx.poo.ubgarden.game.view.SpriteWasp;

import java.util.*;


    public final class GameEngine {

        private static AnimationTimer gameLoop;
        private final Game game;
        private final Gardener gardener;
        private final List<Sprite> sprites = new LinkedList<>();
        private final Set<Sprite> cleanUpSprites = new HashSet<>();
        private final Set<Wasp> waspsWithSprite = new HashSet<>();
        private final Set<Hornet> hornetsWithSprite = new HashSet<>();


        private final Scene scene;

        private StatusBar statusBar;

        private final Pane rootPane = new Pane();
        private final Group root = new Group();
        private final Pane layer = new Pane();
        private Input input;

        public GameEngine(Game game, Scene scene) {
            this.game = game;
            this.scene = scene;
            this.gardener = game.getGardener();
            initialize();
            buildAndSetGameLoop();
        }

        public Pane getRoot() {
            return rootPane;
        }

        private void initialize() {
            int height = game.world().getGrid().height();
            int width = game.world().getGrid().width();
            int sceneWidth = width * ImageResource.size;
            int sceneHeight = height * ImageResource.size;
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/application.css")).toExternalForm());
            input = new Input(scene);

            root.getChildren().clear();
            root.getChildren().add(layer);
            statusBar = new StatusBar(root, sceneWidth, sceneHeight);

            rootPane.getChildren().clear();
            rootPane.setPrefSize(sceneWidth, sceneHeight + StatusBar.height);
            rootPane.getChildren().add(root);

            // Create sprites
            int currentLevel = game.world().currentLevel();

            for (var decor : game.world().getGrid().values()) {
                sprites.add(SpriteFactory.create(layer, decor));
                decor.setModified(true);
                var bonus = decor.getBonus();
                if (bonus != null) {
                    sprites.add(SpriteFactory.create(layer, bonus));
                    bonus.setModified(true);
                }
            }

            for (Wasp wasp : ((Level)game.world().getGrid()).getWasps()) {
                sprites.add(new SpriteWasp(layer, wasp));
            }


            sprites.add(new SpriteGardener(layer, gardener));
            resizeScene(sceneWidth, sceneHeight);
        }

        void buildAndSetGameLoop() {
            gameLoop = new AnimationTimer() {
                public void handle(long now) {
                    checkLevel();

                    // Check keyboard actions
                    processInput();

                    // Do actions
                    update(now);
                    //checkCollision(now);

                    // Graphic update
                    cleanupSprites();
                    render();
                    statusBar.update(game);
                }
            };
        }


        private void checkLevel() {
            if (game.isSwitchLevelRequested()) {
                // clear all sprites
                sprites.clear();
                waspsWithSprite.clear();
                // change the current level
                game.world().setCurrentLevel(game.getSwitchLevel());
                // Set the position of the gardener
                initialize();
                game.clearSwitchLevel();
            }
        }

        private void checkCollision(long now) {
            Level level = (Level) game.world().getGrid();

            for (Wasp wasp : level.getWasps()) {
                if (wasp.isDeleted()) continue;
                if (wasp.getPosition().equals(gardener.getPosition())) {
                    if (gardener.getInsecticide() > 0) {
                        gardener.useInsecticide();
                    } else {
                        gardener.hurt(20, now);
                    }
                    wasp.remove();
                }

                //wasp marche sur un insecticide
                Decor decor = level.get(wasp.getPosition());
                if (decor != null && decor.getBonus() instanceof Insecticide) {
                    wasp.remove();
                    decor.getBonus().remove();
                }
            }


            //collisions avec les hornets
            for (Hornet hornet : level.getHornets()) {
                if (hornet.isDeleted()) continue;
                if (hornet.getPosition().equals(gardener.getPosition())) {
                    if (gardener.getInsecticide() >= 2) {
                        hornet.remove();
                        gardener.useInsecticide();
                        gardener.useInsecticide();
                    } else if (gardener.getInsecticide() == 1 && hornet.getInsecticideHits() == 1) {
                        gardener.useInsecticide();
                        hornet.hitInsecticide();   
                        if (hornet.isKilledByInsecticide()) {
                            hornet.remove();
                        }
                    } else if (gardener.getInsecticide() == 1) {
                        gardener.useInsecticide();
                        hornet.hitInsecticide();   
                        gardener.hurt(30, now);
                        if (hornet.isKilledByInsecticide()) {
                            hornet.remove();
                        }
                    } else if(!gardener.isJustHurt()) {
                        gardener.hurt(30, now); 
                        gardener.setJustHurt(true);
                        hornet.hitGardener();
                    }else if(hornet.isKilledByInsecticide() || hornet.isKilledByGardener()){
                        hornet.remove();
                    }
                }
                //hornet marche sur insecticide
                Decor decor = level.get(hornet.getPosition());
                if (decor != null && decor.getBonus() instanceof Insecticide) {
                    hornet.hitInsecticide();   
                    decor.getBonus().remove();
                    if (hornet.isKilledByInsecticide()) {
                        hornet.remove();
                    }
                }
            }
        }

        private void processInput() {
            if (input.isExit()) {
                gameLoop.stop();
                Platform.exit();
                System.exit(0);
            } else if (input.isMoveDown()) {
                gardener.requestMove(Direction.DOWN);
            } else if (input.isMoveLeft()) {
                gardener.requestMove(Direction.LEFT);
            } else if (input.isMoveRight()) {
                gardener.requestMove(Direction.RIGHT);
            } else if (input.isMoveUp()) {
                gardener.requestMove(Direction.UP);
            }
            input.clear();
        }

        private void showMessage(String msg, Color color) {
            Text message = new Text(msg);
            message.setTextAlignment(TextAlignment.CENTER);
            message.setFont(new Font(60));
            message.setFill(color);

            StackPane pane = new StackPane(message);
            pane.setPrefSize(rootPane.getWidth(), rootPane.getHeight());
            rootPane.getChildren().clear();
            rootPane.getChildren().add(pane);

            new AnimationTimer() {
                public void handle(long now) {
                    processInput();
                }
            }.start();
        }

        private void update(long now) {
            game.world().getGrid().values().forEach(decor -> decor.update(now));
            gardener.update(now);

            Level level = (Level)game.world().getGrid();
            for (Wasp wasp : level.getWasps()) {
                wasp.update(now);
                if (wasp.isDeleted())continue; // si la wasp est supprimee, on l'affiche pas
                // ajoute un sprite pour chaque nouvelle wasp qui n'en a pas encore
                if (!waspsWithSprite.contains(wasp)) {
                    sprites.add(new SpriteWasp(layer, wasp));
                    waspsWithSprite.add(wasp);
                }
            }

            for (Hornet hornet : level.getHornets()) {
                hornet.update(now);
                if (hornet.isDeleted()) continue;
                if (!hornetsWithSprite.contains(hornet)) {
                    sprites.add(new SpriteHornet(layer, hornet));
                    hornetsWithSprite.add(hornet);
                }
            }

            //sprite pour insecticide generé
            for (Decor decor : level.values()) {
                Bonus bonus = decor.getBonus();
                if (bonus instanceof Insecticide) {
                    boolean alreadyDisplayed = sprites.stream()
                        .anyMatch(sprite -> sprite.getGameObject() == bonus);
                    if (!alreadyDisplayed) {
                        sprites.add(SpriteFactory.create(layer, bonus));
                    }
                }
            }

            checkCollision(now);

            if (gardener.getEnergy() <= 0) {
                this.game.setGameOver(true);
                gameLoop.stop();
                showMessage("Game Over !!", Color.RED);
            } else {
                game.world().getGrid().values().stream()
                    .filter(decor -> decor instanceof Hedgehog)
                    .map(decor -> (Hedgehog) decor)
                    .findFirst()
                    .ifPresent(hedgehog -> {
                        if (gardener.getPosition().equals(hedgehog.getPosition())) {
                            gameLoop.stop();
                            showMessage("You Won !!", Color.GREEN);
                        }
                    });
            }
        }

        public void cleanupSprites() {
            sprites.forEach(sprite -> {
                if (sprite.getGameObject().isDeleted()) {
                    cleanUpSprites.add(sprite);
                }
            });
            cleanUpSprites.forEach(Sprite::remove);
            sprites.removeAll(cleanUpSprites);
            cleanUpSprites.clear();
        }

        private void render() {
            sprites.forEach(Sprite::render);
            for (Sprite sprite : sprites) {
                if (sprite instanceof SpriteWasp) {
                    ((SpriteWasp) sprite).updateImage();
                }
                sprite.render();
            }
        }

        public void start() {
            gameLoop.start();
        }

        private void resizeScene(int width, int height) {
            rootPane.setPrefSize(width, height + StatusBar.height);
            layer.setPrefSize(width, height);
            Platform.runLater(() -> scene.getWindow().sizeToScene());
        }

        public void replaceDecorSprite(Decor decor) {
            // supprime l'ancien sprite a cette position
            sprites.removeIf(sprite ->
                sprite.getGameObject() instanceof Decor &&
                ((Decor) sprite.getGameObject()).getPosition().equals(decor.getPosition())
            );
            // ajoute le nouveau
            sprites.add(SpriteFactory.create(layer, decor));
        }
    }