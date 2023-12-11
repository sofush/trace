import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.text.ParseException;

public class IndtastPakkenummerController {
    @FXML protected TextField pakkenummerFelt;
    @FXML protected TextField transportfirmaNavnFelt;
    @FXML protected Button fortsaetKnap;
    public RegistrerController ejerController;

    public void initialize() {
        this.fortsaetKnap.setOnAction((a) -> {
            try {
                this.ejerController.skiftSide(RegistrerController.Side.VIRKSOMHED);
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
