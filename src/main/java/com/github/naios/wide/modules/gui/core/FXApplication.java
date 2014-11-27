package com.github.naios.wide.modules.gui.core;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import com.github.naios.wide.core.Constants;
import com.github.naios.wide.core.WIde;
import com.github.naios.wide.modules.gui.core.view.MainPane;

public class FXApplication extends Application
{
    public static void run(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        stage.setTitle(WIde.getEnviroment().getVersionString());
        stage.getIcons().add(
                new Image(getClass().getClassLoader().getResourceAsStream(
                        Constants.PATH_APPLICATION_ICON.toString())));

        // Assign Scene
        stage.setScene(new Scene(new MainPane()));

        stage.setMinWidth(400);
        stage.setMinHeight(300);

        // Shows the scene
        stage.centerOnScreen();
        stage.show();
    }
}
