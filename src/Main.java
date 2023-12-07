import java.sql.SQLException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws SQLException {
        // Skab en rute.
        Stop stop1 = new Stop(StopType.VIRKSOMHED, "123 False St.", ZonedDateTime.now());
        Stop stop2 = new Stop(StopType.POSTHUS, "432 Parcel Drive", ZonedDateTime.now().plus(Duration.ofHours(5)));
        Rute rute = new Rute(List.of(stop1, stop2));

        // Skab en virksomhed og modtager.
        Virksomhed virksomhed = new Virksomhed("Varer ApS", "987 Money St.");
        Modtager modtager = new Modtager("Jens Andersen", "+45 12 34 56 78", "Delivery Drive");

        // Skab en pakke og et transportfirma.
        String pakkenummer = UUID.randomUUID().toString();
        Pakke pakke = new Pakke(pakkenummer, "Falsk Transportfirma ApS", rute, virksomhed, modtager);
        GeneriskTransportFirma transportFirma = new GeneriskTransportFirma("Deliver the Goods ApS");

        // Registrer pakken.
        if (!transportFirma.registrerPakke(pakke)) {
            System.out.println("Kunne ikke registrere pakken.");
        }

        var r = transportFirma.rute(pakkenummer);
    }
}
