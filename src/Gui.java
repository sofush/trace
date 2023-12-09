import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class Gui extends Application {
    public static void main(String[] args) {
        Gui.launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        URL fxml = Objects.requireNonNull(getClass().getResource("main.fxml"));
        Parent root = FXMLLoader.load(fxml);
        Scene scene = new Scene(root, 800, 500);

        primaryStage.setTitle("Trace & trace");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

