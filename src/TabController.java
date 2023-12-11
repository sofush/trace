import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class TabController {
    @FXML private Tab registrerFane;
    @FXML private Tab oversigtFane;

    public void initialize() throws IOException {
        URL fxml = Objects.requireNonNull(getClass().getResource("registrer-fane.fxml"));
        FXMLLoader loader = new FXMLLoader(fxml);
        Parent root = loader.load();
        registrerFane.setContent(root);
        RegistrerController registrerController = loader.getController();
    }
}
