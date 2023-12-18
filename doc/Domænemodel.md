## Brainstorm af vigtige begreber
- Pakke
- Modtager/Kunde
- Afsender/Virksomhed
- Rute
- Stop (pakkehus, lufthavn, destination)

## Diagrammet
Diagrammet findes i kodebasen under [diagram/domain-model.png](../diagram/domain-model.png).

Man kan se at diagrammet skiller sig i to lag. Det øverste lag viser en virksomhed som sender en pakke til en modtager. Pakken har et pakkenummer vi kan bruge til at tracke pakken. Man kunne forestille sig at pakkenummeret bliver brugt til at slå op i GLS, Postnord osv. til at skaffe information om pakken.

I det andet lag bliver beskrevet ruten som pakken vil tage. En rute er associateret med en transport metode såsom GLS, Postnord osv. Det er dem der står for at transportere pakken. En rute er bygget op om en liste af to eller flere stop (der er mindst to stop: virksomheden og modtageren, og typisk vil der også være flere). Stop er associateret med en stop type som beskriver om det er et posthus, lufthavn, havn osv.