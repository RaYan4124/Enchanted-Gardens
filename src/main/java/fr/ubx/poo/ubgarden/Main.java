package fr.ubx.poo.ubgarden;

import fr.ubx.poo.ubgarden.game.launcher.MapLevelDefaultStart;
import fr.ubx.poo.ubgarden.game.launcher.MapLevelDefaultStart.*;
import fr.ubx.poo.ubgarden.game.view.GameLauncherView;
import fr.ubx.poo.ubgarden.game.Configuration;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage)  {
        Configuration config = Configuration.fromProperties("world\\world_3levels_clear.properties");
        GameLauncherView launcher = new GameLauncherView(stage);
        Scene scene = new Scene(launcher);
        stage.setTitle("UBGarden 2025");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
        stage.toFront();
        stage.requestFocus();
        
        //System.out.println("test Gardener Energy: " + config.gardenerEnergy());
    }

    public static void main(String[] args) { 
        launch(); 
    }
}