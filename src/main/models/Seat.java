package main.models;

public class Seat {
    private String seatNumber;
    private boolean status;

    public Seat(String seatNumber) {
        this.seatNumber = seatNumber;
        this.status = true; // available
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public boolean reserve(){
        if(!status){
            return false;
        }
        status = false;
        return true;
    }

    public boolean release(){
        if(status){
            return false;
        }
        status = true;
        return true;
    }
    public boolean isAvailable(){
        return status;
    }

    public String statusMessage(){
        return status ? "Available" : "Not Available";
    }
}
