package main.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Booking {

    private String bookingID;
    private Passenger passenger;
    private Seat seat;
    private Route route;
    private double totalFare;
    private LocalDateTime bookingDate;
    private String status;
    private String toDisplay;

    public Booking(Passenger passenger, Route route, Seat seat) {
        this.bookingID = "BKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(); //bookingID generator ni

        this.passenger = passenger;
        this.route = route;
        this.seat = seat;

        this.bookingDate = LocalDateTime.now();
        this.status = "PENDING";

        this.totalFare = calculateFare();
    }
    public double calculateFare() {
        double basePrice = route.getPrice();
        if (seat.isPwd()) {
            return basePrice * .80;
        } else if (seat.isStudent()) {
            return basePrice * .90;
        }
        return basePrice;
    }
    public void confirm() {
        if (seat.isAvailable()) {
            this.status = "CONFIRMED";
            seat.setAvailable(false);
            // DISPLAY NI NATO KANANG MURAG ERROR BOX: "Booking confirmed for " + passenger.getName()
        } else {
            // DISPLAY NI NATO KANANG MURAG ERROR BOX: "Error: Seat is already booked.";
            this.status = "FAILED";
        }
    }
    public String generateTicket() {
        if (!this.status.equals("CONFIRMED")) {
            return "Ticket not generated. Booking status: " + this.status;
        }
        StringBuilder ticket = new StringBuilder();
        ticket.append("Booking ID:  ").append(bookingID).append("\n");
        ticket.append("Date:        ").append(bookingDate).append("\n");
        ticket.append("Passenger:   ").append(passenger.getName()).append("\n");
        ticket.append("Route:       ").append(route.getOrigin()).append(" -> ").append(route.getDestination()).append("\n");
        ticket.append("Seat No:     ").append(seat.getSeatNumber()).append("\n");
        ticket.append("Total Fare:  $").append(totalFare).append("\n");
        return ticket.toString();
    }
    public String getBookingID() { return bookingID; }
    public double getTotalFare() { return totalFare; }
}