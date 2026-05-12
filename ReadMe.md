# 1.Drone Club Manager

### Nach Rücksprache mit dem Tutor wurde entschieden, das Projekt Drone Club Manager umzusetzen.

**Hauptaufgabe:** Diese App wird verwendet, um einen Drone Club zu verwalten. Piloten werden Drohnen aufzeichnen, den Zustand der Komponenten (Motoren, ESC, Batterien) überwachen und Flugzeiten aufzeichnen können.

**Zielgruppe:** Piloten und kleine Clubs. Die Vorteile liegen in der regelmäßigen Wartung der Ausrüstung und der Vermeidung von frequenzüberlappungen während des Fluges.

**Motivation:** Als Drohnenpilot möchte ich eine Lösung schaffen, die die technische Wartung und soziale Koordination digitalisiert (wer fliegt wann und auf welcher Frequenz).

## Geplänte Funktionalitäten
- Must Have:
    - Registrierung und Anmeldung von Piloten (Datenbankverbindung).
    - Verwaltung des Inventars (Drohnen und ihre einzelteile).
    - Speicherung und Aktualisierung von Flugdaten aus der SQLite Datenbank.
    - Benachrichtigungen für Wartungsintervalle und Flugzeiten
  
- Should Have:
    - Netzwerkkommunikation: verwaltet die verfügbaren "videofrequenzen", sodass Piloten über den Client in einen kanal aufnehmen können (verhindert Signalstörungen).
    - Simultane Bewegung: mehrere Piloten können gleichzeitig drohnen frequenzen aufzeichnen oder ihren Status aktualisieren.

- Nice to Have:
    - Grafische Benutzeroberfläche (GUI) mit Statistikanzeige (z. B. "welcher motor hat die meisten Betriebsstunden?").
    - Benachrichtigungssystem in Situationen, in denen die Batterie niedrig ist oder auf Wartung wartet.
  
## Bereits implementierte Funktionalitäten
- **Projektstruktur:** Installation der Paketstruktur ('model',' db',' network','main','gui').
- **Datenmodelle:** Vollständige Implementierung der Klassen 'Pilot', 'Drone', 'Part', 'FlightLog'.
- **Standardmethoden:** die Aufgaben 'toString()', 'equals() 'und' hashCode()'sind in den Objektklassen angewendet worden.
- **Kapselung:** Konsistente Verwendung von'private' Eigenschaften mit geeigneten getter und setter Methoden.
- **Systemprototypen:** 'DatabaseManager' und 'NetworkManager' sind mit methodenprototypen für die anstehenden erweiterungsphasen vorbereitet.

## Modul und Klassenübersicht
| Klasse              | Aufgabe                                                                                      |
|:--------------------|:---------------------------------------------------------------------------------------------|
| **Pilot**           | Es speichert Benutzerdaten, z. B. ID, Name und Gesamtflugzeit.                               |
| **Drone**           | Es stellt ein Drohne mit Eigenschaften wie Name, Gewicht und Typ dar.                        |
| **Part**            | Verwaltet die einzelnen Komponenten, die mit der Drohne verbunden sind (z. B. Motoren, ESC). |
| **FlightLog**       | Dokumentiert Flugdaten wie das verbrauchte Datum, die Dauer und die Batteriekapazität.       |
| **DatabaseManager** | Zentralisiert den gesamten Zugriff auf die SQLite-Datenbank.                                 |
| **NetworkManagerr** | Koordiniert Kundenanforderungen und sorgt für konfliktfreie Frequenzzuweisung.               |


## 2. Boutique Shop und Inventarsystem

**Hauptaufgabe:** Mit der App können kleine Boutiquen Ihr Inventar digital verwalten, Produkte zum Verkauf anbieten und Bestellungen effizient ausführen.

**Zielgruppe:** Besitzer und Kunden von kleinen Einzelhandelsgeschäften. Der Fokus liegt auf einer einfachen Lagerverwaltung und einem reibungslosen Bestellprozess.

**Motivation:** Ich möchte ein System entwickeln, das die Lücke zwischen Lagerbestand und Verkauf schließt, so dass es keine überverkauften Verkäufe gibt und die Finanzierung im Auge behält.

## Geplante Funktionalitäten
- Must Have:
    - Verwaltung der Produktdatenbank (Name, Preis, Beschreibung, Lager).
    - Warenkorb Funktionalität und Bestellung erstellen.
    - Inventar ändert alle Transaktionen und speichert sie in einer SQLite Datenbank.
- Should Have:
    - Netzwerkkommunikation: Ein zentraler Server verwaltet das Inventar. Mehrere Kunden können gleichzeitig auf das Produkt zugreifen und kaufen.
    - Simultane Bewegung: Wenn zwei Kunden gleichzeitig das letzte Teil eines Produkts erhalten möchten, stellt das System sicher, dass nur eine bestellung erfolgreich ist.
- Nice to Have:
    - Grafische Benutzeroberfläche für den Admin Bereich mit Visualisierung der monatlichen Verkäufe.
    - Automatisches Warnsystem, wenn die Lagerbestände eines Produkts unter eine Schwelle fallen.

## Modul und Klassenübersicht
| Klasse               | Aufgabe                                                                                         |
|:---------------------|:------------------------------------------------------------------------------------------------|
| **Admin**            | Speichert die Anmeldeinformationen des Ladenbesitzers und ermöglicht die Verwaltung des Ladens. |
| **Product**          | Der Preis stellt das Verkaufsprodukt zusammen mit der SKU und dem aktuellen Inventar dar.       |
| **Customer**         | Speichert Kundendaten und die Historie der getätigten Bestellungen.                             |
| **Order**            | Es fasst die bestellten Produkte zusammen und berechnet den Gesamtpreis.                        |
| **InventoryManager** | Überprüft bestandsänderungen und stellt die Daten dem Server zur Verfügung.                     |
| **DatabaseManager**  | Es führt alle DB Transaktionen für Produkte, Kunden und Bestellungen durch.                     |
| **NetworkServer**    | Verarbeitet Kundenanfragen (Bestellungen, inventarabfragen) in separaten Threads                |
