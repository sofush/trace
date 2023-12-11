import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RegistrerController {
    protected IndtastPakkenummerController indtastPakkenummerController;
    protected IndtastModtagerController indtastModtagerController;
    protected IndtastVirksomhedController indtastVirksomhedController;
    protected IndtastRuteController indtastRuteController;

    enum Side {
        PAKKENUMMER,
        MODTAGER,
        VIRKSOMHED,
        RUTE,
    }
    @FXML private VBox registrerFaneIndhold;

    @FXML
    public void initialize() throws IOException, ParseException {
        skiftSide(Side.PAKKENUMMER);
    }

    void registrer() throws ParseException, IOException {
        String transportFirmaNavn;
        String pakkenummer;
        Modtager modtager = ekstraherModtager();
        Virksomhed virksomhed = ekstraherVirksomhed();
        List<Stop> stopListe = new ArrayList<>(List.of(ekstraherVirksomhedStop(), ekstraherModtagerStop()));

        {
            IndtastPakkenummerController pc = indtastPakkenummerController;
            pakkenummer = pc.pakkenummerFelt.getText();
            transportFirmaNavn = pc.transportfirmaNavnFelt.getText();
        }
        {
            for (int i = 1; i < this.indtastRuteController.stopListe.size() - 1; i++) {
                if (i == 1) continue;

                IndtastStopController c = this.indtastRuteController.stopListe.get(i);
                OffsetDateTime tidspunkt = sammenstoebDatoOgTid(c.datoKontrol.getValue(), c.tidspunktFelt.getText());
                StopType type = StopType.valueOf((String)c.typeComboBox.getValue());
                stopListe.add(i++, new Stop(type, c.adresseFelt.getText(), tidspunkt));
            }
        }

        Rute rute = new Rute(stopListe);
        Pakke pakke = new Pakke(pakkenummer, transportFirmaNavn, rute, virksomhed, modtager);
        GeneriskTransportFirma transportFirma = new GeneriskTransportFirma(transportFirmaNavn);

        try {
            transportFirma.registrerPakke(pakke);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        skiftSide(Side.PAKKENUMMER);
    }

    Modtager ekstraherModtager() {
        IndtastModtagerController mc = indtastModtagerController;
        String navn = mc.navnFelt.getText();
        String mobilnummer = mc.mobilnummerFelt.getText();
        String adresse = mc.adresseFelt.getText();
        return new Modtager(navn, mobilnummer, adresse);
    }

    Stop ekstraherModtagerStop() {
        IndtastModtagerController mc = indtastModtagerController;
        var tidspunkt = sammenstoebDatoOgTid(mc.datoKontrol.getValue(), mc.tidspunktFelt.getText());
        return new Stop(StopType.HJEM, mc.adresseFelt.getText(), tidspunkt);
    }

    Virksomhed ekstraherVirksomhed() {
        IndtastVirksomhedController vc = indtastVirksomhedController;
        String navn = vc.navnFelt.getText();
        String adresse = vc.adresseFelt.getText();
        return new Virksomhed(navn, adresse);
    }

    Stop ekstraherVirksomhedStop() {
        IndtastVirksomhedController vc = indtastVirksomhedController;
        var tidspunkt = sammenstoebDatoOgTid(vc.datoKontrol.getValue(), vc.tidspunktFelt.getText());
        return new Stop(StopType.VIRKSOMHED, vc.adresseFelt.getText(), tidspunkt);
    }

    void skiftSide(Side side) throws IOException, ParseException {
        String resource;
        switch (side) {
            case PAKKENUMMER -> resource = "indtast-pakkenummer.fxml";
            case MODTAGER -> resource = "indtast-modtager.fxml";
            case VIRKSOMHED -> resource = "indtast-virksomhed.fxml";
            case RUTE -> resource = "indtast-rute-2.fxml";
            default -> {
                return;
            }
        }

        URL fxml = Objects.requireNonNull(getClass().getResource(resource));
        FXMLLoader loader = new FXMLLoader(fxml);
        Parent root;
        try {
            root = loader.load();
            registrerFaneIndhold.getChildren().setAll(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        switch (side) {
            case PAKKENUMMER -> {
                IndtastPakkenummerController c = loader.getController();
                c.ejerController = this;
                this.indtastPakkenummerController = c;
            }
            case MODTAGER -> {
                IndtastModtagerController c = loader.getController();
                c.ejerController = this;
                this.indtastModtagerController = c;
            }
            case VIRKSOMHED -> {
                IndtastVirksomhedController c = loader.getController();
                c.ejerController = this;
                this.indtastVirksomhedController = c;
            }
            case RUTE -> {
                IndtastRuteController c = loader.getController();
                c.ejerController = this;
                this.indtastRuteController = c;
                Stop stop1 = ekstraherVirksomhedStop();
                Stop stop2 = ekstraherModtagerStop();
                c.tilfoejStop(0, Optional.of(stop1), false, true);
                c.tilfoejStop(1, Optional.of(stop2), false, false);
            }
        }
    }

    OffsetDateTime sammenstoebDatoOgTid(LocalDate dt, String tid) {
        LocalDateTime ldt = LocalDateTime.of(dt, LocalTime.parse(tid, DateTimeFormatter.ofPattern("HH:mm")));
        ZoneOffset zone = ZoneOffset.systemDefault().getRules().getOffset(ldt);
        return OffsetDateTime.of(ldt, zone);
    }
}
