package wide.modules.gui.core;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import wide.core.WIde;
import wide.modules.gui.core.view.MainPane;

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

        // Assign Scene
        stage.setScene(new Scene(new MainPane()));

        stage.setMinWidth(400);
        stage.setMinHeight(300);

        // Shows the scene
        stage.centerOnScreen();
        stage.show();
    }
}
