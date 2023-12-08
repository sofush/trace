import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main {
    record Valgmulighed<T>(String indeks, T indre) {}

    static final String ALFABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static Pakke anmodPakke(Scanner scanner) throws SQLException {
        Database db = Database.singleton();
        List<String> pakkenumre = db.laesPakkenumre();

        var alfabetIter = ALFABET.chars().iterator();
        var valgmuligheder = pakkenumre
                .stream()
                .map((type) -> {
                    String indeks = String.valueOf((char)alfabetIter.nextInt());
                    return new Valgmulighed<>(indeks, type);
                })
                .toList();

        while (true) {
            System.out.println("Vælg et pakkenummer:");

            for (Valgmulighed<String> valgmulighed : valgmuligheder) {
                System.out.println(valgmulighed.indeks + ") " + valgmulighed.indre);
            }

            String input = scanner.nextLine().trim();
            Optional<Valgmulighed<String>> valg = valgmuligheder
                .stream()
                .filter((valgmulighed) -> {
                    boolean erIndeks = input.toLowerCase().equals(valgmulighed.indeks);
                    boolean erPakkenummer = input.equals(valgmulighed.indre);
                    return erIndeks || erPakkenummer;
                }).findFirst();

            if (valg.isPresent()) {
                String pakkenummer = valg.get().indre;
                Optional<Pakke> pakke = db.laesPakke(pakkenummer);

                if (pakke.isEmpty()) {
                    System.out.println("Kunne ikke finde pakke med pakkenummer: " + pakkenummer);
                    continue;
                }

                return pakke.get();
            } else {
                System.out.println("Ugyldigt svar, prøv igen.");
            }
        }
    }

    public static void visOversigt(Scanner scanner) throws SQLException {
        Pakke pakke = anmodPakke(scanner);
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

    public static Virksomhed anmodVirksomhed(Scanner scanner) {
        System.out.println("Indtast virksomhedens navn (afsender):");
        System.out.print("> ");
        String navn = scanner.nextLine().trim();

        System.out.println("Indtast virksomhedens adresse (afsender):");
        System.out.print("> ");
        String adresse = scanner.nextLine().trim();

        return new Virksomhed(navn, adresse);
    }

    public static Modtager anmodModtager(Scanner scanner) {
        System.out.println("Indtast modtagerens navn:");
        System.out.print("> ");
        String navn = scanner.nextLine().trim();

        System.out.println("Indtast modtagerens adresse (destinationsadresse):");
        System.out.print("> ");
        String adresse = scanner.nextLine().trim();

        System.out.println("Indtast modtagerens mobilnummer:");
        System.out.print("> ");
        String mobilnummer = scanner.nextLine().trim();

        return new Modtager(navn, mobilnummer, adresse);
    }

    public static OffsetDateTime anmodTidspunkt(Scanner scanner, Optional<StopType> type) {
        String anmodBesked = "Indtast et tidspunkt med tidzone (ISO-8601):";

        if (type.isPresent()) {
            switch (type.get()) {
                case VIRKSOMHED -> anmodBesked = "Indtast et tidspunkt med tidzone for afsendelse (ISO-8601):";
                case HJEM -> anmodBesked = "Indtast et tidspunkt med tidzone for levering (ISO-8601):";
            }
        }

        while (true) {
            System.out.println(anmodBesked);
            System.out.print("> ");
            String tidspunkt = scanner.nextLine().trim();

            try {
                return OffsetDateTime.parse(tidspunkt);
            } catch (DateTimeParseException e) {
                System.out.println("Ugyldigt svar, prøv igen.");
            }
        }
    }

    public static Stop anmodStop(Scanner scanner) {
        System.out.println("Indtast en adresse for stoppunkt:");
        System.out.print("> ");
        String adresse = scanner.nextLine().trim();

        var alfabetIter = ALFABET.chars().iterator();
        var valgmuligheder = Arrays.stream(StopType.values())
                .map((type) -> {
                    String indeks = String.valueOf((char)alfabetIter.nextInt());
                    return new Valgmulighed<>(indeks, type);
                })
                .toList();

        OffsetDateTime tidspunkt = anmodTidspunkt(scanner, Optional.empty());

        while (true) {
            System.out.println("Vælg en stoptype:");
            for (Valgmulighed<StopType> valgmulighed : valgmuligheder) {
                System.out.println(valgmulighed.indeks + ") " + valgmulighed.indre);
            }
            System.out.print("> ");
            String valg = scanner.nextLine().trim();
            var resultat = valgmuligheder.stream()
                    .filter((valgmulighed) -> {
                        boolean erIndeks = valgmulighed.indeks.equalsIgnoreCase(valg);
                        boolean erType = valgmulighed.indre.toString().equalsIgnoreCase(valg);
                        return erIndeks || erType;
                    })
                    .findFirst();

            if (resultat.isPresent()) {
                return new Stop(resultat.get().indre, adresse, tidspunkt);
            } else {
                System.out.println("Ugyldigt svar, prøv igen.");
            }
        }
    }

    public static void registrerPakke(Scanner scanner) throws SQLException {
        System.out.println("Indtast pakkenummer:");
        System.out.print("> ");
        String pakkenummer = scanner.nextLine().trim();

        System.out.println("Indtast navnet på transportfirmaet:");
        System.out.print("> ");
        String transportFirmaNavn = scanner.nextLine().trim();
        GeneriskTransportFirma transportFirma = new GeneriskTransportFirma(transportFirmaNavn);

        Virksomhed virksomhed = anmodVirksomhed(scanner);
        OffsetDateTime afsendelse = anmodTidspunkt(scanner, Optional.of(StopType.VIRKSOMHED));
        Modtager modtager = anmodModtager(scanner);
        OffsetDateTime levering = anmodTidspunkt(scanner, Optional.of(StopType.HJEM));

        ArrayList<Stop> stopListe = new ArrayList<>();
        stopListe.add(new Stop(StopType.VIRKSOMHED, virksomhed.adresse(), afsendelse));
        stopListe.add(new Stop(StopType.HJEM, modtager.adresse(), levering));

        ydre: while (true) {
            Stop naestSidste = stopListe.get(stopListe.size() - 2);

            System.out.println("Tilføj stop mellem \"" +
                    naestSidste.adresse() +
                    "\" og \"" +
                    modtager.adresse() + "\"?"
            );
            System.out.println("a) Ja");
            System.out.println("b) Nej");
            System.out.print("> ");

            switch (scanner.nextLine().trim().toLowerCase()) {
                case "a":
                case "ja":
                    stopListe.add(stopListe.size() - 1, anmodStop(scanner));
                    continue;
                case "b":
                case "nej":
                    break ydre;
                default:
                    System.out.println("Ugyldigt svar, prøv igen.");
            }
        }

        Pakke pakke = new Pakke(
            pakkenummer,
            transportFirma.navn,
            new Rute(stopListe),
            virksomhed,
            modtager
        );

        if (transportFirma.registrerPakke(pakke)) {
            System.out.println("Succes.");
        } else {
            System.out.println("Fejl.");
        }
    }

    public static void main(String[] args) throws SQLException {
        while (true) {
            System.out.println("Vælg en af mulighederne:");
            System.out.println("a) Vis oversigt over pakker i registeret");
            System.out.println("b) Registrer en pakke");
            System.out.println("c) Luk");
            System.out.print("> ");

            Scanner scanner = new Scanner(System.in);
            String valg = scanner.nextLine().trim().toLowerCase();

            switch (valg) {
                case "a" -> visOversigt(scanner);
                case "b" -> registrerPakke(scanner);
                case "c" -> System.exit(0);
            }
        }
    }
}
