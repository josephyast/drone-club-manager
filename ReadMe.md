# 1.Drone Club Manager

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