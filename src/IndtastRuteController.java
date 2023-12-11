import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class IndtastRuteController {
    @FXML protected ScrollPane scrollPane;
    @FXML protected Button faerdigKnap;
    @FXML protected VBox stopContainer;
    public List<IndtastStopController> stopListe = new ArrayList<>();
    public RegistrerController ejerController;

    public void initialize() {
        this.faerdigKnap.setOnAction((a) -> {
            try {
                this.ejerController.registrer();
                this.ejerController.skiftSide(RegistrerController.Side.PAKKENUMMER);
            } catch (ParseException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void tilfoejStop(int indeks, Optional<Stop> stop, boolean redigerbar, boolean kanTilfoejeNaesteStop) throws IOException {
        URL fxml = Objects.requireNonNull(getClass().getResource("indtast-stop.fxml"));
        FXMLLoader loader = new FXMLLoader(fxml);
        Parent root = loader.load();

        IndtastStopController c = loader.getController();
        c.opdater(redigerbar, kanTilfoejeNaesteStop);
        c.ejer = this;

        if (stop.isPresent()) {
            c.adresseFelt.setText(stop.get().adresse());
            c.typeComboBox.setValue(stop.get().type().toString());
            c.tidspunktFelt.setText(stop.get().tidspunkt().toLocalTime().toString());
            c.datoKontrol.setValue(stop.get().tidspunkt().toLocalDate());
        }

        stopListe.add(indeks, c);
        stopContainer.getChildren().add(indeks, root);
    }
}
