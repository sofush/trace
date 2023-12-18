#### 4. december - use-cases og diagrammer
- Jeg valgte projekt emne: 1. Track & trace system:
    > Her skal du udvikle et track and trace-system, som kan håndtere afsendelse af pakker fra en virksomhed til en privatperson. Virksomheden skal kunne indtaste information om afsender, modtager, og vælge en transporttype, som fx GLS, Post eller afhentning. I denne opgave skal du realisere et system, som kan modellere afsendelse af pakker, og kan indeholde information om, hvor pakken befinder sig: Hos afsender, hos transportvirksomhed eller modtaget af kunden.) (Forslag til klasser: Virksomhed, Modtager, Transportinfo – men I kan nok finde på nogle bedre klasser!)
- Jeg lavede to [use-cases](Use-cases.md).
- Jeg satte Git op som VCS.
- Jeg lavede to systemsekvensdiagrammer, et til hvert use-case.
- Jeg lavede en domænemodel.
#### 5. december - diagrammer og intellij projekt
- Jeg lavede klassediagrammet.
- Jeg satte et IntelliJ projekt op med `sqlite-jdbc.jar` som afhængighed og begyndte at skrive kildekoden til klasserne ud fra klassediagrammet.
#### 6. december - database
- Jeg skrev videre på kildekoden til [Database.java](../src/Database.java).
#### 7. december - kodebase
- Implementerede [GeneriskTransportFirma](../src/GeneriskTransportFirma.java) klassen.
#### 8. december - CLI
- Jeg implementerede et CLI i [Main](../src/Main.java) til registrering af nye pakker samt en oversigt over pakkerne der er registreret i databasen.
#### 9. december - GUI
- Jeg begyndte at udvikle GUI programmet med fokus på pakkeregistrerings funktionaliteten. 
#### 11. december - GUI
- Jeg lavede GUI delen færdig (opfylder dog kun use-case 2 - registrering af pakker).
#### 12. december - README.md
- Jeg skrev en README dokumentationsfil.
- Jeg lavede en use-case klasse, rettede nogle kommentarer og skiftede byggesystem fra et normalt Intellij projekt til Maven for at gøre det nemmere at importere JavaFX og sqlite-jdbc som afhængigheder.