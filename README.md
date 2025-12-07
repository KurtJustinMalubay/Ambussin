# 🚌 Ambussin: Terminal Booking Kiosk System

**Ambussin** is a Java-based desktop application designed as a **Terminal Kiosk** for bus reservations. It streamlines the ticketing process by allowing walk-in passengers to view schedules, visually select seats, and print tickets.

While currently deployed as a standalone Kiosk for terminal stability, the system is architected to serve as the central hub for a future mobile ecosystem, providing administrators with tools to manage the fleet and audit transaction logs.

---

## Key Features

### Passenger Module
* **Dynamic Trip Selection:** Filters buses by Destination and Type (Aircon vs. Standard).
* **Visual Seat Selection:** Interactive 2D grid representing the bus layout.
    * **Real-time Availability:** Booked seats appear **Red**; Available seats appear **Green**.
    * **Smart Layouts:** Automatically visualizes aisles and "Ghost Seats" (Driver/Door areas) based on the bus type (10 rows for Aircon, 12 rows for Standard).
* **Automated Pricing:** Calculates fares based on passenger type (Regular, Student, Senior, PWD).
* **Ticket Generation:** Generates a digital ticket confirmation with a QR code placeholder.

### Admin Module
* **Access:** To prevent unauthorized public access, the Admin Panel is hidden behind a **Long-Press Gesture (2 seconds)** on the "Book Now" button.
* **Secure Authentication:** Password-protected dashboard (Default: `admin123`).
* **Bus/Fleet Management:** Add new bus units directly from the GUI. The system automatically updates `buses.csv`.
* **Audit Logging:** View a tabular history of all transactions, including passenger names, types, and amounts paid.

---

## 🛠️ Technical Architecture

The project follows a strict **Model-View-Controller (MVC)** architecture to ensure separation of concerns and scalability.

| Layer | Package | Description |
| :--- | :--- | :--- |
| **Model** | `main.models` | Business logic objects (`Bus`, `Passenger`, `Vehicle`). Contains the core rules for seat blocking and fare computation. |
| **View** | `main.gui` | Swing components and Forms (`JPanel`, `JFrame`) that present data to the user. Includes custom components like `RoundedButton` and `BusCardRenderer`. |
| **Controller** | `main.gui.MainFrame` | Manages navigation flow (CardLayout) and communication between the Views and the Data Layer. |
| **Data** | `main.managers` | Handles File I/O for CSV storage and Object instantiation. |

---

## Design Patterns Implemented

### 1. Singleton Pattern
* **Where:** `main.managers.DataManager`
* **Why:** We need a single point of truth for accessing the CSV files (`buses.csv` and `transactions.csv`). The Singleton pattern ensures that only one instance of the file reader/writer exists, preventing resource conflicts and data corruption during concurrent file access.

### 2. Factory Pattern (Creational)
* **Where:** `main.managers.VehicleFactory` & `main.managers.PassengerFactory`
* **Why:**
    * **VehicleFactory:** Decouples the creation logic from the data loading. It reads raw CSV strings and decides whether to instantiate an `AirconBus` (Fixed 41 Capacity) or `StandardBus` (Fixed 49 Capacity).
    * **PassengerFactory:** Encapsulates the logic for creating passenger types. The GUI simply requests a passenger based on the dropdown string ("Student", "Regular"), and the Factory handles the correct object instantiation.

---

## 📂 Project Structure

```text
Ambussin/
├── buses.csv                # Config file (Blueprint of the fleet)
├── transactions.csv         # Log file (Generated automatically)
├── src/
│   ├── resources/           # Assets (Images, Icons) - Marked as Resource Root
│   └── main/
│       ├── Main.java        # Entry Point
│       ├── exceptions/      # Custom Exceptions (AdminAccess, InvalidSeat, etc.)
│       ├── managers/        # VehicleFactory, PassengerFactory, DataManager (Singleton)
│       ├── gui/             # Swing UI Panels
│       │   └── components/  # Custom UI assets (RoundedButton, ImagePanel)
│       └── models/          # Bus, Passenger, Vehicle classes