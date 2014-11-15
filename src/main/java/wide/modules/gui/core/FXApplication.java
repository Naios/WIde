package wide.modules.gui.core;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class FXApplication extends Application
{
    public static void run(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        // Assign Scene
        stage.setScene(new Scene(new Pane()));

        stage.setMinWidth(400);
        stage.setMinHeight(300);

        // Shows the scene
        stage.centerOnScreen();
        stage.show();
    }
}
