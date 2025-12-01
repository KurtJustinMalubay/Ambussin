package main.models;

import java.util.ArrayList;
import java.util.List;

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
    }
    public void generateSeatMap() {
        for (int i = 1; i <= capacity; i++) {
            seats.add(new Seat(String.valueOf(i)));
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
        // DISPLAY NI NATO KANANG MURAG ERROR BOX: "Seat not found: " + seatNumber
        return null;
    }
    @Override
    public String toString() {
        return type + " Bus (ID: " + vehicleID + ")";
    }
}