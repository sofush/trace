import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.text.ParseException;

public class IndtastModtagerController {
    @FXML protected DatePicker datoKontrol;
    @FXML protected TextField tidspunktFelt;
    @FXML protected TextField navnFelt;
    @FXML protected TextField adresseFelt;
    @FXML protected TextField mobilnummerFelt;
    @FXML protected Button fortsaetKnap;
    public RegistrerController ejerController;

    public void initialize() {
        fortsaetKnap.setOnAction((a) -> {
            try {
                this.ejerController.skiftSide(RegistrerController.Side.RUTE);
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
