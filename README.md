<div align="center">
	<h1>track & trace</h1>
</div>

Dette program modellere et track & trace program. Programmet kan benyttes af en bruger eller en virksomhed, enten til at oprette/registrere eller vise en oversigt over pakker i systemet. Al data gemmes i en database. Følgende funktionalitet er understøttet:
- **Registrering:** Information om transportfirma, modtager og virksomhed (afsender) kan indtastes.
	- **Ruteplanlægning:** ved registrering indtastes en liste af stoppunkter som ruten "transporteres" igennem. Hvert stop har et tidspunkt; når tidspunktet er nået har pakken "flyttet sig". Det virker i realtid.
- **Tracking/oversigt:** brugeren kan se ved hvilket stoppunkt som pakken befinder sig ved.

Registrering kan ske med CLI eller GUI. Oversigten er kun understøttet gennem CLI.
## Diagrammerne
Diagrammerne har jeg lavet med et værktøj der hedder [PlantUML](https://plantuml.com/). De kan findes under [diagram/*.png](diagram/). Ellers kan diagrammerne genereres ud fra tekstbaseret input filer som findes under [diagram/*.txt](diagram/) med følgende kommando (kræver PlantUML som program):

```bash
cd diagram/
make all
```
## Kildekode struktur
Kildekoden er bygget op om en række Java klasser. Her er de allervigtigste:

- [Pakke](src/Pakke.java), [Modtager](src/Modtager.java), [Virksomhed](src/Virksomhed.java), [Rute](src/Rute.java), [Stop](src/Stop.java): Dette er alle sammen dataklasser (i Java hedder de `record`s).
- [StopType](src/StopType.java): er en enum der bedømmer hvad det er for et stop, f.eks. `VIRKSOMHED`, `HJEM` eller `POSTHUS`.
- [TransportFirma](src/TransportFirma.java): er et interface som har en metode der returnerer en rute. Ideen er at transportfirmaer (GLS, Postnord, Bring osv.) kan lave en klasse implementering af interfacet der agere som et bindeled mellem deres system (gennem deres egen API eller database) og mit system, f.eks. `PostNordTransportFirma` eller `BringTransportFirma`.
- [GeneriskTransportFirma](src/GeneriskTransportFirma.java) er en implementering af TransportFirma der er generisk over transportfirmaet. Det kræver at al information om pakken gemmes i en database der styres af mit system. Pointen er at virksomheder kan registrere pakker manuelt indtil at transportfirmaerne har lavet klasser der implementerer TransportFirma interfacet.
- [Database](src/Database.java): er en singleton klasse der abstraherer over en SQLite database. På klassen er der defineret en række metoder som sender SQL statements/queries op imod databasen.
- [UseCase](src/UseCase.java): er min use-case klasse.
- [Main](src/Main.java): styrer CLI, tekstbaseret I/O.
- [Gui](src/Gui.java): starter en JavaFX GUI.

Alle andre klasser er en del af GUI systemet. Til sidst er der `fxml` resursefilerne under [res/](res/) folderen som danner mine views til GUI delen af kodebasen, og `txt` filerne under [diagram/](diagram/) som danner input til [generering af diagrammerne](#Diagrammerne).

## Dokumentation
Diagrammerne findes under [diagram/](diagram/) folderen. Diverse dokumentation kan findes under [doc/](doc/) folderen. Projektet er også delvist dokumenteret gennem git loggen som kan vises med følgende kommando:

```bash
git log
```
