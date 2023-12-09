import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Tab;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class TabController {
    public Tab registrerFane;
    public Tab oversigtFane;
    public SubScene registrerFaneSubScene;

    @FXML
    public void initialize() throws IOException {
        assert registrerFaneSubScene != null;
        URL fxml = Objects.requireNonNull(getClass().getResource("input-1.fxml"));
        FXMLLoader loader = new FXMLLoader(fxml);
        loader.getController();
        Parent root = loader.load();
        registrerFaneSubScene.setRoot(root);
    }
}
