calendar
========

Collaborative calendar 

Hauptfunktionen:
- Freie Zeitslots für Termine finden:
    Angabe von: - Beteiligte Personen
                - Dauer
                - Evtl. Zeitrahmen (Nachmittags/...)
    Ausgabe von Terminvorschlägen, an dem alle teilnehmen können.
    - Davon kann man dann einen auswählen.
    - Zwischen zwei Terminen einen Zeitslot anzeigen, keine Termine (sodass nicht für jede Minute ein Termin angezeigt wird)
- Termin als Vorschlag markieren (reserviert), Termin als fest markieren.
- Kategorie-Tagging (mit Priorität) eines Termins, welcher eine Standardpriorität (wichtig für Konfliktlösung) beinhaltet  
  (Beispieltags: Uni:3, Arbeit:1, Privat:2,..), mit jeweils verschiedener Standard-Priorität

- Möglichkeit des Eintragens kollidierender Termine
- Auflösuen von Konflikten (kollidierende Termine) 
    -> Automatisches Auflösen möglich
    -> Halbautomatisches Auflösen möglich

- GUI: - Eine Ansicht: zB. Monatsansicht
       - ...

Fragen
 - Zugriffsrechte vs. Jeder sieht alles ?
 - 

Optionale Zusatzfeatures:
- Import von Terminen von Google/iCal (was passiert bei Konflikt? Nur nicht-kollidierende Übernehmen?)



Technologiestack:

- Scala
- Play
- Akka? http://akka.io/
- DBMS: Postgres http://www.postgresql.org/
- Slick für Datenbankzugriffe
- JavaScript/jQuery + Kalender-Framework




Aufgaben:
- Gut dokumentierte Software, Beschreibung: paar Seiten, hauptsache Präsentation am Ende




## Added database schema creation and deletion
This functionality is provided via the following routes:

  CREATE /createschema
  DROP   /dropschema

Notes:
  1. The tables of the AppointmentProposalDataAccessService are not created at the moment. Support will be added as soon as the component's implementation is finished.
  2. There seems to be a bug in slick regarding compound primary keys. Therefore the compound keys are commented out in CalendarDataAccessService.
