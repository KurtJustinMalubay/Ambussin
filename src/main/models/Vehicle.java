package main.models;

import java.io.*; // Import for File Handling
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Vehicle {
    private String vehicleID;
    private String type;
    private int capacity;
    private List<Seat> seats;

    public Vehicle(String vehicleID, String type, int capacity) {
        this.vehicleID = vehicleID;
        this.type = type;
        this.capacity = capacity;
        this.seats = new ArrayList<>();

        generateSeatMap();

        // Ensure the data directory exists when a vehicle is created
        new File("data_bookings").mkdirs();
    }

    public void generateSeatMap() {
        for (int i = 1; i <= capacity; i++) {
            seats.add(new Seat(String.valueOf(i)));
        }
    }

    // --- NEW: FILE HANDLING METHODS ---

    /**
     * Loads booked seats from a text file specific to this vehicle and date.
     * format: data_bookings/BUS-ID_DATE.txt
     */
    public void loadBookings(String date) {
        // 1. Reset all seats to Available first (important for switching dates)
        for (Seat s : seats) {
            s.release();
        }

        // 2. Construct filename based on ID and Date (sanitized)
        String cleanDate = date.replace(", ", "_").replace(" ", "-"); // format: Monday_Dec-6
        String filename = "data_bookings/" + vehicleID + "_" + cleanDate + ".txt";
        File file = new File(filename);

        if (!file.exists()) {
            return; // No bookings for this day yet
        }
        // 3. Read the file
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String bookedSeatNum = scanner.nextLine().trim();
                Seat s = findSeat(bookedSeatNum);
                if (s != null) {
                    s.reserve(); // Mark as booked in memory
                }
            }
            System.out.println("Loaded bookings for " + vehicleID + " on " + date);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Appends a newly booked seat to the specific file.
     */
    public void saveBooking(String date, String seatNumber) {
        String cleanDate = date.replace(", ", "_").replace(" ", "-");
        String filename = "data_bookings/" + vehicleID + "_" + cleanDate + ".txt";

        try (FileWriter fw = new FileWriter(filename, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            out.println(seatNumber); // Write seat number to new line
            System.out.println("Saved seat " + seatNumber + " to database.");

        } catch (IOException e) {
            System.out.println("Error saving booking: " + e.getMessage());
        }
    }

    public String getAvailableSeat() {
        StringBuilder availableSeats = new StringBuilder();
        for (Seat seat : seats) {
            if (seat.isAvailable()) {
                availableSeats.append(seat.getSeatNumber()).append(", ");
            }
        }
        if (availableSeats.length() > 0) {
            return availableSeats.substring(0, availableSeats.length() - 2);
        }
        return "FULL";
    }
    public Seat findSeat(String seatNumber) {
        for (Seat seat : seats) {
            if (seat.getSeatNumber().equals(seatNumber)) {
                return seat;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return type + " Bus (ID: " + vehicleID + ")";
    }
}