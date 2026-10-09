# Drone Club Manager

🇩🇪 [Deutsche Version](ReadMe.md)

**Main purpose:** This app is used to manage a drone club. Pilots can register drones, monitor the condition of components (motors, ESCs, batteries) and record flight times.

**Target audience:** Pilots and small clubs. The benefits are regular equipment maintenance and avoiding frequency overlaps during flights.

**Motivation:** As a drone pilot, I want to build a solution that digitizes technical maintenance and social coordination (who flies when and on which frequency).

## Planned Features
- Must Have:
    - Registration and login of pilots via secure authentication (database connection).
    - Inventory management (drones and their individual parts).
    - Storing and updating flight data from the SQLite database.
    - Notifications for maintenance intervals and flight times.

- Should Have:
    - Network communication: manages the available video frequencies so pilots can reserve a channel through the client (prevents signal interference).
    - Concurrent operation: multiple pilots can reserve drone frequencies or update their status at the same time.

- Nice to Have:
    - Graphical user interface (GUI) with statistics (e.g. "which motor has the most operating hours?").
    - Notification system for situations where the battery is low or maintenance is due.

## Implemented Features
- **Project structure:** Complete package structure (model, db, network, main, util, gui).
- **Data models:** Full implementation of the classes `Pilot`, `Drone`, `Part`, `FlightLog`.
- **Standard methods:** `toString()`, `equals()` and `hashCode()` are implemented in the model classes.
- **Encapsulation:** Consistent use of `private` fields with appropriate getters and setters.
- **System prototypes:** `DatabaseManager` and `NetworkManager` were prepared with method prototypes for the upcoming extension phases.
- **Secure authentication:** A `PasswordHasher` module using the SHA-256 algorithm. Passwords are never stored in plain text, only as hashes in the database.
- **Centralized data management:** All database access happens exclusively on the server side. The client no longer has a direct connection to the SQLite database, which significantly improves data security and integrity.
- **Type-safe DTO communication:** Client and server do not communicate via error-prone strings or maps, but via dedicated **Data Transfer Objects (DTOs)** (`PilotDTO`, `DroneDTO`, `PartDTO`, `FlightLogDTO`). All DTOs implement `java.io.Serializable` and serve as pure data containers. The exchange is handled via `ObjectInputStream` and `ObjectOutputStream`.

## Functional Changes During Development

Compared to the original plan, the following points changed or evolved during implementation:

- **`NetworkManager` dropped in favor of `DroneServer` + `ClientHandler`:** `NetworkManager` was intended as an early prototype for server coordination (`StartServer()`, `StopServer()`, `broadcastFrequencyList()`). In the final architecture, `DroneServer` (accepting connections via `ServerSocket`) and `ClientHandler` (`dbLock` synchronization) take over this role completely. `NetworkManager` remains as unused legacy code in the `network` package.
- **Drone status evolved from a simple boolean to an enum:** Originally, `drones` only had an `is_functional` field (operational readiness, INTEGER/boolean). Instead, the `DroneStatus` enum (`AVAILABLE`, `IN_FLIGHT`, `MAINTENANCE`) was implemented with its own `status` column (`TEXT DEFAULT 'AVAILABLE'`), since a plain boolean could not represent flight operations (available / currently flying / needs maintenance).
- **Extended frequency management:** In addition to `assigned_frequency` on the pilot, a `FrequencyManager` was introduced: a fixed list of 25 real FPV frequencies (1630–2505 MHz) with a `synchronized` lock/release mechanism, so two pilots never accidentally occupy the same frequency at the same time.
- **Flight workflow extended with "Flight Now":** On top of plain flight log management (CRUD on `flight_logs`), a two-step takeoff/landing workflow was added (`FlightSetupDTO`, `ADD_FLIGHT` → `LAND_DRONE`). It automatically switches the drone status `AVAILABLE` → `IN_FLIGHT` → `AVAILABLE`/`MAINTENANCE` and updates the operating hours of the mounted `parts` on every flight.

### Why TCP Sockets
The *Drone Club Manager* uses connection-oriented **TCP (`Socket` / `ServerSocket`)** on port `8080`.
**Reason:** Since critical, consistent and security-relevant data such as authentication, pilot and drone updates are transmitted over the network, packet loss must not occur under any circumstances. Through its handshake, flow control and packet ordering, TCP guarantees **reliable data transfer**. Unlike UDP, it ensures every request reaches the server completely and unaltered.

### Multi-Client Support & Concurrency
To support multiple pilots at once, the server is multi-threaded:
* `DroneServer` waits for incoming connections in an endless loop using `accept()`.
* Each new connection is immediately handed to its own `ClientHandler` instance, which implements `Runnable`, and started in a **new thread**. This way clients do not block each other while connecting.

### Synchronization & Race Condition Protection
Since embedded SQLite databases can lock up or corrupt data under simultaneous writes, a strict thread-safety concept was implemented using a central lock object (`dbLock`) inside `ClientHandler`:
* **Write operations (ADD, UPDATE, DELETE):** All modifying database operations are explicitly guarded by a `synchronized(dbLock)` block. This guarantees that writing threads modify the database sequentially.
* **Read operations (GET_ALL):** To keep performance high, pure reads run outside the synchronized block. Multiple clients can query data at the same time without being blocked.

### Test Scenarios
The stability and correctness of the implementation were verified with two test classes in the `network` package:
1. `MultiClientTest`: Uses a `CountDownLatch` to simulate exactly simultaneous access by 5 clients (3 readers, 2 writers) to demonstrate stability under heavy asynchronous load.
2. `RaceConditionTest`: Deliberately provokes a race condition where two clients (`Client_A_Speedy` and `Client_B_Flash`) update the same drone record (ID 1) in the same millisecond. Thanks to the `synchronized(dbLock)` lock, the server processes both requests successfully one after another without `SQLITE_BUSY` exceptions, preserving data integrity.

## Modules and Classes
| Class                                            | Responsibility                                                                                                                       |
|:-------------------------------------------------|:-------------------------------------------------------------------------------------------------------------------------------------|
| **Pilot**                                        | Stores user data such as ID, name and total flight time.                                                                             |
| **Drone**                                        | Represents a drone with properties such as name, weight and type.                                                                    |
| **Part**                                         | Manages the individual components attached to a drone (e.g. motors, ESCs).                                                           |
| **FlightLog**                                    | Records flight data such as date, duration and battery capacity used.                                                                |
| **DroneType**                                    | (Enum) Defines the drone categories.                                                                                                 |
| **ExperienceLevel**                              | (Enum) Defines pilot skill levels.                                                                                                   |
| **PartType**                                     | (Enum) Categorizes spare parts and components.                                                                                       |
| **DatabaseConnection**                           | Handles the connection to the local SQLite database file.                                                                            |
| **DatabaseManager**                              | Centralizes all access to the SQLite database.                                                                                       |
| **PilotDAO**                                     | Encapsulates CRUD operations for pilots, including secure registration and login logic.                                              |
| **DroneDAO**                                     | Handles database access for drone entities.                                                                                          |
| **FlightLogDAO**                                 | Runs relational queries and loads complete flight logs including linked pilot and drone objects.                                     |
| **PartDAO**                                      | Manages all drone components in the database and their assignment to drones.                                                         |
| **NetworkManager**                               | Coordinates client requests and ensures conflict-free frequency assignment.                                                          |
| **PasswordHasher**                               | Provides secure password hashing using SHA-256.                                                                                      |
| **Command**                                      | (Enum) Defines the communication protocol vocabulary (`ADD_DRONE`, `UPDATE_DRONE`, etc.).                                            |
| **ClientRequest / ServerResponse**               | Standardized transport containers for network packets.                                                                               |
| **DroneServer**                                  | Opens `ServerSocket(8080)` and waits for incoming clients in an endless loop.                                                        |
| **ClientHandler**                                | Implements `Runnable`. Processes the requests of a single client in its own thread.                                                  |
| **DroneClient**                                  | Lets client applications connect to the server and send data in a type-safe way.                                                     |
| **PilotDTO / DroneDTO / PartDTO / FlightLogDTO** | Plain, serializable data containers for safe network transport.                                                                      |
| **LoginView / RegisterView**                     | JavaFX forms for login and registration, containing UI components only.                                                              |
| **MainDashboardView**                            | Main window after login. Side menu, action toolbar and a `TableView` showing drones/pilots/flights/parts.                            |
| **DroneDialog / PartDialog / FlightNowDialog**   | Modal `Dialog<T>` windows for creating/editing drones and parts, or starting a flight.                                               |
| **LoginController**                              | Handles the login form, sends the `LOGIN` request asynchronously and switches to the dashboard on success.                           |
| **RegisterController**                           | Validates the registration form and sends the `REGISTER` request asynchronously.                                                     |
| **DashboardController**                          | Controls category switching, table setup and all CRUD/frequency/flight actions; the only place where the view meets `DroneClient`.   |

## GUI and MVC Architecture

The interface is built with **JavaFX** and strictly follows the **Model-View-Controller** pattern:

- **View** (`gui`): `LoginView`, `RegisterView`, `MainDashboardView`, `DroneDialog`, `PartDialog`, `FlightNowDialog`. These classes only build the JavaFX node hierarchy and expose getters for their controls (e.g. `getLoginButton()`, `getMainTable()`).
- **Controller** (`controller`): `LoginController`, `RegisterController`, `DashboardController`. They wire up the view's event handlers (`setOnAction`), build `ClientRequest` objects, send them via `DroneClient` and update the view based on the `ServerResponse`.
- **Model**: the `model` package (`Pilot`, `Drone`, `Part`, `FlightLog`, enums) and the DTOs in the `network.dto` package exchanged between client and server.

**Screens:** Login -> Register -> main dashboard with four categories (Drones, Pilots, Flights, Parts), including form dialogs for creating/editing and a dedicated "Flight Now" dialog for takeoff/landing.

**Responsive GUI:** Every network operation (login, registration, loading a category, CRUD actions, takeoff/landing) runs in its own `javafx.concurrent.Task`, started in a separate daemon `Thread` (`setDaemon(true)`). The JavaFX Application Thread is never blocked. Once the result is available, the task callback (`setOnSucceeded`/`setOnFailed`) updates the UI, additionally via `Platform.runLater(...)` where needed, e.g. for error alerts. Connection errors, timeouts and actions rejected by the server are shown as `Alert` popups (`ERROR`/`INFORMATION`) or as status label text.

## Data Structure

The application uses a local SQLite database for persistent storage. Tables, keys and relations are structured as follows:
### 1. Tables and Stored Data

* **`pilots`** (users and pilot profile data)
    * `id`: Primary key (INTEGER, AUTOINCREMENT)
    * `name`: Pilot's full name (TEXT, NOT NULL)
    * `username`: Unique username for authentication (TEXT, UNIQUE, NOT NULL)
    * `password_hash`: Securely hashed password (TEXT, NOT NULL)
    * `experience_level`: Level such as EXPERT, ADVANCED (TEXT)
    * `total_flight_hours`: Total flight time in Duration format (TEXT)
    * `assigned_frequency`: Assigned radio frequency (REAL)
    * `is_active`: Whether the pilot is active (INTEGER)

* **`drones`** (drones registered in the system)
    * `id`: Primary key (INTEGER, AUTOINCREMENT)
    * `model_name`: Drone model name (TEXT, NOT NULL)
    * `type`: Drone category from the `DroneType` enum (TEXT)
    * `weight`: Drone weight in grams or kilograms (REAL)
    * `status`: Operating status from the `DroneStatus` enum: `AVAILABLE`, `IN_FLIGHT` or `MAINTENANCE` (TEXT, default `'AVAILABLE'`)
    * `build_date`: Build date (TEXT)
    * `last_maintenance_date`: Date of last maintenance (TEXT)
    * `total_flight_time`: Total flight time as Duration (TEXT)
    * `current_frequency`: Currently used frequency (REAL)

* **`flight_logs`** (all completed flights and logs)
    * `id`: Primary key (INTEGER, AUTOINCREMENT)
    * `pilot_id`: Foreign key (INTEGER, references `pilots(id)`)
    * `drone_id`: Foreign key (INTEGER, references `drones(id)`)
    * `date`: Flight date (TEXT)
    * `flight_duration`: Flight duration as Duration (TEXT)
    * `comment`: Optional note about the flight (TEXT)
    * `used_frequency`: Frequency used during the flight (REAL)
    * `location`: Flight location (TEXT)

* **`parts`** (individual technical components of drones)
    * `id`: Primary key (INTEGER, AUTOINCREMENT)
    * `name`: Part name (TEXT, NOT NULL)
    * `brand`: Part brand (TEXT)
    * `type`: Component type from the `PartType` enum (TEXT)
    * `drone_id`: Foreign key (INTEGER, references `drones(id)`)
    * `operating_hours`: Operating hours so far (TEXT)
    * `is_working`: Whether the part is functional (INTEGER)

---

### 2. Table Relations

To avoid redundancy and ensure data integrity, the following **1:n (one-to-many)** relations are implemented via foreign keys:

* **`pilots` to `flight_logs` (1:n)**
    * *Description:* A pilot can complete many flights over time and therefore has multiple entries in `flight_logs`. A specific flight log, however, always belongs to exactly one pilot via `pilot_id`.
* **`drones` to `flight_logs` (1:n)**
    * *Description:* A drone can be used for many different flights recorded in `flight_logs`. Each log, however, refers to exactly one drone via `drone_id`.
* **`drones` to `parts` (1:n)**
    * *Description:* A drone consists of several components (e.g. motors, flight controller, propellers), so many parts in `parts` can be assigned to the same drone via `drone_id`. A single part, however, is mounted in exactly one drone.
