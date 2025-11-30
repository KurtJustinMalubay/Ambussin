# Ambussin: Terminal Booking Kiosk System

**Ambussin** is a Java-based desktop application designed as a **Terminal Kiosk** for bus reservations. It streamlines the ticketing process by allowing walk-in passengers to view schedules, visually select seats, and print tickets, while providing administrators with tools to manage the fleet and audit transaction logs (Supposedly a mobile app for ease-of-access).

---

## Key Features

### Passenger Module
* **Dynamic Trip Selection:** Filters buses by Destination and Type (Aircon vs. Standard).
* **Visual Seat Selection:** Interactive 2D grid representing the bus layout.
    * **Smart Layout:** Automatically visualizes "Ghost Seats" (Driver/Door areas) based on bus capacity.
    * **Real-time Availability:** Booked seats appear Red; Available seats appear Green.
* **Automated Pricing:** Calculates fares based on passenger type (Regular, Student, Senior, PWD).

### Admin Module
* **Secure Access:** Password-protected dashboard (Default: `admin123`).
* **Fleet Management:** Add new bus units directly from the GUI (updates `buses.csv`).
* **Audit Logging:** View a tabular history of all transactions, including passenger names, types, and amounts paid.

---

## Technical Architecture

| Layer | Package              | Description                                                                                                            |
| :--- |:---------------------|:-----------------------------------------------------------------------------------------------------------------------|
| **Model** | `main.models`        | Business logic objects (Bus, Passenger, Vehicle, etc). Contains the core rules for seat blocking and fare computation. |
| **View** | `main.gui`           | Swing components (`JPanel`, `JFrame`) that present data to the user.                                                   |
| **Controller** | `main.gui.MainFrame` | Manages navigation flow (CardLayout) and communication between the Views and the Data Layer.                           |
| **Data** | `main.managers`      | Handles File I/O for CSV storage.                                                                                      |

---

## Design Patterns Implemented

### 1. Singleton Pattern
* **Where:** `main.managers.DataManager`
* **Why:** We need a single point of truth for accessing the CSV files (`buses.csv` and `transactions.csv`). The Singleton pattern ensures that only one instance of the file reader/writer exists, preventing resource conflicts and data corruption during concurrent file access.

### 2. Factory Pattern (Creational)
* **Where:** `main.managers.VehicleFactory` & `main.managers.PassengerFactory`
* **Why:**
    * **VehicleFactory:** Decouples the creation logic from the data loading. It reads raw CSV strings and decides whether to instantiate an `AirconBus` (4 columns) or `StandardBus` (5 columns).
    * **PassengerFactory:** Encapsulates the logic for creating passenger types. The GUI simply requests a passenger based on the dropdown string whether you are a discountedPassenger or regularPassenger, and the Factory handles the instantiation.
---

## Project Structure

```text
Ambussin/
├── buses.csv                # Config file (Blueprint of the fleet)
├── transactions.csv         # Log file (Generated automatically)
├── src/
│   └── main/
│       ├── Main.java        # Entry Point
│       ├── exceptions/      # Custom Exceptions (AdminAccess, InvalidSeat, etc.)
│       ├── manager/         # VehicleFactory, PassengerFactory, DataManager (Singleton)
│       ├── gui/             # Swing UI Panels
│       └── models/          # Bus, Passenger, Vehicle, etc.