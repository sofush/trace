import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main {
    record Valgmulighed<T>(String indeks, T indre) {}

    static final String ALFABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static Optional<Pakke> anmodPakke(Scanner scanner) throws SQLException {
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

        if (valgmuligheder.isEmpty()) {
            System.out.println("Ingen pakker. Registrer en pakke først.");
            return Optional.empty();
        }

        while (true) {
            System.out.println("Vælg et pakkenummer:");

            for (Valgmulighed<String> valgmulighed : valgmuligheder) {
                System.out.println(valgmulighed.indeks + ") " + valgmulighed.indre);
            }

            System.out.print("> ");
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

                return pakke;
            } else {
                System.out.println("Ugyldigt svar, prøv igen.");
            }
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

    public static GeneriskTransportFirma anmodTransportfirma(Scanner scanner) {
        System.out.println("Indtast navnet på transportfirmaet:");
        System.out.print("> ");
        String transportFirmaNavn = scanner.nextLine().trim();
        return new GeneriskTransportFirma(transportFirmaNavn);
    }

    public static String anmodPakkenummer(Scanner scanner) {
        System.out.println("Indtast pakkenummer:");
        System.out.print("> ");
        return scanner.nextLine().trim();
    }

    public static Pakke indtastPakke(Scanner scanner) throws SQLException {
        String pakkenummer = anmodPakkenummer(scanner);
        GeneriskTransportFirma transportFirma = anmodTransportfirma(scanner);
        Virksomhed virksomhed = anmodVirksomhed(scanner);
        OffsetDateTime afsendelse = anmodTidspunkt(scanner, Optional.of(StopType.VIRKSOMHED));
        Modtager modtager = anmodModtager(scanner);
        OffsetDateTime levering = anmodTidspunkt(scanner, Optional.of(StopType.HJEM));

        ArrayList<Stop> stopListe = new ArrayList<>();
        stopListe.add(new Stop(StopType.VIRKSOMHED, virksomhed.adresse(), afsendelse));
        stopListe.add(new Stop(StopType.HJEM, modtager.adresse(), levering));

        ydre: while (true) {
            Stop naestSidste = stopListe.get(stopListe.size() - 2);

            System.out.printf("""
                Tilføj stop mellem "%s" og "%s"?
                """,
                naestSidste.adresse(),
                modtager.adresse()
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

        return new Pakke(
            pakkenummer,
            transportFirma.navn,
            new Rute(stopListe),
            virksomhed,
            modtager
        );
    }

    public static void main(String[] args) throws SQLException {
        if (Arrays.asList(args).contains("--gui")) {
            Gui.main(args);
            System.exit(0);
        }

        ydre: while (true) {
            System.out.println("Vælg en af mulighederne:");
            System.out.println("a) Vis oversigt over pakker i registeret");
            System.out.println("b) Registrer en pakke");
            System.out.println("c) Start GUI");
            System.out.println("d) Luk");
            System.out.print("> ");

            Scanner scanner = new Scanner(System.in);
            String valg = scanner.nextLine().trim().toLowerCase();

            switch (valg) {
                case "a" -> {
                    Optional<Pakke> pakke = anmodPakke(scanner);
                    assert pakke.isPresent();
                    UseCase.visOversigt(pakke.get());
                }
                case "b" -> {
                    Pakke pakke = indtastPakke(scanner);
                    String transportfirmaNavn = pakke.transportfirma();
                    GeneriskTransportFirma transportfirma = new GeneriskTransportFirma(transportfirmaNavn);
                    UseCase.registrerPakke(transportfirma, pakke);
                }
                case "c" -> {
                    Gui.main(args);
                    System.exit(0);
                }
                case "d" -> {
                    break ydre;
                }
            }
        }
    }
}
