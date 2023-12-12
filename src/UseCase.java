import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Optional;

public class UseCase {
    public static void registrerPakke(GeneriskTransportFirma transportFirma, Pakke pakke) throws SQLException {
        if (transportFirma.registrerPakke(pakke)) {
            System.out.println("Succes.");
        } else {
            System.out.println("Fejl.");
        }
    }

    public static void visOversigt(Pakke pakke) {
        System.out.printf("""
            Pakke
                Pakkenummer: %s
            Virksomhed (afsender)
                Navn: %s
                Adresse: %s
            Modtager
                Navn: %s
                Mobilnummer: %s
                Adresse: %s
            """,
            pakke.pakkenummer(),
            pakke.virksomhed().navn(),
            pakke.virksomhed().adresse(),
            pakke.modtager().navn(),
            pakke.modtager().mobilnummer(),
            pakke.modtager().adresse()
        );

        OffsetDateTime nu = OffsetDateTime.now();
        Optional<Stop> senesteStop = Optional.empty();
        for (Stop stop : pakke.rute().stop()) {
            if (stop.tidspunkt().isAfter(nu)) {
                break;
            }

            senesteStop = Optional.of(stop);
        }

        int stopIndeks = 0;
        while (stopIndeks < pakke.rute().stop().size()) {
            Stop stop = pakke.rute().stop().get(stopIndeks);

            if (senesteStop.isPresent() && stop == senesteStop.get()) {
                System.out.println("Stop (PAKKE ER HER)");
            } else {
                System.out.println("Stop");
            }

            System.out.printf("""
                    Indeks: %d
                    Adresse: %s
                    Type: %s
                    Tidspunkt: %s
                """,
                    stopIndeks + 1,
                    stop.adresse(),
                    stop.type(),
                    stop.tidspunkt()
            );
            ++stopIndeks;
        }
    }
}
