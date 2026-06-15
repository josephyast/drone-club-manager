# 1.Drone Club Manager

**Hauptaufgabe:** Diese App wird verwendet, um einen Drone Club zu verwalten. Piloten werden Drohnen aufzeichnen, den Zustand der Komponenten (Motoren, ESC, Batterien) überwachen und Flugzeiten aufzeichnen können.

**Zielgruppe:** Piloten und kleine Clubs. Die Vorteile liegen in der regelmäßigen Wartung der Ausrüstung und der Vermeidung von frequenzüberlappungen während des Fluges.

**Motivation:** Als Drohnenpilot möchte ich eine Lösung schaffen, die die technische Wartung und soziale Koordination digitalisiert (wer fliegt wann und auf welcher Frequenz).

## Geplänte Funktionalitäten
- Must Have:
    - Registrierung und Anmeldung von Piloten über eine sichere Authentifizierung (Datenbankverbindung).
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
- **Projektstruktur:** Vollständige Einrichtung der Paketstruktur (model, db, network, main, util, gui).
- **Datenmodelle:** Vollständige Implementierung der Klassen 'Pilot', 'Drone', 'Part', 'FlightLog'.
- **Standardmethoden:** die Aufgaben 'toString()', 'equals() 'und' hashCode()'sind in den Objektklassen angewendet worden.
- **Kapselung:** Konsistente Verwendung von'private' Eigenschaften mit geeigneten getter und setter Methoden.
- **Systemprototypen:** 'DatabaseManager' und 'NetworkManager' sind mit methodenprototypen für die anstehenden erweiterungsphasen vorbereitet.
- **Sichere Authentifizierung:** Implementierung eines PasswordHasher Moduls unter Verwendung des SHA-256-Algorithmus. Passwörter werden niemals im Klartext, sondern ausschließlich gehashed in der Datenbank gespeichert.
- **Zentralisiertes Datenmanagement:** Alle Datenbankzugriffe erfolgen nun ausschließlich serverseitig. Der Client besitzt keine direkte Verbindung zur SQLite Datenbank mehr, was die Datensicherheit und integrität maßgeblich erhöht.
- **Typsichere DTO Kommunikation:** Die Kommunikation zwischen Client und Server erfolgt nicht über fehleranfällige Strings oder Maps, sondern über dedizierte **Data Transfer Objects (DTOs)** (`PilotDTO`, `DroneDTO`, `PartDTO`, `FlightLogDTO`). Alle DTOs implementieren `java.io.Serializable` und dienen als reine Datencontainer. Der Austausch wird über `ObjectInputStream` und `ObjectOutputStream` abgewickelt.

### Begründung der Socket Wahl
Für den *Drone Club Manager* wurde eine verbindungsbasierte **TCP (`Socket` / `ServerSocket`)** auf Port `8080` gewählt.
**Begründung:** Da über das Netzwerk kritische, konsistente und sicherheitsrelevante Daten wie Authentifizierungen, Pilot und Drone-Updates übertragen werden, darf unter keinen Umständen ein Paketverlust auftreten. TCP garantiert durch sein Handshake Verfahren, die Flusskontrolle und die Paket Reihenfolgeüberwachung eine **100% zuverlässige Datenübertragung**. Im Gegensatz zu UDP wird hier sichergestellt, dass jede Anfrage den Server vollständig und unverfälscht erreicht.

### Multi Client Fähigkeit & Nebenläufigkeit
Um mehrere Piloten gleichzeitig zu unterstützen, wurde der Server multi-threaded implementiert:
* Der `DroneServer` wartet in einer Endlosschleife mittels `accept()` auf eingehende Verbindungen.
* Jede neue Verbindung wird sofort an eine eigene Instanz von `ClientHandler` übergeben, die `Runnable` implementiert, und in einem **neuen Thread** gestartet. Dadurch blockieren sich Clients beim Verbindungsaufbau nicht gegenseitig.

### Synchronisations-Konzept & Schutz vor Race Conditions
Da eingebettete SQLite Datenbanken bei simultanen Schreibzugriffen blockieren oder Datenkorruption verursachen können, wurde ein striktes Thread Safety Konzept über ein zentrales Sperrobjekt (`dbLock`) innerhalb des `ClientHandler` realisiert:
* **Schreiboperationen (ADD, UPDATE, DELETE):** Alle verändernden Operationen auf der Datenbank wurden explizit durch einen `synchronized(dbLock)` Block geschützt. Dadurch wird garantiert, dass schreibende Threads die Datenbank sequenziell manipulieren.
* **Leseoperationen (GET_ALL):** Um die Systemperformance hoch zu halten, laufen reine Lesevorgänge außerhalb des synchronisierten Blocks. Mehrere Clients können somit gleichzeitig Daten abfragen, ohne blockiert zu werden.

### Durchgeführte Test-Szenarien
Die Stabilität und Korrektheit der Implementierung wurde durch zwei hochentwickelte Testklassen im `network`Paket verifiziert:
1. `MultiClientTest`: Simuliert mithilfe eines `CountDownLatch` den exakt gleichzeitigen Zugriff von 5 Clients (3 Reader, 2 Writer), um die Stabilität unter hoher asynchroner Last nachzuweisen.
2. `RaceConditionTest`: Provoziert eine gezielte Race Condition, bei der zwei Clients (`Client_A_Speedy` und `Client_B_Flash`) in derselben Millisekunde denselben Drohnen-Datensatz (ID 1) aktualisieren. Dank der `synchronized(dbLock)`Sperre verarbeitet der Server beide Anfragen ohne `SQLITE_BUSY` Ausnahmen erfolgreich nacheinander, wodurch die Datenintegrität gewahrt bleibt.

## Modul und Klassenübersicht
| Klasse                                           | Aufgabe                                                                                                                 |
|:-------------------------------------------------|:------------------------------------------------------------------------------------------------------------------------|
| **Pilot**                                        | Es speichert Benutzerdaten, z. B. ID, Name und Gesamtflugzeit.                                                          |
| **Drone**                                        | Es stellt ein Drohne mit Eigenschaften wie Name, Gewicht und Typ dar.                                                   |
| **Part**                                         | Verwaltet die einzelnen Komponenten, die mit der Drohne verbunden sind (z. B. Motoren, ESC).                            |
| **FlightLog**                                    | Dokumentiert Flugdaten wie das verbrauchte Datum, die Dauer und die Batteriekapazität.                                  |
| **DroneType**                                    | (Enum) Definiert die verschiedenen Drohnenkategorien                                                                    |
| **ExperienceLevel**                              | (Enum) Definiert die Einstufung der Piloten                                                                             |
| **PartType**                                     | (Enum) Kategorisiert die Ersatzteile und Komponenten                                                                    |
| **DatabaseConnection**                           | Verwaltet den Verbindungsaufbau zur lokalen SQLite-Datenbankdatei.                                                      |
| **DatabaseManager**                              | Zentralisiert den gesamten Zugriff auf die SQLite-Datenbank.                                                            |
| **PilotDAO**                                     | Kapselt spezifische CRUD Operationen für Piloten, einschließlich sicherer Registrierungs und Login Logik.               |
| **DroneDAO**                                     | Verwaltet die Datenbankzugriffe für die Drohnen Entitäten.                                                              |
| **FlightLogDAO**                                 | Realisiert relationale Abfragen und lädt vollständige Flugprotokolle inklusive verknüpfter Piloten und Drohnen Objekte. |
| **PartDAO**                                      | Steuert die datenbankseitige Verwaltung aller Drohnenkomponenten und deren Zuordnung zu den Drohnen.                    |
| **NetworkManager**                               | Koordiniert Kundenanforderungen und sorgt für konfliktfreie Frequenzzuweisung.                                          |
| **PasswordHasher**                               | Bietet Funktionen zum sicheren Hashen von Passwörtern unter Verwendung von SHA-256.                                     |
| **Command**                                      | (Enum) Definiert das Kommunikations-Protokoll-Vokabular (`ADD_DRONE`, `UPDATE_DRONE`, etc.).                            |
| **ClientRequest / ServerResponse**               | Die standardisierten Transport-Container für die Netzwerk-Pakete.                                                       |
| **DroneServer**                                  | Öffnet den `ServerSocket(8080)` und wartet in einer Endlosschleife auf eingehende Clients.                              |
| **ClientHandler**                                | Implementiert `Runnable`. Verarbeitet die Requests eines einzelnen Clients im eigenen Thread.                           |
| **DroneClient**                                  | Ermöglicht es Client-Anwendungen, sich mit dem Server zu verbinden und Daten typsicher zu senden.                       |
| **PilotDTO / DroneDTO / PartDTO / FlightLogDTO** | Reine, serialisierbare Datenbehälter für den sicheren Netzwerktransport.                                                |

## Datenstruktur

Die Anwendung verwendet eine lokale SQLite Datenbank, um Projektdaten dauerhaft zu speichern. Tabellenstrukturen, Schlüssel und relationale Links sind wie folgt strukturiert:
### 1. Existierende Tabellen und gespeicherte Informationen

* **`pilots`** (Verwaltet die Benutzer und Profildaten der Piloten)
    * `id`: Primary Key (INTEGER, AUTOINCREMENT)
    * `name`: Vollständiger Name des Piloten (TEXT, NOT NULL)
    * `username`: Eindeutiger Benutzername für Authentifizierung (TEXT, UNIQUE, NOT NULL)
    * `password_hash`: Sicher gehashtes Passwort (TEXT, NOT NULL)
    * `experience_level`: Einstufung wie EXPERT, ADVANCED (TEXT)
    * `total_flight_hours`: Gesamtflugzeit im Duration Format (TEXT)
    * `assigned_frequency`: Zugewiesene Funkfrequenz (REAL)
    * `is_active`: Status, ob der Pilot aktiv ist (INTEGER)

* **`drones`** (Repräsentiert die im System registrierten Drohnen)
    * `id`: Primary Key (INTEGER, AUTOINCREMENT)
    * `model_name`: Modellname der Drohne (TEXT, NOT NULL)
    * `type`: Drohnenkategorie aus dem DroneType-Enum (TEXT)
    * `weight`: Gewicht der Drohne in Gramm oder Kilogramm (REAL)
    * `is_functional`: Betriebsbereitschaft der Drohne (INTEGER)
    * `build_date`: Baudatum der Drohne (TEXT)
    * `last_maintenance_date`: Datum der letzten Wartung (TEXT)
    * `total_flight_time`: Gesamte Flugzeit der Drohne als Duration (TEXT)
    * `current_frequency`: Aktuell genutzte Frequenz (REAL)

* **`flight_logs`** (Dokumentiert alle absolvierten Flüge und Protokolle)
    * `id`: Primary Key (INTEGER, AUTOINCREMENT)
    * `pilot_id`: Foreign Key (INTEGER, verweist auf `pilots(id)`)
    * `drone_id`: Foreign Key (INTEGER, verweist auf `drones(id)`)
    * `date`: Flugdatum (TEXT)
    * `flight_duration`: Dauer des Fluges als Duration (TEXT)
    * `comment`: Optionale Anmerkung zum Flugverlauf (TEXT)
    * `used_frequency`: Während des Fluges genutzte Frequenz (REAL)
    * `location`: Ort des Fluges (TEXT)

* **`parts`** (Verwaltet die einzelnen technischen Komponenten der Drohnen)
    * `id`: Primary Key (INTEGER, AUTOINCREMENT)
    * `name`: Name des Bauteils (TEXT, NOT NULL)
    * `brand`: Marke des Teils (TEXT)
    * `type`: Komponententyp aus dem PartType Enum (TEXT)
    * `drone_id`: Foreign Key (INTEGER, verweist auf `drones(id)`)
    * `operating_hours`: Bisherige Betriebsstunden des Bauteils (TEXT)
    * `is_working`: Funktionstüchtigkeit des Teils (INTEGER)

---

### 2. Beziehungen zwischen den Tabellen (Relations)

Um Redundanzen zu vermeiden und die Datenintegrität zu gewährleisten, wurden folgende **1:n (One to Many)** Beziehungen über Fremdschlüssel (Foreign Keys) realisiert:

* **`pilots` zu `flight_logs` (1:n)**
    * *Beschreibung:* Ein Pilot kann im Laufe der Zeit viele Flüge absolvieren und besitzt somit mehrere Einträge in der Tabelle `flight_logs`. Ein spezifisches Flugprotokoll (`flight_log`) ist jedoch immer genau einem einzigen Piloten zugeordnet via `pilot_id`.
* **`drones` zu `flight_logs` (1:n)**
    * *Beschreibung:* Eine Drohne kann für viele verschiedene Flüge gestartet und in `flight_logs` dokumentiert werden. Jedes einzelne Protokoll bezieht sich jedoch über `drone_id` auf genau eine spezifische Drohne.
* **`drones` zu `parts` (1:n)**
    * *Beschreibung:* Eine Drohne besteht aus mehreren einzelnen Komponenten (z. B. Motoren, Flight Controller, Propeller). Daher können in der Tabelle `parts` viele Bauteile über `drone_id` derselben Drohne zugewiesen sein. Ein einzelnes Bauteil ist jedoch fest in genau einer Drohne verbaut.