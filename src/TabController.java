import javafx.event.Event;
import javafx.scene.control.Tab;

public class TabController {
    public Tab registrerFane;
    public Tab oversigtFane;

    public void onSelectionChanged(Event event) {
        if (registrerFane.isSelected()) {
            System.out.println("Registrer fanen er valgt");
        } else if (oversigtFane.isSelected()) {
            System.out.println("Oversigt fanen er valgt");
        } else {
            System.out.println("Hverken registrer eller oversigt fanen er valgt");
        }
    }
}
