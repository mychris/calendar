Calendar - A Collaborative calendar
========

# Technologiestack:

## DBS
- Postgres http://www.postgresql.org/
- Slick für Datenbankzugriffe

## Backend
- Play
- Scala
- Akka

## Frontend
- JQuery v. 2.1.0
- Bootstrap v. 3.1.1
- FullCalendar v2beta
- D3 v. 3.4.6 (DOM manipulation, HTML generation) 
- bootstrap-datetimepicker
- selectize.js:
  -> Website: http://brianreavis.github.io/selectize.js/
  -> CDN: http://cdnjs.com/libraries/selectize.js
  -> Docu: https://github.com/brianreavis/selectize.js/blob/master/docs/usage.md
  -> API Docu: https://github.com/brianreavis/selectize.js/blob/master/docs/api.md





# Hauptfunktionen:
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
- Auflösen von Konflikten (kollidierende Termine) 
    -> Automatisches Auflösen möglich
    -> Halbautomatisches Auflösen möglich

- GUI: Kalendaransicht




Aufgaben:
- Gut dokumentierte Software, Beschreibung: paar Seiten, hauptsache Präsentation am Ende


Notes
  1. The tables of the AppointmentProposalDataAccessService are not created at the moment. Support will be added as soon as the component's implementation is finished.
  2. There seems to be a bug in slick regarding compound primary keys. Therefore the compound keys are commented out in CalendarDataAccessService.
