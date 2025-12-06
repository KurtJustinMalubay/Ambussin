package main.models;

public class Seat {
    private String seatNumber;
    private boolean isAvailable;
    private boolean isStudent;
    private boolean isPwd;

    public Seat(String seatNumber) {
        this.seatNumber = seatNumber;
        this.isAvailable = true;
        this.isStudent = false;
        this.isPwd = false;
    }
    public String getSeatNumber() {
        return seatNumber;
    }
    public boolean isAvailable() {
        return isAvailable;
    }
    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }
    public boolean isStudent() {
        return isStudent;
    }
    public boolean isPwd() {
        return isPwd;
    }
    public void setPassengerType(String type) {
        switch (type) {
            case "STUDENT":
                this.isStudent = true;
                this.isPwd = false;
                break;
            case "PWD":
                this.isPwd = true;
                this.isStudent = false;
                break;
            default:
                this.isStudent = false;
                this.isPwd = false;
        }
    }

    public void reserve() {
        this.isAvailable = false;
    }
    public void release() {
        this.isAvailable = true;
        this.isStudent = false;
        this.isPwd = false;
    }
}