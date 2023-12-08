import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Optional;

public class GeneriskTransportFirma implements TransportFirma {
    String navn;

    GeneriskTransportFirma(String navn) {
        this.navn = navn;
    }

    boolean registrerPakke(Pakke pakke) throws SQLException {
        Database db = Database.singleton();
        Savepoint savepoint = db.conn.setSavepoint();

        try {
            ArrayList<Optional<Integer>> stopIdListe = new ArrayList<>();

            // Indsæt alle pakkens stop i databasen.
            for (Stop stop : pakke.rute().stop()) {
                stopIdListe.add(db.indsaetStop(stop));
            }

            if (stopIdListe.stream().findAny().isEmpty()) {
                db.conn.rollback(savepoint);
                return false;
            }

            // Indsæt en række i Rute tabellen for hvert stop ID.
            boolean resultat = db.indsaetRute(pakke.pakkenummer(), stopIdListe.stream().map((id) -> {
                // Vi ved at `id.isPresent()` er true fordi vi tjekker for
                // det i et if-statement ovenover.
                assert id.isPresent();
                return id.get();
            }).toList());

            if (!resultat) {
               db.conn.rollback(savepoint);
               return false;
            }

            // Indsæt modtager, virksomhed og transportfirma i databasen.
            Optional<Integer> modtagerId = db.indsaetModtager(pakke.modtager());
            Optional<Integer> virksomhedId = db.indsaetVirksomhed(pakke.virksomhed());

            if (modtagerId.isEmpty() || virksomhedId.isEmpty()) {
                db.conn.rollback(savepoint);
                return false;
            }

            db.indsaetPakke(pakke, modtagerId.get(), virksomhedId.get());
            db.indsaetTransportFirma(pakke.transportfirma(), pakke.pakkenummer());
        } catch (SQLException e) {
            db.conn.rollback(savepoint);
            return false;
        }

        // Commit alle ændringerne.
        db.conn.commit();
        return true;
    }

    @Override
    public Rute rute(String pakkenummer) throws SQLException {
        Database database = Database.singleton();
        return database.laesRute(pakkenummer);
    }

    @Override
    public String navn() {
        return this.navn;
    }
}
