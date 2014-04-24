Calendar - A collaborative calendar
========

## Developing

### Getting started

1. Check out this git respository: 
```git co git@github.com:simonkaltenbacher/calendar.git```
2. Download and install [PostgreSQL](http://www.postgresql.org/download/) 
3. Run the psql-command line: ```psql````
4. Create a database in the psql interactive: ```CREATE DATABASE calendar;``` 
5. Create a user with a password for the database: ```CREATE USER myusername WITH PASSWORD 'mypassword';```
   NOTE: Have a look at the entries starting with `db.default.*` in `application.conf`. 
   The user name and password declared there must match the one you created in the psql interactive.
6. Run the application with ```sbt run```` (it will then be running on the standard port 9000).
7. Open the application in the browser (perferably Google Chrome) with the URL `localhost:9000`
8. Register with user name and password on the login page (must not match the one specified for the database).
9. You can now log in to the calendar.


### Running Tests

Run ```sbt test``` from the command line to run the tests.



## Technology stack:

### DBS 
- [Postgres](http://www.postgresql.org/) as an open source, object-relational database management system
- [Slick] as an open source, modern database query and access library for Scala. 
  ScalaDoc: http://slick.typesafe.com/doc/2.0.1/api/#package
  GitHub: https://github.com/slick/slick

### Backend
- Play
- Scala
- Akka

### Frontend
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




## Hauptfunktionen:
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
