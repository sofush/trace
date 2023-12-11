import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.Optional;

public class IndtastStopController {

    @FXML protected StackPane skabNaesteStopKnap;
    @FXML protected TextField tidspunktFelt;
    @FXML protected DatePicker datoKontrol;
    @FXML protected ComboBox typeComboBox;
    @FXML protected TextField adresseFelt;
    public IndtastRuteController ejer;

    public void initialize() {
        typeComboBox.getItems().addAll(StopType.values());
    }

    public void opdater(boolean redigerbar, boolean kanTilfoejeNaesteStop) {
        if (!redigerbar) {
//            Background b = new Background(new BackgroundFill(Color.LIGHTGRAY,
//                    CornerRadii.EMPTY, Insets.EMPTY));
            this.tidspunktFelt.setDisable(true);
            this.datoKontrol.setDisable(true);
            this.typeComboBox.setDisable(true);
            this.adresseFelt.setDisable(true);
//            this.tidspunktFelt.setBackground(b);
//            this.datoKontrol.setBackground(b);
//            this.typeComboBox.setBackground(b);
//            this.adresseFelt.setBackground(b);
        }

        if (!kanTilfoejeNaesteStop) {
            skabNaesteStopKnap.setVisible(false);
        }
    }

    public void tilfoejStopNedenunder(MouseEvent mouseEvent) throws IOException {
        int i;
        for (i = 0; i < this.ejer.stopListe.size(); ++i) {
            if (this.ejer.stopListe.get(i) == this) break;
        }

        this.ejer.tilfoejStop(i + 1, Optional.empty(), true, true);
    }
}
